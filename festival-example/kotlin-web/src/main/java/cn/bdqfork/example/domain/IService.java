package cn.bdqfork.example.domain;

/**
 * @author bdq
 * @since 2020/1/26
 */
public interface IService {
    String getUserName(String username);

    void testError(String username);
}
