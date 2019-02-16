package cn.bdqfork.example.ioc;

import cn.bdqfork.example.ioc.UserController;
import cn.bdqfork.example.ioc.UserDao;
import cn.bdqfork.core.context.AnnotationApplicationContext;
import cn.bdqfork.core.exception.SpringToyException;

import java.util.Map;

/**
 * @author bdq
 * @date 2019-02-07
 */
public class IocDemo {
    public static void main(String[] args) throws SpringToyException {
        AnnotationApplicationContext ctx = new AnnotationApplicationContext("cn.bdqfork.example");
        Map<String, UserDao> userDaoMap = ctx.getBeans(UserDao.class);
        userDaoMap.forEach((k, v) -> System.out.println(k + "-" + v.getClass().getName() + "-" + v.getUser()));
        UserController userController = ctx.getBean(UserController.class);
        System.out.println(userController.getUserName());
    }
}
