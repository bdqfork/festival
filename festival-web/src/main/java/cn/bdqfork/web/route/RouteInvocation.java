package cn.bdqfork.web.route;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author bdq
 * @since 2020/2/10
 */
public class RouteInvocation {
    Object bean;
    Method method;

    public RouteInvocation(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RouteInvocation that = (RouteInvocation) o;
        return Objects.equals(bean, that.bean) &&
                Objects.equals(method, that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bean, method);
    }
}
