package cn.bdqfork.core.factory.registry;

import cn.bdqfork.core.exception.BeansException;

import javax.inject.Provider;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2019/12/16
 */
public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {
    private final Map<String, Object> singletons = new ConcurrentHashMap<>(256);
    private final Map<String, Object> earlySingletons = new HashMap<>(16);
    private final Map<String, Provider<?>> singletonProviders = new HashMap<>(16);
    private final Set<String> registerSingletons = Collections.newSetFromMap(new ConcurrentHashMap<>(16));
    private final Set<String> creatingSingletons = Collections.newSetFromMap(new ConcurrentHashMap<>(16));
    private final Set<String> destroyingSingletons = Collections.newSetFromMap(new ConcurrentHashMap<>(16));

    /**
     * 必须先初始化的依赖，这里的依赖不一定是类的属性，也有可能是一些其他的类，例如系统配置，JDBC配置等
     */
    private final Map<String, Set<String>> dependentBeanMap = new ConcurrentHashMap<>(16);

    @Override
    public void registerSingleton(String beanName, Object bean) {
        synchronized (singletons) {
            if (singletons.containsKey(beanName)) {
                throw new IllegalArgumentException(String.format("singleton %s is already exist !", beanName));
            }
            addSingleton(beanName, bean);
        }
    }

    protected void addSingleton(String beanName, Object bean) {
        synchronized (singletons) {
            singletons.put(beanName, bean);
            earlySingletons.remove(beanName);
            creatingSingletons.remove(beanName);
            registerSingletons.add(beanName);
        }
    }

    protected void registerCreatingSingleton(String beanName, Provider<?> provider) {
        synchronized (singletons) {
            if (singletonProviders.containsKey(beanName)) {
                throw new IllegalArgumentException(String.format("provider singleton %s is already under creating !", beanName));
            }
            singletonProviders.put(beanName, provider);
            creatingSingletons.add(beanName);
        }
    }

    protected void registerSingleton(String beanName, Provider<?> provider) {
        synchronized (singletons) {
            if (singletonProviders.containsKey(beanName)) {
                throw new IllegalArgumentException(String.format("provider singleton %s is already exist !", beanName));
            }
            registerSingletons.add(beanName);
            singletonProviders.put(beanName, provider);
            earlySingletons.remove(beanName);
        }
    }

    @Override
    public Object getSingleton(String beanName) {
        return getSingleton(beanName, true);
    }

    protected Object getSingleton(String beanName, boolean allowEarly) {
        synchronized (singletons) {
            Object singleton = singletons.get(beanName);
            if (singleton == null && allowEarly) {
                singleton = earlySingletons.get(beanName);
                if (singleton == null && underCreating(beanName)) {
                    Provider<?> provider = singletonProviders.get(beanName);
                    singleton = provider.get();
                    earlySingletons.put(beanName, singleton);
                }
            }
            return singleton;
        }
    }

    /**
     * 创建并注册实例
     *
     * @param beanName
     * @param provider
     * @return
     * @throws BeansException
     */
    public Object getSingleton(String beanName, Provider<?> provider) throws BeansException {
        synchronized (singletons) {
            if (singletons.containsKey(beanName)) {
                throw new BeansException("");
            }
            Object singleton = provider.get();
            addSingleton(beanName, singleton);
            return singleton;
        }
    }


    @Override
    public void destroySingleton(String beanName) {
        synchronized (singletons) {
            destroyingSingletons.add(beanName);
            registerSingletons.remove(beanName);
            singletons.remove(beanName);
            singletonProviders.remove(beanName);
            earlySingletons.remove(beanName);
            creatingSingletons.remove(beanName);
        }
    }

    public Set<String> getSingletonNames() {
        return singletons.keySet();
    }

    @Override
    public boolean containSingleton(String beanName) {
        return registerSingletons.contains(beanName);
    }

    public boolean underCreating(String beanName) {
        synchronized (singletons) {
            return creatingSingletons.contains(beanName);
        }
    }

    public boolean underCreatingOrDestroying(String beanName) {
        synchronized (singletons) {
            return creatingSingletons.contains(beanName) || destroyingSingletons.contains(beanName);
        }
    }

    public void registerDependentForBean(String beanName, String dependOn) {
        synchronized (singletons) {
            dependentBeanMap.computeIfAbsent(beanName, s -> new LinkedHashSet<>(16));
            Set<String> dependents = dependentBeanMap.get(beanName);
            dependents.add(dependOn);
        }
    }

    public boolean hasDependentForBean(String dependent) {
        return dependentBeanMap.containsKey(dependent);
    }

    protected boolean isDependent(String dependOn, String beanName) {
        if (dependOn.equals(beanName)) {
            return true;
        }
        Map<String, Boolean> trace = new HashMap<>();
        trace.put(beanName, true);
        trace.put(dependOn, true);
        return isDependent(dependOn, trace);
    }

    private boolean isDependent(String dependOn, Map<String, Boolean> trace) {
        if (hasDependentForBean(dependOn)) {
            for (String depend : dependentBeanMap.get(dependOn)) {
                if (trace.containsKey(depend)) {
                    return true;
                }
                if (isDependent(depend, trace)) {
                    return true;
                }
                trace.put(depend, true);
            }
        }
        return false;
    }

}
