package cn.bdqfork.security.annotation;

import java.lang.annotation.*;

/**
 * 该注解应使用在Route上，表示该Route需要进行验证
 *
 * @author bdq
 * @since 2020/1/27
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.METHOD})
public @interface Auth {
}
