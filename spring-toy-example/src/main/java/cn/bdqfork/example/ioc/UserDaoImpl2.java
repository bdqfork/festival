package cn.bdqfork.example.ioc;

import cn.bdqfork.ioc.annotation.Repositorty;

/**
 * @author bdq
 * @date 2019-02-13
 */
@Repositorty
public class UserDaoImpl2 implements UserDao {
    @Override
    public User getUser() {
        return new User("hello");
    }
}
