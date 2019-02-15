package cn.bdqfork.example.ioc;


import cn.bdqfork.core.annotation.AutoWired;
import cn.bdqfork.core.annotation.Controller;

/**
 * @author bdq
 * @date 2019-02-13
 */
@Controller
public class UserController extends BaseController{
    @AutoWired
    private UserService userService;

    public String getUserName() {
        return userService.getUserName();
    }

}
