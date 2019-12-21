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
public class FieldCycleServiceImpl implements FieldCycleService {
    @Inject
    private FieldCycleDao fieldCycleDao;

}
