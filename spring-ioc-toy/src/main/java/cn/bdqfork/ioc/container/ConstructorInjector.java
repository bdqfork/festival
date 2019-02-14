package cn.bdqfork.ioc.container;

import cn.bdqfork.ioc.exception.ConstructorInjectedException;
import cn.bdqfork.ioc.exception.InjectedException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author bdq
 * @date 2019-02-14
 */
public class ConstructorInjector extends AbstractInjector {
    private Constructor<?> constructor;

    public ConstructorInjector(Constructor<?> constructor, List<InjectorData> injectorDatas) {
        super(injectorDatas);
        this.constructor = constructor;
    }

    /**
     * 构造器注入
     *
     * @param beanDefination
     * @return
     * @throws ConstructorInjectedException
     */
    public Object inject(BeanDefination beanDefination) throws ConstructorInjectedException {
        return inject(null, beanDefination);
    }

    @Override
    public Object inject(Object instance, BeanDefination beanDefination) throws ConstructorInjectedException {
        if (constructor != null) {
            if (injectorDatas != null && injectorDatas.size() > 0) {
                List<Object> args = new LinkedList<>();
                for (InjectorData injectorData : injectorDatas) {
                    BeanDefination bean = injectorData.getBean();
                    try {
                        if (bean != null) {
                            args.add(bean.getInstance());
                        }
                    } catch (InjectedException e) {
                        throw new ConstructorInjectedException(beanDefination.getName(), e);
                    }
                }
                try {
                    if (args.size() > 0) {
                        instance = constructor.newInstance(args.toArray());
                    }
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new ConstructorInjectedException(beanDefination.getName(), e);
                }
            }
        }
        return instance;
    }

    public List<InjectorData> getConstructorParameterDatas() {
        return injectorDatas;
    }
}
