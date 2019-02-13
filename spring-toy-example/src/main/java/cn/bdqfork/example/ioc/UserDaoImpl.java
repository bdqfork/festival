package cn.bdqfork.example.ioc;

import cn.bdqfork.ioc.annotation.Repositorty;
import cn.bdqfork.ioc.annotation.Scope;

/**
 * @author bdq
 * @date 2019-02-13
 */
@Scope
@Repositorty
public class UserDaoImpl implements UserDao {
    @Override
    public User getUser() {
        return new User("test");
    }
}
