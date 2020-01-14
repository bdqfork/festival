package cn.bdqfork.model.bean.exception;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author fbw
 * @since 2020/1/13
 */
@Singleton
@Named
public class ResolveExceptionBean implements ErrorBean {

    @Inject
    public final InjectedBean injectedBean = null;

}
