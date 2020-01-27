package cn.bdqfork.example.domain;

import cn.bdqfork.mvc.annotation.Verticle;
import io.reactivex.Flowable;

import javax.inject.Singleton;

/**
 * @author bdq
 * @since 2020/1/26
 */
@Singleton
@Verticle("ServiceImpl1")
public class ServiceImpl implements IService {
    @Override
    public Flowable<String> getUserName(String username) {
        return Flowable.just(username);
    }

    @Override
    public Flowable<Void> testError(String username) {
        return Flowable.just(username)
                .map(s -> 1 / 0).map(i -> null);
    }
}