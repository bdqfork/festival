package cn.bdqfork.web.util;


import io.vertx.reactivex.core.Vertx;

/**
 * @author bdq
 * @since 2020/1/21
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
