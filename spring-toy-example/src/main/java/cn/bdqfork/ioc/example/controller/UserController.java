package cn.bdqfork.ioc.example.controller;

import cn.bdqfork.core.annotation.AutoWired;
import cn.bdqfork.core.annotation.Component;
import cn.bdqfork.core.annotation.Scope;
import cn.bdqfork.core.annotation.ScopeType;
import cn.bdqfork.ioc.example.service.InfoService;
import cn.bdqfork.ioc.example.service.UserService;
import cn.bdqfork.ioc.example.service.UserServiceImpl;

import javax.inject.Inject;
import java.util.Date;


/**
 * @author bdq
 * @date 2019-02-19
 */
@Scope(ScopeType.PROTOTYPE)
@Component
public class UserController {
    @Inject
    private UserService userService;

    private InfoService infoService;


    public String getUsername() {
        return userService.getUsername();
    }

    public Date getCreateTime() {
        return userService.getCreateTime();
    }

    @AutoWired
    public void setInfoService(InfoService infoService) {
        this.infoService = infoService;
    }
}
