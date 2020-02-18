package cn.bdqfork.web.route;

import cn.bdqfork.web.annotation.PermitAllowed;
import cn.bdqfork.web.annotation.RolesAllowed;
import cn.bdqfork.web.constant.LogicType;

/**
 * @author bdq
 * @since 2020/2/10
 */
public class PermitHolder {
    private String[] permits;
    private LogicType logicType;

    public PermitHolder(LogicType logicType, String... permits) {
        this.logicType = logicType;
        this.permits = permits;
    }

    public PermitHolder(PermitAllowed permitAllowed) {
        permits = permitAllowed.value();
        logicType = permitAllowed.logic();
    }

    public PermitHolder(RolesAllowed rolesAllowed) {
        permits = rolesAllowed.value();
        logicType = rolesAllowed.logic();
    }

    public String[] getPermits() {
        return permits;
    }

    public LogicType getLogicType() {
        return logicType;
    }
}
