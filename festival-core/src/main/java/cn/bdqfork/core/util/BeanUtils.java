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
    public static boolean checkIfSubType(Class<?> clazz, Class<?> target) {
        return isSubType(clazz, target) || isSubType(target, clazz);
    }

    /**
     * 对bean的集合进行排序，order越小越前，其他的统一放最后，用户实现的order的value必须大于零
     *
     * @param <T>   实现了getOrder或者被@Order注解的类
     * @param beans 待排序的集合
     * @return 排序好的beanList
     */
    public static <T> List<T> sortByOrder(Collection<T> beans) {
        return beans.stream()
                .sorted(Comparator.comparing(BeanUtils::getOrder))
                .collect(Collectors.toList());
    }

    private static <T> int getOrder(T bean) {
        if (bean instanceof OrderAware) {
            OrderAware orderAware = (OrderAware) bean;
            int order = orderAware.getOrder();
            if (order >= 0) {
                return order;
            } else {
                throw new IllegalStateException(String.format("illegal order value %s, order value should >= 0!", order));
            }
        } else {

            Class<?> clazz = AopUtils.getTargetClass(bean);

            if (clazz.isAnnotationPresent(Order.class)) {
                int order = clazz.getAnnotation(Order.class).value();
                if (order >= 0) {
                    return order;
                } else {
                    throw new IllegalStateException(String.format("illegal order value %s, order value should >= 0!", order));
                }
            }
        }


        return Integer.MAX_VALUE;
    }

}
