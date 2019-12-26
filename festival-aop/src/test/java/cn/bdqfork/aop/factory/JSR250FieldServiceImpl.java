package cn.bdqfork.aop.factory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author bdq
 * @since 2019/12/19
 */
@Singleton
@Named
public class JSR250FieldServiceImpl implements JSR250FieldService {
    @Inject
    private JSR250FieldCycleDao jsr250FieldCycleDao;
}
