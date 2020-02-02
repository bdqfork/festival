package cn.bdqfork.web.context.annotation;

import java.lang.annotation.*;

/**
 * 该注解用于方法参数，该注解修饰的参数表示需要注入的参数
 *
 * @author bdq
 * @since 2020/2/1
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
public @interface Param {
    /**
     * 参数名
     */
    String value();

    /**
     * 默认值
     */
    String defaultValue() default "";
}
