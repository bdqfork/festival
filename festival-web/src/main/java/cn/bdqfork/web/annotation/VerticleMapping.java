package cn.bdqfork.web.annotation;

import javax.inject.Named;
import javax.inject.Singleton;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 该注解用于将服务转化为Verticle，服务之间的调用将通过EventBus进行通信。
 *
 * @author bdq
 * @since 2020/1/21
 */
@Singleton
@Named
@Documented
@Retention(RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
public @interface VerticleMapping {
    String value() default "";
}
