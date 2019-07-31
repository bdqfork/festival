package test.cn.bdqfork.ioc.cglib.scope.constructor;

import cn.bdqfork.core.annotation.AutoWired;
import cn.bdqfork.core.annotation.Component;
import cn.bdqfork.core.annotation.Scope;
import cn.bdqfork.core.annotation.ScopeType;

/**
 * @author bdq
 * @since 2019-07-31
 */
@Scope(ScopeType.PROTOTYPE)
@Component
public class UserServiceImpl implements UserService {
    private UserDao userDao;

    @AutoWired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

}
