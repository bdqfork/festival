package cn.bdqfork.example.config;

import cn.bdqfork.context.configuration.Configuration;
import cn.bdqfork.web.constant.LogicType;
import cn.bdqfork.web.route.PermitHolder;
import cn.bdqfork.web.route.RouteAttribute;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.LoggerHandler;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author bdq
 * @since 2020/1/24
 */
@Singleton
@Configuration
public class ServerConfig {

    @Singleton
    @Named
    public Vertx vertx() {
        return Vertx.vertx(new VertxOptions()
                .setEventLoopPoolSize(20)
                .setWorkerPoolSize(50));
    }

    @Singleton
    @Named
    public LoggerHandler loggerHandler() {
        return LoggerHandler.create();
    }

    @Singleton
    @Named
    public CorsHandler corsHandler() {
        return CorsHandler.create("vertx.io")
                .allowedMethod(HttpMethod.GET);
    }

    @Singleton
    @Named("route1")
    public RouteAttribute customRoute() {
        return RouteAttribute.builder()
                .httpMethod(HttpMethod.GET)
                .url("/custom")
                .contextHandler(routingContext -> {
                    routingContext.response().end("test custom!");
                })
                .build();
    }

    @Singleton
    @Named("route2")
    public RouteAttribute customRoute2() {
        return RouteAttribute.builder()
                .httpMethod(HttpMethod.GET)
                .url("/custom2")
                .auth(true)
                .rolesAllowed(new PermitHolder(LogicType.OR, "role:administrator", "role:hispassword"))
                .contextHandler(routingContext -> {
                    routingContext.response().end("test custom2!");
                })
                .build();
    }
}
