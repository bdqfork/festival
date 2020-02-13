package cn.bdqfork.context.configuration;

import javax.inject.Named;
import java.lang.annotation.*;

/**
 * 标识该Bean为配置Bean
 *
 * @author bdq
 * @since 2020/1/9
 */
@Named
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface Configuration {
    /**
     * 配置项的前缀
     */
    String prefix() default "";

    /**
     * @see javax.inject.Named
     */
    String value() default "";
}