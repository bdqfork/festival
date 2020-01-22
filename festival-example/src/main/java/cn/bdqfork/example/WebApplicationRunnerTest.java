package cn.bdqfork.example;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.mvc.context.WebApplicationContext;

public class WebApplicationRunnerTest {

    public static void main(String[] args) throws BeansException {
        WebApplicationContext webApplicationContext = new WebApplicationContext("cn.bdqfork.example.domain");
    }
}