package com.webank.wedatasphere.qualitis.project.excel;

import com.alibaba.excel.annotation.ExcelProperty;

/**
 * @author v_gaojiedeng
 */
public class ExcelExecutionParametersByProject {

    @ExcelProperty(value = "Execution Parameter Json Object", index = 0)
    private String executionParameterJsonObject;

    public ExcelExecutionParametersByProject() {
        // Default
    }

    public String getExecutionParameterJsonObject() {
        return executionParameterJsonObject;
    }

    public void setExecutionParameterJsonObject(String executionParameterJsonObject) {
        this.executionParameterJsonObject = executionParameterJsonObject;
    }

    @Override
    public String toString() {
        return "ExcelExecutionParametersByProject{" +
            "executionParameterJsonObject='" + executionParameterJsonObject + '\'' +
            '}';
    }
}
