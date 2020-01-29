package cn.bdqfork.mvc.context.handler;


import cn.bdqfork.mvc.context.RouteAttribute;

/**
 * @author bdq
 * @since 2020/1/21
 */
public interface RouteMappingHandler {
    String ROUTE_ATTRIBETE_KEY = "routeAttribute";

    void handle(RouteAttribute routeAttribute);

}
