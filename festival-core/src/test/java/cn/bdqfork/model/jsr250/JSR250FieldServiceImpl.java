package cn.bdqfork.model.jsr250;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.inject.Singleton;

/**
 * @author bdq
 * @since 2019/12/19
 */
@Singleton
@ManagedBean
public class JSR250FieldServiceImpl implements JSR250FieldService {
    @Resource
    private JSR250FieldCycleDao jsr250FieldCycleDao;
}
