package cn.bdqfork.web;

import io.vertx.core.http.HttpMethod;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2020/1/27
 */
@Builder
@Getter
@Setter
public class RouteAttribute {
    /**
     * 路由bean
     */
    private Object bean;
    /**
     * base url
     */
    private String baseUrl;
    /**
     * mapping url
     */
    private String url;

    /**
     * http method
     */
    private HttpMethod httpMethod;

    /**
     * mapping方法
     */
    private Method routeMethod;

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
