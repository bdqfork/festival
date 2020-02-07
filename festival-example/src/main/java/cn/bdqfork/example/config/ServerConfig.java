package cn.bdqfork.example.config;

import cn.bdqfork.context.configuration.Configuration;
import io.vertx.core.DeploymentOptions;

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
}
