package test.cn.bdqfork.ioc.jdk.lazy.constructor;

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
    private UserDao userDao;

    @AutoWired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

}
