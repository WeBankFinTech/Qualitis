package com.webank.wedatasphere.qualitis.project.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.webank.wedatasphere.qualitis.project.excel.ExcelProjectInfoListeners;
import com.webank.wedatasphere.qualitis.project.excel.ExcelRuleByProject;

/**
 * @author v_minminghe@webank.com
 * @date 2024-10-29 12:35
 * @description
 */
public class ExcelRuleByProjectListener implements ReadListener<ExcelRuleByProject> {

    private ExcelProjectInfoListeners excelProjectInfoListeners;

    public ExcelRuleByProjectListener(ExcelProjectInfoListeners excelProjectInfoListeners) {
        this.excelProjectInfoListeners = excelProjectInfoListeners;
    }

    @Override
    public void invoke(ExcelRuleByProject data, AnalysisContext context) {
        excelProjectInfoListeners.getExcelRuleContent().add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
//        doing nothing
    }
}
