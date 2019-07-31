package cn.bdqfork.core.utils;

import cn.bdqfork.core.annotation.Component;
import cn.bdqfork.core.annotation.Controller;
import cn.bdqfork.core.annotation.Repositorty;
import cn.bdqfork.core.annotation.Service;

import javax.inject.Named;

/**
 * @author bdq
 * @since 2019-07-31
 */
public class ComponentUtils {
    /**
     * 判断candidate是否为组件
     *
     * @param candidate
     * @return
     */
    public static boolean isComponent(Class<?> candidate) {
        Component component = candidate.getAnnotation(Component.class);
        if (component != null) {
            return true;
        }
        Service service = candidate.getAnnotation(Service.class);
        if (service != null) {
            return true;
        }
        Repositorty repositorty = candidate.getAnnotation(Repositorty.class);
        if (repositorty != null) {
            return true;
        }
        Controller controller = candidate.getAnnotation(Controller.class);
        if (controller != null) {
            return true;
        }
        Named named = candidate.getAnnotation(Named.class);
        if (named != null) {
            return true;
        }
        return false;
    }

    /**
     * 获取组件名称
     *
     * @param candidate
     * @return
     */
    public static String getComponentName(Class<?> candidate) {
        Component component = candidate.getAnnotation(Component.class);
        if (component != null) {
            return component.value();
        }
        Service service = candidate.getAnnotation(Service.class);
        if (service != null) {
            return service.value();
        }
        Repositorty repositorty = candidate.getAnnotation(Repositorty.class);
        if (repositorty != null) {
            return repositorty.value();
        }
        Controller controller = candidate.getAnnotation(Controller.class);
        if (controller != null) {
            return controller.value();
        }
        Named named = candidate.getAnnotation(Named.class);
        if (named != null) {
            return named.value();
        }
        return null;
    }
}
