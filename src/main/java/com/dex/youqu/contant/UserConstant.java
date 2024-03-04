package com.dex.youqu.contant;

/**
 * @author: axin
 * @Date: 2024年03月18日 21:54
 * @Version: 1.0
 * @Description: 用户常量
 */
public interface UserConstant {
    /**
     * 用户登录态键值
     */
    String USER_LOGIN_STATE = "loginUserStatus";

    /**
     * 用户缓存键名称
     */
    String USER_REDIS_KEY = String.format("youqu:user:search:%s", "axin");

    /**
     * 标签缓存键名称
     */
    String TAGS_REDIS_KEY = String.format("youqu:tags:%s", "tags");

    int NODELETE = 0;

    /**
     * 默认权限
     */
    int DEFAULT_ROLE = 0;

    /**
     * 管理员权限
     */
    int ADMIN_ROLE = 1;

    /**
     * 未登录最大可以看多少条
     */
    int NOT_LONGIN_LOOK_MAX = 10;
}
