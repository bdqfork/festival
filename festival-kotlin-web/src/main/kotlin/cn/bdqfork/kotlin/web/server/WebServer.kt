package cn.bdqfork.web.server

/**
 * @author bdq
 * @since 2020/2/12
 */
interface WebServer {
    @Throws(Exception::class)
    fun start()

    @Throws(Exception::class)
    fun stop()
}