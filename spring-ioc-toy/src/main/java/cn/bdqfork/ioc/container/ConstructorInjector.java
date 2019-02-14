package cn.bdqfork.ioc.container;

import cn.bdqfork.ioc.exception.SpringToyException;

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
     * @throws SpringToyException
     */
    public Object inject(BeanDefination beanDefination) throws SpringToyException {
        return inject(null, beanDefination);
    }

    @Override
    public Object inject(Object instance, BeanDefination beanDefination) throws SpringToyException {
        if (constructor != null) {
            if (injectorDatas != null && injectorDatas.size() > 0) {
                List<Object> args = new LinkedList<>();
                for (InjectorData injectorData : injectorDatas) {
                    BeanDefination bean = injectorData.getBean();
                    try {
                        if (bean != null) {
                            args.add(bean.getInstance());
                        }
                    } catch (SpringToyException e) {
                        throw new SpringToyException("failed to init bean : " + beanDefination.getName(), e);
                    }
                }
                try {
                    if (args.size() > 0) {
                        instance = constructor.newInstance(args.toArray());
                    }
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new SpringToyException("failed to init bean : " + beanDefination.getName(), e);
                }
            }
        }
        return instance;
    }

    public List<InjectorData> getConstructorParameterDatas() {
        return injectorDatas;
    }
}
