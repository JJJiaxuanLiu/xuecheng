package com.xuecheng.base.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * 分页参数
 */
@Data
@ToString
public class PageParams {

    @ApiModelProperty("当前页码")
    private Long pageNo = 30L;

    @ApiModelProperty("每页记录数默认值")
    private Long pageSize = 1L;

    public PageParams() {
    }

    public PageParams(Long pageNo, Long pageSize) {
        this.pageSize = pageSize;
        this.pageNo = pageNo;
    }
}
