package cn.bdqfork.web.route.annotation;

import java.lang.annotation.*;

/**
 * @author bdq
 * @since 2020/2/13
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface RequestBody {
}
