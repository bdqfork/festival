package cn.bdqfork.value;

import javax.inject.Qualifier;
import java.lang.annotation.*;

/**
 * @author bdq
 * @since 2020/1/21
 */
@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Bean {
    String value() default "";
}
