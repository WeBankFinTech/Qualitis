package com.webank.wedatasphere.qualitis.project.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.webank.wedatasphere.qualitis.project.excel.ExcelProjectInfoListeners;
import com.webank.wedatasphere.qualitis.project.excel.ExcelRuleUdf;

/**
 * @author v_minminghe@webank.com
 * @date 2024-10-29 14:26
 * @description
 */
public class ExcelRuleUdfListener implements ReadListener<ExcelRuleUdf> {

    private ExcelProjectInfoListeners excelProjectInfoListeners;

    public ExcelRuleUdfListener(ExcelProjectInfoListeners excelProjectInfoListeners) {
        this.excelProjectInfoListeners = excelProjectInfoListeners;
    }
    @Override
    public void invoke(ExcelRuleUdf data, AnalysisContext context) {
        excelProjectInfoListeners.getExcelRuleUdfContent().add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
//        doing nothing
    }
}
