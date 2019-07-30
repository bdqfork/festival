package cn.bdqfork.ioc.example.aop;

import cn.bdqfork.core.aop.aspect.AspectAfterReturningAdvice;
import cn.bdqfork.core.aop.aspect.AspectAroundAdvice;
import cn.bdqfork.core.aop.aspect.AspectMethodBeforeAdvice;
import cn.bdqfork.core.aop.aspect.AspectThrowsAdvice;
import cn.bdqfork.core.proxy.ProxyFactory;
import cn.bdqfork.ioc.example.dao.UserDao;
import cn.bdqfork.ioc.example.dao.UserDaoImpl;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Before;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author bdq
 * @since 2019-07-30
 */
public class TestProxyFactory {
    public static void main(String[] args) {
        ProxyFactory proxyFactory = new ProxyFactory();
        UserDao userDao = new UserDaoImpl();
        proxyFactory.setTarget(userDao);
        Method[] methods = Aspect.class.getMethods();
        Optional<Method> beforeMethod = Arrays.stream(methods)
                .filter(m -> m.getAnnotation(Before.class) != null)
                .findFirst();
        Optional<Method> afterMethod = Arrays.stream(methods)
                .filter(m -> m.getAnnotation(AfterReturning.class) != null)
                .findFirst();
        Optional<Method> aroundMethod = Arrays.stream(methods)
                .filter(m -> m.getAnnotation(Around.class) != null)
                .findFirst();
        Optional<Method> afterThrowsMethod = Arrays.stream(methods)
                .filter(m -> m.getAnnotation(AfterThrowing.class) != null)
                .findFirst();
        Aspect aspect = new Aspect();
        proxyFactory.addAdvice(new AspectMethodBeforeAdvice(aspect, beforeMethod.get()));
        proxyFactory.addAdvice(new AspectAfterReturningAdvice(aspect, afterMethod.get()));
        proxyFactory.addAdvice(new AspectAroundAdvice(aspect, aroundMethod.get()));
        proxyFactory.addAdvice(new AspectThrowsAdvice(aspect, afterThrowsMethod.get()));
        userDao = (UserDao) proxyFactory.getProxy();
        System.out.println(userDao.getUser().getUsername());
        userDao.getDate();
    }
}
