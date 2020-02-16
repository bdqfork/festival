package cn.bdqfork.example;

import cn.bdqfork.web.WebApplicationContext;
import cn.bdqfork.web.route.annotation.GetMapping;
import cn.bdqfork.web.route.annotation.RouteController;

import javax.inject.Singleton;

/**
 * @author bdq
 * @since 2020/2/16
 */
@Singleton
@RouteController
public class Hello {

    @GetMapping("/hello")
    public String hello() {
        return "hello festival";
    }

    public static void main(String[] args) throws Exception {
        WebApplicationContext webApplicationContext = new WebApplicationContext("cn.bdqfork.example");
        webApplicationContext.start();
    }

}
