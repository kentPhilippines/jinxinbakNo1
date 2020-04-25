package com.ruoyi.alipay.service;

import com.ruoyi.alipay.domain.AlipayDealOrderApp;
import com.ruoyi.common.core.domain.StatisticsEntity;

import java.util.List;

/**
 * 商户订单登记Service接口
 *
 * @author kiwi
 * @date 2020-03-17
 */
public interface IAlipayDealOrderAppService {
    /**
     * 查询商户订单登记
     *
     * @param id 商户订单登记ID
     * @return 商户订单登记
     */
    AlipayDealOrderApp selectAlipayDealOrderAppById(Long id);

    /**
     * 查询商户订单登记列表
     *
     * @param alipayDealOrderApp 商户订单登记
     * @return 商户订单登记集合
     */
    List<AlipayDealOrderApp> selectAlipayDealOrderAppList(AlipayDealOrderApp alipayDealOrderApp);

    /**
     * 修改商户订单登记
     *
     * @param alipayDealOrderApp 商户订单登记
     * @return 结果
     */
    int updateAlipayDealOrderApp(AlipayDealOrderApp alipayDealOrderApp);


    List<AlipayDealOrderApp> selectSubMembersOrderList(AlipayDealOrderApp alipayDealOrderApp);

    /**
     * 查询当天商户订单交易统计数据
     * @param dayStart
     * @param dayEnd
     * @return
     */
    StatisticsEntity selectMerchantStatisticsDataByDay(String dayStart, String dayEnd);
}
