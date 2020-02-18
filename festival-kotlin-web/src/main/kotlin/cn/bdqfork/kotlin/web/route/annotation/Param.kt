package cn.bdqfork.kotlin.web.route.annotation

/**
 * 该注解用于方法参数，该注解修饰的参数表示需要注入的参数
 *
 * @author bdq
 * @since 2020/2/1
 */
@MustBeDocumented
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.ANNOTATION_CLASS)
annotation class Param(
        /**
         * 参数名
         */
        val value: String,
        /**
         * 是否必须参数
         */
        val required: Boolean = true,
        /**
         * 默认值
         */
        val defaultValue: String = "null")