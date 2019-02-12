package cn.bdqfork.ioc.generator;

/**
 * @author bdq
 * @date 2019-02-07
 */
public class SimpleBeanNameGenerator implements BeanNameGenerator {
    @Override
    public String generateBeanName(Class<?> clazz) {
        return clazz.getSimpleName();
    }
}
