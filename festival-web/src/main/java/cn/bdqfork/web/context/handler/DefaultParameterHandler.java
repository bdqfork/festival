package cn.bdqfork.web.context.handler;

import io.vertx.reactivex.core.MultiMap;

import java.lang.reflect.Parameter;

/**
 * @author bdq
 * @since 2020/1/31
 */
public class DefaultParameterHandler extends AbstractParameterHandler {

    @Override
    protected Object[] handleQueryParams(MultiMap multiMap, Parameter[] parameters) {
        return new Object[0];
    }

    @Override
    protected Object[] handleFormAttributes(MultiMap multiMap, Parameter[] parameters) {
        return new Object[0];
    }

}
