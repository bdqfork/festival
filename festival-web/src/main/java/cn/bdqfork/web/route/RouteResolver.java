package cn.bdqfork.web.route;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.util.AnnotationUtils;
import cn.bdqfork.core.util.AopUtils;
import cn.bdqfork.web.annotation.*;
import cn.bdqfork.web.route.annotation.RouteMapping;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author bdq
 * @since 2020/2/10
 */
public class RouteResolver {

    public Map<RouteAttribute, RouteInvocation> resovle(List<Object> routeBeans) throws BeansException {
        Map<RouteAttribute, RouteInvocation> routeAttributeMap = new HashMap<>();

        for (Object routeBean : routeBeans) {

            Class<?> beanClass = AopUtils.getTargetClass(routeBean);
            String baseUrl = resolveBaseUrl(beanClass);

            for (Method method : beanClass.getDeclaredMethods()) {

                if (!AnnotationUtils.isAnnotationPresent(method, RouteMapping.class)) {
                    continue;
                }

                RouteMapping routeMapping = AnnotationUtils.getMergedAnnotation(method, RouteMapping.class);

                RouteAttribute attribute = RouteAttribute.builder()
                        .url(baseUrl + routeMapping.value())
                        .httpMethod(routeMapping.method())
                        .build();

                resolveAuthInfo(attribute, beanClass, method);

                routeAttributeMap.put(attribute, new RouteInvocation(routeBean, method));

            }
        }

        return routeAttributeMap;
    }

    private void resolveAuthInfo(RouteAttribute routeAttribute, Class<?> beanClass, Method routeMethod) {
        if (!checkIfAuth(beanClass, routeMethod)) {
            return;
        }
        routeAttribute.setAuth(true);

        boolean permitAll = AnnotationUtils.isAnnotationPresent(routeMethod, PermitAll.class);

        routeAttribute.setPermitAll(permitAll);

        if (permitAll) {
            return;
        }

        PermitAllowed permitAllowed = AnnotationUtils.getMergedAnnotation(routeMethod, PermitAllowed.class);
        if (permitAllowed != null) {
            routeAttribute.setPermitAllowed(new PermitHolder(permitAllowed));
        }

        RolesAllowed rolesAllowed = AnnotationUtils.getMergedAnnotation(routeMethod, RolesAllowed.class);
        if (rolesAllowed != null) {
            routeAttribute.setRolesAllowed(new PermitHolder(rolesAllowed));
        }

    }

    private boolean checkIfAuth(Class<?> beanClass, Method routeMethod) {
        return AnnotationUtils.isAnnotationPresent(beanClass, Auth.class) || AnnotationUtils.isAnnotationPresent(routeMethod, Auth.class);
    }

    private String resolveBaseUrl(Class<?> beanClass) {
        if (AnnotationUtils.isAnnotationPresent(beanClass, RouteMapping.class)) {
            return Objects.requireNonNull(AnnotationUtils.getMergedAnnotation(beanClass, RouteMapping.class))
                    .value();
        }
        return "";
    }
}
