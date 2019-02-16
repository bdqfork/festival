package cn.bdqfork.example.aop;

/**
 * @author bdq
 * @date 2019-02-16
 */
public class UserServiceImpl implements UserService {
    @Override
    public void sayHello() {
        System.out.println("hello");
    }

    @Override
    public void compute() {
        System.out.println(1/0);
    }
}
