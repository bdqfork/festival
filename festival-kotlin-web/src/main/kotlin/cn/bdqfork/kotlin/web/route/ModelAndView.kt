package cn.bdqfork.kotlin.web.route

import java.util.*

/**
 * @author bdq
 * @since 2020/2/21
 */
class ModelAndView @JvmOverloads constructor(val view: String, private val model: MutableMap<String, Any?> = HashMap()) {
    fun add(key: String, value: Any?) {
        model[key] = value
    }

    fun getModel(): Map<String, Any?> {
        return model
    }

}