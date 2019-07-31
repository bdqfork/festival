package test.cn.bdqfork.ioc.jdk.single.setter;

import cn.bdqfork.core.context.AnnotationApplicationContext;
import cn.bdqfork.core.context.ApplicationContext;
import cn.bdqfork.core.exception.ApplicationContextException;
import org.junit.Test;

/**
 * @author bdq
 * @since 2019-07-31
 */
public class TestSetterInject {

    @Test
    public void testJdkSetterInject() throws ApplicationContextException {
        ApplicationContext applicationContext = new AnnotationApplicationContext("test.cn.bdqfork.ioc.cglib.single.setter");
        UserService userService = applicationContext.getBean(UserService.class);
    }
}
