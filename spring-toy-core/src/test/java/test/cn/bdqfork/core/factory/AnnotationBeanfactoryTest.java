package test.cn.bdqfork.core.factory;

import cn.bdqfork.core.factory.support.AnnotationBeanfactory;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import test.cn.bdqfork.core.bean.UserService;

/**
 * AnnotationBeanfactory Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>12月 16, 2019</pre>
 */
public class AnnotationBeanfactoryTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: setBeanNameGenerator(BeanNameGenerator beanNameGenerator)
     */
    @Test
    public void testSetBeanNameGenerator() throws Exception {
    }


    /**
     * Method: scan()
     */
    @Test
    public void testScan() throws Exception {
        AnnotationBeanfactory annotationBeanfactory = new AnnotationBeanfactory("test.cn.bdqfork.core.bean");
        UserService userService = annotationBeanfactory.getBean(UserService.class);
        System.out.println(userService);
        System.out.println(userService.userDao);
    }

} 
