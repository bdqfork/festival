package cn.bdqfork.kotlin.example.domain;

import cn.bdqfork.cache.annotation.Cache;
import cn.bdqfork.cache.annotation.Evict;
import cn.bdqfork.web.annotation.VerticleMapping;
import io.reactivex.Flowable;

import javax.inject.Singleton;

/**
 * @author bdq
 * @since 2020/1/26
 */
@Singleton
@VerticleMapping("ServiceImpl1")
public class ServiceImpl implements IService {

    private String data = "initValue";

    @Override
    public Flowable<String> getUserName(String username) {
        return Flowable.just(username);
    }

    @Override
    public Flowable<Void> testError(String username) {
        return Flowable.just(username)
                .map(s -> 1 / 0).map(i -> null);
    }

    @Override
    @Cache("cn.bdqfork.kotlin.example.domain.ServiceImpl")
    public Flowable<String> testCache() {
        return Flowable.just(this.data);
    }

    @Override
    @Evict("cn.bdqfork.kotlin.example.domain.ServiceImpl")
    public Flowable<String> testCasheEvict(String data) {
        this.data = data;
        return Flowable.just("set successfully");
    }
}
