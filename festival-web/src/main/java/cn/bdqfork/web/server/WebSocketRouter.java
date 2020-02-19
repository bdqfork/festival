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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger log = LoggerFactory.getLogger(WebSocketRouter.class);
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
                .filter(this::checkIfServerEndpoint)
                .collect(Collectors.toList());

        for (BeanDefinition beanDefinition : beanDefinitions) {

            WebSocketRoute webSocketRoute = resolveWebSocketRoute(beanFactory, beanDefinition);

            String path = AnnotationUtils.getMergedAnnotation(beanDefinition.getBeanClass(), ServerEndpoint.class).value();

            registerWebSocketRoute(path, webSocketRoute);
        }
    }

    private boolean checkIfServerEndpoint(BeanDefinition beanDefinition) {
        return AnnotationUtils.isAnnotationPresent(beanDefinition.getBeanClass(), ServerEndpoint.class);
    }

    private WebSocketRoute resolveWebSocketRoute(ConfigurableBeanFactory beanFactory, BeanDefinition beanDefinition) throws BeansException {
        Class<?> beanClass = beanDefinition.getBeanClass();

        Method open = ReflectUtils.getMethodByAnnotation(beanClass, OnOpen.class);
        Method active = ReflectUtils.getMethodByAnnotation(beanClass, OnActive.class);
        Method close = ReflectUtils.getMethodByAnnotation(beanClass, OnClose.class);

        Object bean = beanFactory.getBean(beanDefinition.getBeanName());

        return new WebSocketRoute(bean, open, active, close);
    }

    private void registerWebSocketRoute(String path, WebSocketRoute webSocketRoute) {
        if (webSocketRouteMap.containsKey(path)) {
            throw new IllegalStateException(String.format("conflict websocket point %s!", path));
        }
        if (log.isInfoEnabled()) {
            log.info("register websocket point {}!", path);
        }
        webSocketRouteMap.put(path, webSocketRoute);
    }

    public void accept(ServerWebSocket serverWebSocket) {
        String path = serverWebSocket.path();

        if (!webSocketRouteMap.containsKey(path)) {
            serverWebSocket.reject();
            return;
        }

        WebSocketRoute webSocketRoute = webSocketRouteMap.get(path);

        webSocketRoute.doOpen(serverWebSocket);

        serverWebSocket.frameHandler(webSocketRoute::doActive);

        serverWebSocket.closeHandler((Void) -> webSocketRoute.doClose());
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
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

        public void doOpen(ServerWebSocket serverWebSocket) {
            if (open == null) {
                return;
            }
            try {
                ReflectUtils.invokeMethod(bean, open, serverWebSocket);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException(e.getCause());
            }
        }

        public void doActive(WebSocketFrame frame) {
            if (active == null) {
                return;
            }
            try {
                ReflectUtils.invokeMethod(bean, active, frame);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException(e.getCause());
            }
        }

        public void doClose() {
            if (close == null) {
                return;
            }
            try {
                ReflectUtils.invokeMethod(bean, close);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException(e.getCause());
            }
        }
    }
}
