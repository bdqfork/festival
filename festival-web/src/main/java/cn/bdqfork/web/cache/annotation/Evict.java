package cn.bdqfork.web.cache.annotation;

import java.lang.annotation.*;

/**
 * 该注解使用在方法上，在方法执行完之后，删除指定的缓存
 *
 * @author bdq
 * @since 2020/2/17
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Evict {
    /**
     * 缓存的key
     */
    String value();
}
