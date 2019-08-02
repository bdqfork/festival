package test.cn.bdqfork.ioc.cglib.single.setter;

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
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
