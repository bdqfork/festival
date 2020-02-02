package cn.bdqfork.web.annotation;

import io.vertx.core.http.HttpMethod;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author bdq
 * @since 2020/1/21
 */
@RouteMapping(method = HttpMethod.POST)
@Documented
@Retention(RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
public @interface PostMapping {
    String value();
}