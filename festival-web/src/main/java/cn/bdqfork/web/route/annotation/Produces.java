package cn.bdqfork.web.route.annotation;

import java.lang.annotation.*;

/**
 * 指定路由的produces
 *
 * @author bdq
 * @since 2020/2/12
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Produces {
    String[] value();
}
