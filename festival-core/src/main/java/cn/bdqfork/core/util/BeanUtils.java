package cn.bdqfork.core.util;

import cn.bdqfork.core.annotation.Order;
import cn.bdqfork.core.factory.processor.OrderAware;

import javax.inject.Provider;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author bdq
 * @since 2019-07-30
 */
public class BeanUtils {

    /**
     * 判断clazz是否为target类型或子类型，如果是，返回true，否则返回false
     *
     * @param type 待判断类型
     * @return boolean
     */
    public static boolean isProvider(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            return rawType == Provider.class;
        } else {
            return false;
        }
    }

    public static boolean isCollection(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            return rawType == List.class;
        } else {
            return false;
        }
    }

    public static boolean isMap(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            return rawType == Map.class;
        } else {
            return false;
        }
    }

    /**
     * 判断clazz是否为target类型或子类型，如果是，返回true，否则返回false
     *
     * @param clazz  待判断类型
     * @param target 目标类型
     * @return boolean
     */
    public static boolean isSubType(Class<?> clazz, Class<?> target) {
        return target.isAssignableFrom(clazz);
    }


    /**
     * 判断clazz是否为target类型或子类型，如果是，返回true，否则返回false
     *
     * @param clazz  待判断类型
     * @param target 目标类型
     * @return boolean
     */
    public static boolean checkIsInstance(Class<?> clazz, Class<?> target) {
        return isSubType(clazz, target) || isSubType(target, clazz);
    }

    /**
     * 对bean的集合进行排序，order越小越前，其他的统一放最后，用户实现的order的value必须大于零
     * @return 排序好的beanList
     */
    public static <T> List<T> sort(Collection<T> beans) {
        return beans
                .stream()
                .sorted(Comparator.comparing(BeanUtils::getBeanOrder))
                .collect(Collectors.toList());
    }

    private static <T> int getBeanOrder(T bean) {

        int order = Integer.MAX_VALUE;

        if (bean instanceof OrderAware) {
            OrderAware orderAware = (OrderAware) bean;
            if (orderAware.getOrder() >= 0) {
                return orderAware.getOrder();
            } else {
                throw new IllegalStateException("illegal order");
            }
        }

        Class<?> clazz = bean.getClass();

        clazz = AopUtils.getTargetClass(clazz);

        if (clazz.isAnnotationPresent(Order.class)) {
            if (clazz.getDeclaredAnnotation(Order.class).value() >= 0) {
                order = clazz.getDeclaredAnnotation(Order.class).value();
            } else {
                throw new IllegalStateException("illegal order");
            }
        }

        return order;
    }

}
