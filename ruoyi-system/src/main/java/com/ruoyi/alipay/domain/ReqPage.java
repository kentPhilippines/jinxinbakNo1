package com.ruoyi.alipay.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ruoyi.alipay.domain.util.OrderItem;
import com.ruoyi.alipay.domain.util.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Locale;
import java.util.Map;


/**
 * <p>
 * 分页-响应body
 * </p>
 *
 * @author andy
 * @since 2020/3/12
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class ReqPage<T> extends BasePage {
    private T data;
    private String sortKey;
    private String[] sortField;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params;
    @Override
    public <K> Page<K> getPage() {
        Page<K> page = super.getPage();
        // 默认使用ID倒序排序
        /*if (ArrayUtils.isEmpty(sortField)) {
            sortField = new String[]{"id"};
        }*/
        if (null == sortField || sortField.length == 0) {
            sortField = new String[]{"id"};
        }
        if(!"".equals(sortKey) && null != sortKey){
            if (ASC_SORT.equals(sortKey.toUpperCase(Locale.US))) {
                page.setOrders(OrderItem.ascs(sortField));
            }
            if (DESC_SORT.equals(sortKey.toUpperCase(Locale.US))) {
                page.setOrders(OrderItem.descs(sortField));
            }
        }
        return page;
    }

}

