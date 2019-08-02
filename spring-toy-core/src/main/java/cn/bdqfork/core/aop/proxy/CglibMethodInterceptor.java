package cn.bdqfork.core.aop.proxy;

import cn.bdqfork.core.container.UnSharedInstance;
import cn.bdqfork.core.exception.BeansException;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Cglib代理
 *
 * @author bdq
 * @since 2019-02-14
 */
public class CglibMethodInterceptor extends AbstractProxyInvocationHandler implements MethodInterceptor {
    /**
     * 顾问处理
     */
    private AdvisorInvocationHandler advisorInvocationHandler;

    public CglibMethodInterceptor(AdvisorInvocationHandler advisorInvocationHandler) {
        this.advisorInvocationHandler = advisorInvocationHandler;
    }

    /**
     * 创建代理实例
     *
     * @return Object 代理实例
     */
    @Override
    public Object newProxyInstance() throws BeansException {
        Enhancer enhancer = new Enhancer();
        enhancer.setCallback(this);

        Class targetClass = target.getClass();

        Object[] args = null;
        //处理多实例类
        if (targetClass == UnSharedInstance.class) {

            UnSharedInstance unSharedInstance = (UnSharedInstance) target;
            targetClass = unSharedInstance.getClazz();
            args = unSharedInstance.getArgs();

        }

        enhancer.setSuperclass(targetClass);
        //执行无参构造方法
        if (args == null) {
            return enhancer.create();
        }
        //获取参数
        Class[] argumentTypes = Arrays.stream(args).map(Object::getClass).toArray(Class[]::new);

        return enhancer.create(argumentTypes, args);
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        //获取真实目标实例
        Object targetObject = getTargetObject();
        Object result = invokeObjectMethod(targetObject, method, args);
        if (result == null) {
            result = advisorInvocationHandler.invokeWithAdvice(targetObject, method, args);
        }
        return result;
    }

}
