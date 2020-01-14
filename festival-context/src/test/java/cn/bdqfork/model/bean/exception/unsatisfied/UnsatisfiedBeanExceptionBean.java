package cn.bdqfork.model.bean.exception.unsatisfied;

import cn.bdqfork.model.bean.exception.InjectedBean;

import javax.annotation.Resource;
import javax.inject.Named;

/**
 * @author fbw
 * @since 2020/1/13
 */
@Named
public class UnsatisfiedBeanExceptionBean {


    private InjectedBean injectedBean;

    @Resource(name = "injectedBean")
    public void setInjectedBean(InjectedBean injectedBean) {
        this.injectedBean = injectedBean;
    }
}
