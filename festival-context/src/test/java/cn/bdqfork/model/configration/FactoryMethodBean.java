package cn.bdqfork.model.configration;


import cn.bdqfork.value.Configration;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * 工厂类
 * @author fbw
 * @since 2020/1/23
 */

@Singleton
@Configration
@Named
public class FactoryMethodBean {

    @Named("nonono")
    FactoryBean getService(Server server) {
        FactoryBean factoryBean = new FactoryBean();
        factoryBean.setServer(server);
        return factoryBean;
    }

}
