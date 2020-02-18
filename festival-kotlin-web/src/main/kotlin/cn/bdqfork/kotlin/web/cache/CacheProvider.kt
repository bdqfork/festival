package cn.bdqfork.web.cache

import java.io.Serializable

/**
 * 缓存策略，所有接口均返回Flowable，调用者可通过Flowable坚挺状态。
 *
 * @author bdq
 * @since 2020/2/17
 */
interface CacheProvider {
    /**
     * 添加缓存
     *
     * @param key
     * @param value
     * @return
     */
    fun put(key: String, value: Serializable?)

    /**
     * 删除缓存
     *
     * @param key
     * @return
     */
    fun remove(key: String)

    /**
     * 更新缓存
     *
     * @param key
     * @param value
     * @return
     */
    fun update(key: String, value: Serializable?)

    /**
     * 获取缓存
     *
     * @param key
     * @return
     */
    operator fun get(key: String): Any?

    /**
     * 清楚所有缓存
     *
     * @return
     */
    fun clear()
}