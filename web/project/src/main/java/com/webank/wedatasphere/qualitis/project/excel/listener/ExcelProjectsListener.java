package com.webank.wedatasphere.qualitis.project.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.webank.wedatasphere.qualitis.project.excel.ExcelProject;
import com.webank.wedatasphere.qualitis.project.excel.ExcelProjectInfoListeners;

/**
 * @author v_minminghe@webank.com
 * @date 2024-10-29 14:15
 * @description
 */
public class ExcelProjectsListener implements ReadListener<ExcelProject> {

    private ExcelProjectInfoListeners excelProjectInfoListeners;

    public ExcelProjectsListener(ExcelProjectInfoListeners excelProjectInfoListeners) {
        this.excelProjectInfoListeners = excelProjectInfoListeners;
    }

    @Override
    public void invoke(ExcelProject data, AnalysisContext context) {
        excelProjectInfoListeners.getExcelProjectContent().add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
//        doing nothing
    }

}
