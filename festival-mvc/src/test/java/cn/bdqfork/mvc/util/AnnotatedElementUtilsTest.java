package cn.bdqfork.mvc.util;

import cn.bdqfork.core.util.AnnotatedElementUtils;
import cn.bdqfork.mvc.annotation.Verticle;
import org.junit.Test;

import javax.inject.Qualifier;

public class AnnotatedElementUtilsTest {

    @Test
    public void getMergedAnnotation() {
        Qualifier qualifier = AnnotatedElementUtils.getMergedAnnotation(AnnotationTest.class, Qualifier.class);
        System.out.println(qualifier);
    }

    @Verticle
    class AnnotationTest {

    }
}