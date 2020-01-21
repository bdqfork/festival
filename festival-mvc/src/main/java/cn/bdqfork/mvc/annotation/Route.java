package cn.bdqfork.mvc.annotation;

import java.lang.annotation.*;

/**
 * @author bdq
 * @since 2020/1/21
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.METHOD})
public @interface Route {
    String value() default "";
}
