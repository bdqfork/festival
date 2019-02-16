package cn.bdqfork.example.aop;

import cn.bdqfork.core.aop.ProxyFactory;

/**
 * @author bdq
 * @date 2019-02-16
 */
public class AopDemo {
    public static void main(String[] args) {
        UserServiceImpl userServiceImpl1 = new UserServiceImpl();
        ProxyFactory proxyFactory1 = new ProxyFactory();
        proxyFactory1.setTarget(userServiceImpl1);
        proxyFactory1.addAdvice(new UserBeforeAdvice());
        proxyFactory1.addAdvice(new UserAfterAdvice());
        UserService userService1 = (UserService) proxyFactory1.getProxy();
        userService1.sayHello();

        System.out.println();

        UserServiceImpl userServiceImpl2 = new UserServiceImpl();
        ProxyFactory proxyFactory2 = new ProxyFactory();
        proxyFactory2.setTarget(userServiceImpl2);
        proxyFactory2.addAdvice(new UserAroundAdvice());
        UserService userService2 = (UserService) proxyFactory2.getProxy();
        userService2.sayHello();

        System.out.println();

        UserServiceImpl userServiceImpl3 = new UserServiceImpl();
        ProxyFactory proxyFactory3 = new ProxyFactory();
        proxyFactory3.setTarget(userServiceImpl3);
        proxyFactory3.addAdvice(new UserThrowAdvice());
        UserService userService3 = (UserService) proxyFactory3.getProxy();
        userService3.compute();
    }
}
