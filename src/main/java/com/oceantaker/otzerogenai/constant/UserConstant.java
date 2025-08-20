package com.oceantaker.otzerogenai.constant;

/**
 * 用户常量
 *
 */

public interface UserConstant {

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "user_login";

    // 下面是idea专属语法，使用region 和 endregion包裹代码，idea会包裹成的代码块，方便查看
    //  region 权限

    /**
     * 默认角色
     */
    String DEFAULT_ROLE = "user";

    /**
     * 管理员角色
     */
    String ADMIN_ROLE = "admin";
    
    // endregion
}
