package cn.bdqfork.kotlin.web

import cn.bdqfork.context.AnnotationApplicationContext
import cn.bdqfork.core.exception.BeansException
import cn.bdqfork.core.exception.NoSuchBeanException
import cn.bdqfork.core.factory.definition.BeanDefinition
import cn.bdqfork.core.factory.registry.BeanDefinitionRegistry
import cn.bdqfork.kotlin.web.server.DefaultWebServer
import cn.bdqfork.kotlin.web.util.VertxUtils
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import org.slf4j.LoggerFactory

/**
 * @author bdq
 * @since 2020/1/21
 */
class WebApplicationContext(vararg scanPaths: String) : AnnotationApplicationContext(*scanPaths) {
    private lateinit var vertx: Vertx
    private lateinit var router: Router

    @Throws(BeansException::class)
    override fun registerBean() {
        super.registerBean()
        registerVertx()
        registerRouter()
        registerWebServer()
    }

    @Throws(BeansException::class)
    private fun registerVertx() {
        vertx = try {
            beanFactory.getBean(Vertx::class.java)
        } catch (e: NoSuchBeanException) {
            if (log.isDebugEnabled) {
                log.debug("can't find Vertx, will use default!")
            }
            beanFactory.registerSingleton("vertx", VertxUtils.vertx)
            beanFactory.getBean(Vertx::class.java)
        }
    }

    @Throws(BeansException::class)
    private fun registerRouter() {
        router = try {
            beanFactory.getBean(Router::class.java)
        } catch (e: NoSuchBeanException) {
            if (log.isDebugEnabled) {
                log.debug("can't find Vertx, will use default!")
            }
            beanFactory.registerSingleton("router", Router.router(vertx))
            beanFactory.getBean(Router::class.java)
        }
    }

    @Throws(BeansException::class)
    private fun registerWebServer() {
        val registry: BeanDefinitionRegistry = beanFactory
        val beanDefinition = BeanDefinition.builder()
                .scope(BeanDefinition.SINGLETON)
                .beanClass(DefaultWebServer::class.java)
                .beanName("webserver")
                .build()
        registry.registerBeanDefinition(beanDefinition.beanName, beanDefinition)
    }

    @Throws(BeansException::class)
    override fun processEnvironment() {
        super.processEnvironment()
        try {
            for (vertxAware in beanFactory.getBeans(VertxAware::class.java).values) {
                vertxAware.setVertx(vertx)
            }
        } catch (e: NoSuchBeanException) {
            if (log.isDebugEnabled) {
                log.debug("no vertx aware found!")
            }
        }
        try {
            for (routerAware in beanFactory.getBeans(RouterAware::class.java).values) {
                routerAware.setRouter(router)
            }
        } catch (e: NoSuchBeanException) {
            if (log.isDebugEnabled) {
                log.debug("no router aware found!")
            }
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(WebApplicationContext::class.java)
    }
}