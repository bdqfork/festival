package cn.bdqfork.model.cycle;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author bdq
 * @since 2019/12/18
 */
@Singleton
@Named
public class ProviderFieldCycleDaoImpl implements ProviderFieldCycleDao {
    @Inject
    private ProviderFieldCycleService providerFieldCycleService;
}
