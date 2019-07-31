package test.cn.bdqfork.ioc.aop;

import cn.bdqfork.core.context.AnnotationApplicationContext;
import cn.bdqfork.core.context.ApplicationContext;
import cn.bdqfork.core.exception.ApplicationContextException;
import org.junit.Test;

/**
 * @author bdq
 * @since 2019-07-30
 */
public class TestProxyFactory {

    @Test
    public void testAspect() throws ApplicationContextException {
        ApplicationContext applicationContext = new AnnotationApplicationContext("test.cn.bdqfork.ioc.aop");
        UserDaoImpl userDao = applicationContext.getBean(UserDaoImpl.class);
        userDao.testAop();
        System.out.println("----------------------------------------");
        userDao.testThrowing();
    }
}
