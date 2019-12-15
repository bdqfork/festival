package test.cn.bdqfork.core;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author bdq
 * @since 2019/12/16
 */
@Named
public class UserService {
    @Inject
    private UserDao userDao;
}
