package cn.bdqfork.mvc.context;

import io.vertx.core.Handler;
import io.vertx.reactivex.ext.auth.AuthProvider;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.AuthHandler;
import lombok.Getter;
import lombok.Setter;

/**
 * @author bdq
 * @since 2020/1/27
 */
@Getter
@Setter
public class SecuritySystemManager {
    private AuthProvider authProvider;
    private AuthHandler authHandler;
    private Handler<RoutingContext> permitDeniedHandler;
}
