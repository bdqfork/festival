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
public class UserDaoImpl implements UserDao {
    private UserService userService;

    @AutoWired
    public UserDaoImpl(UserService userService) {
        this.userService = userService;
    }
}
