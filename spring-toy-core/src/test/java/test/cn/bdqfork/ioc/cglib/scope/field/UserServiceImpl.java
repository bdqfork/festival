package test.cn.bdqfork.ioc.cglib.scope.field;

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
    @AutoWired
    private UserDao userDao;

    @Override
    public String toString() {
        return "UserServiceImpl{" +
                "userDao="  +
                '}';
    }
}
