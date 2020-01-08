package cn.bdqfork.core.context;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.processor.BeanFactoryPostProcessor;
import cn.bdqfork.core.factory.support.AnnotationBeanFactory;

/**
 * @author bdq
 * @since 2020/1/8
 */
public class AnnotationApplicationContext extends AbstractApplicationContext {

    public AnnotationApplicationContext(String... scanPaths) throws BeansException {

        super(new AnnotationBeanFactory());

        AnnotationBeanFactory annotationBeanFactory = (AnnotationBeanFactory) getConfigurableBeanFactory();

        for (String scanPath : scanPaths) {
            annotationBeanFactory.scan(scanPath);
        }

        annotationBeanFactory.refresh();

        processBeanFactory(annotationBeanFactory);

    }

    private void processBeanFactory(AnnotationBeanFactory annotationBeanFactory) throws BeansException {
        BeanFactoryPostProcessor[] beanFactoryPostProcessors = annotationBeanFactory.getBeans(BeanFactoryPostProcessor.class).values().toArray(new BeanFactoryPostProcessor[0]);
        for (BeanFactoryPostProcessor beanFactoryPostProcessor : beanFactoryPostProcessors) {
            beanFactoryPostProcessor.postProcessBeanFactory(annotationBeanFactory);
        }
    }

    @Override
    public void close() {
        AnnotationBeanFactory annotationBeanFactory = (AnnotationBeanFactory) getConfigurableBeanFactory();
        annotationBeanFactory.destroy();
    }
}
