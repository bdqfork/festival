package cn.bdqfork.model.proxy;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author bdq
 * @since 2019/12/31
 */
@Singleton
@Named
public class AopProxyTestBean {
    public String testAop() {
        System.out.println("processing");
        return "ok";
    }

    public String testThrowing() {
        System.out.println(1 / 0);
        return "ok";
    }
}
