package cn.bdqfork.kotlin.web.server

import io.vertx.kotlin.coroutines.CoroutineVerticle

/**
 * @author bdq
 * @since 2020/1/21
 */
class WebVerticle(private val webServer: WebServer) : CoroutineVerticle() {

    override suspend fun start() {
        webServer.start()
    }

    override suspend fun stop() {
        webServer.stop()
    }

}