package cn.bdqfork.core.container;


/**
 * @author bdq
 * @date 2019-02-16
 */
public class ObjectFactory<T> implements BeanFactory<T> {
    private T instance;

    public ObjectFactory(T instance) {
        this.instance = instance;
    }

    @Override
    public T get() {
        return instance;
    }
}
