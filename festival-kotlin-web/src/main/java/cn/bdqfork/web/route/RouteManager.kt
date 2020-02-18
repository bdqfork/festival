package cn.bdqfork.web.route

import cn.bdqfork.core.exception.BeansException
import cn.bdqfork.core.exception.NoSuchBeanException
import cn.bdqfork.core.factory.ConfigurableBeanFactory
import cn.bdqfork.core.util.BeanUtils
import cn.bdqfork.core.util.StringUtils
import cn.bdqfork.web.route.filter.Filter
import cn.bdqfork.web.route.filter.FilterChain
import cn.bdqfork.web.route.filter.FilterChainFactory
import cn.bdqfork.web.route.message.DefaultHttpMessageHandler
import cn.bdqfork.web.route.message.HttpMessageHandler
import cn.bdqfork.web.route.message.resolver.AbstractParameterResolver
import cn.bdqfork.web.route.message.resolver.ParameterResolverFactory
import cn.bdqfork.web.route.response.ResponseHandlerFactory
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.AuthHandler
import io.vertx.ext.web.handler.TimeoutHandler
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer

/**
 * @author bdq
 * @since 2020/2/10
 */
class RouteManager(private val beanFactory: ConfigurableBeanFactory, private val router: Router) {
    private val registedRoutes = Collections.newSetFromMap(ConcurrentHashMap<String, Boolean>())
    private var routeResolver: RouteResolver? = null
    private var httpMessageHandler: HttpMessageHandler? = null
    private var filterChainFactory: FilterChainFactory? = null
    private var responseHandlerFactory: ResponseHandlerFactory? = null
    private var authHandler: AuthHandler? = null
    private fun initAuthHandler() {
        try {
            authHandler = beanFactory.getBean(AuthHandler::class.java)
        } catch (e: NoSuchBeanException) {
            if (log.isDebugEnabled) {
                log.debug("no auth handler found!")
            }
        } catch (e: BeansException) {
            throw IllegalStateException(e)
        }
    }

    private fun initFilterChainFactory() {
        var filters: List<Filter?>? = filters
        filters = BeanUtils.sortByOrder(filters)
        val filterChainFactory = FilterChainFactory()
        filterChainFactory.registerFilters(filters)
        this.filterChainFactory = filterChainFactory
    }

    private val filters: List<Filter?>
        private get() = try {
            ArrayList(beanFactory.getBeans(Filter::class.java).values)
        } catch (e: NoSuchBeanException) {
            if (log.isDebugEnabled) {
                log.debug("no filter found!")
            }
            emptyList()
        } catch (e: BeansException) {
            throw IllegalStateException(e)
        }

    private fun initHttpMessageHandler() {
        val parameterResolverFactory = ParameterResolverFactory()
        val parameterResolvers: Collection<AbstractParameterResolver>
        parameterResolvers = try {
            beanFactory.getBeans(AbstractParameterResolver::class.java).values
        } catch (e: BeansException) {
            throw IllegalStateException(e)
        }
        parameterResolverFactory.registerResolver(parameterResolvers)
        httpMessageHandler = DefaultHttpMessageHandler(parameterResolverFactory)
    }

    private fun initResponseHandlerFactory() {
        responseHandlerFactory = ResponseHandlerFactory()
    }

    private fun initRouteResolver() {
        routeResolver = RouteResolver(httpMessageHandler, responseHandlerFactory)
    }

    @Throws(Exception::class)
    fun registerRouteMapping() {
        val routes = routeResolver!!.resovleRoutes(beanFactory)
        routes.forEach(Consumer { routeAttribute: RouteAttribute -> handle(routeAttribute) })
    }

    private fun handle(routeAttribute: RouteAttribute) {
        checkIfRouteConflict(routeAttribute)
        val route = createRoute(routeAttribute)
        setTimeoutIfNeed(routeAttribute, route)
        setContentTypeIfNeed(routeAttribute, route)
        setAuthIfNeed(routeAttribute, route)
        handleMapping(routeAttribute, route)
    }

    private fun checkIfRouteConflict(routeAttribute: RouteAttribute) {
        val signature = generateRouteSignature(routeAttribute.httpMethod, routeAttribute.url)
        check(!registedRoutes.contains(signature)) { String.format("conflict mapping %s !", signature) }
        registedRoutes.add(signature)
    }

    private fun generateRouteSignature(httpMethod: HttpMethod, path: String): String {
        return httpMethod.name + ":" + path
    }

    private fun createRoute(routeAttribute: RouteAttribute): Route {
        return router.route(routeAttribute.httpMethod, routeAttribute.url)
    }

    private fun setTimeoutIfNeed(routeAttribute: RouteAttribute, route: Route) {
        if (routeAttribute.timeout > 0) {
            route.handler(TimeoutHandler.create(routeAttribute.timeout))
        }
    }

    private fun setContentTypeIfNeed(routeAttribute: RouteAttribute, route: Route) {
        if (!StringUtils.isEmpty(routeAttribute.consumes)) {
            route.consumes(routeAttribute.consumes)
        }
        if (!StringUtils.isEmpty(routeAttribute.produces)) {
            route.produces(routeAttribute.produces)
        }
    }

    private fun setAuthIfNeed(routeAttribute: RouteAttribute, route: Route) {
        if (authHandler != null && routeAttribute.isAuth && !routeAttribute.isPermitAll) {
            route.handler(authHandler)
        }
    }

    private fun handleMapping(routeAttribute: RouteAttribute, route: Route) {
        if (log.isInfoEnabled) {
            log.info("{} mapping path:{}!", routeAttribute.httpMethod.name, routeAttribute.url)
        }
        val invoker = Filter { routingContext: RoutingContext?, filterChain: FilterChain? -> routeAttribute.contextHandler.handle(routingContext) }
        route.handler { routingContext: RoutingContext ->
            routingContext.data()[ROUTE_ATTRIBETE_KEY] = routeAttribute
            try {
                filterChainFactory!!.getFilterChain(invoker)
                        .doFilter(routingContext)
            } catch (e: Exception) {
                log.error(e.message, e)
                routingContext.fail(500, e)
            }
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(RouteManager::class.java)
        const val ROUTE_ATTRIBETE_KEY = "routeAttribute"
    }

    init {
        initAuthHandler()
        initFilterChainFactory()
        initHttpMessageHandler()
        initResponseHandlerFactory()
        initRouteResolver()
    }
}