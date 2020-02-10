package cn.bdqfork.example.domain;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.DisposableBean;
import cn.bdqfork.web.annotation.Auth;
import cn.bdqfork.web.annotation.PermitAll;
import cn.bdqfork.web.annotation.PermitAllowed;
import cn.bdqfork.web.annotation.RolesAllowed;
import cn.bdqfork.web.constant.LogicType;
import cn.bdqfork.web.RouterAware;
import cn.bdqfork.web.annotation.GetMapping;
import cn.bdqfork.web.annotation.PostMapping;
import cn.bdqfork.web.annotation.Route;
import cn.bdqfork.web.annotation.RouteMapping;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.vertx.core.http.HttpMethod;
import io.vertx.reactivex.ext.web.FileUpload;
import io.vertx.reactivex.ext.web.Router;
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
public class UserController implements DisposableBean, RouterAware {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Router router;
    @Named("ServiceImpl1")
    @Inject
    private IService iService;

    @PermitAll
    @PostMapping("/file")
    public void file(RoutingContext routingContext) {
        for (FileUpload fileUpload : routingContext.fileUploads()) {
            System.out.println(fileUpload.fileName());
        }
    }

    @PermitAll
    @RouteMapping(value = "/hello", method = HttpMethod.GET)
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
    public String[] hello4() {
        return new String[]{"test1", "test2", "test3"};
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

    @Override
    public void setRouter(Router router) throws BeansException {
        this.router = router;
    }
}
