package com.ruoyi.alipay.domain.util;
@FunctionalInterface
public interface BiIntFunction<T, R> {
    R apply(T t, int i);
}
