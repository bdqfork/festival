package cn.bdqfork.example.domain;

import cn.bdqfork.mvc.annotation.GetMapping;
import cn.bdqfork.mvc.annotation.Route;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.vertx.core.eventbus.Message;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.RoutingContext;

import javax.inject.Inject;
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
    @Named("ServiceImpl1")
    @Inject
    private IService iService;

    @GetMapping("/hello")
    public void hello(RoutingContext routingContext) {
        routingContext.response()
                .putHeader("content-type", "text/plain")
                .end("Hello World from Vert.x-Web!");
    }

    @GetMapping("/hello2")
    public void hello2(RoutingContext routingContext) {
        routingContext.response()
                .putHeader("content-type", "text/plain")
                .end("Hello World from Vert.x-Web 2!");
    }

    @GetMapping("/service")
    public void service(RoutingContext routingContext) {
        Flowable<String> flowable = iService.getUserName("service test");
        flowable.subscribe(msg -> routingContext.response()
                .putHeader("content-type", "text/plain")
                .end(msg));
    }
}
