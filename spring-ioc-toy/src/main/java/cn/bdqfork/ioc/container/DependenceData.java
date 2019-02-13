package cn.bdqfork.ioc.container;

import java.lang.reflect.Field;

/**
 * bean的依赖信息
 *
 * @author bdq
 * @date 2019-02-12
 */
public class DependenceData {
    private String defalultName;
    private String refName;
    private Field field;
    private BeanDefination bean;

    public DependenceData(String defalultName, String refName, Field field) {
        this.defalultName = defalultName;
        this.refName = refName;
        this.field = field;
    }

    public String getDefalultName() {
        return defalultName;
    }

    public String getRefName() {
        return refName;
    }

    public Field getField() {
        return field;
    }

    public BeanDefination getBean() {
        return bean;
    }

    public void setBean(BeanDefination bean) {
        this.bean = bean;
    }

    public boolean isMatch(BeanDefination beanDefination) {
        if (refName != null && refName.equals(beanDefination.getName())) {
            return true;
        } else if (defalultName.equals(beanDefination.getName())) {
            return true;
        } else {
            Class<?> type = field.getType();
            return beanDefination.isType(type);
        }
    }

}
