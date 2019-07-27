package cn.bdqfork.core.container;

/**
 * @author bdq
 * @date 2019-02-07
 */
public class SimpleBeanNameGenerator implements BeanNameGenerator {
    @Override
    public String generateBeanName(Class<?> clazz) {
        String className = clazz.getSimpleName();
        char[] chars = className.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
