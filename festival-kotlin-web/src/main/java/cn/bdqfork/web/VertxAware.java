package cn.bdqfork.web;

import cn.bdqfork.core.exception.BeansException;
import io.vertx.core.Vertx;

/**
 * @author bdq
 * @since 2020/1/26
 */
public interface VertxAware {
    void setVertx(Vertx vertx) throws BeansException;
}
