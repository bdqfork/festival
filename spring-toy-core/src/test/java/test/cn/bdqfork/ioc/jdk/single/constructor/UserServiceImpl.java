package test.cn.bdqfork.ioc.jdk.single.constructor;

import cn.bdqfork.core.annotation.AutoWired;
import cn.bdqfork.core.annotation.Component;

/**
 * @author bdq
 * @since 2019-07-31
 */
@Component
public class UserServiceImpl implements UserService {
    private UserDao userDao;

    @AutoWired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

}
