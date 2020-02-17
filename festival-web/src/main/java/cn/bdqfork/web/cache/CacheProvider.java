package cn.bdqfork.web.cache;

import io.reactivex.Flowable;

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
     * @return
     */
    Flowable<Void> put(String key, Serializable value);

    /**
     * 添加缓存
     *
     * @param key
     * @param value
     * @return
     */
    Flowable<Void> put(String key, Serializable value, long expireTime);

    /**
     * 删除缓存
     *
     * @param key
     * @return
     */
    Flowable<Void> remove(String key);

    /**
     * 更新缓存
     *
     * @param key
     * @param value
     * @return
     */
    Flowable<Void> update(String key, Serializable value);

    /**
     * 更新缓存
     *
     * @param key
     * @param value
     * @return
     */
    Flowable<Void> update(String key, Serializable value, long expireTime);

    /**
     * 获取缓存
     *
     * @param key
     * @return
     */
    Flowable<Serializable> get(String key);

    /**
     * 清楚所有缓存
     *
     * @return
     */
    Flowable<Void> clear();

}
