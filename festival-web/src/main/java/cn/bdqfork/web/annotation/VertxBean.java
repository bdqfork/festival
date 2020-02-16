package cn.bdqfork.web.annotation;

import javax.inject.Named;
import java.lang.annotation.*;

/**
 * @author bdq
 * @since 2020/2/12
 */
@Named
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface VertxBean {
    String value() default "";
}
