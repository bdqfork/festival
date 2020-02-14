package cn.bdqfork.web.route.message.resolver;

import cn.bdqfork.core.util.AnnotationUtils;
import cn.bdqfork.web.route.annotation.Param;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.joda.time.DateTime;

import java.lang.reflect.Parameter;
import java.util.Date;

/**
 * @author bdq
 * @since 2020/2/14
 */
public class DateParameterResolver extends AbstractParameterResolver {
    @Override
    protected Object doResolve(Parameter parameter, RoutingContext routingContext) {
        Param param = AnnotationUtils.getMergedAnnotation(parameter, Param.class);
        if (param != null) {
            MultiMap multiMap = resolveParams(routingContext);
            String name = param.value();
            String value = multiMap.get(name);
            DateTime dateTime = new DateTime(value);
            return dateTime.toDate();
        }
        return null;
    }

    @Override
    protected boolean resolvable(Parameter parameter) {
        return parameter.isAnnotationPresent(Param.class) && parameter.getType() == Date.class;
    }
}
