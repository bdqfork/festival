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

    public void setJsr250FieldCycleDao(JSR250FieldCycleDao jsr250FieldCycleDao) {
        this.jsr250FieldCycleDao = jsr250FieldCycleDao;
    }

    @Override
    public JSR250FieldCycleDao getJsr250FieldCycleDao() {
        System.out.println("test");
        return jsr250FieldCycleDao;
    }

}
