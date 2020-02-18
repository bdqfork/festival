package cn.bdqfork.kotlin.web.route

import cn.bdqfork.context.aware.BeanFactoryAware
import cn.bdqfork.context.aware.ResourceReaderAware
import cn.bdqfork.context.configuration.reader.ResourceReader
import cn.bdqfork.core.exception.BeansException
import cn.bdqfork.core.exception.NoSuchBeanException
import cn.bdqfork.core.factory.BeanFactory
import cn.bdqfork.core.util.StringUtils
import cn.bdqfork.kotlin.web.constant.ServerProperty
import io.vertx.core.Vertx
import io.vertx.ext.auth.AuthProvider
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.SessionHandler
import io.vertx.ext.web.sstore.LocalSessionStore
import io.vertx.ext.web.sstore.SessionStore
import org.slf4j.LoggerFactory

/**
 * @author bdq
 * @since 2020/2/10
 */
class SessionManager(private val router: Router, private val vertx: Vertx) : BeanFactoryAware, ResourceReaderAware {
    private lateinit var beanFactory: BeanFactory
    private lateinit var resourceReader: ResourceReader
    @Throws(BeansException::class)
    fun registerSessionHandler() {
        val sessionStore = try {
            beanFactory.getBean(SessionStore::class.java)
        } catch (e: NoSuchBeanException) {
            LocalSessionStore.create(vertx)
        }

        val sessionHandler = SessionHandler.create(sessionStore)
        resolveAndSetSessionProperties(sessionHandler)

        val authProvider = try {
            beanFactory.getBean(AuthProvider::class.java)
        } catch (e: BeansException) {
            if (log.isDebugEnabled) {
                log.debug("no auth provider")
            }
            null
        }

        if (authProvider != null) {
            sessionHandler.setAuthProvider(authProvider)
        }

        router.route().handler(sessionHandler)
    }

    private fun resolveAndSetSessionProperties(sessionHandler: SessionHandler) {
        val cookieHttpOnly = resourceReader.readProperty(ServerProperty.SERVER_COOKIE_HTTP_ONLY, Boolean::class.java)
        if (cookieHttpOnly != null) {
            sessionHandler.setCookieHttpOnlyFlag(cookieHttpOnly)
        }
        val cookieSecure = resourceReader.readProperty(ServerProperty.SERVER_COOKIE_SECURE, Boolean::class.java)
        if (cookieSecure != null) {
            sessionHandler.setCookieSecureFlag(cookieSecure)
        }
        val sessionTimeout = resourceReader.readProperty(ServerProperty.SERVER_SESSION_TIMEOUT, Long::class.java)
        if (sessionTimeout != null) {
            sessionHandler.setSessionTimeout(sessionTimeout)
        }
        val sessionCookieName = resourceReader.readProperty(ServerProperty.SERVER_SESSION_COOKIE_NAME, String::class.java)
        if (!StringUtils.isEmpty(sessionCookieName)) {
            sessionHandler.setSessionCookieName(sessionCookieName)
        }
        val sessionCookiePath = resourceReader.readProperty(ServerProperty.SERVER_SESSION_COOKIE_PATH, String::class.java)
        if (!StringUtils.isEmpty(sessionCookiePath)) {
            sessionHandler.setSessionCookiePath(sessionCookiePath)
        }
    }

    @Throws(BeansException::class)
    override fun setBeanFactory(beanFactory: BeanFactory) {
        this.beanFactory = beanFactory
    }

    @Throws(BeansException::class)
    override fun setResourceReader(resourceReader: ResourceReader) {
        this.resourceReader = resourceReader
    }

    companion object {
        private val log = LoggerFactory.getLogger(SessionManager::class.java)
    }

}