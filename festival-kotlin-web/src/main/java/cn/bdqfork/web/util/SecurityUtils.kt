package cn.bdqfork.web.util

import cn.bdqfork.web.constant.LogicType
import io.vertx.ext.auth.User
import io.vertx.kotlin.coroutines.awaitResult
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

/**
 * @author bdq
 * @since 2020/1/28
 */
object SecurityUtils {
    @JvmStatic
    fun isPermited(user: User, permits: Array<out String>, logicType: LogicType): Boolean = runBlocking {
        return@runBlocking GlobalScope.async {
            var finalResult = awaitResult<Boolean> { h -> user.isAuthorized(permits[0], h) }
            for (i in 1 until permits.size) {
                val result = awaitResult<Boolean> { h -> user.isAuthorized(permits[i], h) }
                if (logicType == LogicType.AND) {
                    finalResult = finalResult && result
                }
                if (logicType == LogicType.OR) {
                    finalResult = finalResult || result
                }
            }
            return@async finalResult
        }.await()
    }
}