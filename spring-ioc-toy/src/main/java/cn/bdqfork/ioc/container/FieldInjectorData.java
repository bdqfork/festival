package cn.bdqfork.ioc.container;

import java.lang.reflect.Field;

/**
 * bean的依赖信息
 *
 * @author bdq
 * @date 2019-02-12
 */
public class FieldInjectorData extends AbstractInjectorData {
    private Field field;

    public FieldInjectorData(String defalultName, String refName, Field field) {
        super(defalultName,refName);
        this.field = field;
    }

    @Override
    public Class<?> getType(){
        return field.getType();
    }

    public Field getField() {
        return field;
    }

}
