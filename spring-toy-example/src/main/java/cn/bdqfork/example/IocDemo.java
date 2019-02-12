package cn.bdqfork.example;

import cn.bdqfork.example.ioc.UserService;
import cn.bdqfork.ioc.context.ApplicationContext;
import cn.bdqfork.ioc.exception.SpringToyException;

/**
 * @author bdq
 * @date 2019-02-07
 */
public class IocDemo {
    public static void main(String[] args) throws SpringToyException {
        ApplicationContext ctx = new ApplicationContext("cn.bdqfork.example");
        UserService userService = ctx.getBean(UserService.class);
        System.out.println(userService.getUserName());
    }
}
