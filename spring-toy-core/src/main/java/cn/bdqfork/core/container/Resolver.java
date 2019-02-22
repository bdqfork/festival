package cn.bdqfork.core.container;

import cn.bdqfork.core.exception.SpringToyException;
import cn.bdqfork.core.exception.UnsatisfiedBeanException;

import java.util.Map;

/**
 * @author bdq
 * @date 2019-02-22
 */
public class Resolver {
    private BeanContainer beanContainer;

    public Resolver(BeanContainer beanContainer) {
        this.beanContainer = beanContainer;
    }

    public void resolve(BeanDefination beanDefination) throws SpringToyException {
        //如果已经解析过了，则返回
        if (beanDefination.isResolved()) {
            return;
        }
        //优先解析父类
        Class<?> superClass = beanDefination.getClazz().getSuperclass();
        if (superClass != null && superClass != Object.class) {

            for (BeanDefination bean : beanContainer.getBeans(superClass).values()) {
                if (bean != beanDefination) {
                    //递归解析父类
                    resolve(bean);
                }
            }
        }

        InjectorProvider injectorProvider = beanDefination.getInjectorProvider();
        if (injectorProvider != null) {

            //如果有构造器注入，则先解析构造器注入依赖
            if (injectorProvider.getConstructorParameterDatas() != null) {
                for (InjectorData parameterInjectorData : injectorProvider.getConstructorParameterDatas()) {
                    doResolve(beanDefination, injectorProvider, parameterInjectorData, parameterInjectorData.isRequired());
                }
            }

            //如果有字段注入，则解析字段注入依赖
            if (injectorProvider.getFieldInjectorDatas() != null) {
                for (InjectorData fieldInjectorData : injectorProvider.getFieldInjectorDatas()) {
                    doResolve(beanDefination, injectorProvider, fieldInjectorData, fieldInjectorData.isRequired());
                }
            }

            //如果有方法注入，则解析方法注入依赖
            if (injectorProvider.getMethodInjectorAttributes() != null) {
                for (MethodInjectorAttribute methodInjectorAttribute : injectorProvider.getMethodInjectorAttributes()) {
                    if (methodInjectorAttribute.getParameterInjectorDatas() != null) {
                        for (InjectorData parameterInjectorData : methodInjectorAttribute.getParameterInjectorDatas()) {
                            doResolve(beanDefination, injectorProvider, parameterInjectorData, methodInjectorAttribute.isRequired());
                        }
                    }
                }
            }

        }

        beanDefination.setResolved(true);

    }

    private void doResolve(BeanDefination beanDefination, InjectorProvider injectorProvider, InjectorData injectorData, boolean isRequired) throws UnsatisfiedBeanException {
        BeanDefination ref = null;

        Map<String, BeanDefination> beanDefinationMap = beanContainer.getBeanDefinations();
        //判断依赖组件是否存在，先查找指定名称的依赖，如果不存在，则按找默认名称去查找，仍然不存在，则再按类型匹配
        if (injectorData.getRefName() != null && beanDefinationMap.containsKey(injectorData.getRefName())) {
            ref = beanDefinationMap.get(injectorData.getRefName());
        } else if (beanDefinationMap.containsKey(injectorData.getDefaultName())) {
            ref = beanDefinationMap.get(injectorData.getDefaultName());
        } else {
            for (BeanDefination bean : beanDefinationMap.values()) {
                if (bean.isType(injectorData.getType())) {
                    ref = bean;
                    break;
                } else if (bean.isSubType(injectorData.getType())) {
                    ref = bean;
                    break;
                }
            }
        }

        //判断依赖是否存在，如果不存在，则抛出异常。如果依赖存在，但有相互引用的情况，也抛出异常
        if (ref == null) {
            if (isRequired) {
                throw new UnsatisfiedBeanException("unsatisfied entity , the entity named " + injectorData.getType() + " don't exists");
            }
        } else if (beanDefination == ref || injectorProvider.hasDependence(beanDefination)) {
            throw new UnsatisfiedBeanException("unsatisfied entity , there two entity ref each other !");
        } else {
            //设置依赖信息
            injectorData.setBean(ref);
        }
    }
}
