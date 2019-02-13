package cn.bdqfork.ioc.container;

/**
 * @author bdq
 * @date 2019-02-13
 */
public abstract class AbstractInjectorData implements InjectorData {
    private String defalultName;
    private String refName;
    private BeanDefination bean;
    private boolean required;

    public AbstractInjectorData(String defalultName, String refName, boolean required) {
        this.defalultName = defalultName;
        this.refName = refName;
        this.required = required;
    }

    @Override
    public String getDefalultName() {
        return defalultName;
    }

    @Override
    public String getRefName() {
        return refName;
    }

    @Override
    public void setBean(BeanDefination bean) {
        this.bean = bean;
    }

    @Override
    public BeanDefination getBean() {
        return this.bean;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public boolean isMatch(BeanDefination beanDefination) {
        if (refName != null && refName.equals(beanDefination.getName())) {
            return true;
        } else if (defalultName.equals(beanDefination.getName())) {
            return true;
        } else {
            Class<?> type = getType();
            return beanDefination.isType(type);
        }
    }
}
