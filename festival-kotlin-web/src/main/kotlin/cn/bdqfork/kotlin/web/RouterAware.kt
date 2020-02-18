package cn.bdqfork.web

import cn.bdqfork.core.exception.BeansException
import io.vertx.ext.web.Router

/**
 * @author bdq
 * @since 2020/2/2
 */
interface RouterAware {
    @Throws(BeansException::class)
    fun setRouter(router: Router)
}