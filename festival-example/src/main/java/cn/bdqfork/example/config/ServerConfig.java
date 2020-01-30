package cn.bdqfork.example.config;

import cn.bdqfork.configration.Configration;
import io.vertx.core.DeploymentOptions;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author bdq
 * @since 2020/1/24
 */
@Singleton
@Configration
public class ServerConfig {
    @Singleton
    @Named
    public DeploymentOptions deploymentOptions() {
        return new DeploymentOptions().setWorker(true)
                .setWorkerPoolSize(50);
    }
}
