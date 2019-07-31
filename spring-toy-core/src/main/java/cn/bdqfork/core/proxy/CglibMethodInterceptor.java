package cn.bdqfork.core.proxy;

import cn.bdqfork.core.container.BeanFactoryImpl;
import cn.bdqfork.core.container.ObjectFactory;
import cn.bdqfork.core.container.UnSharedInstance;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.utils.BeanUtils;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * Cglib代理
 *
 * @author bdq
 * @since 2019-02-14
 */
public class CglibMethodInterceptor extends AdviceInvocationHandler implements MethodInterceptor {
    private Object target;

    /**
     * 创建代理实例
     *
     * @return Object 代理实例
     */
    public Object newProxyInstance() throws BeansException {
        Enhancer enhancer = new Enhancer();
        enhancer.setCallback(this);
        Class targetClass = target.getClass();
        Object[] args = null;
        if (targetClass == UnSharedInstance.class) {
            UnSharedInstance unSharedInstance = (UnSharedInstance) target;
            targetClass = unSharedInstance.getClazz();
            args = unSharedInstance.getArgs();
        }
        enhancer.setSuperclass(targetClass);
        if (args == null) {
            return enhancer.create();
        }
        Class[] argumentTypes = Arrays.stream(args).map(Object::getClass).toArray(Class[]::new);
        return enhancer.create(argumentTypes, args);
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Object targetObject = target;
        if (target.getClass() == UnSharedInstance.class) {
            UnSharedInstance unSharedInstance = (UnSharedInstance) target;
            targetObject = unSharedInstance.getObjectFactory().getObject();
        }
        if ("toString".equals(method.getName())) {
            return targetObject.toString();
        }
        if ("equals".equals(method.getName())) {
            return targetObject.equals(args[0]);
        }
        if ("hashCode".equals(method.getName())) {
            return targetObject.hashCode();
        }
        return super.invoke(targetObject, method, args);
    }

    public void setTarget(Object target) {
        this.target = target;
    }

}
