package cn.bdqfork.security.annotation;

import java.lang.annotation.*;

/**
 * @author bdq
 * @since 2020/1/28
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface PermitAll {
}
