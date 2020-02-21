package cn.bdqfork.kotlin.web.route.annotation

import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.handler.TimeoutHandler

/**
 * 用于DELETE请求
 *
 * @author bdq
 * @since 2020/1/21
 */
@RouteMapping(method = HttpMethod.DELETE)
@MustBeDocumented
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
annotation class DeleteMapping(val value: String, val timeout: Long = TimeoutHandler.DEFAULT_TIMEOUT)