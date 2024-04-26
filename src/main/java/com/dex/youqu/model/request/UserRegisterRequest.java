package com.dex.youqu.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @Author axin
 * Date:2024/2/7 23:52
 * Version 1.0
 *Description:

 */

@Data
public class UserRegisterRequest implements Serializable {


    private static final long serialVersionUID = -1138611683867992021L;

    private String userAccount;
    private String username;
    private String userPassword;
    private String checkPassword;
}
