package com.webank.wedatasphere.qualitis.project.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.webank.wedatasphere.qualitis.project.excel.ExcelProjectInfoListeners;
import com.webank.wedatasphere.qualitis.project.excel.ExcelPublishScheduled;

/**
 * @author v_minminghe@webank.com
 * @date 2024-10-29 14:29
 * @description
 */
public class ExcelPublishScheduledListener implements ReadListener<ExcelPublishScheduled> {

    private ExcelProjectInfoListeners excelProjectInfoListeners;

    public ExcelPublishScheduledListener(ExcelProjectInfoListeners excelProjectInfoListeners) {
        this.excelProjectInfoListeners = excelProjectInfoListeners;
    }
    @Override
    public void invoke(ExcelPublishScheduled data, AnalysisContext context) {
        excelProjectInfoListeners.getExcelPublishScheduledContent().add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
//        doing nothing
    }
}
