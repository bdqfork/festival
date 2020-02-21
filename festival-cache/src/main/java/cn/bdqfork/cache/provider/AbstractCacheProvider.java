package cn.bdqfork.cache.provider;

import cn.bdqfork.cache.constant.CacheProperty;

import java.io.Serializable;

/**
 * @author bdq
 * @since 2020/2/21
 */
public abstract class AbstractCacheProvider implements CacheProvider {
    @Override
    public void put(String key, Serializable value) {
        put(key, value, CacheProperty.DEFAULT_EXPIRE_TIME);
    }

    @Override
    public Object update(String key, Serializable value) {
        return update(key, value, CacheProperty.DEFAULT_EXPIRE_TIME);
    }

}
