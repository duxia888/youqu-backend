package com.dex.youqu.model.request;

// 本项目_所属 [程序员鱼皮](https://github.com/liyupi)

import lombok.Data;

import java.io.Serializable;

/**
 * 用户加入队伍请求体
 *
 * @author axin
 */
@Data
public class TeamJoinRequest implements Serializable {

    private static final long serialVersionUID = 852273950928111142L;

    /**
     * id
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;
}
