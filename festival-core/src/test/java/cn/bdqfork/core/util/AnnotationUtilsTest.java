package cn.bdqfork.core.util;

import org.junit.Test;

import javax.inject.Named;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static org.junit.Assert.assertEquals;

public class AnnotationUtilsTest {

    @Test
    public void isAnnotationPresent() {
        assert AnnotationUtils.isAnnotationPresent(Bean.class, A.class);

        assert !AnnotationUtils.isAnnotationPresent(Bean.class, Named.class);
    }

    @Test
    public void getMergedAnnotation() {
        A a = AnnotationUtils.getMergedAnnotation(Bean.class, A.class);
        assert a != null;
        assertEquals("hello", a.a());

        Named named = AnnotationUtils.getMergedAnnotation(Bean.class, Named.class);
        assert named == null;
        named = AnnotationUtils.getMergedAnnotation(Bean.class, Named.class);
        assert named == null;
    }

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @interface A {
        String a() default "";
    }

    @A
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @interface B {
        String a() default "world";

        String b() default "";
    }

    @B
    @A
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @interface C {
        String a() default "";

        String b() default "";

        String c() default "";
    }

    @C(a = "hello")
    class Bean {

    }

}