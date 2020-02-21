package cn.bdqfork.web.route.message.resolver;

import cn.bdqfork.core.util.AnnotationUtils;
import cn.bdqfork.web.constant.ContentType;
import cn.bdqfork.web.route.annotation.RequestBody;
import cn.bdqfork.web.util.XmlUtils;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Parameter;

/**
 * @author bdq
 * @since 2020/2/20
 */
public class XmlBodyParameterResolver extends AbstractParameterResolver {
    @Override
    protected Object doResolve(Parameter parameter, RoutingContext routingContext) throws Exception {
        Class<?> parameterType = parameter.getType();
        if (parameterType == String.class) {
            return routingContext.getBodyAsString();
        }
        String xml = routingContext.getBodyAsString();
        return XmlUtils.fromXml(xml, parameterType);
    }

    @Override
    protected boolean resolvable(Parameter parameter, RoutingContext routingContext) {
        String contentType = routingContext.request().getHeader(ContentType.CONTENT_TYPE);
        return AnnotationUtils.isAnnotationPresent(parameter, RequestBody.class) && ContentType.XML.equals(contentType);
    }

}
