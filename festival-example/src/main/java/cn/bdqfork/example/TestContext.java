package cn.bdqfork.example;

import cn.bdqfork.context.AnnotationApplicationContext;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.example.bean.AopProxyTestBean;

/**
 * @author bdq
 * @since 2020/1/16
 */
public class TestContext {
    public static void main(String[] args) throws BeansException {
        AnnotationApplicationContext context = new AnnotationApplicationContext("cn.bdqfork.example");
        AopProxyTestBean bean = context.getBean(AopProxyTestBean.class);
        bean.testAop();
        bean.testThrowing();
    }
}
