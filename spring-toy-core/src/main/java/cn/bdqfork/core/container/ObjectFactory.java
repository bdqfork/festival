package cn.bdqfork.core.container;

import cn.bdqfork.core.exception.InjectedException;

/**
 * @author bdq
 * @date 2019-02-16
 */
public class ObjectFactory<T> implements BeanFactory<T> {
    private BeanDefination beanDefination;

    public ObjectFactory(BeanDefination beanDefination) {
        this.beanDefination = beanDefination;
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
}
