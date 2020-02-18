package cn.bdqfork.web.util

import io.vertx.core.Vertx

/**
 * @author bdq
 * @since 2020/1/21
 */
object VertxUtils {
    @JvmStatic
    var vertx: Vertx = Vertx.vertx()

}