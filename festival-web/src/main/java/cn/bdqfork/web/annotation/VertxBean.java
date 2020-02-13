package cn.bdqfork.web.annotation;

import cn.bdqfork.aop.annotation.Optimize;

import javax.inject.Named;
import java.lang.annotation.*;

/**
 * @author bdq
 * @since 2020/2/12
 */
@Named
@Optimize
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface VertxBean {
    String value() default "";
}
