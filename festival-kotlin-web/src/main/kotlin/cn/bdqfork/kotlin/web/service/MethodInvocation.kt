package cn.bdqfork.kotlin.web.service

import java.io.Serializable
import java.lang.reflect.Method

/**
 * @author bdq
 * @since 2020/1/26
 */
class MethodInvocation(method: Method, var arguments: Array<Any>) : Serializable {
    var methodName: String = method.name
    var argumentClasses: Array<Class<*>> = method.parameterTypes

}