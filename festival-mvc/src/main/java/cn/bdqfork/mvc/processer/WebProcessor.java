package cn.bdqfork.mvc.processer;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.ConfigurableBeanFactory;
import cn.bdqfork.core.factory.processor.BeanFactoryPostProcessor;

/**
 * @author bdq
 * @since 2020/1/21
 */
public class WebProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException {
    }
}
