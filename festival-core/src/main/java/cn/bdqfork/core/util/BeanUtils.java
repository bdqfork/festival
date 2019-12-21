package cn.bdqfork.core.util;

import javax.inject.Provider;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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
}
