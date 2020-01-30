package cn.bdqfork.core.annotation;

import java.lang.annotation.*;

/**
 * 表示bean的优先级，用户实现的value必须大于0否则无效，数字越小优先级越高，未标记或未注明value的bean优先级为最低
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Order {

    int value() default Integer.MAX_VALUE;
}
