package cn.bdqfork.model.cycle;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author bdq
 * @since 2019/12/17
 */
@Named
public class PrototypeConstructorCycleDaoImpl implements PrototypeConstructorCycleDao {
    private PrototypeConstructorCycleService service;

    @Inject
    public PrototypeConstructorCycleDaoImpl(PrototypeConstructorCycleService service) {
        this.service = service;
    }
}
