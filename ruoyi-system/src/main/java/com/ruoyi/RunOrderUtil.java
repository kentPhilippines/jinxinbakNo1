package com.ruoyi;

import cn.hutool.json.JSONUtil;
import com.ruoyi.alipay.domain.*;
import com.ruoyi.common.utils.http.HttpUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class RunOrderUtil extends BaseUtil {
    public final static String PAGE_USER = "/runOrder/page";
    public final static String LIST_USER = "/runOrder/list";

    public static RunOrder selectRunOrderById(Long id) {
        return null;
    }

    public static ResPage< RunOrder> selectRunOrderListPage(ReqPage<RunOrder> runOrder) {
        RunOrder data = runOrder.getData();
        runOrder.setSortField(data.getSortField());
        runOrder.setSortKey(data.getSortKey());
        runOrder.setCurrent(data.getCurrent());
        runOrder.setSize(data.getSize());
        runOrder.setParams(data.getParams());
        String s = HttpUtils.sendPostJson(ZUUL + PAGE_USER, JSONUtil.parse(runOrder).toString());
        System.out.println(s);
        RP<RunOrder> e = new RP<RunOrder>();
        return e.getReqPage(s, RunOrder.class);
    }

    public static int updateRunOrder(RunOrder runOrder) {
        return 0;
    }

    public static int deleteRunOrderByIds(String[] strArray) {
        return 0;
    }

    public static int deleteRunOrderById(Long id) {
        return 0;
    }

    public static List<RunOrder> selectRunOrderList(RunOrder runOrder) {
        return null;
    }

    public static List<RunOrder> selectRunOrderEntityList(RunOrder runOrder) {
        AdminApiVo.RunOrderVo data = new AdminApiVo.RunOrderVo();
        data.setOrderId(runOrder.getOrderId());
        data.setAccount(runOrder.getAccount());
        data.setParams(runOrder.getParams());
        data.setAmountType(runOrder.getAmountType());
        data.setOrderType(runOrder.getOrderType());
        String s = HttpUtils.sendPostJson(ZUUL + LIST_USER, JSONUtil.parse(runOrder).toString());
        System.out.println(s);
        ZullResult bean1 = JSONUtil.toBean(s, ZullResult.class);
        List<RunOrder> list = JSONUtil.toList(bean1.getData().toString(), RunOrder.class);
        return list;
    }
}
