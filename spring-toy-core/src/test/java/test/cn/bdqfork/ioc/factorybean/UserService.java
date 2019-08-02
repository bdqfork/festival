package test.cn.bdqfork.ioc.factorybean;

import cn.bdqfork.core.annotation.Component;

/**
 * @author bdq
 * @since 2019-08-01
 */
@Component
public class UserService {

    public void test() {
        System.out.println("processing");
    }

    @Override
    public String toString() {
        return "UserDao{}";
    }
}
