package com.ruoyi.alipay.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ruoyi.alipay.domain.util.Page;
import lombok.Data;

/**
 * <p>
 * 分页
 * </p>
 *
 * @author
 */
@Data
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class BasePage {
    public static final String DESC_SORT = "DESC";
    public static final String ASC_SORT = "ASC";

    private long current  ;
    private long pageNum  ;
    private long size  ;
    private long pageSize  ;

    public <T> Page<T> getPage() {
        return new Page<>(current, size);
    }


    public void setPageSize(long pageSize) {
        this.size = pageSize;
    }

    public void setPageNum(long pageNum) {
        this.current = pageNum;
    }
}
