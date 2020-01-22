package cn.bdqfork.example;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.mvc.WebApplicationRunner;
import cn.bdqfork.mvc.context.WebApplicationContext;
import io.vertx.core.DeploymentOptions;
import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.Vertx;

public class WebApplicationRunnerTest {

    public static void main(String[] args) throws BeansException {
        WebApplicationContext webApplicationContext = new WebApplicationContext("cn.bdqfork.example.domain");
        WebApplicationRunner runner = webApplicationContext.getBean(WebApplicationRunner.class);
        DeploymentOptions options = new DeploymentOptions();
        Vertx vertx = io.vertx.reactivex.core.Vertx.vertx();
        RxHelper.deployVerticle(vertx, runner, options);
        vertx.deployVerticle(runner, options);
    }
}