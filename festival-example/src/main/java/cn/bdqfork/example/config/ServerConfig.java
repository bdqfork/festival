package cn.bdqfork.example.config;

import cn.bdqfork.context.configuration.Configuration;
import cn.bdqfork.web.RouteAttribute;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.http.HttpMethod;

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
    public DeploymentOptions deploymentOptions() {
        return new DeploymentOptions().setWorker(true)
                .setWorkerPoolSize(50);
    }

    @Singleton
    @Named
    public RouteAttribute routeAttribute() {
        return RouteAttribute.builder()
                .httpMethod(HttpMethod.GET)
                .url("/custom")
                .contextHandler(routingContext -> {
                    routingContext.response().end("test custom!");
                })
                .build();
    }
}
