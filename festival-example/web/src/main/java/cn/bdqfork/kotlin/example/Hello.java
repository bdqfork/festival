package cn.bdqfork.kotlin.example;

import cn.bdqfork.web.WebApplication;
import cn.bdqfork.web.route.ModelAndView;
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

    @GetMapping("/template")
    public ModelAndView template() {
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.add("test", "hello template");
        return modelAndView;
    }

    public static void main(String[] args) throws Exception {
        WebApplication.run(Hello.class);
    }

}
