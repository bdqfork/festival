package cn.bdqfork.mvc.mapping;

import cn.bdqfork.core.util.AnnotationUtils;
import cn.bdqfork.security.annotation.Auth;
import cn.bdqfork.security.annotation.PermitAllowed;
import cn.bdqfork.security.annotation.RolesAllowed;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.AuthHandler;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2020/1/27
 */
public class MappingAttribute {

    private Router router;

    private Object bean;

    private String baseUrl;

    private Method routeMethod;

    private AuthHandler authHandler;

    public Router getRouter() {
        return router;
    }

    public void setRouter(Router router) {
        this.router = router;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Method getRouteMethod() {
        return routeMethod;
    }

    public void setRouteMethod(Method routeMethod) {
        this.routeMethod = routeMethod;
    }

    public AuthHandler getAuthHandler() {
        return authHandler;
    }

    public void setAuthHandler(AuthHandler authHandler) {
        this.authHandler = authHandler;
    }

    public boolean requireAuth() {
        if (authHandler == null) {
            return false;
        }
        Class<?> beanClass = routeMethod.getDeclaringClass();
        return AnnotationUtils.isAnnotationPresent(beanClass, Auth.class)
                || AnnotationUtils.isAnnotationPresent(routeMethod, Auth.class);
    }

    public PermitAllowed getPermits() {
        if (authHandler != null && routeMethod.isAnnotationPresent(PermitAllowed.class)) {
            return routeMethod.getAnnotation(PermitAllowed.class);
        }
        return null;
    }

    public RolesAllowed getRoles() {
        if (authHandler != null && routeMethod.isAnnotationPresent(RolesAllowed.class)) {
            return routeMethod.getAnnotation(RolesAllowed.class);
        }
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Router router;

        private Object bean;

        private String baseUrl;

        private Method routeMethod;

        private AuthHandler authHandler;

        public Builder setRouter(Router router) {
            this.router = router;
            return this;
        }

        public Builder setBean(Object bean) {
            this.bean = bean;
            return this;
        }

        public Builder setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder setRouteMethod(Method routeMethod) {
            this.routeMethod = routeMethod;
            return this;
        }

        public Builder setAuthHandler(AuthHandler authHandler) {
            this.authHandler = authHandler;
            return this;
        }

        public MappingAttribute build() {
            MappingAttribute attribute = new MappingAttribute();
            attribute.setRouter(router);
            attribute.setBean(bean);
            attribute.setBaseUrl(baseUrl);
            attribute.setRouteMethod(routeMethod);
            attribute.setAuthHandler(authHandler);
            return attribute;
        }
    }
}
