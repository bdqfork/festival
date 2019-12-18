package cn.bdqfork.model.cycle;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author bdq
 * @since 2019/12/17
 */
@Named
public class PrototypeSetterCycleDaoImpl implements PrototypeSetterCycleDao{
    private PrototypeSetterCycleService service;

    @Inject
    public void setService(PrototypeSetterCycleService service) {
        this.service = service;
    }
}
