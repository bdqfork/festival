package cn.bdqfork.web.route.annotation

import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.handler.TimeoutHandler

/**
 * 用于PUT请求
 *
 * @author bdq
 * @since 2020/1/21
 */
@RouteMapping(method = HttpMethod.PUT)
@MustBeDocumented
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
annotation class PutMapping(val value: String, val timeout: Long = TimeoutHandler.DEFAULT_TIMEOUT)