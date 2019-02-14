package cn.bdqfork.ioc.container;

import cn.bdqfork.ioc.exception.SpringToyException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
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
    public Object inject(Object instance, BeanDefination beanDefination) throws SpringToyException {
        if (methodInjectorAttributes != null && methodInjectorAttributes.size() > 0) {
            for (MethodInjectorAttribute attribute : methodInjectorAttributes) {
                Method method = attribute.getMethod();
                String methodName = method.getName();
                if (!methodName.startsWith("set")) {
                    throw new SpringToyException("failed to init bean : " + beanDefination.getName());
                }
                List<InjectorData> parameterInjectorDatas = attribute.getParameterInjectorDatas();
                if (parameterInjectorDatas != null && parameterInjectorDatas.size() > 0) {
                    List<Object> args = new ArrayList<>(parameterInjectorDatas.size());
                    for (InjectorData injectorData : parameterInjectorDatas) {
                        BeanDefination bean = injectorData.getBean();
                        try {
                            args.add(bean.getInstance());
                        } catch (SpringToyException e) {
                            if (!injectorData.isRequired()) {
                                throw new SpringToyException("failed to init bean : " + beanDefination.getName(), e);
                            }
                        }
                    }
                    try {
                        method.invoke(instance, args.toArray());
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new SpringToyException("failed to init bean : " + beanDefination.getName(), e);
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
