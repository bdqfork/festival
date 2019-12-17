package cn.bdqfork.model.cycle;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author bdq
 * @since 2019/12/17
 */
@Named
public class ConstructorCycleDaoImpl implements ConstructorCycleDao {
    private ConstructorCycleService service;

    @Inject
    public ConstructorCycleDaoImpl(ConstructorCycleService service) {
        this.service = service;
    }
}
