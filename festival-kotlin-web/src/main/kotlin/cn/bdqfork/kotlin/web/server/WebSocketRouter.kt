package cn.bdqfork.kotlin.web.server

import cn.bdqfork.context.aware.BeanFactoryAware
import cn.bdqfork.core.exception.BeansException
import cn.bdqfork.core.factory.BeanFactory
import cn.bdqfork.core.factory.ConfigurableBeanFactory
import cn.bdqfork.core.factory.definition.BeanDefinition
import cn.bdqfork.core.util.AnnotationUtils
import cn.bdqfork.core.util.ReflectUtils
import cn.bdqfork.kotlin.web.route.annotation.OnActive
import cn.bdqfork.kotlin.web.route.annotation.OnClose
import cn.bdqfork.kotlin.web.route.annotation.OnOpen
import cn.bdqfork.kotlin.web.route.annotation.ServerEndpoint
import io.vertx.core.http.ServerWebSocket
import io.vertx.core.http.WebSocketFrame
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Collectors

/**
 * @author bdq
 * @since 2020/2/19
 */
class WebSocketRouter(private var beanFactory: BeanFactory) : BeanFactoryAware {

    private val webSocketRouteMap: MutableMap<String, WebSocketRoute> = ConcurrentHashMap(16)

    @Throws(BeansException::class)
    private fun refresh() {
        val beanFactory = beanFactory as ConfigurableBeanFactory
        val beanDefinitions = beanFactory.beanDefinitions.values
                .stream()
                .filter { beanDefinition: BeanDefinition -> checkIfServerEndpoint(beanDefinition) }
                .collect(Collectors.toList())
        for (beanDefinition in beanDefinitions) {
            val webSocketRoute = resolveWebSocketRoute(beanFactory, beanDefinition)
            val path = AnnotationUtils.getMergedAnnotation(beanDefinition.beanClass, ServerEndpoint::class.java).value
            registerWebSocketRoute(path, webSocketRoute)
        }
    }

    private fun checkIfServerEndpoint(beanDefinition: BeanDefinition): Boolean {
        return AnnotationUtils.isAnnotationPresent(beanDefinition.beanClass, ServerEndpoint::class.java)
    }

    @Throws(BeansException::class)
    private fun resolveWebSocketRoute(beanFactory: ConfigurableBeanFactory, beanDefinition: BeanDefinition): WebSocketRoute {
        val beanClass = beanDefinition.beanClass
        val open = ReflectUtils.getMethodByAnnotation(beanClass, OnOpen::class.java)
        val active = ReflectUtils.getMethodByAnnotation(beanClass, OnActive::class.java)
        val close = ReflectUtils.getMethodByAnnotation(beanClass, OnClose::class.java)
        val bean = beanFactory.getBean<Any>(beanDefinition.beanName)
        return WebSocketRoute(bean, open, active, close)
    }

    private fun registerWebSocketRoute(path: String, webSocketRoute: WebSocketRoute) {
        check(!webSocketRouteMap.containsKey(path)) { String.format("conflict websocket point %s!", path) }
        if (log.isInfoEnabled) {
            log.info("register websocket point {}!", path)
        }
        webSocketRouteMap[path] = webSocketRoute
    }

    fun accept(serverWebSocket: ServerWebSocket) {
        val path = serverWebSocket.path()
        if (!webSocketRouteMap.containsKey(path)) {
            serverWebSocket.reject()
            return
        }
        val webSocketRoute = webSocketRouteMap[path]!!
        webSocketRoute.doOpen(serverWebSocket)
        serverWebSocket.frameHandler { frame: WebSocketFrame -> webSocketRoute.doActive(serverWebSocket, frame) }
        serverWebSocket.closeHandler { webSocketRoute.doClose(serverWebSocket) }
    }

    @Throws(BeansException::class)
    override fun setBeanFactory(beanFactory: BeanFactory) {
        this.beanFactory = beanFactory
    }

    private class WebSocketRoute(var bean: Any, var open: Method?, var active: Method?, var close: Method?) {
        fun doOpen(serverWebSocket: ServerWebSocket) {
            if (open == null) {
                return
            }
            try {
                ReflectUtils.invokeMethod(bean, open, serverWebSocket)
            } catch (e: InvocationTargetException) {
                throw IllegalStateException(e.cause)
            }
        }

        fun doActive(serverWebSocket: ServerWebSocket, frame: WebSocketFrame) {
            if (active == null) {
                return
            }
            try {
                ReflectUtils.invokeMethod(bean, active, serverWebSocket, frame)
            } catch (e: InvocationTargetException) {
                throw IllegalStateException(e.cause)
            }
        }

        fun doClose(serverWebSocket: ServerWebSocket) {
            if (close == null) {
                return
            }
            try {
                ReflectUtils.invokeMethod(bean, close, serverWebSocket)
            } catch (e: InvocationTargetException) {
                throw IllegalStateException(e.cause)
            }
        }

    }

    companion object {
        private val log = LoggerFactory.getLogger(WebSocketRouter::class.java)
    }

    init {
        refresh()
    }
}