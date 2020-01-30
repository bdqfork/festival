package cn.bdqfork.model.processor;

import cn.bdqfork.core.annotation.Order;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.processor.BeanPostProcessor;

import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Named
@Order(1)
public class MyOrder1processor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitializtion(String beanName, Object bean) throws BeansException {
        System.out.println("MyOrder1processor Before Initializtion");
        return bean;
    }

    @Override
    public Object postProcessAfterInitializtion(String beanName, Object bean) throws BeansException {
        System.out.println("MyOrder1processor After Initializtion");
        return bean;
    }
}
