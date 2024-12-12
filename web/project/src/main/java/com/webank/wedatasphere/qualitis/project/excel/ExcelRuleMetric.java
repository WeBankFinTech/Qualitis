/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.qualitis.project.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

/**
 * @author allenzhou
 */
public class ExcelRuleMetric extends BaseRowModel {
    @ExcelProperty(value = "Rule Metric Name", index = 0)
    private String name;
    @ExcelProperty(value = "Rule Metric Chinese Name", index = 1)
    private String chName;
    @ExcelProperty(value = "Rule Metric Desc", index = 2)
    private String metricDesc;
    @ExcelProperty(value = "Rule Metric Dimension", index = 3)
    private String dimension;

    @ExcelProperty(value = "Sub System ID", index = 4)
    private String subSystemId;
    @ExcelProperty(value = "Sub System Name", index = 5)
    private String subSystemName;
    @ExcelProperty(value = "Sub System Full Chinese Name", index = 6)
    private String fullCnName;

    @ExcelProperty(value = "Product Id", index = 7)
    private String productId;
    @ExcelProperty(value = "Product Name", index = 8)
    private String productName;


    @ExcelProperty(value = "Bussiness Custom", index = 9)
    private String bussCustom;

    @ExcelProperty(value = "Dev Department Name", index = 10)
    private String devDepartmentName;
    @ExcelProperty(value = "Ops Department Name", index = 11)
    private String opsDepartmentName;

    @ExcelProperty(value = "En Code", index = 12)
    private String enCode;
    @ExcelProperty(value = "Type", index = 13)
    private Integer type;
    @ExcelProperty(value = "Available", index = 14)
    private Boolean available;
    @ExcelProperty(value = "Frequency", index = 15)
    private String frequency;

    @ExcelProperty(value = "Department Code", index = 16)
    private String departmentCode;
    @ExcelProperty(value = "Department Name", index = 17)
    private String departmentName;

    public ExcelRuleMetric() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChName() {
        return chName;
    }

    public void setChName(String chName) {
        this.chName = chName;
    }

    public String getMetricDesc() {
        return metricDesc;
    }

    public void setMetricDesc(String metricDesc) {
        this.metricDesc = metricDesc;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getSubSystemId() {
        return subSystemId;
    }

    public void setSubSystemId(String subSystemId) {
        this.subSystemId = subSystemId;
    }

    public String getSubSystemName() {
        return subSystemName;
    }

    public void setSubSystemName(String subSystemName) {
        this.subSystemName = subSystemName;
    }

    public String getFullCnName() {
        return fullCnName;
    }

    public void setFullCnName(String fullCnName) {
        this.fullCnName = fullCnName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBussCustom() {
        return bussCustom;
    }

    public void setBussCustom(String bussCustom) {
        this.bussCustom = bussCustom;
    }

    public String getDevDepartmentName() {
        return devDepartmentName;
    }

    public void setDevDepartmentName(String devDepartmentName) {
        this.devDepartmentName = devDepartmentName;
    }

    public String getOpsDepartmentName() {
        return opsDepartmentName;
    }

    public void setOpsDepartmentName(String opsDepartmentName) {
        this.opsDepartmentName = opsDepartmentName;
    }

    public String getEnCode() {
        return enCode;
    }

    public void setEnCode(String enCode) {
        this.enCode = enCode;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    @Override
    public String toString() {
        return "ExcelRuleMetric{" +
            "name='" + name + '\'' +
            ", dimension='" + dimension + '\'' +
            ", devDepartmentName='" + devDepartmentName + '\'' +
            ", opsDepartmentName='" + opsDepartmentName + '\'' +
            ", enCode='" + enCode + '\'' +
            ", available=" + available +
            ", departmentName='" + departmentName + '\'' +
            '}';
    }
}
