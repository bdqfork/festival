package cn.bdqfork.kotlin.web.route.annotation

import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.handler.TimeoutHandler

/**
 * 用于将url请求映射到方法
 *
 * @author bdq
 * @since 2020/1/21
 */
@MustBeDocumented
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class RouteMapping(val value: String = "", val method: HttpMethod = HttpMethod.OPTIONS, val timeout: Long = TimeoutHandler.DEFAULT_TIMEOUT)