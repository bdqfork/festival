package cn.bdqfork.example;

import cn.bdqfork.kotlin.web.WebApplication;
import cn.bdqfork.kotlin.web.route.ModelAndView;
import cn.bdqfork.kotlin.web.route.annotation.GetMapping;
import cn.bdqfork.kotlin.web.route.annotation.RouteController;

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

    @GetMapping("/template")
    public ModelAndView template() {
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.add("test", "hello template");
        return modelAndView;
    }

    public static void main(String[] args) throws Exception {
        WebApplication.Companion.run(Hello.class);
    }

}
