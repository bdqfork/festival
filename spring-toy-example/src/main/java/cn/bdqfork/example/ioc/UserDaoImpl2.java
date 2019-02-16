package cn.bdqfork.example.ioc;


import cn.bdqfork.core.annotation.AutoWired;
import cn.bdqfork.core.annotation.Repositorty;
import cn.bdqfork.core.container.BeanFactory;

/**
 * @author bdq
 * @date 2019-02-13
 */
@Repositorty
public class UserDaoImpl2 implements UserDao {
    @AutoWired
    private BeanFactory<UserDao> userDao;
    private BeanFactory<UserController> userController;

    @AutoWired
    public UserDaoImpl2(BeanFactory<UserService> userService) {
        System.out.println(userService);
    }

    @AutoWired
    public void setUserInfoDao(BeanFactory<UserController> userController) {
        this.userController = userController;
        System.out.println(userController.get());
    }

    @Override
    public User getUser() {
        return new User("hello");
    }

}
