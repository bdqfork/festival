package cn.bdqfork.cache.util;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

/**
 * 生成代理类
 *
 * @author h-l-j
 * @since 2020/2/29
 */
public class ProxyUtil {

    public static Object createProxyBean(ClassLoader classLoader, Class<?> targetClass, MethodInterceptor methodInterceptor) {
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(classLoader);
        enhancer.setCallbackType(MethodInterceptor.class);

        enhancer.setSuperclass(targetClass);
        enhancer.setCallback(methodInterceptor);

        return enhancer.create();
    }
}