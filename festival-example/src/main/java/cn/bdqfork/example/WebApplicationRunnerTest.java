package cn.bdqfork.example;

import cn.bdqfork.web.WebApplicationContext;

public class WebApplicationRunnerTest {

    public static void main(String[] args) throws Exception {
        WebApplicationContext webApplicationContext = new WebApplicationContext("cn.bdqfork.example");
        webApplicationContext.start();
    }
}