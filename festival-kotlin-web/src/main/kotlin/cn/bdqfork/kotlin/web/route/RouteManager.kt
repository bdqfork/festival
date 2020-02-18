package cn.bdqfork.kotlin.web.route

import cn.bdqfork.core.exception.BeansException
import cn.bdqfork.core.exception.NoSuchBeanException
import cn.bdqfork.core.factory.ConfigurableBeanFactory
import cn.bdqfork.core.util.BeanUtils
import cn.bdqfork.core.util.StringUtils
import cn.bdqfork.kotlin.web.route.filter.Filter
import cn.bdqfork.kotlin.web.route.filter.FilterChain
import cn.bdqfork.kotlin.web.route.filter.FilterChainFactory
import cn.bdqfork.kotlin.web.route.message.DefaultHttpMessageHandler
import cn.bdqfork.kotlin.web.route.message.HttpMessageHandler
import cn.bdqfork.kotlin.web.route.message.resolver.AbstractParameterResolver
import cn.bdqfork.kotlin.web.route.message.resolver.ParameterResolverFactory
import cn.bdqfork.kotlin.web.route.response.ResponseHandlerFactory
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.AuthHandler
import io.vertx.ext.web.handler.TimeoutHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
    private lateinit var routeResolver: RouteResolver
    private lateinit var httpMessageHandler: HttpMessageHandler
    private lateinit var filterChainFactory: FilterChainFactory
    private lateinit var responseHandlerFactory: ResponseHandlerFactory
    private var authHandler: AuthHandler? = null

    @Throws(Exception::class)
    fun registerRouteMapping() {
        val routes = routeResolver.resovleRoutes(beanFactory)

        routes.forEach(Consumer { routeAttribute: RouteAttribute ->

            val signature = generateRouteSignature(routeAttribute.httpMethod, routeAttribute.url)

            check(!registedRoutes.contains(signature)) { String.format("conflict mapping %s !", signature) }
            registedRoutes.add(signature)

            val route = router.route(routeAttribute.httpMethod, routeAttribute.url)

            setTimeoutIfNeed(routeAttribute, route)

            setContentTypeIfNeed(routeAttribute, route)

            setAuthIfNeed(routeAttribute, route)

            handleMapping(routeAttribute, route)
        })
    }

    private fun generateRouteSignature(httpMethod: HttpMethod, path: String): String {
        return httpMethod.name + ":" + path
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
        val invoker = object : Filter {
            override fun doFilter(routingContext: RoutingContext, filterChain: FilterChain) {
                routeAttribute.contextHandler.handle(routingContext)
            }
        }
        route.handler { routingContext: RoutingContext ->
            GlobalScope.launch {
                routingContext.data()[ROUTE_ATTRIBETE_KEY] = routeAttribute
                try {
                    filterChainFactory.getFilterChain(invoker).doFilter(routingContext)
                } catch (e: Exception) {
                    log.error(e.message, e)
                    routingContext.fail(500, e)
                }
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
        var filters: List<Filter> = getFilters()

        filters = BeanUtils.sortByOrder(filters)

        this.filterChainFactory = FilterChainFactory()

        filterChainFactory.registerFilters(filters)
    }

    private fun getFilters(): List<Filter> {
        return try {
            ArrayList(beanFactory.getBeans(Filter::class.java).values)
        } catch (e: NoSuchBeanException) {
            if (log.isDebugEnabled) {
                log.debug("no filter found!")
            }
            emptyList()
        } catch (e: BeansException) {
            throw IllegalStateException(e)
        }
    }

    private fun initHttpMessageHandler() {
        val parameterResolvers = getParameterResolvers()

        val parameterResolverFactory = ParameterResolverFactory()

        parameterResolverFactory.registerResolver(parameterResolvers)

        httpMessageHandler = DefaultHttpMessageHandler(parameterResolverFactory)
    }

    private fun getParameterResolvers(): MutableCollection<AbstractParameterResolver> {
        return try {
            beanFactory.getBeans(AbstractParameterResolver::class.java).values
        } catch (e: BeansException) {
            throw IllegalStateException(e)
        }
    }

    private fun initResponseHandlerFactory() {
        responseHandlerFactory = ResponseHandlerFactory()
    }

    private fun initRouteResolver() {
        routeResolver = RouteResolver(httpMessageHandler, responseHandlerFactory)
    }
}