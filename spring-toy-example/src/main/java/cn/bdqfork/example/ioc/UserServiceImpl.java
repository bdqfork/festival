package cn.bdqfork.example.ioc;


import cn.bdqfork.core.annotation.AutoWired;
import cn.bdqfork.core.annotation.Service;
import cn.bdqfork.core.container.BeanFactory;

/**
 * @author bdq
 * @date 2019-02-07
 */
@Service
public class UserServiceImpl implements UserService {
    private UserDao userDao;
    @AutoWired(required = false)
    private UserInfoDao userInfoDao;

    @AutoWired
    public void setUserDao(BeanFactory<UserDao> userDao) {
        this.userDao = userDao.get();
        System.out.println(userDao);
    }

    @Override
    public String getUserName() {
        return userDao.getUser().getName();
    }
}
