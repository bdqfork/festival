package cn.bdqfork.core.container;

/**
 * @author bdq
 * @date 2019-02-13
 */
public abstract class AbstractInjectorData implements InjectorData {
    /**
     * 默认依赖名称
     */
    private String defalultName;
    /**
     * 指定依赖名称
     */
    private String refName;
    /**
     * 依赖的BeanDefination实例
     */
    private BeanDefinition bean;
    /**
     * 是否必须
     */
    private boolean isRequired;
    /**
     * 是否是Provider或者BeanFactory依赖
     */
    private boolean isProvider;
    /**
     * Provider或者BeanFactory提供的依赖类
     */
    private Class<?> providedType;

    public AbstractInjectorData(String defalultName, String refName, boolean isRequired) {
        this.defalultName = defalultName;
        this.refName = refName;
        this.isRequired = isRequired;
    }

    @Override
    public void setDefaultName(String defaultName) {
        this.defalultName = defaultName;
    }

    @Override
    public String getDefaultName() {
        return defalultName;
    }

    @Override
    public String getRefName() {
        return refName;
    }

    @Override
    public void setBean(BeanDefinition bean) {
        this.bean = bean;
    }

    @Override
    public BeanDefinition getBean() {
        return this.bean;
    }

    @Override
    public boolean isRequired() {
        return isRequired;
    }

    @Override
    public boolean isMatch(BeanDefinition beanDefinition) {
        if (refName != null && refName.equals(beanDefinition.getName())) {
            return true;
        } else if (defalultName.equals(beanDefinition.getName())) {
            return true;
        } else {
            Class<?> type = getType();
            return beanDefinition.isType(type);
        }
    }

    @Override
    public void setProvider(boolean provider) {
        isProvider = provider;
    }

    @Override
    public boolean isProvider() {
        return isProvider;
    }

    @Override
    public void setProvidedType(Class<?> providedType) {
        this.providedType = providedType;
    }

    protected Class<?> getProvidedType() {
        return providedType;
    }
}
