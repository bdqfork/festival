package cn.bdqfork.ioc.annotation;

import java.lang.annotation.*;

/**
 * @author bdq
 * @date 2019-02-07
 */
@Component
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Service {
    String name() default "";
}
