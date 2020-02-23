package cn.bdqfork.cache.util;


import io.vertx.core.Vertx;

/**
 * @author h-l-j
 * @since 2020/2/23
 */
public class VertxUtils {
    private static final Vertx vertx;

    static {
        vertx = Vertx.vertx();
    }

    public static Vertx getVertx() {
        return vertx;
    }
}
