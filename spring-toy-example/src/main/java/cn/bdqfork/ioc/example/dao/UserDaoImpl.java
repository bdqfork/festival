package cn.bdqfork.ioc.example.dao;

import cn.bdqfork.core.annotation.AutoWired;
import cn.bdqfork.core.annotation.Repositorty;
import cn.bdqfork.ioc.example.entity.User;
import cn.bdqfork.ioc.example.service.UserService;

/**
 * @author bdq
 * @date 2019-02-19
 */
@Repositorty
public class UserDaoImpl implements UserDao {
    @AutoWired
    private UserService userService;

    @Override
    public User getUser() {
        System.out.println(userService.getCreateTime().toString() + "yyyyyy");
        return new User("test", "pass");
    }
}
