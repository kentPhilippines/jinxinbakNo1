package com.ruoyi;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ruoyi.alipay.domain.ResPage;
import com.ruoyi.alipay.domain.ZullResult;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
@Slf4j
public class BaseUtil {
 //   public final static String ZUUL = "http://127.0.0.1:8080/v1/admin";
  //  public final static String ZUUL = "http://alipay:9010/v1/admin";
    public final static String ZUUL = "http://api.e-cny.xyz/v1/admin";


    public static class RP<T> {
        public ResPage<T> getReqPage(String s, Class<T> elementType) {
            ZullResult bean1 = JSONUtil.toBean(s, ZullResult.class);
            Object data1 = bean1.getData();
            JSONObject jsonObject = JSONUtil.parseObj(data1);
            log.info(jsonObject.toString());
            JSONArray list = jsonObject.getJSONArray("list");
            Class<T> T = null;
            List<T> list1 = JSONUtil.toList(list, elementType);
            ResPage<T> rp = new ResPage<>();
            rp.setList(list1);
            rp.setTotal(jsonObject.getLong("total"));
            rp.setRows(list1);
            rp.setPages(jsonObject.getLong("pages"));
            rp.setSize(jsonObject.getLong("size"));
            rp.setCurrent(jsonObject.getLong("current"));
            return rp;

        }
    }







}
