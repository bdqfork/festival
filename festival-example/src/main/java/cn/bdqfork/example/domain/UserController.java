package cn.bdqfork.example.domain;

import cn.bdqfork.core.factory.DisposableBean;
import cn.bdqfork.security.annotation.Auth;
import cn.bdqfork.security.annotation.PermitAll;
import cn.bdqfork.security.annotation.PermitAllowed;
import cn.bdqfork.security.annotation.RolesAllowed;
import cn.bdqfork.security.common.LogicType;
import cn.bdqfork.web.context.annotation.GetMapping;
import cn.bdqfork.web.context.annotation.Route;
import cn.bdqfork.web.context.annotation.RouteMapping;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.vertx.reactivex.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author bdq
 * @since 2020/1/21
 */
@Auth
@Slf4j
@Singleton
@RouteMapping("/users")
@Route
public class UserController implements DisposableBean {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Named("ServiceImpl1")
    @Inject
    private IService iService;

    @PermitAll
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

    @PermitAll
    @GetMapping("/hello3")
    public String hello3(RoutingContext routingContext) {
        return "Hello World from Vert.x-Web 3!";
    }

    @PermitAll
    @GetMapping("/hello4")
    public String[] hello4(RoutingContext routingContext) {
        String[] strings = new String[]{"test1", "test2", "test3"};
        return strings;
    }

    @RolesAllowed(value = {"role:administrator", "role:hispassword"}, logic = LogicType.AND)
    @GetMapping("/service")
    public void service(RoutingContext routingContext) {
        Flowable<String> flowable = iService.getUserName("service test");
        Disposable disposable = flowable.subscribe(msg -> routingContext.response()
                .putHeader("content-type", "text/plain")
                .end(msg));
        compositeDisposable.add(disposable);
    }

    @PermitAllowed(value = {"play_golf"}, logic = LogicType.AND)
    @GetMapping("/error")
    public void error(RoutingContext routingContext) {
        Flowable<Void> flowable = iService.testError("service test");
        Disposable disposable = flowable.subscribe(msg -> routingContext.response()
                .putHeader("content-type", "text/plain")
                .end(), e -> routingContext.response()
                .putHeader("content-type", "text/plain")
                .end("error"));
        compositeDisposable.add(disposable);
    }

    public void destroy() throws Exception {
        compositeDisposable.dispose();
    }
}
