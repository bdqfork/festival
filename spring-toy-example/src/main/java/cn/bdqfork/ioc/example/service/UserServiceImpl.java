package cn.bdqfork.ioc.example.service;

import cn.bdqfork.core.annotation.AutoWired;
import cn.bdqfork.core.annotation.Scope;
import cn.bdqfork.core.annotation.ScopeType;
import cn.bdqfork.core.annotation.Service;
import cn.bdqfork.ioc.example.controller.UserController;
import cn.bdqfork.ioc.example.dao.UserDao;

import javax.inject.Inject;
import java.util.Date;

/**
 * @author bdq
 * @date 2019-02-19
 */
@Scope(ScopeType.PROTOTYPE)
@Service
public class UserServiceImpl implements UserService {
    private UserDao userDao;
    private Date createTime = new Date();

    public UserServiceImpl() {
    }

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public String getUsername() {
        userDao.getDate();
        return userDao.getUser().getUsername();
    }

    public Date getCreateTime() {
        return createTime;
    }

    @AutoWired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}
