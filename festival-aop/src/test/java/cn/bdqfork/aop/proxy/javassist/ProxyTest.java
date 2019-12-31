package cn.bdqfork.aop.proxy.javassist;

import cn.bdqfork.aop.factory.JSR250FieldService;
import cn.bdqfork.aop.factory.JSR250FieldServiceImpl;
import cn.bdqfork.core.exception.BeansException;
import org.junit.Test;

import java.lang.reflect.InvocationHandler;

import static org.junit.Assert.*;

public class ProxyTest {

    @Test
    public void newInstance() throws BeansException {
        JavassistInvocationHandler handler = new JavassistInvocationHandler();
        handler.setTarget(new JSR250FieldServiceImpl());
        handler.setInterfaces(JSR250FieldService.class);
        JSR250FieldService instance = (JSR250FieldService) handler.newProxyInstance();
        System.out.println(instance);
        System.out.println(instance.getJsr250FieldCycleDao());
    }
}