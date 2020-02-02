package cn.bdqfork.web.annotation;

import io.vertx.core.http.HttpMethod;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 用于GET请求
 *
 * @author bdq
 * @since 2020/1/21
 */
@RouteMapping(method = HttpMethod.GET)
@Documented
@Retention(RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
public @interface GetMapping {
    String value();
}
