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
public class JSR250SetterCycleServiceImpl implements JSR250SetterCycleService {
    private JSR250SetterCycleDao dao;

    @Resource
    public void setJSR250SetterCycleDao(JSR250SetterCycleDao dao) {
        this.dao = dao;
    }
}
