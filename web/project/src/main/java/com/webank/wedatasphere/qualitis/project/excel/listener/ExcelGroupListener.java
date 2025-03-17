package com.webank.wedatasphere.qualitis.project.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.webank.wedatasphere.qualitis.project.excel.ExcelGroupByProject;
import com.webank.wedatasphere.qualitis.project.excel.ExcelProjectInfoListeners;

/**
 * @author v_minminghe@webank.com
 * @date 2024-10-29 12:36
 * @description
 */
public class ExcelGroupListener implements ReadListener<ExcelGroupByProject> {

    private ExcelProjectInfoListeners excelProjectInfoListeners;

    public ExcelGroupListener(ExcelProjectInfoListeners excelProjectInfoListeners) {
        this.excelProjectInfoListeners = excelProjectInfoListeners;
    }

    @Override
    public void invoke(ExcelGroupByProject data, AnalysisContext context) {
        excelProjectInfoListeners.getExcelGroupByProjects().add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
//        doing nothing
    }
}
