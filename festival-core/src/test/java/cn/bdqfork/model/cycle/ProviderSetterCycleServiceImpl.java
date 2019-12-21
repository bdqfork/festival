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
public class ProviderSetterCycleServiceImpl implements ProviderSetterCycleService {
    private Provider<ProviderCycleSetterDao> dao;

    @Inject
    public void setDao(Provider<ProviderCycleSetterDao> dao) {
        this.dao = dao;
    }
}
