package cn.bdqfork.model.cycle;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author bdq
 * @since 2019/12/17
 */
@Named
public class PrototypeSetterCycleServiceImpl implements PrototypeSetterCycleService {
    private PrototypeSetterCycleDao dao;

    @Inject
    public void setDao(PrototypeSetterCycleDao dao) {
        this.dao = dao;
    }
}
