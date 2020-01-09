package cn.bdqfork.value;

import java.lang.annotation.*;

/**
 * 标识该Bean为配置Bean
 *
 * @author bdq
 * @since 2020/1/9
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Configration {
    /**
     * 配置前缀
     */
    String prefix() default "";
}
