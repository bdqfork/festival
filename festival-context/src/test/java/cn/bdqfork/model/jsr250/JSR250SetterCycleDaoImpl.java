package cn.bdqfork.model.jsr250;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author bdq
 * @since 2019/12/17
 */
@Singleton
@Named
public class JSR250SetterCycleDaoImpl implements JSR250SetterCycleDao {
    private JSR250SetterCycleService service;

    @Resource(name = "jSR250SetterCycleServiceImpl")
    public void setService(JSR250SetterCycleService service) {
        this.service = service;
    }
}
