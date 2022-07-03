package com.ruoyi.common.utils;


import com.ruoyi.common.utils.bean.WithdrawalBean;

import java.util.List;

public class CommonUtils {

    //比较两个list元素是否完全一样
    public static boolean compareToList(List<WithdrawalBean> list1, List<WithdrawalBean> list2) {
        boolean result = false;
        if (list1.size() != list2.size()) { //判断两个list长度是否相同
            return false;
        } else {
            for (WithdrawalBean bean : list1) {
                if(list2.contains(bean)){
                    result = true;
                }else{
                    result = false;
                }
            }
        }
        return result;
    }
}
