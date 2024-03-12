package com.ruoyi.alipay.domain;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * 流水信息对象 run_order
 *
 * @author kkkkk
 * @date 2024-03-07
 */
@Data
public class RunOrder extends ReqPage implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    private Long id;

    /**
     *
     */
    @Excel(name = "订单号")
    private String orderId;
    @Excel(name = "账户")
    private String account;

    /**
     *
     */
    @Excel(name = "金额")
    private BigDecimal amount;

    /**
     *
     */
    @Excel(name = "操作类型", readConverterExp = "1=收入,0=支出")
    private Integer orderType;

    /**
     *
     */
    @Excel(name = "流水类型", readConverterExp = "10=代付余额扣减,11=代付手续费扣减,13=手动加款,14=手动减款,7=人工处理")
    private Integer amountType;

    /**
     *
     */
    @Excel(name = "余额")
    private BigDecimal balance;
    @Excel(name = "数据修改时间", width = 30, dateFormat = DatePattern.NORM_DATETIME_PATTERN)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


}
