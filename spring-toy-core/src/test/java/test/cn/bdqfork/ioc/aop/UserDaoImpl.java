package test.cn.bdqfork.ioc.aop;

import cn.bdqfork.core.annotation.Repositorty;
import cn.bdqfork.core.annotation.Scope;
import cn.bdqfork.core.annotation.ScopeType;

/**
 * @author bdq
 * @since 2019-02-19
 */
@Scope(ScopeType.PROTOTYPE)
@Repositorty
public class UserDaoImpl {


    public String testAop() {
        System.out.println("processing");
        return "ok";
    }

    public String testThrowing() {
        System.out.println(1 / 0);
        return "ok";
    }

}
