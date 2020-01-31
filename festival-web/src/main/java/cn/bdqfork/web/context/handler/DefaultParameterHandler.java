package cn.bdqfork.web.context.handler;

import io.vertx.reactivex.ext.web.RoutingContext;

import java.lang.reflect.Parameter;

/**
 * @author bdq
 * @since 2020/1/31
 */
public class DefaultParameterHandler extends AbstractParameterHandler {

    @Override
    protected Object[] doHandle(RoutingContext routingContext, Parameter[] parameters) {
        return new Object[0];
    }
}
