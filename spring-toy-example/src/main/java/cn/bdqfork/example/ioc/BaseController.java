package cn.bdqfork.example.ioc;

import cn.bdqfork.core.annotation.AutoWired;
import cn.bdqfork.core.annotation.Controller;

/**
 * @author bdq
 * @date 2019-02-15
 */
@Controller
public class BaseController {
    @AutoWired
    private UserDao userDao;

    public UserDao getUserDao() {
        return userDao;
    }
}
