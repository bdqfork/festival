package cn.bdqfork.model.jsr250;

import cn.bdqfork.core.factory.DisposableBean;
import cn.bdqfork.core.factory.InitializingBean;

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
public class JSR250FieldCycleDaoImpl implements JSR250FieldCycleDao, InitializingBean, DisposableBean {

    @Resource(name = "jSR250FieldServiceImpl")
    private JSR250FieldService jsr250FieldService;

    @PostConstruct
    private void init() {
        System.out.println("init......");
    }

    @PreDestroy
    private void preDestroy() {
        System.out.println("preDestroy......");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("afterPropertiesSet......");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("destroy......");
    }
}
