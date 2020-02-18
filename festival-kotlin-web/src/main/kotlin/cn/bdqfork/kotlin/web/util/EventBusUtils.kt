package cn.bdqfork.kotlin.web.util

/**
 * @author bdq
 * @since 2020/1/26
 */
object EventBusUtils {
    fun getAddress(clazz: Class<*>): String {
        return clazz.canonicalName
    }
}