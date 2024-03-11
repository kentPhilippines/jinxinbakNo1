package com.ruoyi.alipay.domain.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OrderItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private String column;
    private boolean asc = true;

    public OrderItem() {
    }

    public static OrderItem asc(String column) {
        return build(column, true);
    }

    public static OrderItem desc(String column) {
        return build(column, false);
    }

    public static List<OrderItem> ascs(String... columns) {
        return (List) Arrays.stream(columns).map(OrderItem::asc).collect(Collectors.toList());
    }

    public static List<OrderItem> descs(String... columns) {
        return (List)Arrays.stream(columns).map(OrderItem::desc).collect(Collectors.toList());
    }

    private static OrderItem build(String column, boolean asc) {
        return (new OrderItem()).setColumn(column).setAsc(asc);
    }

    public OrderItem setColumn(String column) {
        this.column = StringUtils.replaceAllBlank(column);
        return this;
    }

    public OrderItem setAsc(boolean asc) {
        this.asc = asc;
        return this;
    }

    public String getColumn() {
        return this.column;
    }

    public boolean isAsc() {
        return this.asc;
    }
}
