package cn.bdqfork.core.generator;

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
     * @param clazz
     * @return
     */
    String generateBeanName(Class<?> clazz);
}
