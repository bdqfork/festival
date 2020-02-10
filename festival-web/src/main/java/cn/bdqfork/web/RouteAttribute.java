package cn.bdqfork.web;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.reactivex.ext.web.RoutingContext;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author bdq
 * @since 2020/1/27
 */
@Builder
@Getter
@Setter
public class RouteAttribute {
    /**
     * url
     */
    private String url;

    /**
     * http method
     */
    private HttpMethod httpMethod;

    /**
     * route handler
     */
    private Handler<RoutingContext> contextHandler;
    /**
     * 访问权限
     */
    private PermitHolder permitAllowed;

    /**
     * 访问角色
     */
    private PermitHolder rolesAllowed;

    /**
     * 是否需要登录
     */
    private boolean auth;

    /**
     * 是否允许所有访问
     */
    private boolean permitAll;

}
