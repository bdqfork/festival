package cn.bdqfork.example.config;

import cn.bdqfork.mvc.context.filter.AuthFilter;
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
    public AuthProvider authProvider(Vertx vertx) {
        JsonObject config = new JsonObject().put("properties_path", "classpath:vertx-users.properties");
        ShiroAuthOptions options = new ShiroAuthOptions().setType(ShiroAuthRealmType.PROPERTIES).setConfig(config);
        return ShiroAuth.create(vertx, options);
    }

    @Singleton
    @Named
    public AuthHandler authHandler(AuthProvider authProvider) {
        return BasicAuthHandler.create(authProvider);
    }

    @Singleton
    @Named
    public AuthFilter authFilter() {
        AuthFilter authFilter = new AuthFilter();
        authFilter.setDeniedHandler(routingContext -> {
            routingContext.response().setStatusCode(403).end("you have not permissions!");
        });
        return authFilter;
    }

}
