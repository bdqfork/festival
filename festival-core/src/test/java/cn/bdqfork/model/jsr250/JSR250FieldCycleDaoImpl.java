package cn.bdqfork.model.jsr250;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.inject.Singleton;

/**
 * @author bdq
 * @since 2019/12/17
 */
@Singleton
@ManagedBean
public class JSR250FieldCycleDaoImpl implements JSR250FieldCycleDao {

    @Resource(name = "jSR250FieldServiceImpl")
    private JSR250FieldService jsr250FieldService;

    @PostConstruct
    private void init() {
        System.out.println("init......");
    }

    @PreDestroy
    private void destroy() {
        System.out.println("destroy......");
    }
}
