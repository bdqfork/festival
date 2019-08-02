package cn.bdqfork.core.annotation;

import java.lang.annotation.*;

/**
 * 延迟初始化
 *
 * @author bdq
 * @since 2019-07-27
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface Lazy {
    boolean value() default true;
}
