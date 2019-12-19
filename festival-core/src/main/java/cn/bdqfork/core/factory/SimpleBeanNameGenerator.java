package cn.bdqfork.core.factory;

import cn.bdqfork.core.util.StringUtils;

/**
 * @author bdq
 * @since 2019-02-07
 */
public class SimpleBeanNameGenerator implements BeanNameGenerator {
    /**
     * 生成BeanName
     *
     * @param clazz 目标类型
     * @return String 简单类的名首字符小写形式
     */
    @Override
    public String generateBeanName(Class<?> clazz) {
        Class<?>[] interfaces = clazz.getInterfaces();
        String className;
        if (interfaces.length > 0) {
            className = interfaces[0].getSimpleName();
        } else {
            className = clazz.getSimpleName();
        }
        return StringUtils.lowerFirstChar(className);
    }
}
