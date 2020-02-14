package cn.bdqfork.web.route.message.resolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author bdq
 * @since 2020/2/13
 */
public class ParameterResolverFactory {
    private List<AbstractParameterResolver> parameterResolvers = new ArrayList<>();

    public ParameterResolverFactory() {
        registerResolver(new ContextParameterResolver());
        registerResolver(new PrimitiveParameterResolver());
        registerResolver(new ObjectParameterResolver());
        registerResolver(new JsonBodyParameterResolver());
        registerResolver(new DateParameterResolver());
    }

    public void registerResolver(AbstractParameterResolver parameterResolver) {
        parameterResolvers.add(parameterResolver);
    }

    public void registerResolver(Collection<AbstractParameterResolver> parameterResolvers) {
        this.parameterResolvers.addAll(parameterResolvers);
    }

    public ParameterResolver createResolverChain() {
        if (parameterResolvers != null && parameterResolvers.size() > 0) {
            for (int i = 0; i < parameterResolvers.size() - 1; i++) {
                AbstractParameterResolver resolver = parameterResolvers.get(i);
                AbstractParameterResolver next = parameterResolvers.get(i + 1);
                resolver.setNext(next);
            }
            return parameterResolvers.get(0);
        }
        return null;
    }

}
