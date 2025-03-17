package com.webank.wedatasphere.qualitis.project.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.webank.wedatasphere.qualitis.project.excel.ExcelExecutionParametersByProject;
import com.webank.wedatasphere.qualitis.project.excel.ExcelProjectInfoListeners;

/**
 * @author v_minminghe@webank.com
 * @date 2024-10-29 12:44
 * @description
 */
public class ExcelExecutionParameterListener implements ReadListener<ExcelExecutionParametersByProject> {

    private ExcelProjectInfoListeners excelProjectInfoListeners;

    public ExcelExecutionParameterListener(ExcelProjectInfoListeners excelProjectInfoListeners) {
        this.excelProjectInfoListeners = excelProjectInfoListeners;
    }

    @Override
    public void invoke(ExcelExecutionParametersByProject data, AnalysisContext context) {
        excelProjectInfoListeners.getExcelExecutionParametersContent().add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
//        doing nothing
    }
}
