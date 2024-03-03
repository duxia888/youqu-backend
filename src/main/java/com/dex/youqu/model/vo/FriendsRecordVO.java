package com.dex.youqu.model.vo;

import com.dex.youqu.model.domain.User;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: axin
 * @Date: 2024年03月02日 08:19
 * @Version: 1.0
 * @Description:
 */
@Data
public class FriendsRecordVO implements Serializable {
    private static final long serialVersionUID = 1928465648232335L;

    private Long id;

    /**
     * 申请状态 默认0 （0-未通过 1-已同意 2-已过期）
     */
    private Integer status;

    /**
     * 好友申请备注信息
     */
    private String remark;

    /**
     * 申请用户
     */
    private User applyUser;
}
