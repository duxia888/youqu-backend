package com.dex.youqu.common;

import lombok.Getter;

/**
 * 错误码
 *
 * @Author axin
 * Date:2024/2/18 16:46
 * <p>
 * Version 1.0
 * Description
 */

@Getter
public enum ErrorCode {
    SUCCESS(0,"ok",""),
    PARAMS_ERROR(40000,"请求参数错误",""),
    NULL_ERROR(40001,"请求数据为空",""),
    NOT_LOGIN(40100,"未登录",""),
    NO_AUTH(40101,"没有权限",""),
    FORBIDDEN(40301,"禁止访问",""),
    SYSTEM_ERROR(50000,"系统内部异常", "");
    ;


    private final int code;
    /**
     * 状态码信息
     */
    private final String message;
    /**
     * 状态码描述(详情)
     */
    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

}
