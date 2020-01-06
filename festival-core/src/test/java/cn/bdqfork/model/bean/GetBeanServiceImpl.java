package cn.bdqfork.model.bean;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author fbw
 * @since 2020/1/5
 */

@Singleton
@Named("getBeanServiceImpl")
public class GetBeanServiceImpl implements GetBeanService {


    private GetBeanDao getBeanDao;


}
