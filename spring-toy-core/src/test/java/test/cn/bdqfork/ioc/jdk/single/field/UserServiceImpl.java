package test.cn.bdqfork.ioc.jdk.single.field;

import cn.bdqfork.core.annotation.AutoWired;
import cn.bdqfork.core.annotation.Component;

/**
 * @author bdq
 * @since 2019-07-31
 */
@Component
public class UserServiceImpl implements UserService {
    @AutoWired
    private UserDao userDao;

    @Override
    public String toString() {
        return "UserServiceImpl{" +
                "userDao="  +
                '}';
    }
}
