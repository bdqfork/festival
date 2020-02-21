package cn.bdqfork.kotlin.web.route.annotation

/**
 * @author bdq
 * @since 2020/2/19
 */
@MustBeDocumented
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class ServerEndpoint(val value: String)