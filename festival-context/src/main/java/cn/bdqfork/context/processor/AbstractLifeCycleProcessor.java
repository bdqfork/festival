package cn.bdqfork.context.processor;

import cn.bdqfork.context.ApplicationContext;

/**
 * @author bdq
 * @since 2020/2/22
 */
public abstract class AbstractLifeCycleProcessor implements LifeCycleProcessor {

    @Override
    public void beforeStart(ApplicationContext applicationContext) throws Exception {

    }

    @Override
    public void afterStart(ApplicationContext applicationContext) throws Exception {

    }

    @Override
    public void beforeStop(ApplicationContext applicationContext) throws Exception {

    }
}
