package cn.bdqfork.core.container;

/**
 * beanName生成器
 *
 * @author bdq
 * @date 2019-02-07
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
