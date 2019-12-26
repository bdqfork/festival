package cn.bdqfork.aop.factory;

import cn.bdqfork.core.factory.support.AnnotationBeanFactory;

/**
 * @author bdq
 * @since 2019/12/26
 */
public class AspectAnnotationBeanFactory extends AnnotationBeanFactory {
    public AspectAnnotationBeanFactory() {
        super();
        setParentBeanFactory(new AspectBeanFactory());
    }
}
