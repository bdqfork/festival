package test.cn.bdqfork.ioc.jdk.lazy.field;

import cn.bdqfork.core.annotation.AutoWired;
import cn.bdqfork.core.annotation.Component;
import cn.bdqfork.core.annotation.Lazy;

/**
 * @author bdq
 * @since 2019-07-31
 */
@Lazy
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
