package cn.bdqfork.ioc.example.dao;

import cn.bdqfork.core.annotation.Repositorty;
import cn.bdqfork.ioc.example.entity.User;

/**
 * @author bdq
 * @date 2019-02-19
 */
@Repositorty
public class UserDaoImpl implements UserDao {

    @Override
    public User getUser() {
        return new User("test", "pass");
    }
}
