package cn.bdqfork.example.ioc;


import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author bdq
 * @date 2019-02-13
 */
@Named
public class UserController extends BaseController {
    @Inject
    private UserService userService;

    public String getUserName() {
        return userService.getUserName();
    }

}
