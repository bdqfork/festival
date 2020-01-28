package cn.bdqfork.mvc.context;

import io.vertx.core.Handler;
import io.vertx.reactivex.ext.auth.AuthProvider;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.AuthHandler;
import io.vertx.reactivex.ext.web.handler.SessionHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author bdq
 * @since 2020/1/27
 */
@Slf4j
public class SecuritySystemManager {
    private AuthProvider authProvider;
    private AuthHandler authHandler;
    private SessionHandler sessionHandler;
    private Handler<RoutingContext> permitDeniedHandler;

    public SecuritySystemManager() {
        this.permitDeniedHandler = getDefaultPermitDeniedHandler();
    }

    private Handler<RoutingContext> getDefaultPermitDeniedHandler() {
        return new Handler<RoutingContext>() {
            @Override
            public void handle(RoutingContext routingContext) {
                if (log.isTraceEnabled()) {
                    log.trace("do default permit denied handler!");
                }
                routingContext.response().setStatusCode(401).end("permisson denied!");
            }
        };
    }

    public AuthProvider getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(AuthProvider authProvider) {
        this.authProvider = authProvider;
    }

    public AuthHandler getAuthHandler() {
        return authHandler;
    }

    public void setAuthHandler(AuthHandler authHandler) {
        this.authHandler = authHandler;
    }

    public SessionHandler getSessionHandler() {
        return sessionHandler;
    }

    public void setSessionHandler(SessionHandler sessionHandler) {
        this.sessionHandler = sessionHandler;
    }

    public Handler<RoutingContext> getPermitDeniedHandler() {
        return permitDeniedHandler;
    }

    public void setPermitDeniedHandler(Handler<RoutingContext> permitDeniedHandler) {
        this.permitDeniedHandler = permitDeniedHandler;
    }
}
