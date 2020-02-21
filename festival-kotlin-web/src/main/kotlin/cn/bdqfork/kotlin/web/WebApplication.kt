package cn.bdqfork.kotlin.web

/**
 * @author bdq
 * @since 2020/2/17
 */
class WebApplication {

    companion object {
        fun run(clazz: Class<*>) {
            val scanPath = clazz.getPackage().name
            try {
                WebApplicationContext(scanPath).start()
            } catch (e: Exception) {
                throw IllegalStateException(e)
            }
        }
    }
}