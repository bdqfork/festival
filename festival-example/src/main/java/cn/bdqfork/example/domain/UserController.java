package cn.bdqfork.example.domain;

import cn.bdqfork.mvc.annotation.GetMapping;
import cn.bdqfork.mvc.annotation.Route;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.RoutingContext;

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
        routingContext.response()
                .putHeader("content-type", "text/plain")
                .end("Hello World from Vert.x-Web!");
    }
}
