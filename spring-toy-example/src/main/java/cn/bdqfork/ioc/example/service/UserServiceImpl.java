package cn.bdqfork.ioc.example.service;

import cn.bdqfork.core.annotation.AutoWired;
import cn.bdqfork.core.annotation.Service;
import cn.bdqfork.ioc.example.dao.UserDao;

/**
 * @author bdq
 * @date 2019-02-19
 */
@Service
public class UserServiceImpl implements UserService {
    @AutoWired
    private UserDao userDao;

    @Override
    public String getUsername() {
        return userDao.getUser().getUsername();
    }
}
