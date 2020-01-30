package cn.bdqfork.core.annotation;

import java.lang.annotation.*;

/**
 * 表示扩展点执行顺序，value必须为正数，数字小的先执行，未标记或未注明value的扩展点都最后执行
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Order {

    int value() default Integer.MAX_VALUE;
}
