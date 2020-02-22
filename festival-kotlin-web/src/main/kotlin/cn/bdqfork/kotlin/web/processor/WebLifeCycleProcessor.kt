package cn.bdqfork.kotlin.web.processor

import cn.bdqfork.context.AbstractLifeCycleProcessor
import cn.bdqfork.context.ApplicationContext
import cn.bdqfork.core.exception.BeansException
import cn.bdqfork.core.exception.NoSuchBeanException
import cn.bdqfork.core.factory.definition.BeanDefinition
import cn.bdqfork.kotlin.web.server.WebServer
import cn.bdqfork.kotlin.web.server.WebVerticle
import cn.bdqfork.kotlin.web.service.JsonMessageCodec
import io.vertx.core.AsyncResult
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import org.slf4j.LoggerFactory
import java.util.concurrent.CountDownLatch

/**
 * @author bdq
 * @since 2020/2/22
 */
class WebLifeCycleProcessor : AbstractLifeCycleProcessor() {
    @Throws(Exception::class)
    override fun beforeStart(applicationContext: ApplicationContext) {
        super.beforeStart(applicationContext)
        val beanDefinition = BeanDefinition.builder()
                .beanName("verticleProxyProcessor")
                .beanClass(VerticleProxyProcessor::class.java)
                .scope(BeanDefinition.SINGLETON)
                .build()
        applicationContext.beanFactory.registerBeanDefinition(beanDefinition.beanName, beanDefinition)
    }

    @Throws(Exception::class)
    override fun afterStart(applicationContext: ApplicationContext) {
        val vertx = applicationContext.getBean(Vertx::class.java)
        vertx.eventBus().registerCodec(JsonMessageCodec())
        val options = getDeploymentOptions(applicationContext)
        val webServer = applicationContext.getBean(WebServer::class.java)
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
    private fun getDeploymentOptions(applicationContext: ApplicationContext): DeploymentOptions {
        var options: DeploymentOptions
        try {
            options = applicationContext.getSpecificBean(SERVER_OPTIONS_NAME, DeploymentOptions::class.java)
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

    @Throws(Exception::class)
    override fun beforeStop(applicationContext: ApplicationContext) {
        val vertx = applicationContext.getBean(Vertx::class.java)
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
    }

    companion object {
        private val log = LoggerFactory.getLogger(WebLifeCycleProcessor::class.java)
        const val SERVER_OPTIONS_NAME = "serverOptions"
    }
}