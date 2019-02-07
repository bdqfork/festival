package cn.bdqfork.ioc.annotation;

import java.lang.annotation.*;

/**
 * @author bdq
 * @date 2019-02-07
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Documented
public @interface Component {
    String name() default "";
}
