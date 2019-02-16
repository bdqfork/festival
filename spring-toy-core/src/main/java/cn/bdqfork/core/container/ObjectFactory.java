package cn.bdqfork.core.container;

import cn.bdqfork.core.exception.InjectedException;

/**
 * @author bdq
 * @date 2019-02-16
 */
public class ObjectFactory<T> implements BeanFactory {
    private String beanName;
    private BeanDefination beanDefination;

    public ObjectFactory(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public T get() {
        try {
            return (T) beanDefination.getInstance();
        } catch (InjectedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setBeanDefination(BeanDefination beanDefination) {
        this.beanDefination = beanDefination;
    }
}
