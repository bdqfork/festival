package cn.bdqfork.mvc.mapping.annotation;

import io.vertx.core.http.HttpMethod;

import java.lang.annotation.*;

/**
 * @author bdq
 * @since 2020/1/21
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.METHOD})
public @interface RouteMapping {
    String value() default "";

    HttpMethod method() default HttpMethod.OPTIONS;
}
