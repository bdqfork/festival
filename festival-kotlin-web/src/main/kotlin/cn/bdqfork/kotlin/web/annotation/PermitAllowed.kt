package cn.bdqfork.web.annotation

import cn.bdqfork.web.constant.LogicType

/**
 * 表示哪些权限可以访问api
 *
 * @author bdq
 * @since 2020/1/27
 */
@Auth
@MustBeDocumented
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
annotation class PermitAllowed(vararg val value: String, val logic: LogicType = LogicType.AND)