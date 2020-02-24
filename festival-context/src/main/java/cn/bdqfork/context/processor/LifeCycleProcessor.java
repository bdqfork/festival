package cn.bdqfork.context.processor;

import cn.bdqfork.context.ApplicationContext;
import cn.bdqfork.core.extension.SPI;

/**
 * @author bdq
 * @since 2020/2/22
 */
@SPI
public interface LifeCycleProcessor {

    void beforeStart(ApplicationContext applicationContext) throws Exception;

    void afterStart(ApplicationContext applicationContext) throws Exception;

    void beforeStop(ApplicationContext applicationContext) throws Exception;

}
