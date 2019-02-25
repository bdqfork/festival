package cn.bdqfork.core.container;


import cn.bdqfork.core.exception.ConstructorInjectedException;
import cn.bdqfork.core.exception.InjectedException;

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
     * @param beanDefinition
     * @return
     * @throws ConstructorInjectedException
     */
    public Object inject(BeanDefinition beanDefinition) throws ConstructorInjectedException {
        return inject(null, beanDefinition);
    }

    @Override
    public Object inject(Object instance, BeanDefinition beanDefinition) throws ConstructorInjectedException {
        if (constructor != null) {
            if (injectorDatas != null && injectorDatas.size() > 0) {
                List<Object> args = new LinkedList<>();
                //遍历构造函数的参数依赖信息
                for (InjectorData injectorData : injectorDatas) {
                    BeanDefinition bean = injectorData.getBean();
                    try {
                        if (bean != null) {
                            //判断是否是Provider
                            if (injectorData.isProvider()) {
                                //添加实例到Provider参数
                                args.add(new ObjectFactory<>(bean.getInstance()));
                            } else {
                                //添加实例作为参数
                                args.add(bean.getInstance());
                            }
                        }
                    } catch (InjectedException e) {
                        throw new ConstructorInjectedException(String.format("failed to inject entity: %s by constructor!", beanDefinition.getName()), e);
                    }
                }
                try {
                    if (args.size() > 0) {
                        //反射调用构造器，构造对象实例
                        instance = constructor.newInstance(args.toArray());
                    }
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new ConstructorInjectedException(String.format("failed to inject entity: %s by constructor!", beanDefinition.getName()), e);
                }
            }
        }
        return instance;
    }

    public List<InjectorData> getConstructorParameterDatas() {
        return injectorDatas;
    }
}
