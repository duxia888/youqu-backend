package com.dex.youqu.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: axin
 * @Date: 2024年02月10日 14:21
 * @Version: 1.0
 * @Description:
 */
@Data
public class MessageRequest implements Serializable {
    private static final long serialVersionUID = 1324635911327892058L;
    private Long toId;
    private Long teamId;
    private String text;
    private Integer chatType;
    private boolean isAdmin;
}
