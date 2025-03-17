package com.webank.wedatasphere.qualitis.project.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.webank.wedatasphere.qualitis.project.excel.ExcelDatasourceEnv;
import com.webank.wedatasphere.qualitis.project.excel.ExcelProjectInfoListeners;

/**
 * @author v_minminghe@webank.com
 * @date 2024-10-29 14:27
 * @description
 */
public class ExcelDatasourceEnvListener implements ReadListener<ExcelDatasourceEnv> {

    private ExcelProjectInfoListeners excelProjectInfoListeners;

    public ExcelDatasourceEnvListener(ExcelProjectInfoListeners excelProjectInfoListeners) {
        this.excelProjectInfoListeners = excelProjectInfoListeners;
    }
    @Override
    public void invoke(ExcelDatasourceEnv data, AnalysisContext context) {
        excelProjectInfoListeners.getExcelDatasourceEnvContent().add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
//        doing nothing
    }
}
