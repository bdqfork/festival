package cn.bdqfork.example.domain;

import cn.bdqfork.example.model.User;
import cn.bdqfork.web.route.annotation.*;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.RoutingContext;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
@RouteController("/test")
public class TestRestfulController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/hello1")
    public void hello1(RoutingContext routingContext) {
        routingContext.response()
                .putHeader("content-type", "text/plain")
                .end("Hello World from Vert.x-Web!");
    }

    @GetMapping("/hello2")
    public void hello2(HttpServerResponse response) {
        response.putHeader("content-type", "text/plain").end("hello2");
    }

    @Produces("application/json")
    @GetMapping("/hello3")
    public String hello3(HttpServerRequest request) {
        return "id: " + request.getParam("id");
    }

    @Produces({"text/plain", "application/json"})
    @GetMapping("/hello4")
    public String hello3(@Param("id") int id) {
        return "id: " + id;
    }

    @PostMapping("/post")
    public int getId(@Param("id") short id) {
        return id;
    }

    @PutMapping("/put")
    public Map<String, Object> getUser(@Param("username") String username, @Param("age") int age) {
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("age", age);
        return user;
    }

    @PutMapping("/delete")
    public Boolean deleteUser(@Param("id") Long id) {
        return id == 12;
    }

    @PostMapping("/map")
    public MultiMap postMap(MultiMap multiMap) {
        return multiMap;
    }

    @PostMapping("/postUser")
    public Map<String, Object> postObject(User user) {
        Map<String, Object> res = new HashMap<>();
        res.put("id", user.getId());
        res.put("name", user.getUsername());
        res.put("isActive", user.isActive());
        return res;
    }
}
