package cn.bdqfork.core.util;


import cn.bdqfork.core.proxy.FestivalProxy;
import cn.bdqfork.core.proxy.TargetClassAware;

/**
 * @author bdq
 * @since 2020/1/25
 */
public class AopUtils {
    /**
     * 获取代理类的真实类
     *
     * @param candidate
     * @return
     */
    public static Class<?> getTargetClass(Object candidate) {
        if (isProxy(candidate) && candidate instanceof TargetClassAware) {
            return ((TargetClassAware) candidate).getTargetClass();
        }
        return candidate.getClass();
    }

    /**
     * 是否是代理类
     *
     * @param candidate
     * @return
     */
    public static boolean isProxy(Object candidate) {
        return candidate instanceof FestivalProxy;
    }
}
