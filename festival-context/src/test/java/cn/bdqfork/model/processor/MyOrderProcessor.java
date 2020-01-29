package cn.bdqfork.model.processor;

import cn.bdqfork.core.annotation.Order;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.processor.BeanPostProcessor;

import javax.inject.Named;
import javax.inject.Singleton;


@Named
public class MyOrderProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitializtion(String beanName, Object bean) throws BeansException {
        System.out.println("MyOrderProcessor Before Initializtion");
        return bean;
    }

    @Override
    public Object postProcessAfterInitializtion(String beanName, Object bean) throws BeansException {
        System.out.println("MyOrderProcessor After Initializtion");
        return bean;
    }
}
