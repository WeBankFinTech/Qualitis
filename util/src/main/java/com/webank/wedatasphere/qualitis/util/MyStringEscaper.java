package com.webank.wedatasphere.qualitis.util;

/**
 * @author allenzhou@webank.com
 * @date 2024/2/23 12:00
 */
public class MyStringEscaper {
    public static String escapeStringForQuotes(String input) {
        if (input == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '\\':
                    sb.append("\\\\"); // 转义反斜杠
                    break;
                case '"':
                    sb.append("\\\""); // 转义双引号
                    break;
                case '\n':
                    sb.append("\\n"); // 转义换行符
                    break;
                case '\r':
                    sb.append("\\r"); // 转义回车符
                    break;
                case '\t':
                    sb.append("\\t"); // 转义制表符
                    break;
                // 根据需要添加其他需要转义的字符
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }
}
