package cn.bdqfork.kotlin.web.service

import cn.bdqfork.core.util.AopUtils
import cn.bdqfork.core.util.ReflectUtils
import cn.bdqfork.kotlin.web.util.EventBusUtils
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.slf4j.LoggerFactory

/**
 * @author bdq
 * @since 2020/1/26
 */
class ServiceVerticle(private val serviceBean: Any) : CoroutineVerticle() {
    private val options: DeliveryOptions = DeliveryOptions()
    override suspend fun start() {
        val eventBus = vertx.eventBus()
        val targetClass = AopUtils.getTargetClass(serviceBean)
        val address = EventBusUtils.getAddress(targetClass)
        eventBus.consumer<Any>(address) { message ->
            try {
                val invocation = message.body() as MethodInvocation
                val methodName = invocation.methodName
                val argumentClasses = invocation.argumentClasses
                val method = serviceBean.javaClass.getMethod(methodName, *argumentClasses)
                val result = ReflectUtils.invokeMethod(serviceBean, method, *invocation.arguments)
                message.reply(result, options)
            } catch (e: Exception) {
                message.reply(e.cause, options)
            }
        }
        if (log.isInfoEnabled) {
            log.info("deploy verticle service {}!", targetClass.canonicalName)
        }
    }

    init {
        options.codecName = HessianMessageCodec.NAME
    }

    companion object {
        private val log = LoggerFactory.getLogger(ServiceVerticle::class.java)
    }

}