package cn.bdqfork.value;

import javax.inject.Qualifier;
import java.lang.annotation.*;

/**
 * @author bdq
<<<<<<< HEAD
 * @since 2020/1/21
=======
 * @since 2020/1/22
>>>>>>> bean
 */
@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
<<<<<<< HEAD
@Target(ElementType.METHOD)
=======
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
>>>>>>> bean
public @interface Bean {
    String value() default "";
}
