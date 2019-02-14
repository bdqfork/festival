package cn.bdqfork.ioc.container;

import cn.bdqfork.ioc.exception.FieldInjectedException;
import cn.bdqfork.ioc.exception.SpringToyException;

import java.lang.reflect.Field;
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
    public Object inject(Object instance, BeanDefination beanDefination) throws FieldInjectedException {
        if (injectorDatas != null && injectorDatas.size() > 0) {
            for (InjectorData injectorData : injectorDatas) {
                FieldInjectorData fieldInjectorData = (FieldInjectorData) injectorData;
                Field field = fieldInjectorData.getField();
                field.setAccessible(true);
                try {
                    BeanDefination bean = injectorData.getBean();
                    if (bean != null) {
                        field.set(instance, bean.getInstance());
                    }
                } catch (IllegalAccessException | SpringToyException e) {
                    throw new FieldInjectedException(beanDefination.getName(), e);
                }
            }
        }
        return instance;
    }

    public List<InjectorData> getFieldInjectorDatas() {
        return injectorDatas;
    }
}
