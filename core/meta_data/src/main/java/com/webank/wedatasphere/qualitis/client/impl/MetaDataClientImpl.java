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

import com.webank.wedatasphere.qualitis.config.LinkisConfig;
import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.request.GetClusterByUserRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetColumnByUserAndTableRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetDbByUserAndClusterRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetTableByUserAndDbRequest;
import com.webank.wedatasphere.qualitis.metadata.response.DataInfo;
import com.webank.wedatasphere.qualitis.metadata.response.cluster.ClusterInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.column.ColumnInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.db.DbInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.table.TableInfoDetail;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.UriBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author howeye
 * @date 2019-04-22
 */
@Component
public class MetaDataClientImpl implements MetaDataClient {

  @Autowired
  private ClusterInfoDao clusterInfoDao;

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private LinkisConfig linkisConfig;

  private static final Logger LOGGER = LoggerFactory.getLogger(MetaDataClientImpl.class);

  @Override
  public DataInfo<ClusterInfoDetail> getClusterByUser(GetClusterByUserRequest request) {
    Long total = clusterInfoDao.countAll();
    List<ClusterInfo> allCluster = clusterInfoDao.findAllClusterInfo(request.getStartIndex(), request.getPageSize());

    DataInfo<ClusterInfoDetail> dataInfo = new DataInfo<>(total.intValue());
    if (CollectionUtils.isEmpty(allCluster)) {
      return dataInfo;
    }
    List<ClusterInfoDetail> details = new ArrayList<>();
    for (ClusterInfo clusterInfo : allCluster) {
      ClusterInfoDetail detail = new ClusterInfoDetail(clusterInfo.getClusterName());
      details.add(detail);
    }
    dataInfo.setContent(details);
    return dataInfo;
  }

  @Override
  public DataInfo<DbInfoDetail> getDbByUserAndCluster(GetDbByUserAndClusterRequest request)
          throws UnExpectedRequestException, MetaDataAcquireFailedException {
    // Check existence of cluster name
    ClusterInfo clusterInfo = checkClusterNameExists(
        request.getClusterName());
    String authUser = request.getLoginUser();
    // send request to get dbs
    String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDbPath()).toString();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add("Token-User", authUser);
    headers.add("Token-Code", clusterInfo.getLinkisToken());

    HttpEntity<Object> entity = new HttpEntity<>(headers);
    LOGGER.info("Start to get db by user and cluster by linkis. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.GET, entity);
    Map<String, Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
    LOGGER.info("Start to get db by user and cluster by linkis. response: {}", response);

    if (!checkResponse(response)) {
      String message = (String) response.get("message");
      throw new MetaDataAcquireFailedException("Error! Can not get meta data from linkis, exception: " + message);
    }

    List<String> allDbs = ((List<Map<String, String>>)((Map<String, Object>)response.get("data")).get("dbs")).stream()
            .map(o -> o.get("dbName")).collect(Collectors.toList());

    DataInfo<DbInfoDetail> dataInfo = new DataInfo<>(allDbs.size());
    if (CollectionUtils.isEmpty(allDbs)) {
      return dataInfo;
    }

    List<DbInfoDetail> details = new ArrayList<>();
    for (String data : allDbs) {
      DbInfoDetail detail = new DbInfoDetail(data);
      details.add(detail);
    }
    dataInfo.setContent(details);
    return dataInfo;
  }


  @Override
  public DataInfo<TableInfoDetail> getTableByUserAndDb(GetTableByUserAndDbRequest request)
          throws UnExpectedRequestException, MetaDataAcquireFailedException {
    ClusterInfo clusterInfo = checkClusterNameExists(request.getClusterName());
    String authUser = request.getLoginUser();

    // send request to get dbs
    String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getTablePath())
            .queryParam("database", request.getDbName()).toString();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add("Token-User", authUser);
    headers.add("Token-Code", clusterInfo.getLinkisToken());

    HttpEntity<Object> entity = new HttpEntity<>(headers);
    LOGGER.info("Start to get table by user and cluster and db by linkis. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.GET, entity);
    Map<String, Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
    LOGGER.info("Start to get table by user and cluster and db by linkis. response: {}", response);

    if (!checkResponse(response)) {
      String message = (String) response.get("message");
      throw new MetaDataAcquireFailedException("Error! Can not get meta data from linkis, exception: " + message);
    }

    List<String> allTables = ((List<Map<String, String>>)((Map<String, Object>)response.get("data")).get("tables")).stream()
            .map(o -> o.get("tableName")).collect(Collectors.toList());

    DataInfo<TableInfoDetail> dataInfo = new DataInfo<>(allTables.size());

    if (CollectionUtils.isEmpty(allTables)) {
      return dataInfo;
    }
    List<TableInfoDetail> details = new ArrayList<>();
    for (String data : allTables) {
      TableInfoDetail detail = new TableInfoDetail(data);
      details.add(detail);
    }
    dataInfo.setContent(details);
    return dataInfo;
  }

  @Override
  public DataInfo<ColumnInfoDetail> getColumnByUserAndTable(GetColumnByUserAndTableRequest request)
          throws UnExpectedRequestException, MetaDataAcquireFailedException {
    ClusterInfo clusterInfo = checkClusterNameExists(request.getClusterName());
    String authUser = request.getLoginUser();

    // send request to get dbs
    String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getColumnPath())
            .queryParam("database", request.getDbName()).queryParam("table", request.getTableName()).toString();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add("Token-User", authUser);
    headers.add("Token-Code", clusterInfo.getLinkisToken());

    HttpEntity<Object> entity = new HttpEntity<>(headers);
    LOGGER.info("Start to get column by user and cluster and db and table by linkis. url: {}, method: {}, body: {}", url,
            javax.ws.rs.HttpMethod.GET, entity);
    Map<String, Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
    LOGGER.info("Start to get table by user and cluster and and table by linkis. response: {}", response);

    if (!checkResponse(response)) {
      String message = (String) response.get("message");
      throw new MetaDataAcquireFailedException("Error! Can not get meta data from linkis, exception: " + message);
    }

    List<Map<String, String>> allTables = ((List<Map<String, String>>)((Map<String, Object>)response.get("data")).get("columns"));

    DataInfo<ColumnInfoDetail> dataInfo = new DataInfo<>(allTables.size());
    if (CollectionUtils.isEmpty(allTables)) {
      return dataInfo;
    }
    List<ColumnInfoDetail> details = new ArrayList<>();
    for (Map<String, String> table : allTables) {
      ColumnInfoDetail detail = new ColumnInfoDetail(table.get("columnName"), table.get("columnType"));
      details.add(detail);
    }
    dataInfo.setContent(details);
    return dataInfo;
  }

  private ClusterInfo checkClusterNameExists(String clusterName) throws
      UnExpectedRequestException {
    ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(clusterName);
    if (clusterInfo == null) {
      throw new UnExpectedRequestException(String.format("%s 集群名称不存在", clusterName));
    }
    return clusterInfo;
  }

  private UriBuilder getPath(String linkisAddress) {
    return UriBuilder.fromUri(linkisAddress).path(linkisConfig.getPrefix());
  }

  private boolean checkResponse(Map response) {
    Integer responseStatus = (Integer) response.get("status");
    return responseStatus == 0;
  }
}
