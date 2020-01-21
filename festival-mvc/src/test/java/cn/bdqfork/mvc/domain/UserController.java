package cn.bdqfork.mvc.domain;

import cn.bdqfork.mvc.annotation.GetMapping;
import cn.bdqfork.mvc.annotation.Route;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author bdq
 * @since 2020/1/21
 */
@Route("/users")
@Singleton
@Named
public class UserController {
    @GetMapping("/hello")
    public void hello(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        response.putHeader("content-type", "text/plain");

        // Write to the response and end it
        response.end("Hello World from Vert.x-Web!");
    }
}
