package cn.bdqfork.core.factory.resolver;

import cn.bdqfork.core.exception.ResolvedException;
import cn.bdqfork.core.factory.BeanDefinition;

import java.util.Map;

/**
 * 解析器
 *
 * @author bdq
 * @since 2019-02-22
 */
public interface Resolver {
    /**
     * 解析
     *
     * @return Map<String, BeanDefinition>
     * @throws ResolvedException 解析异常
     */
    Map<String, BeanDefinition> resolve() throws ResolvedException;
}
