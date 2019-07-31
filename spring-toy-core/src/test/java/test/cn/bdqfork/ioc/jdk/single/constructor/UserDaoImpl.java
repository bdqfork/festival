package test.cn.bdqfork.ioc.jdk.single.constructor;

import cn.bdqfork.core.annotation.AutoWired;
import cn.bdqfork.core.annotation.Component;

/**
 * @author bdq
 * @since 2019-07-31
 */
@Component
public class UserDaoImpl implements UserDao {
    private UserService userService;

    @AutoWired
    public UserDaoImpl(UserService userService) {
        this.userService = userService;
    }
}
