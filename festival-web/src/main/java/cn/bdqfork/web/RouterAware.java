package cn.bdqfork.web;

import cn.bdqfork.core.exception.BeansException;
import io.vertx.reactivex.ext.web.Router;

/**
 * @author bdq
 * @since 2020/2/2
 */
public interface RouterAware {
    void setRouter(Router router) throws BeansException;
}
