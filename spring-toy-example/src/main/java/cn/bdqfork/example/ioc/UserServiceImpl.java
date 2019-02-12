package cn.bdqfork.example.ioc;

import cn.bdqfork.ioc.annotation.Service;

/**
 * @author bdq
 * @date 2019-02-07
 */
@Service
public class UserServiceImpl implements UserService {
    @Override
    public String getUserName() {
        return "hello";
    }
}
