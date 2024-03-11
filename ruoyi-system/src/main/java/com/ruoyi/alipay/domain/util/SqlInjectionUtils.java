package com.ruoyi.alipay.domain.util;

import java.util.Objects;
import java.util.regex.Pattern;

public class SqlInjectionUtils {
    private static final Pattern SQL_SYNTAX_PATTERN = Pattern.compile("(insert|delete|update|select|create|drop|truncate|grant|alter|deny|revoke|call|execute|exec|declare|show|rename|set)" +
            "\\s+.*(into|from|set|where|table|database|view|index|on|cursor|procedure|trigger|for|password|union|and|or)|(select\\s*\\*\\s*from\\s+)|(and|or)\\s+.*(like|=|>|<|in|between|is|not|exists)", Pattern.CASE_INSENSITIVE);
    /**
     * 使用'、;或注释截断SQL检查正则
     */
    private static final Pattern SQL_COMMENT_PATTERN = Pattern.compile("'.*(or|union|--|#|/\\*|;)", Pattern.CASE_INSENSITIVE);


    /**
     * 检查参数是否存在 SQL 注入
     *
     * @param value 检查参数
     * @return true 非法 false 合法
     */
    public static boolean check(String value) {
        Objects.requireNonNull(value);
        // 处理是否包含SQL注释字符 || 检查是否包含SQL注入敏感字符
        return SQL_COMMENT_PATTERN.matcher(value).find() || SQL_SYNTAX_PATTERN.matcher(value).find();
    }

    /**
     * 刪除字段转义符单引号双引号
     *
     * @param text 待处理字段
     * @return
     */
    public static String removeEscapeCharacter(String text) {
        Objects.nonNull(text);
        return text.replaceAll("\"", "").replaceAll("'", "");
    }
}
