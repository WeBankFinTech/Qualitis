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

package com.webank.wedatasphere.qualitis.query.service;

import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.response.DataInfo;
import com.webank.wedatasphere.qualitis.metadata.response.column.ColumnInfoDetail;
import com.webank.wedatasphere.qualitis.project.response.HiveRuleDetail;
import com.webank.wedatasphere.qualitis.query.request.LineageParameterRequest;
import com.webank.wedatasphere.qualitis.project.request.RuleQueryRequest;
import com.webank.wedatasphere.qualitis.query.request.RulesDeleteRequest;
import com.webank.wedatasphere.qualitis.query.response.LineageParameterResponse;
import com.webank.wedatasphere.qualitis.query.response.RuleQueryDataSource;
import com.webank.wedatasphere.qualitis.request.PageRequest;

import java.util.List;
import java.util.Map;

/**
 * @author v_wblwyan
 * @date 2018-11-1
 */
public interface RuleQueryService {

  /**
   * Filter datasource with conditions
   *
   * @param pageRequest
   * @param user
   * @param clusterName
   * @param dbName
   * @param tableName
   * @param datasourceType
   * @param subSystemId
   * @param tagCode
   * @param departmentName
   * @param devDepartmentName
   * @param envName
   * @return
   */
  DataInfo<RuleQueryDataSource> filter(PageRequest pageRequest, String user, String clusterName, String dbName, String tableName,
                                       Integer datasourceType, String subSystemId, String tagCode, String departmentName, String devDepartmentName, String envName);

  /**
   * Get all rule datasource by user
   * @param user
   * @return
   */
  Map<String, Object> conditions(String user);

  /**
   * Get all rule datasource[cluster,db,table,comment] by user
   * @param user
   * @param pageRequest
   * @return
   */
  List<RuleQueryDataSource> all(String user, PageRequest pageRequest);

  /**
   * Get the column information of the table.
   * @param cluster
   * @param datasourceId
   * @param db
   * @param table
   * @param user
   * @return
   * @throws UnExpectedRequestException
   * @throws MetaDataAcquireFailedException
   */
    List<ColumnInfoDetail> getColumnsFromMetaService(String cluster, Long datasourceId, String db, String table, String user)
        throws UnExpectedRequestException, MetaDataAcquireFailedException;

  /**
   * filter results of getColumnsFromMetaService() in local
   * @param columnInfoDetailList
   * @param filterCondition
   * @return
   */
    List<ColumnInfoDetail> filterColumns(List<ColumnInfoDetail> columnInfoDetailList, RuleQueryRequest filterCondition);

  /**
   * Get rules related with columns
   * @param cluster
   * @param db
   * @param table
   * @param user
   * @param s
   * @param ruleTemplateId
   * @param relationObjectType
   * @param page
   * @param size
   * @return
   */
  DataInfo<HiveRuleDetail> getRulesByCondition(String cluster, String db, String table, String user, String s, Long ruleTemplateId, Integer relationObjectType, int page, int size);

  /**
   * delete rule in rule query UI.
   * @param request
   * @throws UnExpectedRequestException
   * @throws PermissionDeniedRequestException
   */
    void deleteRules(RulesDeleteRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException;

  /**
   * Compare datasource in hive.
   * @param cols
   * @param columnInfoDetails
   * @return
   */
    boolean compareDataSource(List<String> cols, List<ColumnInfoDetail> columnInfoDetails);

  /**
   * Find cols with conditions to compare datasource in hive.
   * @param cluster
   * @param db
   * @param table
   * @param user
   * @return
   */
  List<String> findCols(String cluster, String db, String table, String user);

  /**
   * Count rule datasource.
   * @param user
   * @param cluster
   * @param db
   * @param table
   * @param datasourceType
   * @param subSystemId
   * @param departmentName
   * @param devDepartmentName
   * @param tagCode
   * @param envName
   * @return
   */
  int count(String user, String cluster, String db, String table, Integer datasourceType, String subSystemId, String departmentName,
      String devDepartmentName, String tagCode, String envName);

  /**
   * Get table tags from dms
   * @return
   * @throws MetaDataAcquireFailedException
   */
  DataInfo<Map<String, Object>> getTagList() throws MetaDataAcquireFailedException;

  /**
   * get some parameter for data lineage in front-end page
   * @param request
   * @return
   * @throws UnExpectedRequestException
   * @throws MetaDataAcquireFailedException
   */
  LineageParameterResponse getLineageParameter(LineageParameterRequest request) throws UnExpectedRequestException, MetaDataAcquireFailedException;
}
