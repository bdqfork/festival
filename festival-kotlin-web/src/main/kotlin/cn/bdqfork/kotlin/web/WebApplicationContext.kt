package cn.bdqfork.kotlin.web

import cn.bdqfork.cache.constant.CacheProperty
import cn.bdqfork.cache.processor.CacheSupportProcessor
import cn.bdqfork.context.AnnotationApplicationContext
import cn.bdqfork.context.configuration.reader.ResourceReader
import cn.bdqfork.core.exception.BeansException
import cn.bdqfork.core.exception.NoSuchBeanException
import cn.bdqfork.core.factory.BeanFactory
import cn.bdqfork.core.factory.definition.BeanDefinition
import cn.bdqfork.core.factory.registry.BeanDefinitionRegistry
import cn.bdqfork.kotlin.web.processor.VerticleProxyProcessor
import cn.bdqfork.kotlin.web.server.DefaultWebServer
import cn.bdqfork.kotlin.web.server.WebServer
import cn.bdqfork.kotlin.web.server.WebVerticle
import cn.bdqfork.kotlin.web.service.JsonMessageCodec
import cn.bdqfork.kotlin.web.util.VertxUtils
import io.vertx.core.AsyncResult
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import org.slf4j.LoggerFactory
import java.util.concurrent.CountDownLatch

/**
 * @author bdq
 * @since 2020/1/21
 */
class WebApplicationContext(vararg scanPaths: String) : AnnotationApplicationContext(*scanPaths) {
    private lateinit var vertx: Vertx
    private lateinit var router: Router

    @Throws(Exception::class)
    override fun start() {
        super.start()
        val beanFactory: BeanFactory = beanFactory
        vertx.eventBus().registerCodec(JsonMessageCodec())
        val options = getDeploymentOptions(beanFactory)
        val webServer = beanFactory.getBean(WebServer::class.java)
        val webVerticle = WebVerticle(webServer)
        vertx.deployVerticle(webVerticle, options) { res: AsyncResult<String?> ->
            if (res.failed()) {
                if (log.isErrorEnabled) {
                    log.error("failed to deploy web verticle!", res.cause())
                }
                vertx.close()
            }
        }
    }

    @Throws(BeansException::class)
    private fun getDeploymentOptions(beanFactory: BeanFactory): DeploymentOptions {
        var options: DeploymentOptions
        try {
            options = beanFactory.getSpecificBean(SERVER_OPTIONS_NAME, DeploymentOptions::class.java)
            if (log.isInfoEnabled) {
                log.info("server options find, will use it's options!")
            }
        } catch (e: NoSuchBeanException) {
            if (log.isWarnEnabled) {
                log.warn("no server options find, so will use default options, " +
                        "but we recommend you using customer options!")
            }
            options = DeploymentOptions()
        }
        return options
    }

    @Throws(BeansException::class)
    override fun registerProcessor() {
        super.registerProcessor()
        try {
            ClassLoader.getSystemClassLoader().loadClass("cn.bdqfork.cache.processor.CacheSupportProcessor")
            if (log.isInfoEnabled) {
                log.info("cache support!")
            }
        } catch (e: ClassNotFoundException) {
            if (log.isDebugEnabled) {
                log.debug("no cache support!")
            }
            return
        }
        val resourceReader = getBean(ResourceReader::class.java)
        val cacheEnable = resourceReader.readProperty(CacheProperty.CACHE_ENABLE, Boolean::class.java, false)
        if (cacheEnable) {
            if (log.isInfoEnabled) {
                log.info("enable cache!")
            }
            val beanDefinition = BeanDefinition.builder()
                    .beanName("cacheSupportProcessor")
                    .beanClass(CacheSupportProcessor::class.java)
                    .scope(BeanDefinition.SINGLETON)
                    .build()
            beanFactory.registerBeanDefinition(beanDefinition.beanName, beanDefinition)
        }
    }

    @Throws(BeansException::class)
    override fun registerProxyProcessorBean() {
        val beanDefinition = BeanDefinition.builder()
                .beanName("verticleProxyProcessor")
                .beanClass(VerticleProxyProcessor::class.java)
                .scope(BeanDefinition.SINGLETON)
                .build()
        beanFactory.registerBeanDefinition(beanDefinition.beanName, beanDefinition)
    }

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

    @Throws(InterruptedException::class)
    override fun doClose() {
        val latch = CountDownLatch(1)
        vertx.close { res: AsyncResult<Void?> ->
            if (res.succeeded()) {
                if (log.isInfoEnabled) {
                    log.info("closed vertx!")
                }
            }
            latch.countDown()
        }
        latch.await()
        super.doClose()
    }

    companion object {
        private val log = LoggerFactory.getLogger(WebApplicationContext::class.java)
        const val SERVER_OPTIONS_NAME = "serverOptions"
    }
}