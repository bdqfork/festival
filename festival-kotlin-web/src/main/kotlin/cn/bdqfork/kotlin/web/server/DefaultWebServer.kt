package cn.bdqfork.kotlin.web.server

import cn.bdqfork.context.aware.BeanFactoryAware
import cn.bdqfork.context.aware.ResourceReaderAware
import cn.bdqfork.context.configuration.reader.ResourceReader
import cn.bdqfork.core.exception.BeansException
import cn.bdqfork.core.exception.NoSuchBeanException
import cn.bdqfork.core.factory.BeanFactory
import cn.bdqfork.core.factory.ConfigurableBeanFactory
import cn.bdqfork.core.util.StringUtils
import cn.bdqfork.kotlin.web.RouterAware
import cn.bdqfork.kotlin.web.VertxAware
import cn.bdqfork.kotlin.web.constant.ServerProperty
import cn.bdqfork.kotlin.web.route.RouteManager
import cn.bdqfork.kotlin.web.route.SessionManager
import io.vertx.core.AsyncResult
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.http.HttpVersion
import io.vertx.core.net.JksOptions
import io.vertx.core.net.SocketAddress
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.*
import org.slf4j.LoggerFactory

/**
 * @author bdq
 * @since 2020/2/12
 */
class DefaultWebServer : WebServer, RouterAware, VertxAware, BeanFactoryAware, ResourceReaderAware {
    private lateinit var vertx: Vertx
    private lateinit var router: Router
    private var isRunning = false
    private lateinit var beanFactory: ConfigurableBeanFactory
    private lateinit var resourceReader: ResourceReader
    private lateinit var httpServer: HttpServer

    override fun start() {
        registerCoreHandler()
        registerOptionHandler()
        registerRouteMapping()
        doStart()
    }

    @Throws(Exception::class)
    fun registerCoreHandler() {
        router.route().handler(ResponseContentTypeHandler.create())
        registerSessionHandler()
        registerBodyHandler()
    }

    @Throws(Exception::class)
    private fun registerSessionHandler() {
        val sessionManager = SessionManager(router, vertx)
        sessionManager.setBeanFactory(beanFactory)
        sessionManager.setResourceReader(resourceReader)
        sessionManager.registerSessionHandler()
    }

    private fun registerBodyHandler() {
        val bodyHandler = BodyHandler.create()
        val uploadsDirectory = resourceReader.readProperty(ServerProperty.SERVER_UPLOAD_DERICTORY, String::class.java)
        if (!StringUtils.isEmpty(uploadsDirectory)) {
            bodyHandler.setUploadsDirectory(uploadsDirectory)
        }
        val limit = resourceReader.readProperty(ServerProperty.SERVER_UPLOAD_LIMIT, Long::class.java)
        if (limit != null) {
            bodyHandler.setBodyLimit(limit)
        }
        router.route().handler(bodyHandler)
    }

    @Throws(Exception::class)
    fun registerOptionHandler() {
        try {
            val loggerHandler = beanFactory.getBean(LoggerHandler::class.java)
            router.route().handler(loggerHandler)
        } catch (e: NoSuchBeanException) {
            if (log.isDebugEnabled) {
                log.debug("no logger handler registed!")
            }
        }
        try {
            val errorHandler = beanFactory.getBean(ErrorHandler::class.java)
            router.route().handler(errorHandler)
        } catch (e: NoSuchBeanException) {
            if (log.isDebugEnabled) {
                log.debug("no error handler registed!")
            }
        }
        try {
            val corsHandler = beanFactory.getBean(CorsHandler::class.java)
            router.route().handler(corsHandler)
        } catch (e: NoSuchBeanException) {
            if (log.isDebugEnabled) {
                log.debug("no cors handler registed!")
            }
        }
        val staticEnable = resourceReader.readProperty(ServerProperty.SERVER_STATIC_ENABLE, Boolean::class.java,
                false)
        if (staticEnable) {
            val webRoot = resourceReader.readProperty(ServerProperty.SERVER_STATIC_ROOT, String::class.java,
                    ServerProperty.DEFAULT_STATIC_ROOT)
            val staticHandler = StaticHandler.create(webRoot)

            val staticPath = resourceReader.readProperty(ServerProperty.SERVER_STATIC_PATH, String::class.java,
                    ServerProperty.DEFAULT_STATIC_PATH)
            router.route(staticPath).handler(staticHandler)
        }
    }

    @Throws(Exception::class)
    fun registerRouteMapping() {
        val routeManager = RouteManager(beanFactory, router)
        routeManager.registerRouteMapping()
    }

    @Throws(Exception::class)
    fun doStart() {
        val webSocketRouter = WebSocketRouter(beanFactory)
        val options = resolveHttpServerOptions()
        httpServer = vertx.createHttpServer(options)
                .websocketHandler(webSocketRouter::accept)
                .requestHandler(router)
                .listen { res: AsyncResult<HttpServer?> ->
                    if (res.succeeded()) {
                        if (log.isInfoEnabled) {
                            log.info("started web server at {}:{}!",
                                    options.host, options.port)
                        }
                    } else {
                        if (log.isErrorEnabled) {
                            log.error("failed to start web server at {}:{}!",
                                    options.host, options.port, res.cause())
                        }
                        vertx.close()
                    }
                }
    }

    @Throws(BeansException::class)
    private fun resolveHttpServerOptions(): HttpServerOptions {
        val options = try {
            val options = beanFactory.getBean(HttpServerOptions::class.java)
            HttpServerOptions(options)
        } catch (e: NoSuchBeanException) {
            if (log.isInfoEnabled) {
                log.info("use default web server options!")
            }
            HttpServerOptions()
        }

        val socketAddress = resolveAddress()
        options.setHost(socketAddress.host()).port = socketAddress.port()

        val enableHttp2 = resourceReader.readProperty(ServerProperty.SERVER_HTTP2_ENABLE, Boolean::class.java, false)
        if (enableHttp2) {
            if (log.isInfoEnabled) {
                log.info("http2 enabled!")
            }
            options.alpnVersions.add(HttpVersion.HTTP_2)
        }

        val sslEnable = resourceReader.readProperty(ServerProperty.SERVER_SSL_ENABLE, Boolean::class.java, false)
        if (sslEnable) {
            options.isSsl = sslEnable
            val path = resourceReader.readProperty(ServerProperty.SERVER_SSL_PATH, String::class.java)
            val password = resourceReader.readProperty(ServerProperty.SERVER_SSL_PASSWORD, String::class.java)
            val jksOptions = JksOptions()
                    .setPath(path)
                    .setPassword(password)
            options.keyCertOptions = jksOptions
        }

        return options
    }

    private fun resolveAddress(): SocketAddress {
        val host = resourceReader.readProperty(ServerProperty.SERVER_HOST, String::class.java, ServerProperty.DEFAULT_HOST)
        val port = resourceReader.readProperty(ServerProperty.SERVER_PORT, Int::class.java, ServerProperty.DEFAULT_PORT)
        return SocketAddress.inetSocketAddress(port, host)
    }

    override fun stop() {
        if (isRunning) {
            doStop()
            isRunning = false
        }
    }

    @Throws(Exception::class)
    fun doStop() {
        httpServer.close { res: AsyncResult<Void?> ->
            if (res.succeeded()) {
                if (log.isInfoEnabled) {
                    log.info("closed server!")
                }
            } else {
                if (log.isErrorEnabled) {
                    log.error("failed to close server!", res.cause())
                }
            }
        }
    }

    @Throws(BeansException::class)
    override fun setBeanFactory(beanFactory: BeanFactory) {
        this.beanFactory = beanFactory as ConfigurableBeanFactory
    }

    @Throws(BeansException::class)
    override fun setResourceReader(resourceReader: ResourceReader) {
        this.resourceReader = resourceReader
    }

    companion object {
        private val log = LoggerFactory.getLogger(DefaultWebServer::class.java)
    }

    override fun setRouter(router: Router) {
        this.router = router
    }

    override fun setVertx(vertx: Vertx) {
        this.vertx = vertx
    }

}