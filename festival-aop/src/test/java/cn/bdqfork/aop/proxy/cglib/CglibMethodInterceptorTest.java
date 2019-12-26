package cn.bdqfork.aop.proxy.cglib;

import cn.bdqfork.aop.factory.JSR250FieldCycleDaoImpl;
import cn.bdqfork.core.exception.BeansException;
import org.junit.Test;

import static org.junit.Assert.*;

public class CglibMethodInterceptorTest {

    @Test
    public void newProxyInstance() throws BeansException {
        CglibMethodInterceptor interceptor = new CglibMethodInterceptor();
        interceptor.setTarget(new JSR250FieldCycleDaoImpl());
        interceptor.newProxyInstance();
    }
}