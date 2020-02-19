package cn.bdqfork.web.server;

import cn.bdqfork.context.aware.BeanFactoryAware;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.BeanFactory;
import cn.bdqfork.core.factory.ConfigurableBeanFactory;
import cn.bdqfork.core.factory.definition.BeanDefinition;
import cn.bdqfork.core.util.AnnotationUtils;
import cn.bdqfork.core.util.ReflectUtils;
import cn.bdqfork.web.route.annotation.OnActive;
import cn.bdqfork.web.route.annotation.OnClose;
import cn.bdqfork.web.route.annotation.OnOpen;
import cn.bdqfork.web.route.annotation.ServerEndpoint;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author bdq
 * @since 2020/2/19
 */
public class WebSocketRouter implements BeanFactoryAware {
    private Map<String, WebSocketRoute> webSocketRouteMap = new ConcurrentHashMap<>();
    private BeanFactory beanFactory;

    public WebSocketRouter(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        refrash();
    }

    private void refrash() throws BeansException {
        ConfigurableBeanFactory beanFactory = (ConfigurableBeanFactory) this.beanFactory;
        List<BeanDefinition> beanDefinitions = beanFactory.getBeanDefinitions().values()
                .stream()
                .filter(beanDefinition -> AnnotationUtils.isAnnotationPresent(beanDefinition.getBeanClass(), ServerEndpoint.class))
                .collect(Collectors.toList());
        for (BeanDefinition beanDefinition : beanDefinitions) {
            Class<?> beanClass = beanDefinition.getBeanClass();

            Method open = getMethod(beanClass, OnOpen.class);
            Method active = getMethod(beanClass, OnActive.class);
            Method close = getMethod(beanClass, OnClose.class);
            Object bean = beanFactory.getBean(beanDefinition.getBeanName());

            WebSocketRoute webSocketRoute = new WebSocketRoute(bean, open, active, close);
            String path = AnnotationUtils.getMergedAnnotation(beanClass, ServerEndpoint.class).value();

            registerWebSocketRoute(path, webSocketRoute);
        }
    }

    private void registerWebSocketRoute(String path, WebSocketRoute webSocketRoute) {
        webSocketRouteMap.put(path, webSocketRoute);
    }

    public void accept(ServerWebSocket serverWebSocket) {
        String path = serverWebSocket.path();

        if (!webSocketRouteMap.containsKey(path)) {
            serverWebSocket.reject();
        }

        WebSocketRoute webSocketRoute = webSocketRouteMap.get(path);

        doOpen(serverWebSocket, webSocketRoute);

        serverWebSocket.frameHandler(frame -> doActive(webSocketRoute, frame));

        serverWebSocket.closeHandler((Void) -> doClose(webSocketRoute));
    }

    private void doOpen(ServerWebSocket serverWebSocket, WebSocketRoute webSocketRoute) {
        try {
            ReflectUtils.invokeMethod(webSocketRoute.bean, webSocketRoute.open, serverWebSocket);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e.getCause());
        }
    }

    private void doActive(WebSocketRoute webSocketRoute, WebSocketFrame frame) {
        try {
            ReflectUtils.invokeMethod(webSocketRoute.bean, webSocketRoute.active, frame);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e.getCause());
        }
    }

    private void doClose(WebSocketRoute webSocketRoute) {
        try {
            ReflectUtils.invokeMethod(webSocketRoute.bean, webSocketRoute.close);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e.getCause());
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    private Method getMethod(Class<?> beanClass, Class<? extends Annotation> annotation) {
        for (Method method : beanClass.getDeclaredMethods()) {
            if (AnnotationUtils.isAnnotationPresent(method, annotation)) {
                return method;
            }
        }
        return null;
    }

    private static class WebSocketRoute {
        Object bean;
        Method open;
        Method active;
        Method close;

        public WebSocketRoute(Object bean, Method open, Method active, Method close) {
            this.bean = bean;
            this.open = open;
            this.active = active;
            this.close = close;
        }
    }
}
