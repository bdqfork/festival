package cn.bdqfork.kotlin.web

import cn.bdqfork.core.exception.BeansException
import io.vertx.core.Vertx

/**
 * @author bdq
 * @since 2020/1/26
 */
interface VertxAware {
    @Throws(BeansException::class)
    fun setVertx(vertx: Vertx)
}