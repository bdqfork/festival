package cn.bdqfork.ioc.container;

import cn.bdqfork.ioc.exception.SpringToyException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * @author bdq
 * @date 2019-02-14
 */
public class FieldInjector extends AbstractInjector {
    public FieldInjector(List<InjectorData> injectorDatas) {
        super(injectorDatas);
    }

    @Override
    public Object inject(Object instance, BeanDefination beanDefination) throws SpringToyException {
        if (injectorDatas != null && injectorDatas.size() > 0) {
            for (InjectorData injectorData : injectorDatas) {
                FieldInjectorData fieldInjectorData = (FieldInjectorData) injectorData;
                Field field = fieldInjectorData.getField();
                if (Modifier.isFinal(field.getModifiers())) {
                    throw new SpringToyException("failed to init bean : " + beanDefination.getName());
                }
                field.setAccessible(true);
                try {
                    field.set(instance, injectorData.getBean().getInstance());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return instance;
    }

    public List<InjectorData> getFieldInjectorDatas() {
        return injectorDatas;
    }
}
