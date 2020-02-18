package cn.bdqfork.web.cache;


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
    void put(String key, Serializable value);

    /**
     * 删除缓存
     *
     * @param key
     * @return
     */
    void remove(String key);

    /**
     * 更新缓存
     *
     * @param key
     * @param value
     * @return
     */
    void update(String key, Serializable value);

    /**
     * 获取缓存
     *
     * @param key
     * @return
     */
    Object get(String key);

    /**
     * 清楚所有缓存
     *
     * @return
     */
    void clear();

}
