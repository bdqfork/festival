package cn.bdqfork.mvc.context.annotation;

import cn.bdqfork.aop.annotation.Optimize;

import javax.inject.Named;
import java.lang.annotation.*;

/**
 * @author bdq
 * @since 2020/1/29
 */
@Optimize
@Named
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface Router {
    String value() default "";
}