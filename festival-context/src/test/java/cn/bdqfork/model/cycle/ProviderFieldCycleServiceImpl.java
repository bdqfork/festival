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
public class ProviderFieldCycleServiceImpl implements ProviderFieldCycleService {
    @Inject
    private ProviderFieldCycleDao providerFieldCycleDao;
}
