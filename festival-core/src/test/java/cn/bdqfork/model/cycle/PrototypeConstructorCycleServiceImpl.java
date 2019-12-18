package cn.bdqfork.model.cycle;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author bdq
 * @since 2019/12/17
 */
@Named
public class PrototypeConstructorCycleServiceImpl implements PrototypeConstructorCycleService {
    private PrototypeConstructorCycleDao dao;

    @Inject
    public PrototypeConstructorCycleServiceImpl(PrototypeConstructorCycleDao dao) {
        this.dao = dao;
    }
}
