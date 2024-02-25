package com.dex.youqu.common;

/**
 * 返回工具类
 *
 * @Author axin
 * Date:2024/2/18 16:28
 * <p>
 * Version 1.0
 * Description
 */

public class ResultUtils {

    /**
     * 成功
     *
     * @param data
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(0,data,"ok");
    }

    public static BaseResponse success(){
        return new BaseResponse<>(0,null,"ok");
    }

    /**
     * 失败
     *
     * @param code
     * @param message
     * @param description
     * @return
     */
    public static BaseResponse error(int code, String message, String description) {
        return new BaseResponse<>(code, null, message, description);
    }

    /**
     * 失败
     *
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode.getCode(),null,errorCode.getMessage(), errorCode.getDescription());
    }

    public static BaseResponse error(ErrorCode errorCode, String message, String description) {
        return new BaseResponse<>(errorCode.getCode(),null, message, description);
    }

    public static BaseResponse error(ErrorCode errorCode, String description) {
        return new BaseResponse<>(errorCode.getCode(), errorCode.getMessage(), description);
    }
}

