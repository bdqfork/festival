package cn.bdqfork.example.domain;

import cn.bdqfork.web.annotation.Auth;
import cn.bdqfork.web.annotation.PermitAll;
import cn.bdqfork.web.annotation.PermitAllowed;
import cn.bdqfork.web.annotation.RolesAllowed;
import cn.bdqfork.web.constant.LogicType;
import cn.bdqfork.web.route.annotation.GetMapping;
import cn.bdqfork.web.route.annotation.PostMapping;
import cn.bdqfork.web.route.annotation.RouteController;
import cn.bdqfork.web.route.annotation.RouteMapping;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author bdq
 * @since 2020/1/21
 */
@Auth
@Singleton
@RouteController("/users")
public class UserController {
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
    @RouteMapping(value = "/hello", method = HttpMethod.GET, timeout = 1000)
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

    @RolesAllowed(value = {"role:administrator", "role:hispassword"}, logic = LogicType.OR)
    @GetMapping("/service")
    public void service(RoutingContext routingContext) {
        String msg = iService.getUserName("service test");
        routingContext.response()
                .putHeader("content-type", "text/plain")
                .end(msg);
    }

    @PermitAllowed(value = {"play_golf"}, logic = LogicType.AND)
    @GetMapping("/error")
    public void error(RoutingContext routingContext) {
        try {
            iService.testError("service test");
        } catch (Exception e) {
            routingContext.response()
                    .putHeader("content-type", "text/plain")
                    .end(e.getMessage());
        }
    }

}
