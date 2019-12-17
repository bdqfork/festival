package cn.bdqfork.model.cycle;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author bdq
 * @since 2019/12/17
 */
@Singleton
@Named
public class ConstructorCycleDaoImpl implements ConstructorCycleDao {
    private ConstructorCycleService service;

    @Inject
    public ConstructorCycleDaoImpl(ConstructorCycleService service) {
        this.service = service;
    }
}
