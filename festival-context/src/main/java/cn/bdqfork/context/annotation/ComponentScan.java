package cn.bdqfork.context.annotation;

import java.lang.annotation.*;

/**
 * @author bdq
 * @since 2020/1/30
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface ComponentScan {
    String[] value();
}
