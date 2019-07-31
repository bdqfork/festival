package test.cn.bdqfork.ioc.cglib.single.constructor;

import cn.bdqfork.core.context.AnnotationApplicationContext;
import cn.bdqfork.core.context.ApplicationContext;
import cn.bdqfork.core.exception.ApplicationContextException;
import org.junit.Test;

/**
 * @author bdq
 * @since 2019-07-31
 */
public class TestConstructorInject {

    @Test
    public void testJdkConstructorInject() throws ApplicationContextException {
        ApplicationContext applicationContext = new AnnotationApplicationContext("test.cn.bdqfork.ioc.cglib.single.constructor");
        UserService userService = applicationContext.getBean(UserService.class);
        System.out.println(userService);
    }
}
