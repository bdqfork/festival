package cn.bdqfork.kotlin.web.route

import cn.bdqfork.kotlin.web.annotation.PermitAllowed
import cn.bdqfork.kotlin.web.annotation.RolesAllowed
import cn.bdqfork.kotlin.web.constant.LogicType

/**
 * @author bdq
 * @since 2020/2/10
 */
class PermitHolder {
    var permits: Array<out String>
    var logicType: LogicType

    constructor(logicType: LogicType, vararg permits: String) {
        this.logicType = logicType
        this.permits = permits
    }

    constructor(permitAllowed: PermitAllowed) {
        permits = permitAllowed.value
        logicType = permitAllowed.logic
    }

    constructor(rolesAllowed: RolesAllowed) {
        permits = rolesAllowed.value
        logicType = rolesAllowed.logic
    }

}