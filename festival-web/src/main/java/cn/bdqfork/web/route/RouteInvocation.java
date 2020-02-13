package cn.bdqfork.web.route;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2020/2/10
 */
@EqualsAndHashCode
@ToString
public class RouteInvocation {
    Object bean;
    Method method;

    public RouteInvocation(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
    }

}
