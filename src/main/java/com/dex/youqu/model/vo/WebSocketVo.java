package com.dex.youqu.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: axin
 * @Date: 2024年02月10日 13:37
 * @Version: 1.0
 * @Description:
 */
@Data
public class WebSocketVo implements Serializable {

    private static final long serialVersionUID = 5946461520536759455L;

    private long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

}
