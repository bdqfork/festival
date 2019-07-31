package test.cn.bdqfork.ioc.cglib.scope.setter;

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
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
