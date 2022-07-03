package com.ruoyi.framework.util;

import com.ruoyi.common.annotation.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;


public class AuditLogUtils {
    final static Logger logger = LoggerFactory.getLogger(AuditLogUtils.class);

    /**
     * 动态修改注解上值
     * @Title: alterAnnotationOn
     * @Description: 动态AuditLog修改注解的operate操作事件类型字段
     * @param @param method
     * @param @param type
     * @param @throws Exception 参数
     * @return void 返回类型
     * @throws
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void alterAnnotationOn(Method method, String operateDetail) throws Exception {
        method.setAccessible(true);
        logger.info("执行的方法={}",method.getName());
        boolean methodHasAnno = method.isAnnotationPresent(Log.class); // 是否指定类型的注释存在于此元素上
        if (methodHasAnno) {
            // 得到注解
            Log methodAnno = method.getAnnotation(Log.class);
            // 修改
            InvocationHandler handler = Proxy.getInvocationHandler(methodAnno);
            // annotation注解的membervalues
            Field hField = handler.getClass().getDeclaredField("memberValues");
            // 因为这个字段是 private final 修饰，所以要打开权限
            hField.setAccessible(true);
            // 获取 memberValues
            Map<String, Object> memberValues = (Map) hField.get(handler);
            logger.info("修改操作注解 改为={}",operateDetail);
            memberValues.put("operateDetail", operateDetail);
        }
    }
}
