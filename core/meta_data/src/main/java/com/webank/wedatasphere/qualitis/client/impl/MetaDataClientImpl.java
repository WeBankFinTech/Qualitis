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

package com.webank.wedatasphere.qualitis.client.impl;

import com.webank.wedatasphere.qualitis.constant.MetaDataAuthEnum;
import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.dao.MetaDataAuthDao;
import com.webank.wedatasphere.qualitis.dao.MetaDataClusterDao;
import com.webank.wedatasphere.qualitis.dao.MetaDataColumnDao;
import com.webank.wedatasphere.qualitis.dao.MetaDataDbDao;
import com.webank.wedatasphere.qualitis.dao.MetaDataTableDao;
import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import com.webank.wedatasphere.qualitis.metadata.request.GetClusterByUserRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetColumnByUserAndTableRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetDbByUserAndClusterRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetTableByUserAndDbRequest;
import com.webank.wedatasphere.qualitis.metadata.response.ClusterMappingDetail;
import com.webank.wedatasphere.qualitis.metadata.response.DataInfo;
import com.webank.wedatasphere.qualitis.metadata.response.cluster.ClusterInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.column.ColumnInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.db.DbInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.table.TableInfoDetail;
import com.webank.wedatasphere.qualitis.entity.AuthMetaData;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import com.webank.wedatasphere.qualitis.entity.MetaDataCluster;
import com.webank.wedatasphere.qualitis.entity.MetaDataColumn;
import com.webank.wedatasphere.qualitis.entity.MetaDataDb;
import com.webank.wedatasphere.qualitis.entity.MetaDataTable;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;

import com.webank.wedatasphere.qualitis.constant.MetaDataAuthEnum;
import com.webank.wedatasphere.qualitis.entity.*;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import com.webank.wedatasphere.qualitis.metadata.request.GetClusterByUserRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetColumnByUserAndTableRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetDbByUserAndClusterRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetTableByUserAndDbRequest;
import com.webank.wedatasphere.qualitis.metadata.response.DataInfo;
import com.webank.wedatasphere.qualitis.metadata.response.cluster.ClusterInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.column.ColumnInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.db.DbInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.table.TableInfoDetail;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author howeye
 * @date 2019-04-22
 */
@Component
public class MetaDataClientImpl implements MetaDataClient {

  @Autowired
  private MetaDataAuthDao metaDataAuthDao;
  @Autowired
  private MetaDataClusterDao metaDataClusterDao;
  @Autowired
  private MetaDataDbDao metaDataDbDao;
  @Autowired
  private MetaDataTableDao metaDataTableDao;
  @Autowired
  private MetaDataColumnDao metaDataColumnDao;
  @Autowired
  private ClusterInfoDao clusterInfoDao;

  /**
   * Paging query auth_meta_data
   * Query SQL: select distinct(cluster_name) from auth_meta_data where username = "${login_user}" and is_org = 0 limit 0,5;
   * @param request
   * @return
   */
  @Override
  public DataInfo<ClusterInfoDetail> getClusterByUser(GetClusterByUserRequest request) {
    String authUser = request.getLoginUser();
    List<String> authMetaDatas = metaDataAuthDao.findDistinctClusterPageByUsernameAndIsOrg(
        authUser, false, request.getStartIndex(), request.getPageSize());
    long total = metaDataAuthDao.countDistinctClusterByUsernameAndIsOrg(authUser, false);
    DataInfo<ClusterInfoDetail> dataInfo = new DataInfo<>((int) total);
    if (CollectionUtils.isEmpty(authMetaDatas)) {
      return dataInfo;
    }
    List<ClusterInfoDetail> details = new ArrayList<>();
    for (String clusterName : authMetaDatas) {
      ClusterInfoDetail detail = new ClusterInfoDetail(clusterName);
      details.add(detail);
    }
    dataInfo.setContent(details);
    return dataInfo;
  }

  @Override
  public DataInfo<DbInfoDetail> getDbByUserAndCluster(GetDbByUserAndClusterRequest request)
      throws UnExpectedRequestException {
    // Check existence of cluster name
    MetaDataCluster metaDataCluster = checkClusterNameExists(
        request.getClusterName());
    String authUser = request.getLoginUser();
    // Check permission of user
    boolean existsClusterAuth = metaDataAuthDao.existsClusterAuth(
        MetaDataAuthEnum.CLUSTER_AUTH.getCode(), request.getClusterName(), authUser, false);
    if (existsClusterAuth) {
      List<MetaDataDb> metaDataDbs = metaDataDbDao.queryPageByCluster(metaDataCluster,
                                                                      request.getStartIndex(),
                                                                      request.getPageSize());
      long total = metaDataDbDao.countByCluster(metaDataCluster);
      DataInfo<DbInfoDetail> dataInfo = new DataInfo<>((int) total);
      if (CollectionUtils.isEmpty(metaDataDbs)) {
        return dataInfo;
      }
      List<DbInfoDetail> details = new ArrayList<>();
      for (MetaDataDb data : metaDataDbs) {
        DbInfoDetail detail = new DbInfoDetail(data.getDbName());
        details.add(detail);
      }
      dataInfo.setContent(details);
      return dataInfo;
    }

    List<String> authMetaDatas = metaDataAuthDao.findDistinctDbPageByClusterNameAndUsernameAndIsOrg(
        request.getClusterName(), authUser, false, request.getStartIndex(), request.getPageSize());
    long total = metaDataAuthDao.countDistinctDbByClusterNameAndUsernameAndIsOrg(
        request.getClusterName(), authUser, false);
    DataInfo<DbInfoDetail> dataInfo = new DataInfo<>((int) total);

    if (CollectionUtils.isEmpty(authMetaDatas)) {
      return dataInfo;
    }

    List<DbInfoDetail> details = new ArrayList<>();
    for (String data : authMetaDatas) {
      DbInfoDetail detail = new DbInfoDetail(data);
      details.add(detail);
    }
    dataInfo.setContent(details);
    return dataInfo;
  }


  @Override
  public DataInfo<TableInfoDetail> getTableByUserAndDb(GetTableByUserAndDbRequest request)
      throws UnExpectedRequestException {

    MetaDataDb metaData = checkClusterAndDbNameExists(request.getClusterName(),
                                                                    request.getDbName());
    String authUser = request.getLoginUser();

    boolean existsClusterAuth = metaDataAuthDao.existsClusterAuth(
        MetaDataAuthEnum.CLUSTER_AUTH.getCode(), request.getClusterName(), authUser, false);

    boolean existsDbAuth = metaDataAuthDao.existsDbAuth(MetaDataAuthEnum.DB_AUTH.getCode(),
                                                        request.getClusterName(),
                                                        request.getDbName(), authUser, false);

    if (existsClusterAuth || existsDbAuth) {
      List<MetaDataTable> metaDatas = metaDataTableDao.queryPageByDb(metaData,
                                                                     request.getStartIndex(),
                                                                     request.getPageSize());
      long total = metaDataTableDao.countByDb(metaData);
      DataInfo<TableInfoDetail> dataInfo = new DataInfo<>((int) total);
      if (CollectionUtils.isEmpty(metaDatas)) {
        return dataInfo;
      }
      List<TableInfoDetail> details = new ArrayList<>();
      for (MetaDataTable data : metaDatas) {
        TableInfoDetail detail = new TableInfoDetail(data.getTableName());
        details.add(detail);
      }
      dataInfo.setContent(details);
      return dataInfo;
    }

    List<String> authMetaDatas = metaDataAuthDao.findDistinctTablePageByClusterNameAndDbNameAndUsernameAndIsOrg(
        request.getClusterName(), request.getDbName(), authUser, false, request.getStartIndex(),
        request.getPageSize());
    long total = metaDataAuthDao.countDistinctTableByClusterNameAndDbNameAndUsernameAndIsOrg(
        request.getClusterName(), request.getDbName(), authUser, false);
    DataInfo<TableInfoDetail> dataInfo = new DataInfo<>((int) total);
    if (CollectionUtils.isEmpty(authMetaDatas)) {
      return dataInfo;
    }
    List<TableInfoDetail> details = new ArrayList<>();
    for (String data : authMetaDatas) {
      TableInfoDetail detail = new TableInfoDetail(data);
      details.add(detail);
    }
    dataInfo.setContent(details);
    return dataInfo;
  }

  @Override
  public DataInfo<ColumnInfoDetail> getColumnByUserAndTable(GetColumnByUserAndTableRequest request)
      throws UnExpectedRequestException {

    MetaDataTable metaData = checkClusterAndDbAndTableNameExists(
        request.getClusterName(), request.getDbName(), request.getTableName());
    String authUser = request.getLoginUser();

    boolean existsClusterAuth = metaDataAuthDao.existsClusterAuth(
        MetaDataAuthEnum.CLUSTER_AUTH.getCode(), request.getClusterName(), authUser, false);

    boolean existsDbAuth = metaDataAuthDao.existsDbAuth(MetaDataAuthEnum.DB_AUTH.getCode(),
                                                        request.getClusterName(),
                                                        request.getDbName(), authUser, false);

    boolean existsTableAuth = metaDataAuthDao.existsTableAuth(MetaDataAuthEnum.TABLE_AUTH.getCode(),
                                                              request.getClusterName(),
                                                              request.getDbName(),
                                                              request.getTableName(), authUser,
                                                              false);

    if (existsClusterAuth || existsDbAuth || existsTableAuth) {
      List<MetaDataColumn> metaDatas = metaDataColumnDao.queryPageByTable(metaData,
                                                                          request.getStartIndex(),
                                                                          request.getPageSize());
      long total = metaDataColumnDao.countByTable(metaData);
      DataInfo<ColumnInfoDetail> dataInfo = new DataInfo<>((int) total);
      if (CollectionUtils.isEmpty(metaDatas)) {
        return dataInfo;
      }
      List<ColumnInfoDetail> details = new ArrayList<>();
      for (MetaDataColumn data : metaDatas) {
        ColumnInfoDetail detail = new ColumnInfoDetail(data.getColumnName(), data.getColumnType());
        details.add(detail);
      }
      dataInfo.setContent(details);
      return dataInfo;
    }

    List<AuthMetaData> authMetaDatas = metaDataAuthDao.findColumnPageByAuthTypeAndClusterNameAndDbNameAndTableNameAndUsernameAndIsOrg(
        MetaDataAuthEnum.COLUMN_AUTH.getCode(), request.getClusterName(), request.getDbName(),
        request.getTableName(), authUser, false, request.getStartIndex(), request.getPageSize());
    long total = metaDataAuthDao.countColumnByAuthTypeAndClusterNameAndDbNameAndTableNameAndUsernameAndIsOrg(
        MetaDataAuthEnum.COLUMN_AUTH.getCode(), request.getClusterName(), request.getDbName(),
        request.getTableName(), authUser, false);
    DataInfo<ColumnInfoDetail> dataInfo = new DataInfo<>((int) total);
    if (CollectionUtils.isEmpty(authMetaDatas)) {
      return dataInfo;
    }
    List<ColumnInfoDetail> details = new ArrayList<>();
    for (AuthMetaData data : authMetaDatas) {
      ColumnInfoDetail detail = new ColumnInfoDetail(data.getColumnName(), data.getColumnType());
      details.add(detail);
    }
    dataInfo.setContent(details);
    return dataInfo;
  }

  private MetaDataCluster checkClusterNameExists(String clusterName) throws
      UnExpectedRequestException {
    MetaDataCluster metaDataCluster = metaDataClusterDao.findByClusterName(clusterName);
    if (metaDataCluster == null) {
      throw new UnExpectedRequestException(String.format("%s 集群名称不存在", clusterName));
    }
    return metaDataCluster;
  }

  private MetaDataDb checkClusterAndDbNameExists(String clusterName, String dbName) throws
      UnExpectedRequestException {
    MetaDataCluster metaDataCluster = checkClusterNameExists(clusterName);
    MetaDataDb metaDataDb = metaDataDbDao.findByDbNameAndCluster(dbName, metaDataCluster);
    if (metaDataDb == null) {
      throw new UnExpectedRequestException(String.format("%s.%s 集群-库关系不存在", clusterName, dbName));
    }
    return metaDataDb;
  }

  private MetaDataTable checkClusterAndDbAndTableNameExists(String clusterName, String dbName,
      String tableName) throws
      UnExpectedRequestException {
    MetaDataDb metaDataDb = checkClusterAndDbNameExists(clusterName, dbName);
    MetaDataTable metaDataTable = metaDataTableDao.findByTableNameAndDb(tableName, metaDataDb);
    if (metaDataTable == null) {
      throw new UnExpectedRequestException(String.format("%s.%s.%s 集群-库-表关系不存在", clusterName, dbName, tableName));
    }
    return metaDataTable;
  }
}
