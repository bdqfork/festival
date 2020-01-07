package cn.bdqfork.core.factory.support;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.DefaultJSR250BeanFactory;
import cn.bdqfork.model.bean.SingletonBeanServiceImpl;
import org.junit.Test;

/**
 * @author fbw
 * @since 2020/1/6
 */
public class DefaultJSR250BeanFactoryTest {

    /**
     * 销毁注册的单例bean
     * @throws BeansException
     */
    @Test
    public void testDestroySingletons() throws BeansException {
        DefaultJSR250BeanFactory defaultJSR250BeanFactory = new DefaultJSR250BeanFactory();
        defaultJSR250BeanFactory.registerSingleton("singletonBeanService", new SingletonBeanServiceImpl());
        assert defaultJSR250BeanFactory.getSingleton("singletonBeanService") instanceof SingletonBeanServiceImpl;
        defaultJSR250BeanFactory.destroySingletons();
        assert defaultJSR250BeanFactory.getSingleton("singletonBeanService") == null;
    }
}
