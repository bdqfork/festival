package cn.bdqfork.kotlin.web.route

import io.vertx.core.Handler
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.RoutingContext

/**
 * @author bdq
 * @since 2020/1/27
 */
open class RouteAttribute {
    /**
     * url
     */
    open lateinit var url: String
    /**
     * http method
     */
    open lateinit var httpMethod: HttpMethod
    /**
     * produces
     */
    open var produces: String? = null
    /**
     * consumes
     */
    open var consumes: String? = null
    /**
     * timeout
     */
    open var timeout: Long = 0
    /**
     * route handler
     */
    open lateinit var contextHandler: Handler<RoutingContext>
    /**
     * 访问权限
     */
    open var permitAllowed: PermitHolder? = null
    /**
     * 访问角色
     */
    open var rolesAllowed: PermitHolder? = null
    /**
     * 是否需要登录
     */
    open var isAuth = false
    /**
     * 是否允许所有访问
     */
    open var isPermitAll = false

    open class Builder {
        private lateinit var url: String
        private lateinit var httpMethod: HttpMethod
        private var produces: String? = null
        private var consumes: String? = null
        private var timeout: Long = 0
        private lateinit var contextHandler: Handler<RoutingContext>
        private var permitAllowed: PermitHolder? = null
        private var rolesAllowed: PermitHolder? = null
        private var auth = false
        private var permitAll = false
        fun url(url: String): Builder {
            this.url = url
            return this
        }

        fun httpMethod(httpMethod: HttpMethod): Builder {
            this.httpMethod = httpMethod
            return this
        }

        fun produces(produces: String?): Builder {
            this.produces = produces
            return this
        }

        fun consumes(consumes: String?): Builder {
            this.consumes = consumes
            return this
        }

        fun timeout(timeout: Long): Builder {
            this.timeout = timeout
            return this
        }

        fun contextHandler(contextHandler: Handler<RoutingContext>): Builder {
            this.contextHandler = contextHandler
            return this
        }

        fun permitAllowed(permitAllowed: PermitHolder?): Builder {
            this.permitAllowed = permitAllowed
            return this
        }

        fun rolesAllowed(rolesAllowed: PermitHolder?): Builder {
            this.rolesAllowed = rolesAllowed
            return this
        }

        fun auth(auth: Boolean): Builder {
            this.auth = auth
            return this
        }

        fun permitAll(permitAll: Boolean): Builder {
            this.permitAll = permitAll
            return this
        }

        fun build(): RouteAttribute {
            val routeAttribute = RouteAttribute()
            routeAttribute.url = url
            routeAttribute.httpMethod = httpMethod
            routeAttribute.consumes = consumes
            routeAttribute.produces = produces
            routeAttribute.timeout = timeout
            routeAttribute.contextHandler = contextHandler
            routeAttribute.permitAllowed = permitAllowed
            routeAttribute.rolesAllowed = rolesAllowed
            routeAttribute.isAuth = auth
            routeAttribute.isPermitAll = permitAll
            return routeAttribute
        }
    }

    companion object {
        @JvmStatic
        fun builder(): Builder {
            return Builder()
        }
    }
}