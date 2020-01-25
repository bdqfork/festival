package cn.bdqfork.aop.util;

import cn.bdqfork.aop.proxy.TargetClassAware;

/**
 * @author bdq
 * @since 2020/1/25
 */
public class AopUtils {
    public static Class<?> getTargetClass(Object candidate) {
        if (candidate instanceof TargetClassAware) {
            return ((TargetClassAware) candidate).getTargetClass();
        }
        return null;
    }
}
