package cn.bdqfork.kotlin.web.service

import cn.bdqfork.kotlin.web.util.EventBusUtils
import io.vertx.core.Vertx
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.Message
import io.vertx.kotlin.coroutines.awaitResult
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 * @author bdq
 * @since 2020/1/26
 */
class VerticleProxyHandler(private val vertx: Vertx, private val targetClass: Class<*>) : InvocationHandler {
    private val options: DeliveryOptions = DeliveryOptions()
    @Throws(Throwable::class)
    override fun invoke(proxy: Any, method: Method, args: Array<Any?>?): Any? = runBlocking {
        val methodInvocation = MethodInvocation(method, args ?: emptyArray())
        val address = EventBusUtils.getAddress(targetClass)
        return@runBlocking GlobalScope.async {
            val message = awaitResult<Message<Any>> { h -> vertx.eventBus().request(address, methodInvocation, options, h) }
            val result = message.body()
            if (result is Throwable) {
                throw result
            }
            return@async result
        }.await()
    }

    init {
        options.codecName = JsonMessageCodec.NAME
    }
}