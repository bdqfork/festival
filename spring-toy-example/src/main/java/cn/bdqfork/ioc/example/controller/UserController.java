package cn.bdqfork.ioc.example.controller;

import cn.bdqfork.core.annotation.Component;
import cn.bdqfork.ioc.example.service.UserService;

import javax.inject.Inject;


/**
 * @author bdq
 * @date 2019-02-19
 */
@Component
public class UserController {
    @Inject
    private UserService userService;

    public String getUsername() {
        return userService.getUsername();
    }
}
