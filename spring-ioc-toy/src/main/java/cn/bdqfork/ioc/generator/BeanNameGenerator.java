package cn.bdqfork.ioc.generator;

/**
 * @author bdq
 * @date 2019-02-07
 */
public interface BeanNameGenerator {
    String generateBeanName(Class<?> clazz);
}
