package cn.bdqfork.kotlin.web.constant

/**
 * @author bdq
 * @since 2020/1/28
 */
object ServerProperty {
    const val DEFAULT_HOST = "localhost"
    const val DEFAULT_PORT = 8080
    const val SERVER_HOST = "server.host"
    const val SERVER_PORT = "server.port"
    const val SERVER_UPLOAD_DERICTORY = "server.uploads.directory"
    const val SERVER_UPLOAD_LIMIT = "server.uploads.limit"
    const val SERVER_COOKIE_HTTP_ONLY = "server.cookie.http.only"
    const val SERVER_COOKIE_SECURE = "server.cookie.secure"
    const val SERVER_SESSION_TIMEOUT = "server.session.timeout"
    const val SERVER_SESSION_COOKIE_NAME = "server.session.cookie.name"
    const val SERVER_SESSION_COOKIE_PATH = "server.session.cookie.path"
    const val SERVER_SSL_ENABLE = "server.ssl.enable"
    const val SERVER_SSL_PATH = "server.ssl.path"
    const val SERVER_SSL_PASSWORD = "server.ssl.password"
    const val SERVER_HTTP2_ENABLE = "server.http2.enable"
    const val SERVER_TEMPLATE_ENABLE = "server.template.enable"
    const val SERVER_TEMPLATE_TYPE = "server.template.type"
}