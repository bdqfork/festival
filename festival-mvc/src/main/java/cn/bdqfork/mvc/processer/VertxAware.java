package cn.bdqfork.mvc.processer;

import io.vertx.reactivex.core.Vertx;

/**
 * @author bdq
 * @since 2020/1/26
 */
public interface VertxAware {
    void setVertx(Vertx vertx);
}
