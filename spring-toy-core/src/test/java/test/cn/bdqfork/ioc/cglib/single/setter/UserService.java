package test.cn.bdqfork.ioc.cglib.single.setter;

import cn.bdqfork.core.annotation.AutoWired;
import cn.bdqfork.core.annotation.Component;

/**
 * @author bdq
 * @since 2019-07-31
 */
@Component
public class UserService {
    private UserDao userDao;

    @AutoWired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}
