package cn.bdqfork.web.util;

/**
 * @author bdq
 * @since 2020/1/26
 */
public class EventBusUtils {
    public static String getAddress(Class<?> clazz) {
        return clazz.getCanonicalName();
    }
}
