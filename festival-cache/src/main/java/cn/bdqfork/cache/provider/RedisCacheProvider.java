package cn.bdqfork.cache.provider;

import java.io.Serializable;

/**
 * @author bdq
 * @since 2020/2/21
 */
public class RedisCacheProvider extends AbstractCacheProvider {
    @Override
    public void put(String key, Serializable value, long expireTime) {

    }

    @Override
    public void remove(String key) {

    }

    @Override
    public Object update(String key, Serializable value, long expireTime) {
        return null;
    }

    @Override
    public Object get(String key) {
        return null;
    }

    @Override
    public boolean containKey(String key) {
        return false;
    }

    @Override
    public void clear() {

    }
}
