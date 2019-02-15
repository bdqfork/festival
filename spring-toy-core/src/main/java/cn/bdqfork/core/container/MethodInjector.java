package cn.bdqfork.core.container;


import cn.bdqfork.core.exception.InjectedException;
import cn.bdqfork.core.exception.MethodInjectedException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * @author bdq
 * @date 2019-02-14
 */
public class MethodInjector extends AbstractInjector {
    private List<MethodInjectorAttribute> methodInjectorAttributes;

    public MethodInjector(List<MethodInjectorAttribute> methodInjectorAttributes, List<InjectorData> injectorDatas) {
        super(injectorDatas);
        this.methodInjectorAttributes = methodInjectorAttributes;
    }

    @Override
    public Object inject(Object instance, BeanDefination beanDefination) throws InjectedException {
        if (methodInjectorAttributes != null && methodInjectorAttributes.size() > 0) {
            for (MethodInjectorAttribute attribute : methodInjectorAttributes) {
                Method method = attribute.getMethod();
                List<InjectorData> parameterInjectorDatas = attribute.getParameterInjectorDatas();
                if (parameterInjectorDatas != null && parameterInjectorDatas.size() > 0) {
                    List<Object> args = new LinkedList<>();
                    for (InjectorData injectorData : parameterInjectorDatas) {
                        BeanDefination bean = injectorData.getBean();
                        try {
                            if (bean != null) {
                                args.add(bean.getInstance());
                            }
                        } catch (InjectedException e) {
                            throw new MethodInjectedException(beanDefination.getName(), e);
                        }
                    }
                    try {
                        if (args.size() > 0) {
                            method.invoke(instance, args.toArray());
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new MethodInjectedException(beanDefination.getName(), e);
                    }
                }
            }
        }
        return instance;
    }

    public List<MethodInjectorAttribute> getMethodInjectorAttributes() {
        return methodInjectorAttributes;
    }
}
