package cn.bdqfork.model.jsr250;

import cn.bdqfork.model.cycle.SetterCycleDao;
import cn.bdqfork.model.cycle.SetterCycleService;

import javax.annotation.Resource;
import javax.inject.Inject;
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

    @Resource(type = JSR250SetterCycleDao.class)
    public void setDao(JSR250SetterCycleDao dao) {
        this.dao = dao;
    }
}
