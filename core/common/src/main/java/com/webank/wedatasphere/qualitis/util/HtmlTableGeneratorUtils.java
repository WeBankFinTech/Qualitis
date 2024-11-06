package com.webank.wedatasphere.qualitis.util;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public class HtmlTableGeneratorUtils {

    private int rows;
    private int columns;
    private List<String> headers;
    private List<List<String>> data;

    public HtmlTableGeneratorUtils(int rows, int columns, List<String> headers, List<List<String>> data) {
        this.rows = rows;
        this.columns = columns;
        this.headers = headers;
        this.data = data;
    }

    public String generateTable() {
        StringBuilder table = new StringBuilder();
        // 添加table标签
        table.append("<table >\n");

        // 生成表头
        table.append("<tr>\n");
        for (int i = 1; i <= columns; i++) {
            table.append("<th style=\"background-color:rgb(220,229,238);text-align:center;width:120px;\"> " + headers.get(i - 1) + "</th>\n");
        }
        table.append("</tr>\n");

        // 生成表格数据
        for (List<String> row : data) {
            table.append("<tr>\n");
            for (String cellData : row) {
                table.append("<td style=\"background-color:rgb(241,245,248);text-align:center;width:120;\">" + cellData + "</td>\n");
            }
            table.append("</tr>\n");
        }

        table.append("</table>");

        return table.toString();
    }

}
