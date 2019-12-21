package cn.bdqfork.model.cycle;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

/**
 * @author bdq
 * @since 2019/12/17
 */
@Singleton
@Named
public class ProviderSetterCycleDaoImpl implements ProviderCycleSetterDao{
    private Provider<ProviderSetterCycleService> service;

    @Inject
    public void setService(Provider<ProviderSetterCycleService> service) {
        this.service = service;
    }
}
