package test.cn.bdqfork.ioc.jdk.single.field;

import cn.bdqfork.core.context.AnnotationApplicationContext;
import cn.bdqfork.core.context.ApplicationContext;
import cn.bdqfork.core.exception.ApplicationContextException;
import org.junit.Test;

/**
 * @author bdq
 * @since 2019-07-31
 */
public class TestFieldInject {

    @Test
    public void testJdkFieldInject() throws ApplicationContextException {
        ApplicationContext applicationContext = new AnnotationApplicationContext("test.cn.bdqfork.ioc.jdk.single.field");
        UserService userService = applicationContext.getBean(UserService.class);
        System.out.println(userService.hashCode());
    }
}
