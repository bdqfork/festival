package cn.bdqfork.example.config;

import cn.bdqfork.mvc.context.SecuritySystemManager;
import cn.bdqfork.value.Configration;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.shiro.ShiroAuthOptions;
import io.vertx.ext.auth.shiro.ShiroAuthRealmType;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.auth.AuthProvider;
import io.vertx.reactivex.ext.auth.shiro.ShiroAuth;
import io.vertx.reactivex.ext.web.handler.AuthHandler;
import io.vertx.reactivex.ext.web.handler.BasicAuthHandler;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author bdq
 * @since 2020/1/28
 */
@Singleton
@Configration
public class AuthConfig {

    @Singleton
    @Named
    public SecuritySystemManager securityManager(Vertx vertx) {
        JsonObject config = new JsonObject().put("properties_path", "classpath:vertx-users.properties");
        ShiroAuthOptions options = new ShiroAuthOptions().setType(ShiroAuthRealmType.PROPERTIES).setConfig(config);
        AuthProvider authProvider = ShiroAuth.create(vertx, options);
        AuthHandler authHandler = BasicAuthHandler.create(authProvider);
        SecuritySystemManager securitySystemManager = new SecuritySystemManager();
        securitySystemManager.setAuthProvider(authProvider);
        securitySystemManager.setAuthHandler(authHandler);
        securitySystemManager.setPermitDeniedHandler(routingContext -> {
            routingContext.response().setStatusCode(401).end("you have not permission!");
        });
        return securitySystemManager;
    }

}
