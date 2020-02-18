package cn.bdqfork.web.route.annotation

/**
 * 指定路由的consumes
 *
 * @author bdq
 * @since 2020/2/12
 */
@MustBeDocumented
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Consumes(vararg val value: String)