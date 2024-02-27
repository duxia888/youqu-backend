package com.dex.youqu.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用删除请求
 *
 * @author axin
 *
 */
@Data
public class DeleteRequest implements Serializable {

    private static final long serialVersionUID = -455714261583907959L;

    private long id;
}
