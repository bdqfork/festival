package cn.bdqfork.model.jsr250;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author bdq
 * @since 2019/12/17
 */
@Singleton
@Named
public class PostConstructFieldCycleDaoImpl implements PostConstructFieldCycleDao {

    @PostConstruct
    private void init() {
        System.out.println("init......");
    }

    @PreDestroy
    private void destroy() {
        System.out.println("destroy......");
    }
}
