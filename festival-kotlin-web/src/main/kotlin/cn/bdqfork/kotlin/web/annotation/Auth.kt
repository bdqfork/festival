package cn.bdqfork.kotlin.web.annotation

/**
 * 该注解应使用在Route上，表示该Route需要进行验证
 *
 * @author bdq
 * @since 2020/1/27
 */
@MustBeDocumented
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Auth