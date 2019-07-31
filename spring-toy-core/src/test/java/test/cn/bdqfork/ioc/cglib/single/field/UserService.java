package test.cn.bdqfork.ioc.cglib.single.field;

import cn.bdqfork.core.annotation.AutoWired;
import cn.bdqfork.core.annotation.Component;

/**
 * @author bdq
 * @since 2019-07-31
 */
@Component
public class UserService {
    @AutoWired
    private UserDao userDao;

    @Override
    public String toString() {
        return "UserServiceImpl{" +
                "userDao="  +
                '}';
    }
}
