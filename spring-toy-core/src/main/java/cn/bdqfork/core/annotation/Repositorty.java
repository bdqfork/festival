package cn.bdqfork.core.annotation;

import java.lang.annotation.*;

/**
 * @author bdq
 * @since 2019-02-07
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Documented
public @interface Repositorty {
    String value() default "";
}
