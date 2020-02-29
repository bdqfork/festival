package cn.bdqfork.cache.proxy;

import cn.bdqfork.cache.provider.CacheProvider;
import cn.bdqfork.cache.annotation.Cache;
import cn.bdqfork.cache.annotation.Evict;
import cn.bdqfork.core.util.AnnotationUtils;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2020/2/21
 */
public class CacheInvocationHandler implements MethodInterceptor {
    private Object target;
    private CacheProvider cacheProvider;

    public CacheInvocationHandler(Object target, CacheProvider cacheProvider) {
        this.target = target;
        this.cacheProvider = cacheProvider;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {

        if (AnnotationUtils.isAnnotationPresent(method, Cache.class)) {
            Cache cache = AnnotationUtils.getMergedAnnotation(method, Cache.class);
            String cacheKey = cache.value();
            if (cacheProvider.containKey(cacheKey)) {
                return cacheProvider.get(cacheKey);
            }
            Object result = method.invoke(target, args);
            cacheProvider.put(cacheKey, (Serializable) result, cache.expireTime());
            return result;
        }

        if (AnnotationUtils.isAnnotationPresent(method, Evict.class)) {
            Object result = method.invoke(target, args);
            Evict evict = AnnotationUtils.getMergedAnnotation(method, Evict.class);
            cacheProvider.remove(evict.value());
            return result;
        }

        return method.invoke(target, args);
    }
}
