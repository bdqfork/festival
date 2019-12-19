package cn.bdqfork.core.util;

/**
 * @author bdq
 * @since 2019/12/19
 */
public class StringUtils {

    public static boolean isEmpty(String s) {
        return s == null || "".equals(s);
    }

    public static String lowerFirstChar(String s) {
        char[] chars = s.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
