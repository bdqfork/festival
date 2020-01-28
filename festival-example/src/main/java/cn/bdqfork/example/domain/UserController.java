package cn.bdqfork.example.domain;

import cn.bdqfork.core.factory.DisposableBean;
import cn.bdqfork.mvc.mapping.annotation.GetMapping;
import cn.bdqfork.mvc.mapping.annotation.RouteMapping;
import cn.bdqfork.security.annotation.Auth;
import cn.bdqfork.security.annotation.RolesAllowed;
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
@Slf4j
@RouteMapping("/users")
@Singleton
@Named
public class UserController implements DisposableBean {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Named("ServiceImpl1")
    @Inject
    private IService iService;

    @GetMapping("/hello")
    public void hello(RoutingContext routingContext) {
        routingContext.response()
                .putHeader("content-type", "text/plain")
                .end("Hello World from Vert.x-Web!");
    }

    @Auth
    @GetMapping("/hello2")
    public void hello2(RoutingContext routingContext) {
        routingContext.response()
                .putHeader("content-type", "text/plain")
                .end("Hello World from Vert.x-Web 2!");
    }

    @Auth
    @RolesAllowed("role:administrator")
    @GetMapping("/service")
    public void service(RoutingContext routingContext) {
        Flowable<String> flowable = iService.getUserName("service test");
        Disposable disposable = flowable.subscribe(msg -> routingContext.response()
                .putHeader("content-type", "text/plain")
                .end(msg));
        compositeDisposable.add(disposable);
    }

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

    @Override
    public void destroy() throws Exception {
        compositeDisposable.dispose();
    }
}
