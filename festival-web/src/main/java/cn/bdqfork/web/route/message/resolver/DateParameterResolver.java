package cn.bdqfork.web.route.message.resolver;

import cn.bdqfork.core.util.AnnotationUtils;
import cn.bdqfork.web.route.annotation.Param;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Parameter;
import java.util.Date;

/**
 * @author bdq
 * @since 2020/2/14
 */
public class DateParameterResolver extends AbstractParameterResolver {
    private static final Logger log = LoggerFactory.getLogger(DateParameterResolver.class);
    private static boolean enable = true;

    static {
        try {
            Thread.currentThread().getContextClassLoader().loadClass("org.joda.time.DateTime");
            if (log.isInfoEnabled()) {
                log.info("enable date parameter resolver");
            }
        } catch (ClassNotFoundException e) {
            enable = false;
        }
    }

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
        return enable && parameter.isAnnotationPresent(Param.class) && parameter.getType() == Date.class;
    }
}
