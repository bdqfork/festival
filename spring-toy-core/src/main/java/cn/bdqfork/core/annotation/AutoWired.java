package cn.bdqfork.core.annotation;

import java.lang.annotation.*;

/**
 * @author bdq
 * @since 2019-02-12
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE})
@Documented
public @interface AutoWired {
    boolean required() default true;
}
