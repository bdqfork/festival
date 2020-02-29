package cn.bdqfork.cache.provider;


import cn.bdqfork.context.configuration.reader.ResourceReader;

import java.io.Serializable;

/**
 * 缓存策略，所有接口均返回Flowable，调用者可通过Flowable坚挺状态。
 *
 * @author bdq
 * @since 2020/2/17
 */
public interface CacheProvider {

    /**
     * 添加缓存
     *
     * @param key
     * @param value
     */
    void put(String key, Serializable value);

    /**
     * 添加缓存
     *
     * @param key
     * @param value
     */
    void put(String key, Serializable value, long expireTime);

    /**
     * 删除缓存
     *
     * @param key
     */
    void remove(String key);

    /**
     * 更新缓存
     *
     * @param key
     * @param value
     * @return 旧的值
     */
    Object update(String key, Serializable value);

    /**
     * 更新缓存
     *
     * @param key
     * @param value
     * @return 旧的值
     */
    Object update(String key, Serializable value, long expireTime);

    /**
     * 获取缓存
     *
     * @param key
     * @return 缓存的值
     */
    Object get(String key);

    /**
     * key对应的缓存是否存在
     *
     * @param key
     * @return
     */
    boolean containKey(String key);

    /**
     * 清空所有缓存
     */
    void clear();

}
