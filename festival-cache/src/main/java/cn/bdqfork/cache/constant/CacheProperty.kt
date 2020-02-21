package cn.bdqfork.cache.constant

/**
 * @author bdq
 * @since 2020/2/17
 */
object CacheProperty {
    const val REDIS_CACHE_TYPE = "redis"

    const val DEFAULT_EXPIRE_TIME: Long = 60
    const val DEFAULT_CACHE_TYPE = REDIS_CACHE_TYPE


    const val CACHE_ENABLE = "cache.enable"
    const val CACHE_TYPE = "cache.type"
}