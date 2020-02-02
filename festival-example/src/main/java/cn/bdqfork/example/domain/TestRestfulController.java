package cn.bdqfork.example.domain;

import cn.bdqfork.web.context.annotation.PostMapping;
import cn.bdqfork.web.context.annotation.Route;
import cn.bdqfork.web.context.annotation.RouteMapping;
import io.vertx.reactivex.ext.web.RoutingContext;

import javax.inject.Singleton;

@Singleton
@Route
@RouteMapping("/test")
public class TestRestfulController {

    @PostMapping("/hello")
    public void hello(RoutingContext routingContext) {
        routingContext.response()
                .putHeader("content-type", "text/plain")
                .end("Hello World from Vert.x-Web!");
    }
}
