package com.ruoyi.alipay.domain;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ruoyi.common.annotation.Excel;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单信息对象 user_order
 *
 * @author kkkk
 * @date 2024-03-06
 */
@Data
public class UserOrder extends ReqPage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    private Long id;

    /**
     * 订单号
     */
    @Excel(name = "订单号")
    private String orderId;

    /**
     * 商户上送订单号
     */
    @Excel(name = "商户上送订单号")
    private String orderAppId;

    /**
     * 商户id
     */
    @Excel(name = "商户id")
    private String name;

    /**
     * 订单金额
     */
    @Excel(name = "订单金额")
    private BigDecimal amount;

    /**
     * 结算费率
     */
    @Excel(name = "结算费率")
    private BigDecimal fee;

    /**
     * 取款信息
     */
    @Excel(name = "取款信息")
    private String info;

    /**
     * 订单状态
     */
    @Excel(name = "订单状态")
    private Integer orderStatus;

    /**
     * 渠道响应状态
     */

    /**
     * 货币类型
     */
    @Excel(name = "货币类型")
    private String currency;

    /**
     * 代付产品
     */

    /**
     * 结算状态
     */
    @Excel(name = "结算状态")
    private Integer payStatus;

    /**
     * 推送状态
     */
    /**
     * 描述情况
     */
    @Excel(name = "描述情况")
    private String apply;
    /**
     * 成功时间
     */
    @Excel(name = "成功时间", width = 30, dateFormat = DatePattern.NORM_DATETIME_PATTERN)
    private Date submitTime;
    @Excel(name = "创建时间", width = 30, dateFormat = DatePattern.NORM_DATETIME_PATTERN)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;





    public String getApply() {
        if(StrUtil.isNotEmpty(apply)){
            return  StrUtil.trim(apply);
        }
        return apply;
    }




}
