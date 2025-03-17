package com.webank.wedatasphere.qualitis.project.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.webank.wedatasphere.qualitis.project.excel.ExcelProjectInfoListeners;
import com.webank.wedatasphere.qualitis.project.excel.ExcelWorkflowBusiness;

/**
 * @author v_minminghe@webank.com
 * @date 2024-10-29 14:35
 * @description
 */
public class ExcelWorkflowBusinessListener implements ReadListener<ExcelWorkflowBusiness> {

    private ExcelProjectInfoListeners excelProjectInfoListeners;

    public ExcelWorkflowBusinessListener(ExcelProjectInfoListeners excelProjectInfoListeners) {
        this.excelProjectInfoListeners = excelProjectInfoListeners;
    }

    @Override
    public void invoke(ExcelWorkflowBusiness data, AnalysisContext context) {
        excelProjectInfoListeners.getExcelWorkflowBusinessContent().add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
//        doing nothing
    }
}
