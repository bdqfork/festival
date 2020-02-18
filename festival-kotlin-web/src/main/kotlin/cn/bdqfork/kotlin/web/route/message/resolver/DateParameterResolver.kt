package cn.bdqfork.kotlin.web.route.message.resolver

import cn.bdqfork.core.util.AnnotationUtils
import cn.bdqfork.kotlin.web.route.annotation.Param
import io.vertx.ext.web.RoutingContext
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import java.lang.reflect.Parameter
import java.util.*

/**
 * @author bdq
 * @since 2020/2/14
 */
class DateParameterResolver : AbstractParameterResolver() {
    companion object {
        private val log = LoggerFactory.getLogger(DateParameterResolver::class.java)
        private var enable = true

        init {
            try {
                Thread.currentThread().contextClassLoader.loadClass("org.joda.time.DateTime")
                if (log.isInfoEnabled) {
                    log.info("enable date parameter resolver")
                }
            } catch (e: ClassNotFoundException) {
                enable = false
            }
        }
    }

    override fun doResolve(parameter: Parameter, routingContext: RoutingContext): Any? {
        val param = AnnotationUtils.getMergedAnnotation(parameter, Param::class.java)
        if (param != null) {
            val multiMap = resolveParams(routingContext)
            val name: String = param.value
            val value = multiMap[name]
            val dateTime = DateTime(value)
            return dateTime.toDate()
        }
        return null
    }

    override fun resolvable(parameter: Parameter): Boolean {
        return enable && parameter.isAnnotationPresent(Param::class.java) && parameter.type == Date::class.java
    }
}