package cn.bdqfork.core.util;

import cn.bdqfork.core.factory.BeanFactory;
import cn.bdqfork.core.factory.DefaultBeanFactory;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ReflectUtilsTest {

    @Test
    public void isSubType() {
        assertTrue(ReflectUtils.isSubType(DefaultBeanFactory.class, BeanFactory.class));
    }
}