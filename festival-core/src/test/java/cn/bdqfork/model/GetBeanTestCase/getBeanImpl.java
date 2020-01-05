package cn.bdqfork.model.GetBeanTestCase;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author fbw
 * @since 2020/1/5
 */

@Singleton
@Named("getBeanImpl")
public class getBeanImpl implements IgetBean {


    private IgetBeanDao igetBeanDao;


}
