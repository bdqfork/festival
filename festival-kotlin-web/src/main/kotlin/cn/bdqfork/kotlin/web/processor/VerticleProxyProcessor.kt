package cn.bdqfork.kotlin.web.processor

import cn.bdqfork.aop.processor.AopProxyProcessor
import cn.bdqfork.aop.proxy.javassist.Proxy
import cn.bdqfork.context.aware.ClassLoaderAware
import cn.bdqfork.core.exception.BeansException
import cn.bdqfork.core.util.AnnotationUtils
import cn.bdqfork.core.util.AopUtils
import cn.bdqfork.kotlin.web.VertxAware
import cn.bdqfork.kotlin.web.annotation.VerticleMapping
import cn.bdqfork.kotlin.web.service.ServiceVerticle
import cn.bdqfork.kotlin.web.service.VerticleProxyHandler
import io.vertx.core.AsyncResult
import io.vertx.core.Vertx
import org.slf4j.LoggerFactory

/**
 * @author bdq
 * @since 2020/1/26
 */
class VerticleProxyProcessor : AopProxyProcessor(), ClassLoaderAware, VertxAware {
    private lateinit var vertx: Vertx
    private lateinit var classLoader: ClassLoader
    @Throws(BeansException::class)
    override fun postProcessAfterInitializtion(beanName: String, bean: Any): Any {
        val processed = super.postProcessAfterInitializtion(beanName, bean)
        val targetClass = AopUtils.getTargetClass(processed)
        if (AnnotationUtils.isAnnotationPresent(targetClass, VerticleMapping::class.java)) {
            val verticle = ServiceVerticle(processed)
            vertx.deployVerticle(verticle) { res: AsyncResult<String?> ->
                if (res.succeeded()) {
                    if (log.isDebugEnabled) {
                        log.debug("deployed service {} of {} by id {}!", beanName, targetClass.canonicalName, res.result())
                    } else {
                        if (log.isErrorEnabled) {
                            log.error("failed to deploy service {} of {}!", beanName, targetClass.canonicalName, res.cause())
                        }
                        vertx.close()
                    }
                }
            }
            return Proxy.newProxyInstance(classLoader, targetClass.interfaces, VerticleProxyHandler(vertx!!, targetClass))
        }
        return processed
    }

    @Throws(BeansException::class)
    override fun setVertx(vertx: Vertx) {
        this.vertx = vertx
    }

    @Throws(BeansException::class)
    override fun setClassLoader(classLoader: ClassLoader) {
        this.classLoader = classLoader
    }

    companion object {
        private val log = LoggerFactory.getLogger(VerticleProxyProcessor::class.java)
    }
}