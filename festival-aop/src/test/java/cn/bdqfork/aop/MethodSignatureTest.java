package cn.bdqfork.aop;

import cn.bdqfork.core.util.ReflectUtils;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class MethodSignatureTest {

    @Test
    public void toShortString() throws NoSuchMethodException {
        Method method = Foo.class.getMethod("foo", String.class);
        MethodSignature methodSignature = new MethodSignature(method);
        System.out.println(methodSignature.toShortString());
        System.out.println(ReflectUtils.getSignature(method));
    }

    static class Foo {
        public void foo(String s) {

        }
    }
}