package cn.bdqfork.core.container;

import cn.bdqfork.core.exception.BeansException;

/**
 * 多实例包装类
 *
 * @author bdq
 * @since 2019-07-31
 */
public class UnSharedInstance {
    private Class<?> clazz;
    private ArgumentHolder argumentHolder;
    private ObjectFactory<Object> objectFactory;

    public UnSharedInstance(Class<?> clazz, ArgumentHolder argumentHolder) {
        this.clazz = clazz;
        this.argumentHolder = argumentHolder;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Object[] getArgs() throws BeansException {
        return argumentHolder.getArgs();
    }

    public void setObjectFactory(ObjectFactory<Object> objectFactory) {
        this.objectFactory = objectFactory;
    }

    public ObjectFactory<Object> getObjectFactory() {
        return objectFactory;
    }

    public interface ArgumentHolder {
        Object[] getArgs() throws BeansException;
    }
}
