package cn.bdqfork.kotlin.example.domain;

import cn.bdqfork.web.annotation.VerticleMapping;
import io.reactivex.Flowable;

import javax.inject.Singleton;

/**
 * @author bdq
 * @since 2020/1/26
 */
@Singleton
@VerticleMapping("ServiceImpl2")
public class ServiceImpl2 implements IService {
    @Override
    public Flowable<String> getUserName(String username) {
        return Flowable.just(username);
    }

    @Override
    public Flowable<Void> testError(String username) {
        return null;
    }
}
