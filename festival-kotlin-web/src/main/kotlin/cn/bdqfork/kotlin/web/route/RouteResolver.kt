package cn.bdqfork.web.route

import cn.bdqfork.core.exception.BeansException
import cn.bdqfork.core.exception.NoSuchBeanException
import cn.bdqfork.core.factory.ConfigurableBeanFactory
import cn.bdqfork.core.util.AnnotationUtils
import cn.bdqfork.core.util.AopUtils
import cn.bdqfork.web.annotation.Auth
import cn.bdqfork.web.annotation.PermitAll
import cn.bdqfork.web.annotation.PermitAllowed
import cn.bdqfork.web.annotation.RolesAllowed
import cn.bdqfork.web.route.annotation.Consumes
import cn.bdqfork.web.route.annotation.Produces
import cn.bdqfork.web.route.annotation.RouteController
import cn.bdqfork.web.route.annotation.RouteMapping
import cn.bdqfork.web.route.message.HttpMessageHandler
import cn.bdqfork.web.route.response.ResponseHandlerFactory
import java.lang.reflect.Method
import java.util.*

/**
 * @author bdq
 * @since 2020/2/10
 */
class RouteResolver(private val httpMessageHandler: HttpMessageHandler, private val responseHandlerFactory: ResponseHandlerFactory) {
    @Throws(BeansException::class)
    fun resovleRoutes(beanFactory: ConfigurableBeanFactory): Collection<RouteAttribute> {
        val routeAttributes: MutableList<RouteAttribute> = LinkedList()
        for (routeBean in getRouteBeans(beanFactory)) {
            val beanClass = AopUtils.getTargetClass(routeBean)
            val baseUrl = resolveBaseUrl(beanClass)
            for (method in beanClass.declaredMethods) {
                if (!AnnotationUtils.isAnnotationPresent(method, RouteMapping::class.java)) {
                    continue
                }
                val attribute = createRouteAttribute(baseUrl, routeBean, method)
                setProducesIfNeed(method, attribute)
                setConsumesIfNeed(method, attribute)
                setAuthInfoIfNeed(attribute, beanClass, method)
                routeAttributes.add(attribute)
            }
        }
        val customRoutes = resolveCustomRoutes(beanFactory)
        routeAttributes.addAll(customRoutes)
        return routeAttributes
    }

    @Throws(BeansException::class)
    private fun getRouteBeans(beanFactory: ConfigurableBeanFactory): List<Any> {
        val beans: MutableList<Any> = LinkedList()
        for (beanDefinition in beanFactory.beanDefinitions.values) {
            if (AnnotationUtils.isAnnotationPresent(beanDefinition.beanClass, RouteController::class.java)) {
                val beanName = beanDefinition.beanName
                val bean = beanFactory.getBean<Any>(beanName)
                beans.add(bean)
            }
        }
        return beans
    }

    private fun resolveBaseUrl(beanClass: Class<*>): String {
        return if (AnnotationUtils.isAnnotationPresent(beanClass, RouteMapping::class.java)) {
            AnnotationUtils.getMergedAnnotation(beanClass, RouteMapping::class.java).value
        } else ""
    }

    private fun createRouteAttribute(baseUrl: String, bean: Any, method: Method): RouteAttribute {
        val routeMapping = AnnotationUtils.getMergedAnnotation(method, RouteMapping::class.java)
        return RouteAttribute.builder()
                .url(baseUrl + routeMapping.value)
                .httpMethod(routeMapping.method)
                .timeout(routeMapping.timeout)
                .contextHandler(RouteHandler(httpMessageHandler, responseHandlerFactory, method, bean))
                .build()
    }

    private fun setProducesIfNeed(method: Method, attribute: RouteAttribute) {
        val produces = AnnotationUtils.getMergedAnnotation(method, Produces::class.java)
        if (produces != null) {
            for (produce in produces.value) {
                attribute.produces = produce
            }
        }
    }

    private fun setConsumesIfNeed(method: Method, attribute: RouteAttribute) {
        val consumes = AnnotationUtils.getMergedAnnotation(method, Consumes::class.java)
        if (consumes != null) {
            for (consume in consumes.value) {
                attribute.consumes = consume
            }
        }
    }

    private fun setAuthInfoIfNeed(routeAttribute: RouteAttribute, beanClass: Class<*>, routeMethod: Method) {
        if (!checkIfAuth(beanClass, routeMethod)) {
            return
        }
        setAuthInfo(routeAttribute, routeMethod)
    }

    private fun checkIfAuth(beanClass: Class<*>, routeMethod: Method): Boolean {
        return AnnotationUtils.isAnnotationPresent(beanClass, Auth::class.java) || AnnotationUtils.isAnnotationPresent(routeMethod, Auth::class.java)
    }

    private fun setAuthInfo(routeAttribute: RouteAttribute, routeMethod: Method) {
        routeAttribute.isAuth = true
        val permitAll = AnnotationUtils.isAnnotationPresent(routeMethod, PermitAll::class.java)
        routeAttribute.isPermitAll = permitAll
        if (permitAll) {
            return
        }
        val permitAllowed = AnnotationUtils.getMergedAnnotation(routeMethod, PermitAllowed::class.java)
        if (permitAllowed != null) {
            routeAttribute.permitAllowed = PermitHolder(permitAllowed)
        }
        val rolesAllowed = AnnotationUtils.getMergedAnnotation(routeMethod, RolesAllowed::class.java)
        if (rolesAllowed != null) {
            routeAttribute.rolesAllowed = PermitHolder(rolesAllowed)
        }
    }

    @Throws(BeansException::class)
    private fun resolveCustomRoutes(beanFactory: ConfigurableBeanFactory): Collection<RouteAttribute> {
        return try {
            beanFactory.getBeans(RouteAttribute::class.java).values
        } catch (e: NoSuchBeanException) {
            emptyList()
        }
    }

}