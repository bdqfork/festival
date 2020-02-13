package cn.bdqfork.core.util;

/**
 * @author bdq
 * @since 2019/12/19
 */
public class StringUtils {

    public static boolean isEmpty(String s) {
        return s == null || "".equals(s);
    }

    public static String makeInitialLowercase(String s) {
        char[] chars = s.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    @SuppressWarnings("unchecked")
    public static <T> T castToPrimitive(String value, Class<T> type) {

        if (type == Integer.class || type == int.class) {
            return (T) Integer.valueOf(value);
        }

        if (type == Long.class || type == long.class) {
            return (T) Long.valueOf(value);
        }

        if (type == Double.class || type == double.class) {
            return (T) Double.valueOf(value);
        }

        if (type == Float.class || type == float.class) {
            return (T) Float.valueOf(value);
        }

        if (type == Short.class || type == short.class) {
            return (T) Short.valueOf(value);
        }

        if (type == Byte.class || type == byte.class) {
            return (T) Byte.valueOf(value);
        }

        if (type == Character.class || type == char.class) {
            return (T) Character.valueOf(value.charAt(0));
        }

        if (type == Boolean.class || type == boolean.class) {
            return (T) Boolean.valueOf(value);
        }

        throw new IllegalArgumentException(String.format("unsupport type %s!", type.getCanonicalName()));
    }
}
