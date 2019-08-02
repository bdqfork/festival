package test.cn.bdqfork.ioc.cglib.single.constructor;

import cn.bdqfork.core.annotation.AutoWired;
import cn.bdqfork.core.annotation.Component;

/**
 * @author bdq
 * @since 2019-07-31
 */
@Component
public class UserDao {
    private UserService userService;

    @AutoWired
    public UserDao(UserService userService) {
        this.userService = userService;
    }
}
