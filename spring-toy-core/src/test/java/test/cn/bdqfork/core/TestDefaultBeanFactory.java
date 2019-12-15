package test.cn.bdqfork.core;

import cn.bdqfork.core.container.BeanDefinition;
import cn.bdqfork.core.container.DefaultBefactory;
import cn.bdqfork.core.exception.BeansException;
import org.junit.Test;

/**
 * @author bdq
 * @since 2019/12/16
 */
public class TestDefaultBeanFactory {

    @Test
    public void testGetBean() throws BeansException {
        DefaultBefactory defaultBefactory = new DefaultBefactory();
        BeanDefinition beanDefinition = new BeanDefinition("userDao", UserDao.class);
        defaultBefactory.registerBeanDefinition("userDao", beanDefinition);
        UserDao userDao = defaultBefactory.getBean("userDao");
        System.out.println(userDao);
    }
}
