package cn.bdqfork.web.route.annotation

/**
 * 指定路由的produces
 *
 * @author bdq
 * @since 2020/2/12
 */
@MustBeDocumented
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Produces(vararg val value: String)