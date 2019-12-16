package test.cn.bdqfork.core.bean;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author bdq
 * @since 2019/12/16
 */
@Named
public class UserService {
    @Inject
    public UserDao userDao;
}
