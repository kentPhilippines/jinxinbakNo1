package com.ruoyi;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ruoyi.alipay.domain.*;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.common.utils.http.HttpUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class UserOrderUtil extends BaseUtil {
    public final static String PAGE_USER = "/userOrder/page";
    public final static String QUERY_ONE = "/userOrder/info";
    public final static String LIST = "/userOrder/list";

    public static UserOrder selectUserOrderById(String orderId) {
        AdminApiVo.UserOrderVo vo = new AdminApiVo.UserOrderVo();
        vo.setOrderId(orderId);
        String s = HttpUtils.sendPostJson(ZUUL + QUERY_ONE, JSONUtil.parse(vo).toString());
        ZullResult bean = JSONUtil.toBean(s, ZullResult.class);
        JSONObject jsonObject = JSONUtil.parseObj(bean.getData());
        UserOrder bean1 = JSONUtil.toBean(jsonObject, UserOrder.class);
        return bean1;
    }

    public static List<UserOrder> selectUserOrderList(UserOrder userOrder) {
        String s = HttpUtils.sendPostJson(ZUUL + LIST, JSONUtil.parse(userOrder).toString());
        System.out.println(s);
        ZullResult bean1 = JSONUtil.toBean(s, ZullResult.class);
        List<UserOrder> list = JSONUtil.toList(bean1.getData().toString(), UserOrder.class);
        return list;
    }

    public static int insertUserOrder(UserOrder userOrder) {
        return 0;
    }


    public static int deleteUserOrderByIds(String[] strArray) {
        return 0;
    }

    public static int deleteUserOrderById(Long id) {
        return 0;
    }

    public static ResPage<UserOrder> selectProductListPage(ReqPage<UserOrder> userOrder) {
        UserOrder data = userOrder.getData();
        userOrder.setSortField(data.getSortField());
        userOrder.setSortKey(data.getSortKey());
        userOrder.setCurrent(data.getCurrent());
        userOrder.setSize(data.getSize());
        userOrder.setParams(data.getParams());
        String s = HttpUtils.sendPostJson(ZUUL + PAGE_USER, JSONUtil.parse(userOrder).toString());
        System.out.println(s);
        RP<UserOrder> e = new RP<UserOrder>();
        return e.getReqPage(s, UserOrder.class);
    }

    public static List<UserOrder> selectUserOrderByIds(String[] strArray) {
        List<UserOrder>  orders = new ArrayList<>();
        for (String id : strArray) {
            UserOrder userOrder = selectUserOrderById(id);
            orders.add(userOrder);
        }
        return orders;
    }

}
