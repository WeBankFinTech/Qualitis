package com.webank.wedatasphere.qualitis.project.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.webank.wedatasphere.qualitis.project.excel.ExcelProjectInfoListeners;
import com.webank.wedatasphere.qualitis.project.excel.ExcelStandardValue;

/**
 * @author v_minminghe@webank.com
 * @date 2024-10-29 14:33
 * @description
 */
public class ExcelStandardValueListener implements ReadListener<ExcelStandardValue> {

    private ExcelProjectInfoListeners excelProjectInfoListeners;

    public ExcelStandardValueListener(ExcelProjectInfoListeners excelProjectInfoListeners) {
        this.excelProjectInfoListeners = excelProjectInfoListeners;
    }
    @Override
    public void invoke(ExcelStandardValue data, AnalysisContext context) {
        excelProjectInfoListeners.getExcelStandardValueContent().add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
//        doing nothing
    }
}
