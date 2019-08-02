package test.cn.bdqfork.ioc.jdk.scope.field;

import cn.bdqfork.core.annotation.AutoWired;
import cn.bdqfork.core.annotation.Component;
import cn.bdqfork.core.annotation.Scope;
import cn.bdqfork.core.annotation.ScopeType;

import java.util.Objects;

/**
 * @author bdq
 * @since 2019-07-31
 */
@Scope(ScopeType.PROTOTYPE)
@Component
public class UserDaoImpl implements UserDao {
    @AutoWired
    private UserService userService;

    @Override
    public String toString() {
        return "UserDaoImpl{" +
                "userService="  +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDaoImpl userDao = (UserDaoImpl) o;
        return Objects.equals(userService, userDao.userService);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userService);
    }
}
