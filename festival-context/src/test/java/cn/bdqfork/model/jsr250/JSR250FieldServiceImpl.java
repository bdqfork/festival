package cn.bdqfork.model.jsr250;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author bdq
 * @since 2019/12/19
 */
@Singleton
@Named
public class JSR250FieldServiceImpl implements JSR250FieldService {
    @Resource(type = JSR250FieldCycleDao.class)
    private JSR250FieldCycleDao jsr250FieldCycleDao;
}
