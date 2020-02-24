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

    @GetMapping("/thymeleaf")
    public ModelAndView testThymeleaf() {
        ModelAndView modelAndView = new ModelAndView("thymeleaf");
        modelAndView.add("hello", "hello thymeleaf");
        return modelAndView;
    }

    @GetMapping("/jade")
    public ModelAndView testJade() {
        ModelAndView modelAndView = new ModelAndView("jade");
        modelAndView.add("hello", "hello jade");
        return modelAndView;
    }

    @GetMapping("/freemarker")
    public ModelAndView testFreeMarker() {
        ModelAndView modelAndView = new ModelAndView("freemarker");
        modelAndView.add("test", "hello freemarker");
        return modelAndView;
    }

    public static void main(String[] args) throws Exception {
        WebApplication.run(Hello.class);
    }

}
