package cn.bdqfork.example.ioc;

import cn.bdqfork.ioc.annotation.AutoWired;
import cn.bdqfork.ioc.annotation.Service;

/**
 * @author bdq
 * @date 2019-02-07
 */
@Service
public class UserServiceImpl implements UserService {
    private UserDao userDao;
    @AutoWired
    private UserInfoDao userInfoDao;

    @AutoWired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public String getUserName() {
        return userDao.getUser().getName();
    }
}
