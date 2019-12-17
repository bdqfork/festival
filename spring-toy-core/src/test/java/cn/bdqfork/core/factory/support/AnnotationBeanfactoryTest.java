package cn.bdqfork.core.factory.support;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.UnsatisfiedBeanException;
import cn.bdqfork.model.cycle.ConstructorCycleServiceImpl;
import cn.bdqfork.model.cycle.FieldCycleServiceImpl;
import cn.bdqfork.model.cycle.SetterCycleServiceImpl;
import org.junit.Test;

public class AnnotationBeanfactoryTest {

    @Test(expected = UnsatisfiedBeanException.class)
    public void testConstructorCycle() throws BeansException {
        AnnotationBeanfactory annotationBeanfactory = new AnnotationBeanfactory("cn.bdqfork.model");
        annotationBeanfactory.getBean(ConstructorCycleServiceImpl.class);
    }

    @Test
    public void testFieldCycle() throws BeansException {
        AnnotationBeanfactory annotationBeanfactory = new AnnotationBeanfactory("cn.bdqfork.model");
        annotationBeanfactory.getBean(FieldCycleServiceImpl.class);
    }

    @Test
    public void testMethodCycle() throws BeansException {
        AnnotationBeanfactory annotationBeanfactory = new AnnotationBeanfactory("cn.bdqfork.model");
        annotationBeanfactory.getBean(SetterCycleServiceImpl.class);
    }
}