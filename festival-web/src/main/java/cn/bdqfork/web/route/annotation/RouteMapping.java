package cn.bdqfork.web.route.annotation;

import io.vertx.core.http.HttpMethod;

import java.lang.annotation.*;

/**
 * 用于将url请求映射到方法
 *
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
