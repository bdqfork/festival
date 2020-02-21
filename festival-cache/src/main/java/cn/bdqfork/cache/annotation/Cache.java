package cn.bdqfork.cache.annotation;

import java.lang.annotation.*;

/**
 * 该注解使用在方法上，在方法执行完之后，对方法的结果进行缓存，再次执行操作时，若缓存不为空，则直接返回缓存结果。
 *
 * @author bdq
 * @since 2020/2/17
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cache {
    /**
     * 缓存的key
     */
    String value();

    /**
     * 缓存过期时间，单位秒，默认60秒
     */
    long expireTime() default 60;
}
