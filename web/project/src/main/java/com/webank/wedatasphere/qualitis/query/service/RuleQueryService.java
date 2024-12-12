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
import com.webank.wedatasphere.qualitis.query.request.RuleQueryRequest;
import com.webank.wedatasphere.qualitis.query.request.RulesDeleteRequest;
import com.webank.wedatasphere.qualitis.query.response.RuleQueryDataSource;
import com.webank.wedatasphere.qualitis.query.response.RuleQueryProject;

import com.webank.wedatasphere.qualitis.request.PageRequest;
import java.util.List;
import java.util.Map;

/**
 * @author v_wblwyan
 * @date 2018-11-1
 */
public interface RuleQueryService {

  /**
   * initial query api
   * @param user
   * @return
   */
  List<RuleQueryProject> init(String user);

  /**
   * Rule query api
   * @param param
   * @return
   */
  List<RuleQueryProject> query(RuleQueryRequest param);

  /**
   * Filter datasource with conditions
   *
   * @param pageRequest
   * @param user
   * @param clusterName
   * @param dbName
   * @param tableName
   * @param b
   * @return
   */
  List<RuleQueryDataSource> filter(PageRequest pageRequest, String user, String clusterName, String dbName,
      String tableName, boolean b);

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
   * Get the column information of the table
   * @param cluster
   * @param datasourceId
   * @param db
   * @param table
   * @param user
   * @return
   * @throws UnExpectedRequestException
   */
    List<ColumnInfoDetail> getColumnsByTableName(String cluster, Long datasourceId, String db, String table, String user)
        throws UnExpectedRequestException, MetaDataAcquireFailedException;

  /**
   * Get rules related with columns
   * @param cluster
   * @param db
   * @param table
   * @param user
   * @param s
   * @param page
   * @param size
   * @return
   */
  DataInfo<HiveRuleDetail> getRulesByColumn(String cluster, String db, String table, String user, String s, int page, int size);

  /**
   * delete rule in rule query UI.
   * @param request
   * @throws UnExpectedRequestException
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
}
