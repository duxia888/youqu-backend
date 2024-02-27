package com.dex.youqu.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户退出队伍请求体
 *
 * @author axin
 */
@Data
public class TeamQuitRequest implements Serializable {

    private static final long serialVersionUID = 6414394966749264810L;

    /**
     * id
     */
    private Long teamId;

}
