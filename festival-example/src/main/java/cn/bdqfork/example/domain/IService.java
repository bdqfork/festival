package cn.bdqfork.example.domain;

import io.reactivex.Flowable;

/**
 * @author bdq
 * @since 2020/1/26
 */
public interface IService {
    Flowable<String> getUserName(String username);

    Flowable<Void> testError(String username);
}
