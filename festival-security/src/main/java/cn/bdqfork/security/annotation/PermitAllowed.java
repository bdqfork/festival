package cn.bdqfork.security.annotation;

import cn.bdqfork.security.common.LogicType;

import java.lang.annotation.*;

/**
 * 表示哪些权限可以访问api
 *
 * @author bdq
 * @since 2020/1/27
 */
@Auth
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface PermitAllowed {
    String[] value();

    LogicType logic() default LogicType.AND;
}
