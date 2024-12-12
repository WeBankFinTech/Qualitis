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

package com.webank.wedatasphere.qualitis.project.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author v_wblwyan
 * @date 2018-11-1
 */
public class RuleQueryRequest {

  private String cluster;
  @JsonProperty("datasource_id")
  private Long datasourceId;
  private String db;
  private String table;
  private String column;
  private String user;
  private Integer datasourceType;
  @JsonProperty("rule_template_id")
  private Long ruleTemplateId;
  /**
   * 1 == table, 2 == column
   */
  @JsonProperty("relation_object_type")
  private Integer relationObjectType;
  @JsonProperty("sub_system_id")
  private String subSystemId;
  @JsonProperty("department_name")
  private String departmentName;
  @JsonProperty("dev_department_name")
  private String devDepartmentName;
  @JsonProperty("tag_code")
  private String tagCode;
  private String envName;

  @JsonProperty("column_name")
  private String fieldName;
  @JsonProperty("data_type")
  private String dataType;
  @JsonProperty("is_primary")
  private Boolean isPrimary;
  @JsonProperty("is_partition")
  private Boolean isPartitionField;

  private int page;
  private int size;

  public RuleQueryRequest() {
    this.page = 0;
    this.size = 10;
  }

  public Integer getRelationObjectType() {
    return relationObjectType;
  }

  public void setRelationObjectType(Integer relationObjectType) {
    this.relationObjectType = relationObjectType;
  }

  public RuleQueryRequest(String user) {
    this.user = user;
  }

  public String getCluster() {
    return cluster;
  }

  public void setCluster(String cluster) {
    this.cluster = cluster;
  }

  public Long getDatasourceId() {
    return datasourceId;
  }

  public void setDatasourceId(Long datasourceId) {
    this.datasourceId = datasourceId;
  }

  public String getDb() {
    return db;
  }

  public void setDb(String db) {
    this.db = db;
  }

  public String getTable() {
    return table;
  }

  public void setTable(String table) {
    this.table = table;
  }

  public String getColumn() {
    return column;
  }

  public void setColumn(String column) {
    this.column = column;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public Integer getDatasourceType() {
    return datasourceType;
  }

  public void setDatasourceType(Integer datasourceType) {
    this.datasourceType = datasourceType;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public Long getRuleTemplateId() {
    return ruleTemplateId;
  }

  public void setRuleTemplateId(Long ruleTemplateId) {
    this.ruleTemplateId = ruleTemplateId;
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public String getDataType() {
    return dataType;
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }

  public Boolean getPrimary() {
    return isPrimary;
  }

  public void setPrimary(Boolean primary) {
    isPrimary = primary;
  }

  public Boolean getPartitionField() {
    return isPartitionField;
  }

  public void setPartitionField(Boolean partitionField) {
    isPartitionField = partitionField;
  }

  public String getSubSystemId() {
    return subSystemId;
  }

  public void setSubSystemId(String subSystemId) {
    this.subSystemId = subSystemId;
  }

  public String getDepartmentName() {
    return departmentName;
  }

  public void setDepartmentName(String departmentName) {
    this.departmentName = departmentName;
  }

  public String getDevDepartmentName() {
    return devDepartmentName;
  }

  public void setDevDepartmentName(String devDepartmentName) {
    this.devDepartmentName = devDepartmentName;
  }

  public String getTagCode() {
    return tagCode;
  }

  public void setTagCode(String tagCode) {
    this.tagCode = tagCode;
  }

  public String getEnvName() {
    return envName;
  }

  public void setEnvName(String envName) {
    this.envName = envName;
  }

  public void checkRequest() throws UnExpectedRequestException {
    if (cluster == null || "".equals(cluster)
       || table == null || "".equals(table)) {
      throw new UnExpectedRequestException("Params of {&REQUEST_CAN_NOT_BE_NULL}");
    }
  }

  public void convertParameter() {
    this.cluster = StringUtils.trimToNull(this.cluster);
    this.column = StringUtils.trimToNull(this.column);
    this.db = StringUtils.trimToNull(this.db);
    this.table = StringUtils.trimToNull(this.table);
    this.tagCode = StringUtils.trimToNull(this.tagCode);
    this.envName = StringUtils.trimToNull(this.envName);
    if (StringUtils.isEmpty(this.departmentName)) {
      this.departmentName = null;
    } else {
      this.departmentName = "%"+this.departmentName+"%";
    }
    if (StringUtils.isEmpty(this.devDepartmentName)) {
      this.devDepartmentName = null;
    } else {
      this.devDepartmentName = "%"+this.devDepartmentName+"%";
    }
  }

}