package cn.bdqfork.aop.factory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author bdq
 * @since 2019/12/17
 */
@Singleton
@Named
public class JSR250FieldCycleDaoImpl implements JSR250FieldCycleDao {

    @Inject
    private JSR250FieldService jsr250FieldService;

}
