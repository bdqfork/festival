package cn.bdqfork.mvc;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.mvc.context.WebApplicationContext;
import cn.bdqfork.mvc.util.VertxUtils;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

public class WebApplicationRunnerTest {

    @Test
    public void start() throws BeansException, InterruptedException {
        WebApplicationContext webApplicationContext = new WebApplicationContext("cn.bdqfork.mvc.domain");
        WebApplicationRunner runner = webApplicationContext.getBean(WebApplicationRunner.class);
        Vertx vertx = VertxUtils.getVertx();
        DeploymentOptions options = new DeploymentOptions().setWorker(true);
        vertx.deployVerticle(runner, options);
        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }
}