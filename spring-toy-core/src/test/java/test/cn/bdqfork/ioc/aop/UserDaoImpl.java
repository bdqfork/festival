package test.cn.bdqfork.ioc.aop;

import cn.bdqfork.core.annotation.Repositorty;
import cn.bdqfork.core.annotation.Scope;
import cn.bdqfork.core.annotation.ScopeType;

/**
 * @author bdq
 * @date 2019-02-19
 */
@Scope(ScopeType.PROTOTYPE)
@Repositorty
public class UserDaoImpl {


    public void test() {
        System.out.println("processing");
    }

}
