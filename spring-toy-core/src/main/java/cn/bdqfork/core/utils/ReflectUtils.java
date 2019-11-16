package cn.bdqfork.core.utils;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019/10/1
 */
public class ReflectUtils {
    public static String getSignature(Method method) {
        StringBuilder signBuilder = new StringBuilder();
        signBuilder.append(method.getName())
                .append("(");
        Class<?>[] parameters = method.getParameterTypes();

        for (int i = 0; i < parameters.length; i++) {
            if (i > 0) {
                signBuilder.append(",");
            }
            signBuilder.append(parameters[i].getName());
        }
        signBuilder.append(")");
        return signBuilder.toString();
    }

}
