package com.ruoyi.alipay.domain;

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
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
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
    @Excel(name = "渠道响应状态")
    private Long channelStatus;

    /**
     * 渠道id
     */
    @Excel(name = "渠道id")
    private String channelId;

    /**
     * 货币类型
     */
    @Excel(name = "货币类型")
    private String currency;

    /**
     * 代付产品
     */
    @Excel(name = "代付产品")
    private String witType;

    /**
     * 结算状态
     */
    @Excel(name = "结算状态")
    private Integer payStatus;

    /**
     * 推送状态
     */
    @Excel(name = "推送状态")
    private Integer pushOrder;

    /**
     * 锁定状态
     */
    @Excel(name = "锁定状态")
    private Integer witLock;

    /**
     * 等待时间
     */
    @Excel(name = "等待时间")
    private Integer watingTime;

    /**
     * 商户请求报文
     */
    @Excel(name = "商户请求报文")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String requestApp;

    /**
     * 响应商户报文
     */
    @Excel(name = "响应商户报文")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String responseApp;

    /**
     * 渠道请求报文
     */
    @Excel(name = "渠道请求报文")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String requestChannel;

    /**
     * 渠道响应报文
     */
    @Excel(name = "渠道响应报文")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String responseChannel;

    /**
     * 描述情况
     */
    @Excel(name = "描述情况")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String apply;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String notify;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String notifyLog;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer isNotify;
    /**
     * 成功时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "成功时间", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date submitTime;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderAppId(String orderAppId) {
        this.orderAppId = orderAppId;
    }

    public String getOrderAppId() {
        return orderAppId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setChannelStatus(Long channelStatus) {
        this.channelStatus = channelStatus;
    }

    public Long getChannelStatus() {
        return channelStatus;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }

    public void setWitType(String witType) {
        this.witType = witType;
    }

    public String getWitType() {
        return witType;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }

    public Integer getPayStatus() {
        return payStatus;
    }

    public void setPushOrder(Integer pushOrder) {
        this.pushOrder = pushOrder;
    }

    public Integer getPushOrder() {
        return pushOrder;
    }

    public void setWitLock(Integer witLock) {
        this.witLock = witLock;
    }

    public Integer getWitLock() {
        return witLock;
    }

    public void setWatingTime(Integer watingTime) {
        this.watingTime = watingTime;
    }

    public Integer getWatingTime() {
        return watingTime;
    }

    public void setRequestApp(String requestApp) {
        this.requestApp = requestApp;
    }

    public String getRequestApp() {
        if(StrUtil.isNotEmpty(requestApp)){
            return  StrUtil.trim(requestApp);
        }
        return requestApp;
    }

    public void setResponseApp(String responseApp) {
        this.responseApp = responseApp;
    }

    public String getResponseApp() {
        if(StrUtil.isNotEmpty(responseApp)){
            return  StrUtil.trim(responseApp);
        }
        return responseApp;
    }

    public void setRequestChannel(String requestChannel) {
        this.requestChannel = requestChannel;
    }

    public String getRequestChannel() {
        if(StrUtil.isNotEmpty(requestChannel)){
            return  StrUtil.trim(requestChannel);
        }
        return requestChannel;
    }

    public void setResponseChannel(String responseChannel) {
        this.responseChannel = responseChannel;
    }

    public String getResponseChannel() {
        if(StrUtil.isNotEmpty(responseChannel)){
            return  StrUtil.trim(responseChannel);
        }
        return responseChannel;
    }

    public void setApply(String apply) {
        this.apply = apply;
    }

    public String getApply() {
        if(StrUtil.isNotEmpty(apply)){
            return  StrUtil.trim(apply);
        }
        return apply;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("orderId", getOrderId())
                .append("orderAppId", getOrderAppId())
                .append("name", getName())
                .append("amount", getAmount())
                .append("fee", getFee())
                .append("info", getInfo())
                .append("orderStatus", getOrderStatus())
                .append("channelStatus", getChannelStatus())
                .append("channelId", getChannelId())
                .append("currency", getCurrency())
                .append("witType", getWitType())
                .append("payStatus", getPayStatus())
                .append("pushOrder", getPushOrder())
                .append("witLock", getWitLock())
                .append("watingTime", getWatingTime())
                .append("requestApp", getRequestApp())
                .append("responseApp", getResponseApp())
                .append("requestChannel", getRequestChannel())
                .append("responseChannel", getResponseChannel())
                .append("apply", getApply())
                .append("submitTime", getSubmitTime())
                .toString();
    }
}
