package com.webank.wedatasphere.qualitis.project.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.webank.wedatasphere.qualitis.project.excel.ExcelProjectInfoListeners;
import com.webank.wedatasphere.qualitis.project.excel.ExcelPublishScheduled;
import com.webank.wedatasphere.qualitis.project.excel.ExcelRelationScheduled;

import java.util.ArrayList;
import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2024-10-29 14:29
 * @description
 */
public class ExcelRelationScheduledListener implements ReadListener<ExcelRelationScheduled> {

    private ExcelProjectInfoListeners excelProjectInfoListeners;

    public ExcelRelationScheduledListener(ExcelProjectInfoListeners excelProjectInfoListeners) {
        this.excelProjectInfoListeners = excelProjectInfoListeners;
    }
    @Override
    public void invoke(ExcelRelationScheduled data, AnalysisContext context) {
        excelProjectInfoListeners.getExcelRelationScheduledContent().add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
//        doing nothing
    }
}
