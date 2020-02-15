package cn.bdqfork.web.route;

import cn.bdqfork.context.aware.BeanFactoryAware;
import cn.bdqfork.context.aware.ResourceReaderAware;
import cn.bdqfork.context.configuration.reader.ResourceReader;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.NoSuchBeanException;
import cn.bdqfork.core.factory.BeanFactory;
import cn.bdqfork.core.util.StringUtils;
import cn.bdqfork.web.constant.ServerProperty;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.auth.AuthProvider;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.SessionHandler;
import io.vertx.reactivex.ext.web.sstore.LocalSessionStore;
import io.vertx.reactivex.ext.web.sstore.SessionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bdq
 * @since 2020/2/10
 */
public class SessionManager implements BeanFactoryAware, ResourceReaderAware {
    private static final Logger log = LoggerFactory.getLogger(SessionManager.class);
    private BeanFactory beanFactory;
    private ResourceReader resourceReader;
    private Router router;
    private Vertx vertx;

    public SessionManager(Router router, Vertx vertx) {
        this.router = router;
        this.vertx = vertx;
    }

    public void registerSessionHandler() throws BeansException {
        SessionStore sessionStore = getOrCreateSessionStore();
        SessionHandler sessionHandler = SessionHandler.create(sessionStore);

        resolveAndSetSessionProperties(sessionHandler);

        AuthProvider authProvider = getAuthProvider();
        if (authProvider != null) {
            sessionHandler.setAuthProvider(authProvider);
        }

        router.route().handler(sessionHandler);
    }

    private SessionStore getOrCreateSessionStore() throws BeansException {
        SessionStore sessionStore;
        try {
            sessionStore = beanFactory.getBean(SessionStore.class);
        } catch (NoSuchBeanException e) {
            sessionStore = LocalSessionStore.create(vertx);
        }
        return sessionStore;
    }

    private AuthProvider getAuthProvider() {
        AuthProvider authProvider = null;
        try {
            authProvider = beanFactory.getBean(AuthProvider.class);
        } catch (BeansException e) {
            if (log.isDebugEnabled()) {
                log.debug("no auth provider");
            }
        }
        return authProvider;
    }

    private void resolveAndSetSessionProperties(SessionHandler sessionHandler) {
        Boolean cookieHttpOnly = resourceReader.readProperty(ServerProperty.SERVER_COOKIE_HTTP_ONLY, Boolean.class);
        if (cookieHttpOnly != null) {
            sessionHandler.setCookieHttpOnlyFlag(cookieHttpOnly);
        }

        Boolean cookieSecure = resourceReader.readProperty(ServerProperty.SERVER_COOKIE_SECURE, Boolean.class);
        if (cookieSecure != null) {
            sessionHandler.setCookieSecureFlag(cookieSecure);
        }

        Long sessionTimeout = resourceReader.readProperty(ServerProperty.SERVER_SESSION_TIMEOUT, Long.class);
        if (sessionTimeout != null) {
            sessionHandler.setSessionTimeout(sessionTimeout);
        }

        String sessionCookieName = resourceReader.readProperty(ServerProperty.SERVER_SESSION_COOKIE_NAME, String.class);
        if (!StringUtils.isEmpty(sessionCookieName)) {
            sessionHandler.setSessionCookieName(sessionCookieName);
        }

        String sessionCookiePath = resourceReader.readProperty(ServerProperty.SERVER_SESSION_COOKIE_PATH, String.class);
        if (!StringUtils.isEmpty(sessionCookiePath)) {
            sessionHandler.setSessionCookiePath(sessionCookiePath);
        }

    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setResourceReader(ResourceReader resourceReader) throws BeansException {
        this.resourceReader = resourceReader;
    }
}
