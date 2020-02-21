package cn.bdqfork.kotlin.web.route.annotation

import io.vertx.core.http.HttpMethod

/**
 * 用于GET请求
 *
 * @author bdq
 * @since 2020/1/21
 */
@RouteMapping(method = HttpMethod.GET)
@MustBeDocumented
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
annotation class GetMapping(val value: String, val timeout: Long = -1)