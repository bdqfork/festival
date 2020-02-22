package cn.bdqfork.core.extension;

import java.lang.annotation.*;

/**
 * 注解在扩展类上，表示可以扩展
 *
 * @author bdq
 * @since 2019/9/21
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SPI {
}
