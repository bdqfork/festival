package cn.bdqfork.example.ioc;

import cn.bdqfork.ioc.annotation.AutoWired;
import cn.bdqfork.ioc.annotation.Controller;

/**
 * @author bdq
 * @date 2019-02-13
 */
@Controller
public class UserController {
    @AutoWired
    private UserService userService;

    public String getUserName() {
        return userService.getUserName();
    }
}
