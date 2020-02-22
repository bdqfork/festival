package cn.bdqfork.kotlin.example.domain;

import cn.bdqfork.kotlin.web.annotation.VerticleMapping;

import javax.inject.Singleton;

/**
 * @author bdq
 * @since 2020/1/26
 */
@Singleton
@VerticleMapping("ServiceImpl2")
public class ServiceImpl2 implements IService {
    @Override
    public String getUserName(String username) {
        return username;
    }

    @Override
    public void testError(String username) {

    }
}
