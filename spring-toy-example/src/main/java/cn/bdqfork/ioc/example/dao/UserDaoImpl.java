package cn.bdqfork.ioc.example.dao;

import cn.bdqfork.core.annotation.AutoWired;
import cn.bdqfork.core.annotation.Repositorty;
import cn.bdqfork.core.annotation.Scope;
import cn.bdqfork.core.annotation.ScopeType;
import cn.bdqfork.ioc.example.entity.User;
import cn.bdqfork.ioc.example.service.UserService;

import java.util.Date;

/**
 * @author bdq
 * @date 2019-02-19
 */
@Repositorty
public class UserDaoImpl implements UserDao {
    private UserService userService;

    @AutoWired
    public UserDaoImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public User getUser() {
        return new User("test", "pass");
    }
    @Override
    public void getDate(){
        System.out.println(userService.getCreateTime().toString() + "yyyyyy");
    }
}
