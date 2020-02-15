package cn.bdqfork.web.route;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.reactivex.ext.web.RoutingContext;

/**
 * @author bdq
 * @since 2020/1/27
 */
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
     * produces
     */
    private String produces;

    /**
     * consumes
     */
    private String consumes;

    /**
     * timeout
     */
    private long timeout;

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

    private RouteInvocation routeInvocation;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getProduces() {
        return produces;
    }

    public void setProduces(String produces) {
        this.produces = produces;
    }

    public String getConsumes() {
        return consumes;
    }

    public void setConsumes(String consumes) {
        this.consumes = consumes;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public Handler<RoutingContext> getContextHandler() {
        return contextHandler;
    }

    public void setContextHandler(Handler<RoutingContext> contextHandler) {
        this.contextHandler = contextHandler;
    }

    public PermitHolder getPermitAllowed() {
        return permitAllowed;
    }

    public void setPermitAllowed(PermitHolder permitAllowed) {
        this.permitAllowed = permitAllowed;
    }

    public PermitHolder getRolesAllowed() {
        return rolesAllowed;
    }

    public void setRolesAllowed(PermitHolder rolesAllowed) {
        this.rolesAllowed = rolesAllowed;
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public boolean isPermitAll() {
        return permitAll;
    }

    public void setPermitAll(boolean permitAll) {
        this.permitAll = permitAll;
    }

    public RouteInvocation getRouteInvocation() {
        return routeInvocation;
    }

    public void setRouteInvocation(RouteInvocation routeInvocation) {
        this.routeInvocation = routeInvocation;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String url;
        private HttpMethod httpMethod;
        private String produces;
        private String consumes;
        private long timeout;
        private Handler<RoutingContext> contextHandler;
        private PermitHolder permitAllowed;
        private PermitHolder rolesAllowed;
        private boolean auth;
        private boolean permitAll;
        private RouteInvocation routeInvocation;

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder httpMethod(HttpMethod httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public Builder produces(String produces) {
            this.produces = produces;
            return this;
        }

        public Builder consumes(String consumes) {
            this.consumes = consumes;
            return this;
        }

        public Builder timeout(long timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder contextHandler(Handler<RoutingContext> contextHandler) {
            this.contextHandler = contextHandler;
            return this;
        }

        public Builder permitAllowed(PermitHolder permitAllowed) {
            this.permitAllowed = permitAllowed;
            return this;
        }

        public Builder rolesAllowed(PermitHolder rolesAllowed) {
            this.rolesAllowed = rolesAllowed;
            return this;
        }

        public Builder auth(boolean auth) {
            this.auth = auth;
            return this;
        }

        public Builder permitAll(boolean permitAll) {
            this.permitAll = permitAll;
            return this;
        }

        public Builder routeInvocation(RouteInvocation routeInvocation) {
            this.routeInvocation = routeInvocation;
            return this;
        }

        public RouteAttribute build() {
            RouteAttribute routeAttribute = new RouteAttribute();
            routeAttribute.setUrl(url);
            routeAttribute.setHttpMethod(httpMethod);
            routeAttribute.setConsumes(consumes);
            routeAttribute.setProduces(produces);
            routeAttribute.setTimeout(timeout);
            routeAttribute.setContextHandler(contextHandler);
            routeAttribute.setPermitAllowed(permitAllowed);
            routeAttribute.setRolesAllowed(rolesAllowed);
            routeAttribute.setAuth(auth);
            routeAttribute.setPermitAll(permitAll);
            routeAttribute.setRouteInvocation(routeInvocation);
            return routeAttribute;
        }
    }
}
