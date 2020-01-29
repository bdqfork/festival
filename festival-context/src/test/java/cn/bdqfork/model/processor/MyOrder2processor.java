package cn.bdqfork.model.processor;

import cn.bdqfork.core.annotation.Order;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.processor.BeanPostProcessor;

import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Named
@Order(2)
public class MyOrder2processor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitializtion(String beanName, Object bean) throws BeansException {
        System.out.println("MyOrder2processor Before Initializtion");
        return bean;
    }

    @Override
    public Object postProcessAfterInitializtion(String beanName, Object bean) throws BeansException {
        System.out.println("MyOrder3processor After Initializtion");
        return bean;
    }
}
