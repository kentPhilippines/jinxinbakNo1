package com.ruoyi.alipay.domain;

import com.ruoyi.alipay.domain.util.Page;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 分页-响应body
 * </p>
 *
 * @author andy
 * @since 2020/3/12
 */
@Data
public class ResPage<T> {
    private long current = 1;
    private long size = 10;
    private List<T> list = new ArrayList<>();
    private long total = 0;
    private long pages = 0;
    private List<T> rows = new ArrayList<>();


    public void setList(List<T> list) {
        this.list = list;
        this.rows = list;
    }

    public static <K> ResPage<K> get(Page<K> page) {
        ResPage<K> p = new ResPage<>();
        if (null != page) {
            p.setPages(page.getPages());
            p.setList(page.getRecords());
            p.setTotal(page.getTotal());
            p.setCurrent(page.getCurrent());
            p.setSize(page.getSize());
        }
        return p;
    }
}
