package com.webank.wedatasphere.qualitis.project.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.webank.wedatasphere.qualitis.project.excel.ExcelProjectInfoListeners;
import com.webank.wedatasphere.qualitis.project.excel.ExcelRuleMetric;

/**
 * @author v_minminghe@webank.com
 * @date 2024-10-29 14:24
 * @description
 */
public class ExcelRuleMetricListener implements ReadListener<ExcelRuleMetric> {
    private ExcelProjectInfoListeners excelProjectInfoListeners;

    public ExcelRuleMetricListener(ExcelProjectInfoListeners excelProjectInfoListeners) {
        this.excelProjectInfoListeners = excelProjectInfoListeners;
    }
    @Override
    public void invoke(ExcelRuleMetric data, AnalysisContext context) {
        excelProjectInfoListeners.getExcelMetricContent().add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
//        doing nothing
    }
}
