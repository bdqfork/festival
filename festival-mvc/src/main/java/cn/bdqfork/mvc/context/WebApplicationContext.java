package cn.bdqfork.mvc.context;

import cn.bdqfork.context.AnnotationApplicationContext;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.definition.BeanDefinition;
import cn.bdqfork.core.factory.definition.BeanDefinitionBuilder;
import cn.bdqfork.core.factory.registry.BeanDefinitionRegistry;
import cn.bdqfork.mvc.WebApplicationRunner;

/**
 * @author bdq
 * @since 2020/1/21
 */
public class WebApplicationContext extends AnnotationApplicationContext {
    public WebApplicationContext(String... scanPaths) throws BeansException {
        super(scanPaths);
    }

    @Override
    protected void registerBeanDefinition() throws BeansException {
        super.registerBeanDefinition();
        BeanDefinitionRegistry registry = getConfigurableBeanFactory();
        BeanDefinition beanDefinition = new BeanDefinitionBuilder().setScope(BeanDefinition.SINGLETON)
                .setBeanClass(WebApplicationRunner.class)
                .setBeanName("webserver")
                .build();
        registry.registerBeanDefinition(beanDefinition.getBeanName(), beanDefinition);
    }
}
