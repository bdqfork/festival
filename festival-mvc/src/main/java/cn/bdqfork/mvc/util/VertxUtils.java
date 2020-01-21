package cn.bdqfork.mvc.util;

import io.vertx.core.Vertx;

/**
 * @author bdq
 * @since 2020/1/21
 */
public class VertxUtils {
    private static Vertx vertx;

    static {
        vertx = Vertx.vertx();
    }

    public static Vertx getVertx() {
        return vertx;
    }
}
