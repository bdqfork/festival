package cn.bdqfork.web;

import cn.bdqfork.core.util.AnnotationUtils;
import cn.bdqfork.core.util.AopUtils;
import cn.bdqfork.web.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author bdq
 * @since 2020/1/29
 */
@Slf4j
public class RouteResolver {

    public List<RouteAttribute> resolve(List<?> beans) {
        List<RouteAttribute> attributes = new LinkedList<>();

        for (Object routeBean : beans) {

            Class<?> beanClass = AopUtils.getTargetClass(routeBean);
            String baseUrl = resolveBaseUrl(beanClass);

            for (Method method : beanClass.getDeclaredMethods()) {

                if (!AnnotationUtils.isAnnotationPresent(method, RouteMapping.class)) {
                    continue;
                }

                RouteMapping routeMapping = AnnotationUtils.getMergedAnnotation(method, RouteMapping.class);

                RouteAttribute attribute = RouteAttribute.builder()
                        .routeMethod(method)
                        .bean(routeBean)
                        .baseUrl(baseUrl)
                        .url(routeMapping.value())
                        .httpMethod(routeMapping.method())
                        .build();

                resolveAuthInfo(attribute, beanClass, method);

                attributes.add(attribute);
            }
        }

        return attributes;
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
