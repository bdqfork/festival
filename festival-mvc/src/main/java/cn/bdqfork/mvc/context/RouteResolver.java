package cn.bdqfork.mvc.context;

import cn.bdqfork.core.util.AnnotationUtils;
import cn.bdqfork.core.util.AopUtils;
import cn.bdqfork.core.util.ReflectUtils;
import cn.bdqfork.mvc.context.annotation.RouteMapping;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.AuthHandler;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author bdq
 * @since 2020/1/29
 */
public class RouteResolver {
    private Router router;
    private AuthHandler authHandler;

    public RouteResolver(Router router, AuthHandler authHandler) {
        this.router = router;
        this.authHandler = authHandler;
    }

    public List<RouteAttribute> resolve(List<?> beans) throws NoSuchMethodException {
        List<RouteAttribute> attributes = new LinkedList<>();

        for (Object routeBean : beans) {

            Class<?> beanClass = AopUtils.getTargetClass(routeBean);

            String baseUrl = resolveBaseUrl(beanClass);

            for (Method declaredMethod : beanClass.getDeclaredMethods()) {

                if (!AnnotationUtils.isAnnotationPresent(declaredMethod, RouteMapping.class)) {
                    continue;
                }

                RouteAttribute attribute = buildRouteAttribute(routeBean, baseUrl, declaredMethod);

                attributes.add(attribute);
            }
        }
        return attributes;
    }

    private Method getRouteMethod(Object routeBean, Method declaredMethod) throws NoSuchMethodException {
        return ReflectUtils.getDeclaredMethod(routeBean,
                declaredMethod.getName(), declaredMethod.getParameterTypes());
    }

    private RouteAttribute buildRouteAttribute(Object routeBean, String baseUrl, Method routeMethod) {
        return RouteAttribute.builder()
                .setRouter(router)
                .setRouteMethod(routeMethod)
                .setBean(routeBean)
                .setBaseUrl(baseUrl)
                .setAuthHandler(authHandler)
                .build();
    }

    private String resolveBaseUrl(Class<?> beanClass) {
        if (AnnotationUtils.isAnnotationPresent(beanClass, RouteMapping.class)) {
            return Objects.requireNonNull(AnnotationUtils.getMergedAnnotation(beanClass, RouteMapping.class))
                    .value();
        }
        return "";
    }

}
