package cn.bdqfork.value;


import java.lang.annotation.*;

/**
 * @author bdq
 * @since 2020/1/9
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)

public @interface Value {
    /**
     * 配置前缀
     */
    String value();
}
