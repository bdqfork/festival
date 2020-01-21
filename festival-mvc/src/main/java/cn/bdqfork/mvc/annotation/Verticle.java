package cn.bdqfork.mvc.annotation;

import javax.inject.Named;
import javax.inject.Singleton;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author bdq
 * @since 2020/1/21
 */
@Singleton
@Named
@Documented
@Retention(RUNTIME)
@Target({ElementType.ANNOTATION_TYPE,ElementType.TYPE})
public @interface Verticle {
    String value() default "";
}
