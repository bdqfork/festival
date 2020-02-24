package cn.bdqfork.kotlin.example.domain;

import cn.bdqfork.kotlin.web.annotation.VerticleMapping;

import javax.inject.Singleton;

/**
 * @author bdq
 * @since 2020/1/26
 */
@Singleton
@VerticleMapping("ServiceImpl1")
public class ServiceImpl implements IService {
    @Override
    public String getUserName(String username) {
        return username;
    }

    @Override
    public void testError(String username) {
        System.out.println( 1 / 0);
    }
}
