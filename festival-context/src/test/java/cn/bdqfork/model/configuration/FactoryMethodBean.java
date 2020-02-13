package cn.bdqfork.model.configuration;


import cn.bdqfork.context.configuration.Configuration;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * 工厂类
 *
 * @author fbw
 * @since 2020/1/23
 */

@Singleton
@Configuration
public class FactoryMethodBean {

    @Named
    private FactoryBean getService(Server server) {
        FactoryBean factoryBean = new FactoryBean();
        factoryBean.setServer(server);
        return factoryBean;
    }

}
