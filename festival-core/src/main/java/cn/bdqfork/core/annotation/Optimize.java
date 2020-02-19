package cn.bdqfork.core.annotation;

import java.lang.annotation.*;

/**
 * 该类应用于需要强制被CGlib代理的实例
 *
 * @author bdq
 * @since 2020/1/29
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.METHOD})
public @interface Optimize {
}
