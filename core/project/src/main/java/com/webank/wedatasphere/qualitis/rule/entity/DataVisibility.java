package com.webank.wedatasphere.qualitis.rule.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;

/**
 * @author v_minminghe@webank.com
 * @date 2022-09-14 9:23
 * @description
 */
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Table(name = "qualitis_auth_data_visibility")
public class DataVisibility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_data_id")
    private Long tableDataId;

    /**
     * ruleMetric：指标
     * standardValue：标准值
     * ruleTemplate：规则模板
     * linkisDataSource：linkis数据源
     * linkisUdf：linkis UDF
     */
    @Column(name = "table_data_type")
    private String tableDataType;

    /**
     * 科室名称
     */
    @Column(name = "department_sub_name")
    private String departmentSubName;

    /**
     * 科室编码，对应用户的sub_department_code字段
     */
    @Column(name = "department_sub_id")
    private Long departmentSubId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTableDataId() {
        return tableDataId;
    }

    public void setTableDataId(Long tableDataId) {
        this.tableDataId = tableDataId;
    }

    public String getTableDataType() {
        return tableDataType;
    }

    public void setTableDataType(String tableDataType) {
        this.tableDataType = tableDataType;
    }

    public String getDepartmentSubName() {
        return departmentSubName;
    }

    public void setDepartmentSubName(String departmentSubName) {
        this.departmentSubName = departmentSubName;
    }

    public Long getDepartmentSubId() {
        return departmentSubId;
    }

    public void setDepartmentSubId(Long departmentSubId) {
        this.departmentSubId = departmentSubId;
    }
}
