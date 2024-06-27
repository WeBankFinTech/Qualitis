package com.webank.wedatasphere.qualitis.project.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

/**
 * @author v_gaojiedeng@webank.com
 */
public class ExcelStandardValue extends BaseRowModel {

    @ExcelProperty(value = "Standard Value Json Object", index = 0)
    private String standardValueJsonObject;

    @ExcelProperty(value = "Data Visibility Json Object", index = 1)
    private String dataVisibilityJsonObject;

    public ExcelStandardValue() {
        // Default Constructor
    }

    public String getStandardValueJsonObject() {
        return standardValueJsonObject;
    }

    public void setStandardValueJsonObject(String standardValueJsonObject) {
        this.standardValueJsonObject = standardValueJsonObject;
    }

    public String getDataVisibilityJsonObject() {
        return dataVisibilityJsonObject;
    }

    public void setDataVisibilityJsonObject(String dataVisibilityJsonObject) {
        this.dataVisibilityJsonObject = dataVisibilityJsonObject;
    }
}
