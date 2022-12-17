package com.ruoyi.web.controller.back;

import cn.hutool.core.util.ObjectUtil;
import com.google.api.client.util.Lists;
import com.google.common.base.Joiner;
import com.ruoyi.alipay.domain.AlipayDealOrderApp;
import com.ruoyi.alipay.domain.AlipayProductEntity;
import com.ruoyi.alipay.domain.AlipayUserFundEntity;
import com.ruoyi.alipay.domain.AlipayUserInfo;
import com.ruoyi.alipay.service.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.StatisticsEntity;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysUser;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 商户订单登记Controller
 *
 * @author kiwi
 * @date 2020-03-17
 */
@Controller
@RequestMapping("/alipay/orderApp")
public class AlipayDealOrderAppController extends BaseController {
    private String prefix = "alipay/orderApp";
    @Autowired
    private IAlipayDealOrderAppService alipayDealOrderAppService;
    @Autowired
    private IAlipayUserFundEntityService alipayUserFundEntityService;
    @Autowired
    private IAlipayProductService iAlipayProductService;

    @Autowired
    private IAlipayUserInfoService userInfoService;;

    @Autowired
    private IMerchantInfoEntityService merchantInfoEntityService;


    @GetMapping()
    public String orderApp() {
        return prefix + "/orderApp";
    }

    /**
     * 查询商户订单登记列表
     */
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(AlipayDealOrderApp alipayDealOrderApp) {
        startPage();
        List<AlipayDealOrderApp> list = alipayDealOrderAppService.selectAlipayDealOrderAppList(alipayDealOrderApp);
        return getDataTable(list);
    }

    @GetMapping("/orderAppAgent")
    public String orderAppAgent() {
        return prefix + "/orderAppAgent";
    }

    /**
     * 查询代理商分润
     */
    @PostMapping("/listAgent")
    @ResponseBody
    public TableDataInfo listAgent(AlipayDealOrderApp alipayDealOrderApp) {
        startPage();
        List<AlipayDealOrderApp> list = alipayDealOrderAppService.listAgent(alipayDealOrderApp);
        return getDataTable(list);
    }

    /**
     * 导出商户订单登记列表
     */
    @Log(title = "商户交易订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(AlipayDealOrderApp alipayDealOrderApp) {
        List<AlipayDealOrderApp> list = alipayDealOrderAppService.selectAlipayDealOrderAppList(alipayDealOrderApp);
        ExcelUtil<AlipayDealOrderApp> util = new ExcelUtil<AlipayDealOrderApp>(AlipayDealOrderApp.class);
        return util.exportExcel(list, "orderApp");
    }

    /**
     * 显示商户订单详情
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        AlipayDealOrderApp alipayDealOrderApp = alipayDealOrderAppService.selectAlipayDealOrderAppById(id);
        mmap.put("alipayDealOrderApp", alipayDealOrderApp);
        return prefix + "/edit";
    }

    /**
     * 转发财务
     */
    @Log(title = "商户交易订单", businessType = BusinessType.INSERT)
    @PostMapping("/updateOrder")
    @ResponseBody
    public AjaxResult updateOrder(String id) {
        AlipayDealOrderApp order = alipayDealOrderAppService.selectAlipayDealOrderAppById(Long.valueOf(id));
        order.setOrderStatus("7");//人工处理
        int i = alipayDealOrderAppService.updateAlipayDealOrderApp(order);
        return toAjax(i);
    }

    /**
     * 显示统计table
     */
    @GetMapping("/statistics/merchant/table")
    public String showTable(ModelMap mmap) {
        //查询产品类型下拉菜单
        AlipayProductEntity alipayProductEntity = new AlipayProductEntity();
        alipayProductEntity.setStatus(1);
        List<AlipayProductEntity> list = iAlipayProductService.selectAlipayProductList(alipayProductEntity);
        mmap.put("productList", list);
        return prefix + "/currentTable";
    }

    /**
     * 商户交易订单统计（仅当天数据）
     */
    @PostMapping("/statistics/merchant/orderApp")
    @ResponseBody
    public TableDataInfo dayStat(StatisticsEntity statisticsEntity) {
        // 获取当前的用户
        SysUser currentUser = ShiroUtils.getSysUser();
        statisticsEntity.setUserAgent(currentUser.getLoginName());
        //过滤出下级id
        List<String> subUserIds = merchantInfoEntityService.selectNextAgentByParentId(currentUser.getLoginName());
//        String userIdStr = subUserIds.get(0)==null ? "":subUserIds.get(0);
//        userIdStr = userIdStr.replace("$,","");
        if(CollectionUtils.isNotEmpty(subUserIds)) {

            String[] idArray = subUserIds.get(0).split(",");
            subUserIds = Arrays.asList(idArray);
        }else
        {
            subUserIds = Lists.newArrayList();
            subUserIds.add(currentUser.getLoginName());
        }
        startPage();

        //List subUserIds = userInfoService.selectAllUserInfoList(new AlipayUserInfo()).stream().filter(a->a.getUserId().equals(currentUser.getUserId())).map(AlipayUserInfo::getUserId).collect(Collectors.toList());
        List<StatisticsEntity> list = alipayDealOrderAppService.selectMerchantStatisticsDataByDay(statisticsEntity, DateUtils.dayStart(), DateUtils.dayEnd(),subUserIds);
        List<AlipayUserFundEntity> listFund = alipayUserFundEntityService.findUserFundAll();
        ConcurrentHashMap<String, AlipayUserFundEntity> userCollect = listFund.stream().collect(Collectors.toConcurrentMap(AlipayUserFundEntity::getUserId, Function.identity(), (o1, o2) -> o1, ConcurrentHashMap::new));
        BigDecimal amount = new BigDecimal("0");
        ConcurrentHashMap.KeySetView<String, AlipayUserFundEntity> strings = userCollect.keySet();
        for (String key : strings) {
            AlipayUserFundEntity alipayUserFundEntity = userCollect.get(key);
            if (alipayUserFundEntity.getUserType().equals("1")) {
                amount = amount.add(new BigDecimal(alipayUserFundEntity.getAccountBalance()));
            }
        }
        for (StatisticsEntity sta : list) {
            if (ObjectUtil.isNotNull(userCollect.get(sta.getUserId()))) {
                sta.setUserName(userCollect.get(sta.getUserId()).getUserName());
            }
            if (ObjectUtil.isNotNull(userCollect.get(sta.getUserId()))) {
                sta.setAccountAmount(userCollect.get(sta.getUserId()).getAccountBalance().toString());
            }
            if ("所有".equals(sta.getUserId())) {
                sta.setAccountAmount(amount.doubleValue() + "");
            }
            sta.setTodayAmount(sta.getSuccessAmount().subtract(new BigDecimal(sta.getSuccessFee())).toString());
        }
        return getDataTable(list);
    }


}
