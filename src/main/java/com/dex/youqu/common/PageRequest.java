package com.dex.youqu.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用分页请求参数
 *
 * @Author axin
 * Date:2024/2/27 11:32
 * <p>
 * Version 1.0
 * Description:
 */

@Data
public class PageRequest implements Serializable {


    private static final long serialVersionUID = -662498331214118503L;
    /**
     * 页面大小
     */
    protected int pageSize;

    /**
     * 当前是第几页
     */
    protected int pageNum;
}
