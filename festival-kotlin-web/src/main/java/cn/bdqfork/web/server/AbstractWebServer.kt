package cn.bdqfork.web.server

import cn.bdqfork.core.exception.BeansException
import cn.bdqfork.web.RouterAware
import cn.bdqfork.web.VertxAware
import io.vertx.core.Vertx
import io.vertx.ext.web.Router

/**
 * @author bdq
 * @since 2020/2/12
 */
abstract class AbstractWebServer : WebServer, RouterAware, VertxAware {
    @JvmField
    protected var vertx: Vertx? = null
    @JvmField
    protected var router: Router? = null
    private var isRunning = false

    @Throws(Exception::class)
    override fun start() {
        registerCoreHandler(router)
        registerOptionHandler(router)
        registerRouteMapping(router)
        doStart()
    }

    @Throws(Exception::class)
    protected abstract fun registerRouteMapping(router: Router?)

    @Throws(Exception::class)
    protected abstract fun registerOptionHandler(router: Router?)

    @Throws(Exception::class)
    protected abstract fun registerCoreHandler(router: Router?)

    @Throws(Exception::class)
    protected abstract fun doStart()

    @Throws(Exception::class)
    override fun stop() {
        if (isRunning) {
            doStop()
            isRunning = false
        }
    }

    @Throws(Exception::class)
    protected abstract fun doStop()

    @Throws(BeansException::class)
    override fun setRouter(router: Router?) {
        this.router = router
    }

    @Throws(BeansException::class)
    override fun setVertx(vertx: Vertx) {
        this.vertx = vertx
    }
}