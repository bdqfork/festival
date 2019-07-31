package cn.bdqfork.ioc.example.service;

import cn.bdqfork.core.annotation.AutoWired;
import cn.bdqfork.core.annotation.Component;
import cn.bdqfork.core.annotation.Scope;
import cn.bdqfork.core.annotation.ScopeType;
import cn.bdqfork.ioc.example.controller.UserController;

/**
 * @author bdq
 * @since 2019-07-31
 */
@Scope(ScopeType.PROTOTYPE)
@Component
public class InfoService {
    private UserController userController;

    @AutoWired
    public void setUserController(UserController userController) {
        this.userController = userController;
    }
}
