package cn.bdqfork.model.cycle;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author bdq
 * @since 2019/12/17
 */
@Named
public class ConstructorCycleServiceImpl implements ConstructorCycleService {
    private ConstructorCycleDao constructorCycleDao;

    @Inject
    public ConstructorCycleServiceImpl(ConstructorCycleDao constructorCycleDao) {
        this.constructorCycleDao = constructorCycleDao;
    }
}
