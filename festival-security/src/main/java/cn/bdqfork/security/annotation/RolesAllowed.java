package cn.bdqfork.security.annotation;

import java.lang.annotation.*;

/**
 * 表示哪些角色可以访问api
 *
 * @author bdq
 * @since 2020/1/27
 */
@Auth
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RolesAllowed {
    String[] value();
}
