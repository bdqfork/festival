package cn.bdqfork.core.factory;

import java.lang.reflect.Member;

/**
 * @author bdq
 * @since 2019/12/16
 */
public interface InjectedPoint {

    void setMember(Member member);

    Member getMember();

    String[] getInjectedNames();

    Class<?>[] getInjectedTypes();

}
