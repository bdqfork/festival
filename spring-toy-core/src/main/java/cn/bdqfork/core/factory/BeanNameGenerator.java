package cn.bdqfork.core.factory;

/**
 * beanName生成器
 *
 * @author bdq
 * @since 2019-02-07
 */
public interface BeanNameGenerator {
    /**
     * 生成beanName
     *
     * @param clazz 目标类型
     * @return String beanName
     */
    String generateBeanName(Class<?> clazz);
}
