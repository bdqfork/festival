package cn.bdqfork.web.route;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.NoSuchBeanException;
import cn.bdqfork.core.factory.ConfigurableBeanFactory;
import cn.bdqfork.core.factory.definition.BeanDefinition;
import cn.bdqfork.core.util.AnnotationUtils;
import cn.bdqfork.core.util.AopUtils;
import cn.bdqfork.web.annotation.Auth;
import cn.bdqfork.web.annotation.PermitAll;
import cn.bdqfork.web.annotation.PermitAllowed;
import cn.bdqfork.web.annotation.RolesAllowed;
import cn.bdqfork.web.route.annotation.Consumes;
import cn.bdqfork.web.route.annotation.Produces;
import cn.bdqfork.web.route.annotation.RouteController;
import cn.bdqfork.web.route.annotation.RouteMapping;
import cn.bdqfork.web.route.message.HttpMessageHandler;
import cn.bdqfork.web.route.response.ResponseHandlerFactory;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author bdq
 * @since 2020/2/10
 */
public class RouteResolver {
    private HttpMessageHandler httpMessageHandler;
    private ResponseHandlerFactory responseHandlerFactory;

    public RouteResolver(HttpMessageHandler httpMessageHandler, ResponseHandlerFactory responseHandlerFactory) {
        this.httpMessageHandler = httpMessageHandler;
        this.responseHandlerFactory = responseHandlerFactory;
    }

    public Collection<RouteAttribute> resovleRoutes(ConfigurableBeanFactory beanFactory) throws BeansException {

        List<RouteAttribute> routeAttributes = new LinkedList<>();

        for (Object routeBean : getRouteBeans(beanFactory)) {
            Class<?> beanClass = AopUtils.getTargetClass(routeBean);
            String baseUrl = resolveBaseUrl(beanClass);
            for (Method method : beanClass.getDeclaredMethods()) {
                if (!AnnotationUtils.isAnnotationPresent(method, RouteMapping.class)) {
                    continue;
                }
                RouteAttribute attribute = createRouteAttribute(baseUrl, routeBean, method);
                setProducesIfNeed(method, attribute);
                setConsumesIfNeed(method, attribute);
                setAuthInfoIfNeed(attribute, beanClass, method);
                routeAttributes.add(attribute);
            }
        }
        Collection<RouteAttribute> customRoutes = resolveCustomRoutes(beanFactory);
        routeAttributes.addAll(customRoutes);
        return routeAttributes;
    }

    private List<Object> getRouteBeans(ConfigurableBeanFactory beanFactory) throws BeansException {
        List<Object> beans = new LinkedList<>();
        for (BeanDefinition beanDefinition : beanFactory.getBeanDefinitions().values()) {
            if (AnnotationUtils.isAnnotationPresent(beanDefinition.getBeanClass(), RouteController.class)) {
                String beanName = beanDefinition.getBeanName();
                Object bean = beanFactory.getBean(beanName);
                beans.add(bean);
            }
        }
        return beans;
    }

    private String resolveBaseUrl(Class<?> beanClass) {
        if (AnnotationUtils.isAnnotationPresent(beanClass, RouteMapping.class)) {
            return Objects.requireNonNull(AnnotationUtils.getMergedAnnotation(beanClass, RouteMapping.class))
                    .value();
        }
        return "";
    }

    private RouteAttribute createRouteAttribute(String baseUrl, Object bean, Method method) {
        RouteMapping routeMapping = AnnotationUtils.getMergedAnnotation(method, RouteMapping.class);

        return RouteAttribute.builder()
                .url(baseUrl + routeMapping.value())
                .httpMethod(routeMapping.method())
                .timeout(routeMapping.timeout())
                .contextHandler(new RouteHandler(httpMessageHandler, responseHandlerFactory, method, bean))
                .build();
    }

    private void setProducesIfNeed(Method method, RouteAttribute attribute) {
        Produces produces = AnnotationUtils.getMergedAnnotation(method, Produces.class);
        if (produces != null) {
            for (String produce : produces.value()) {
                attribute.setProduces(produce);
            }
        }
    }

    private void setConsumesIfNeed(Method method, RouteAttribute attribute) {
        Consumes consumes = AnnotationUtils.getMergedAnnotation(method, Consumes.class);
        if (consumes != null) {
            for (String consume : consumes.value()) {
                attribute.setConsumes(consume);
            }
        }
    }

    private void setAuthInfoIfNeed(RouteAttribute routeAttribute, Class<?> beanClass, Method routeMethod) {
        if (!checkIfAuth(beanClass, routeMethod)) {
            return;
        }

        setAuthInfo(routeAttribute, routeMethod);

    }

    private boolean checkIfAuth(Class<?> beanClass, Method routeMethod) {
        return AnnotationUtils.isAnnotationPresent(beanClass, Auth.class) || AnnotationUtils.isAnnotationPresent(routeMethod, Auth.class);
    }

    private void setAuthInfo(RouteAttribute routeAttribute, Method routeMethod) {
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

    private Collection<RouteAttribute> resolveCustomRoutes(ConfigurableBeanFactory beanFactory) throws BeansException {
        try {
            return beanFactory.getBeans(RouteAttribute.class).values();
        } catch (NoSuchBeanException e) {
            return Collections.emptyList();
        }
    }

}
