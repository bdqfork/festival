package cn.bdqfork.core.annotation;

import java.lang.annotation.*;

/**
 * @author bdq
 * @date 2019-02-12
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
@Documented
public @interface Qualifier {
    String value() default "";
}
