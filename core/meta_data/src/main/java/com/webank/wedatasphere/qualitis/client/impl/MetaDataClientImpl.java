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
import com.webank.wedatasphere.qualitis.constant.LinkisResponseKeyEnum;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import com.webank.wedatasphere.qualitis.metadata.request.GetClusterByUserRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetColumnByUserAndTableRequest;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.request.GetDbByUserAndClusterRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetTableByUserAndDbRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetUserTableByCsIdRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetUserColumnByCsRequest;
import com.webank.wedatasphere.qualitis.metadata.response.DataInfo;
import com.webank.wedatasphere.qualitis.metadata.response.cluster.ClusterInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.column.ColumnInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.db.DbInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.table.CsTableInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.table.PartitionStatisticsInfo;
import com.webank.wedatasphere.qualitis.metadata.response.table.TableInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.table.TableStatisticsInfo;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ws.rs.core.UriBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * @author howeye
 * @date 2019-04-22
 */
@Component
public class MetaDataClientImpl implements MetaDataClient {
    private final static String QUERY_CS_TABLE_PATH = "/dss/cs/tables";
    private final static String QUERY_CS_COLUMN_PATH = "/dss/cs/columns";
    private final static String QUERY_WORKFLOW_TABLE_PATH = "/dss/workflow/tables";
    private final static String QUERY_WORKFLOW_COLUMN_PATH = "/dss/workflow/columns";

    private static final String LINKIS_ONE_VERSION = "1.0";

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

        if (! checkResponse(response)) {
            String message = (String) response.get("message");
            LOGGER.error("Error! Can not get meta data from linkis, message: " + message);
            throw new MetaDataAcquireFailedException("Error! Can not get meta data from linkis, message: " + message);
        }

        List<String> allDbs = ((List<Map<String, String>>) ((Map<String, Object>) response.get("data")).get("dbs")).stream()
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
        LOGGER.info("Finished to get table by user and cluster and db by linkis. response: {}", response);

        if (! checkResponse(response)) {
            String message = (String) response.get("message");
            LOGGER.error("Error! Can not get meta data from linkis, message: " + message);
            throw new MetaDataAcquireFailedException("Error! Can not get meta data from linkis, exception: " + message);
        }

        List<String> allTables = ((List<Map<String, String>>) ((Map<String, Object>) response.get("data")).get("tables")).stream()
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
        LOGGER.info("Finished to get table by user and cluster and and table by linkis. response: {}", response);

        if (! checkResponse(response)) {
            String message = (String) response.get("message");
            LOGGER.error("Error! Can not get meta data from linkis, message: " + message);
            throw new MetaDataAcquireFailedException("Error! Can not get meta data from linkis, exception: " + message);
        }

        List<Map<String, String>> allTables = ((List<Map<String, String>>) ((Map<String, Object>) response.get("data")).get("columns"));

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

    @Override
    public String getTableBasicInfo(String clusterName, String dbName, String tableName, String userName)
        throws MetaDataAcquireFailedException, UnExpectedRequestException {
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get table comment.
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getTableInfo())
            .queryParam("database", dbName).queryParam("tableName", tableName).toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", userName);
        headers.add("Token-Code", clusterInfo.getLinkisToken());

        HttpEntity<Object> entity = new HttpEntity<>(headers);
        LOGGER.info("Start to get table comment by user and cluster and db by linkis. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.GET,
            entity);
        Map<String, Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
        LOGGER.info("Finished to get table comment by user and cluster and db by linkis. response: {}", response);

        if (! checkResponse(response)) {
            String message = (String) response.get("message");
            LOGGER.error("Error! Can not get meta data from linkis, message: " + message);
            throw new MetaDataAcquireFailedException("Error! Can not get meta data from linkis, exception: " + message);
        }
        Object result = ((Map<String, Object>) ((Map<String, Object>) ((Map<String, Object>) response.get("data")).get("tableBaseInfo")).get("base"))
            .get("comment");
        String comment = result == null ? "no comment" : result.toString();
        return comment;
    }


    @Override
    public DataInfo<CsTableInfoDetail> getTableByCsId(GetUserTableByCsIdRequest request)
        throws MetaDataAcquireFailedException, UnExpectedRequestException {
        DataInfo<CsTableInfoDetail> result = new DataInfo<>();
        List<CsTableInfoDetail> csTableInfoDetailList = new ArrayList<>();
        try {
            LOGGER.info("Start to get tables with context service ID and node name by restful API. csId: {}, nodeName: {}", request.getCsId(), request.getNodeName());
            ClusterInfo clusterInfo = checkClusterNameExists(request.getClusterName());
            String authUser = request.getLoginUser();

            // send request
            String url;
            if (clusterInfo.getClusterType().endsWith(LINKIS_ONE_VERSION)) {
                url = getPath(clusterInfo.getLinkisAddress()).path(QUERY_WORKFLOW_TABLE_PATH).toString();
            } else {
                url = getPath(clusterInfo.getLinkisAddress()).path(QUERY_CS_TABLE_PATH).toString();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Token-User", authUser);
            headers.add("Token-Code", clusterInfo.getLinkisToken());
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("contextID", request.getCsId());
                jsonObject.put("nodeName", request.getNodeName());
            } catch (JSONException e) {
                LOGGER.error(e.getMessage(), e);
                throw new UnExpectedRequestException("Failed to construct http body json with context ID and node name", 500);
            }

            HttpEntity<Object> entity = new HttpEntity<>(jsonObject.toString(), headers);
            LOGGER.info("Start to get table with context service ID and node name by restful API. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.POST, entity);
            Map<String, Object> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class).getBody();

            if (! checkResponse(response)) {
                String message = (String) response.get("message");
                LOGGER.error("Error! Can not get meta data from linkis, message: " + message);
                throw new MetaDataAcquireFailedException("Error! Can not get meta data from linkis, exception: " + message);
            }
            LOGGER.info("Finished to get table with context service ID and node name by restful API. response: {}", response);
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            List<Map<String, Object>> tables = (List<Map<String, Object>>) data.get("tables");
            if (tables == null || tables.size() == 0) {
                return result;
            }
            LOGGER.info("Successfully to get tables with context service ID and node name by restful API. csId: {}, nodeName: {}, tables: {}",
                request.getCsId(), request.getNodeName(), tables);
            for (Map<String, Object> table : tables) {
                CsTableInfoDetail csTableInfoDetail = new CsTableInfoDetail();
                csTableInfoDetail.setTableName(table.get("tableName").toString());
                csTableInfoDetail.setContextKey(table.get("contextKey").toString());
                csTableInfoDetailList.add(csTableInfoDetail);
            }
            result.setContent(csTableInfoDetailList);
            result.setTotalCount(tables.size());
        } catch (RestClientException e) {
            LOGGER.error(e.getMessage(), e);
            throw new MetaDataAcquireFailedException("Error! Can not get tables by context service ID and node name", 500);
        }
        return result;
    }

    @Override
    public List<ColumnInfoDetail> getColumnInfo(String clusterName, String dbName, String tableName, String userName) throws MetaDataAcquireFailedException, UnExpectedRequestException {
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get table comment.
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getColumnInfo())
            .queryParam("database", dbName).queryParam("tableName", tableName).toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", userName);
        headers.add("Token-Code", clusterInfo.getLinkisToken());
        HttpEntity<Object> entity = new HttpEntity<>(headers);
        LOGGER.info("Start to get column info by user and cluster and db and table by linkis. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.GET, entity);
        Map<String, Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
        LOGGER.info("Finished to get column info by user and cluster and db and table by linkis. response: {}", response);

        if (! checkResponse(response)) {
            String message = (String) response.get("message");
            LOGGER.error("Error! Can not get meta data from linkis, message: " + message);
            throw new MetaDataAcquireFailedException("Error! Can not get meta data from linkis, exception: " + message);
        }

        List<Map<String, Object>> tableFieldInfo = (List<Map<String, Object>>) ((Map<String, Object>) response.get("data")).get("tableFieldsInfo");
        List<ColumnInfoDetail> result = new ArrayList<>();
        for (Map<String, Object> map : tableFieldInfo) {
            ColumnInfoDetail columnInfoDetail = new ColumnInfoDetail();
            columnInfoDetail.setFieldName(map.get("name").toString());
            columnInfoDetail.setDataType(map.get("type").toString());
            if (map.get("length") != null && ! "".equals(map.get("length").toString())) {
                columnInfoDetail.setColumnLen(Integer.parseInt(map.get("length").toString()));
            }
            columnInfoDetail.setColumnAlias(map.get("alias") == null ? "" : map.get("alias").toString());
            columnInfoDetail.setColumnComment(map.get("comment") == null ? "" : map.get("comment").toString());
            columnInfoDetail.setPrimary((Boolean) map.get("primary"));
            columnInfoDetail.setPartitionField((Boolean) map.get("partitionField"));
            result.add(columnInfoDetail);
        }
        return result;
    }

    @Override
    public DataInfo<ColumnInfoDetail> getColumnByCsId(GetUserColumnByCsRequest request)
        throws MetaDataAcquireFailedException, UnExpectedRequestException {
        DataInfo<ColumnInfoDetail> result = new DataInfo<>();
        List<ColumnInfoDetail> list = new ArrayList<>();

        try {
            LOGGER.info("Start to get columns with context service ID and table's context key. csId: {}, contextKey: {}", request.getCsId(),
                request.getContextKey());
            ClusterInfo clusterInfo = checkClusterNameExists(request.getClusterName());
            String authUser = request.getLoginUser();
            // send request
            String url;
            if (clusterInfo.getClusterType().endsWith(LINKIS_ONE_VERSION)) {
                url = getPath(clusterInfo.getLinkisAddress()).path(QUERY_WORKFLOW_COLUMN_PATH).toString();
            } else {
                url = getPath(clusterInfo.getLinkisAddress()).path(QUERY_CS_COLUMN_PATH).toString();
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Token-User", authUser);
            headers.add("Token-Code", clusterInfo.getLinkisToken());
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("contextID", request.getCsId());
                jsonObject.put("contextKey", request.getContextKey());
            } catch (JSONException e) {
                LOGGER.error("Failed to construct http body json, exception is : {}", e);
            }

            HttpEntity<Object> entity = new HttpEntity<>(jsonObject.toString(), headers);
            LOGGER.info("Start to get column with context service ID and table's context key by restful API. url: {}, method: {}, body: {}", url,
                javax.ws.rs.HttpMethod.POST, entity);
            Map<String, Object> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class).getBody();

            if (! checkResponse(response)) {
                String message = (String) response.get("message");
                LOGGER.error("Error! Can not get meta data from linkis, message: " + message);
                throw new MetaDataAcquireFailedException("Error! Can not get meta data from linkis, exception: " + message);
            }
            LOGGER.info("Finished to get column with context service ID and table's context key by restful API. response: {}", response);
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            List<Map<String, Object>> columns = (List<Map<String, Object>>) data.get("columns");
            if (columns == null || columns.size() == 0) {
                return result;
            }
            LOGGER.info("Successfully to get columns with context service ID and table's context key by restful API. csId: {}, contextKey: {}",
                request.getCsId(), request.getContextKey());
            for (Map<String, Object> column : columns) {
                ColumnInfoDetail columnInfoDetail = new ColumnInfoDetail();
                columnInfoDetail.setFieldName(column.get("columnName").toString());
                columnInfoDetail.setDataType(column.get("columnType").toString());
                columnInfoDetail.setColumnComment(column.get("columnComment") == null ? "" : column.get("columnComment").toString());
                columnInfoDetail.setPartitionField((Boolean) column.get("partitioned"));
                list.add(columnInfoDetail);
            }
            result.setTotalCount(columns.size());
            result.setContent(list);
        } catch (RestClientException e) {
            LOGGER.error(e.getMessage(), e);
            throw new MetaDataAcquireFailedException("Error! Can not get column by context service ID", 500);
        }
        return result;
    }

    @Override
    public TableStatisticsInfo getTableStatisticsInfo(String clusterName, String dbName, String tableName, String userName)
        throws UnExpectedRequestException, MetaDataAcquireFailedException, RestClientException {
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);

        // send request to get dbs
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getTableStatistics())
            .queryParam("database", dbName)
            .queryParam("tableName", tableName).toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", userName);
        headers.add("Token-Code", clusterInfo.getLinkisToken());

        HttpEntity<Object> entity = new HttpEntity<>(headers);
        LOGGER.info("Start to get table info by linkis. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.GET, entity);
        Map<String, Object> response;
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
        } catch (ResourceAccessException e) {
            LOGGER.error(e.getMessage(), e);
            throw new MetaDataAcquireFailedException("Error! Can not get table info from linkis, exception: " + e.getMessage(), 500);
        }
        LOGGER.info("Finish to get table info by linkis. response: {}", response);

        if (! checkResponse(response)) {
            String message = (String) response.get("message");
            LOGGER.error("Error! Can not get meta data from linkis, message: " + message);
            throw new MetaDataAcquireFailedException("Error! Can not get table info from linkis, exception: " + message);
        }
        Map<String, Object> result = (Map<String, Object>) ((Map<String, Object>) response.get("data")).get("tableStatisticInfo");
        TableStatisticsInfo tableStatisticsInfo = new TableStatisticsInfo();
        tableStatisticsInfo.setTableFileCount(Integer.parseInt(result.get("fileNum").toString()));
        tableStatisticsInfo.setTableSize(result.get("tableSize").toString());
        tableStatisticsInfo.setPartitions((List<Map>) result.get("partitions"));

        return tableStatisticsInfo;
    }

    @Override
    public PartitionStatisticsInfo getPartitionStatisticsInfo(String clusterName, String dbName, String tableName, String partitionPath, String userName)
        throws UnExpectedRequestException, MetaDataAcquireFailedException, RestClientException {
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);

        // send request to get dbs
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getPartitionStatistics())
            .queryParam("database", dbName)
            .queryParam("tableName", tableName)
            .queryParam("partitionPath", partitionPath).toString();
        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage(), e);
            throw new UnExpectedRequestException("Decode get partition statistic info exception", 500);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", userName);
        headers.add("Token-Code", clusterInfo.getLinkisToken());

        HttpEntity<Object> entity = new HttpEntity<>(headers);
        LOGGER.info("Start to get partition info by linkis. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.GET, entity);
        Map<String, Object> response = null;
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
        } catch (ResourceAccessException e) {
            LOGGER.error(e.getMessage(), e);
            throw new MetaDataAcquireFailedException("Error! Can not get partition info from linkis, exception: " + e.getMessage(), 500);
        }
        LOGGER.info("Finish to get partition info by linkis. response: {}", response);

        if (! checkResponse(response)) {
            String message = (String) response.get("message");
            LOGGER.error("Error! Can not get meta data from linkis, message: " + message);
            throw new MetaDataAcquireFailedException("Error! Can not get partition info from linkis, exception: " + message);
        }
        Map<String, Object> result = (Map<String, Object>) ((Map<String, Object>) response.get("data")).get("partitionStatisticInfo");
        PartitionStatisticsInfo partitionStatisticsInfo = new PartitionStatisticsInfo();
        partitionStatisticsInfo.setPartitionChildCount(Integer.parseInt(result.get("fileNum").toString()));
        partitionStatisticsInfo.setPartitionSize(result.get("partitionSize").toString());
        partitionStatisticsInfo.setPartitions((List<Map>) result.get("childrens"));
        return partitionStatisticsInfo;
    }

    @Override
    public boolean fieldExist(String col, List<ColumnInfoDetail> cols, Map<String, String> mappingCols) {
        // single or custom
        if (StringUtils.isNotBlank(col)) {
            // single field
            if (cols == null) {
                return false;
            }
            if (col.equals(SpecCharEnum.STAR.getValue())){
                return cols != null && cols.size() > 0;
            }
            String[] colsInfo = col.split("\\|");
            int diff = colsInfo.length;
            for (String column : colsInfo) {
                for (ColumnInfoDetail columnInfoDetail : cols) {
                    String realNameWithType = columnInfoDetail.getFieldName() + ":" + columnInfoDetail.getDataType();
                    if (realNameWithType.equals(column)) {
                        diff --;
                        break;
                    }
                }

            }

            return diff == 0;
        } else {// table level check or multi
            if (mappingCols != null && mappingCols.size() > 0) {
                int diff = mappingCols.size();
                for (String colName : mappingCols.keySet()) {
                    for (ColumnInfoDetail columnInfoDetail : cols) {
                        if (columnInfoDetail.getFieldName().equals(colName) && columnInfoDetail.getDataType().equals(mappingCols.get(colName))) {
                            diff --;
                            break;
                        }
                    }
                }
                return diff == 0;
            }
            return CollectionUtils.isNotEmpty(cols);
        }
    }

    @Override
    public GeneralResponse<Map> getAllDataSourceTypes(String clusterName, String authUser) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceTypes()).toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", authUser);
        headers.add("Token-Code", clusterInfo.getLinkisToken());

        HttpEntity<Object> entity = new HttpEntity<>(headers);
        LOGGER.info("Start to get data source types by user and cluster by linkis. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.GET, entity);
        Map<String, Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
        LOGGER.info("Finish to get data source types by user and cluster by linkis. response: {}", response);
        if (! checkResponse(response)) {
            String message = (String) response.get("message");
            LOGGER.error("Error! Can not get meta data from linkis, message: " + message);
            throw new MetaDataAcquireFailedException("Error! Can not get meta data from linkis, exception: " + message);
        }
        Map data = (Map) response.get(LinkisResponseKeyEnum.DATA.getKey());
        List<Map> types = (List<Map>) data.get("type_list");

        return new GeneralResponse<>("200", "Success to get all datasource types", data);
    }

    @Override
    public GeneralResponse<Map> getDataSourceEnv(String clusterName, String authUser)
        throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceEnv()).toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", authUser);
        headers.add("Token-Code", clusterInfo.getLinkisToken());

        HttpEntity<Object> entity = new HttpEntity<>(headers);
        LOGGER.info("Start to get data source env by user and cluster by linkis. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.GET, entity);
        Map<String, Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
        LOGGER.info("Finish to get data source env by user and cluster by linkis. response: {}", response);

        if (! checkResponse(response)) {
            String message = (String) response.get("message");
            LOGGER.error("Error! Can not get meta data from linkis, message: " + message);
            throw new MetaDataAcquireFailedException("Error! Can not get meta data from linkis, exception: " + message);
        }
        Map data = (Map) response.get(LinkisResponseKeyEnum.DATA.getKey());
        List<Map> types = (List<Map>) data.get("query_list");
        return new GeneralResponse<>("200", "Success to get datasource env", data);
    }

    @Override
    public GeneralResponse<Map> getDataSourceInfoPage(String clusterName, String authUser, int page, int size, String searchName,
        Long typeId) throws UnExpectedRequestException, MetaDataAcquireFailedException, UnsupportedEncodingException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        UriBuilder uriBuilder = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceInfo())
            .queryParam("currentPage", page).queryParam("pageSize", size);
        if (StringUtils.isNotBlank(searchName)) {
            uriBuilder.queryParam("name", searchName);
        }
        if (typeId != null) {
            uriBuilder.queryParam("typeId", typeId);
        }

        String url = uriBuilder.toString();
        url = URLDecoder.decode(url, "UTF-8");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", authUser);
        headers.add("Token-Code", clusterInfo.getLinkisToken());

        HttpEntity<Object> entity = new HttpEntity<>(headers);
        LOGGER.info("Start to get data source info by user and cluster by linkis. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.GET, entity);
        Map<String, Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
        LOGGER.info("Finish to get data source info by user and cluster by linkis. response: {}", response);

        if (! checkResponse(response)) {
            String message = (String) response.get("message");
            LOGGER.error("Error! Can not get meta data from linkis, message: " + message);
            throw new MetaDataAcquireFailedException("Error! Can not get meta data from linkis, exception: " + message);
        }
        Map data = (Map) response.get(LinkisResponseKeyEnum.DATA.getKey());
        List<Map> types = (List<Map>) data.get("query_list");
        return new GeneralResponse<>("200", "Success to get datasource info", data);
    }

    @Override
    public GeneralResponse<Map> getDataSourceVersions(String clusterName, String authUser, Long dataSourceId) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceVersions()).toString().replace("{DATA_SOURCE_ID}", dataSourceId.toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", authUser);
        headers.add("Token-Code", clusterInfo.getLinkisToken());

        HttpEntity<Object> entity = new HttpEntity<>(headers);
        LOGGER.info("Start to get data source versions by user and cluster by linkis. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.GET, entity);
        Map<String, Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
        LOGGER.info("Finish to get data source versions by user and cluster by linkis. response: {}", response);

        if (! checkResponse(response)) {
            String message = (String) response.get("message");
            LOGGER.error("Error! Can not get meta data from linkis, message: " + message);
            throw new MetaDataAcquireFailedException("Error! Can not get meta data from linkis, exception: " + message);
        }
        Map data = (Map) response.get(LinkisResponseKeyEnum.DATA.getKey());
        List<Map> types = (List<Map>) data.get("versions");
        return new GeneralResponse<>("200", "Success to get datasource version", data);
    }

    @Override
    public GeneralResponse<Map> getDataSourceInfoDetail(String clusterName, String authUser, Long dataSourceId, Long versionId) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        UriBuilder uriBuilder = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceInfo()).path(dataSourceId.toString());
        if (versionId != null) {
            uriBuilder.path(versionId.toString());
        }
        String url = uriBuilder.toString();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", authUser);
        headers.add("Token-Code", clusterInfo.getLinkisToken());

        HttpEntity<Object> entity = new HttpEntity<>(headers);
        LOGGER.info("Start to get data source info detail by user and cluster by linkis. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.GET, entity);
        Map<String, Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
        LOGGER.info("Finish to get data source info detail by user and cluster by linkis. response: {}", response);

        if (! checkResponse(response)) {
            String message = (String) response.get("message");
            LOGGER.error("Error! Can not get meta data from linkis, message: " + message);
            throw new MetaDataAcquireFailedException("Error! Can not get meta data from linkis, exception: " + message);
        }
        Map data = (Map) response.get(LinkisResponseKeyEnum.DATA.getKey());
        Map types = (Map) data.get("info");
        return new GeneralResponse<>("200", "Success to get datasource detail info", data);
    }

    @Override
    public GeneralResponse<Map> getDataSourceInfoDetailByName(String clusterName, String authUser, String dataSourceName) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        UriBuilder uriBuilder = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceInfoName()).path(dataSourceName);

        String url = uriBuilder.toString();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", authUser);
        headers.add("Token-Code", clusterInfo.getLinkisToken());

        HttpEntity<Object> entity = new HttpEntity<>(headers);
        LOGGER.info("Start to get data source info detail by user and cluster and name by linkis. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.GET, entity);
        Map<String, Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
        LOGGER.info("Finish to get data source info detail by user and cluster and name by linkis. response: {}", response);

        if (! checkResponse(response)) {
            String message = (String) response.get("message");
            LOGGER.error("Error! Can not get meta data from linkis, message: " + message);
            throw new MetaDataAcquireFailedException("Error! Can not get meta data from linkis, exception: " + message);
        }
        Map data = (Map) response.get(LinkisResponseKeyEnum.DATA.getKey());
        return new GeneralResponse<>("200", "Success to get datasource info detail by datasource name", data);
    }

    @Override
    public GeneralResponse<Map> getDataSourceKeyDefine(String clusterName, String authUser, Long keyId) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceKeyDefine()).path(keyId.toString()).toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", authUser);
        headers.add("Token-Code", clusterInfo.getLinkisToken());

        HttpEntity<Object> entity = new HttpEntity<>(headers);
        LOGGER.info("Start to get data source key define by user and cluster by linkis. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.GET, entity);
        Map<String, Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
        LOGGER.info("Finish to get data source key define by user and cluster by linkis. response: {}", response);

        if (! checkResponse(response)) {
            String message = (String) response.get("message");
            LOGGER.error("Error! Can not get meta data from linkis, message: " + message);
            throw new MetaDataAcquireFailedException("Error! Can not get meta data from linkis, exception: " + message);
        }
        Map data = (Map) response.get(LinkisResponseKeyEnum.DATA.getKey());
        return new GeneralResponse<>("200", "Success to get datasource key define", data);
    }

    @Override
    public GeneralResponse<Map> connectDataSource(String clusterName, String authUser, String jsonRequest) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceConnect()).toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", authUser);
        headers.add("Token-Code", clusterInfo.getLinkisToken());

        HttpEntity<Object> entity = new HttpEntity<>(jsonRequest, headers);
        LOGGER.info("Start to connect data source by user and cluster by linkis. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.POST, entity);
        Map<String, Object> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class).getBody();
        LOGGER.info("Finish to connect data source by user and cluster by linkis. response: {}", response);

        if (! checkResponse(response)) {
            String message = (String) response.get("message");
            LOGGER.error("Error! Can not get meta data from linkis, message: " + message);
            throw new MetaDataAcquireFailedException("Error! Can not get meta data from linkis, exception: " + message);
        }
        Map data = (Map) response.get(LinkisResponseKeyEnum.DATA.getKey());
        return new GeneralResponse<>("200", "{&CONNECT_SUCCESS}", data);
    }


    @Override
    public GeneralResponse<Map> getDataSourceConnectParams(String clusterName, String authUser, Long dataSourceId, Long versionId) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        UriBuilder uriBuilder = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceConnectParam());
        if (versionId != null) {
            uriBuilder.path(versionId.toString());
        }
        String url = uriBuilder.toString().replace("{DATA_SOURCE_ID}", dataSourceId.toString());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", authUser);
        headers.add("Token-Code", clusterInfo.getLinkisToken());

        HttpEntity<Object> entity = new HttpEntity<>(headers);
        LOGGER.info("Start to get data source connect params by user and cluster by linkis. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.GET, entity);
        Map<String, Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
        LOGGER.info("Finish to get data source connect params by user and cluster by linkis. response: {}", response);

        if (! checkResponse(response)) {
            String message = (String) response.get("message");
            LOGGER.error("Error! Can not get meta data from linkis, message: " + message);
            throw new MetaDataAcquireFailedException("Error! Can not get meta data from linkis, exception: " + message);
        }
        Map data = (Map) response.get(LinkisResponseKeyEnum.DATA.getKey());

        return new GeneralResponse<>("200", "Success to get datasource connect params", data);
    }

    @Override
    public GeneralResponse<Map> publishDataSource(String clusterName, String authUser, Long dataSourceId, Long versionId) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourcePublish()).path(dataSourceId.toString()).path(versionId.toString()).toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", authUser);
        headers.add("Token-Code", clusterInfo.getLinkisToken());

        HttpEntity<Object> entity = new HttpEntity<>(headers);
        LOGGER.info("Start to publish data source by user and cluster by linkis. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.POST, entity);
        Map<String, Object> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class).getBody();
        LOGGER.info("Finish to publish data source by user and cluster by linkis. response: {}", response);

        if (! checkResponse(response)) {
            String message = (String) response.get("message");
            LOGGER.error("Error! Can not get meta data from linkis, message: " + message);
            throw new MetaDataAcquireFailedException("Error! Can not get meta data from linkis, exception: " + message);
        }
        Map data = (Map) response.get(LinkisResponseKeyEnum.DATA.getKey());
        return new GeneralResponse<>("200", "Success to publish datasource", data);
    }

    @Override
    public GeneralResponse<Map> expireDataSource(String clusterName, String authUser, Long dataSourceId) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceExpire()).toString().replace("{DATA_SOURCE_ID}", dataSourceId.toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", authUser);
        headers.add("Token-Code", clusterInfo.getLinkisToken());

        HttpEntity<Object> entity = new HttpEntity<>(headers);
        LOGGER.info("Start to expire data source by user and cluster by linkis. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.PUT, entity);
        Map<String, Object> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Map.class).getBody();
        LOGGER.info("Finish to expire data source by user and cluster by linkis. response: {}", response);

        if (! checkResponse(response)) {
            String message = (String) response.get("message");
            LOGGER.error("Error! Can not get meta data from linkis, message: " + message);
            throw new MetaDataAcquireFailedException("Error! Can not get meta data from linkis, exception: " + message);
        }
        Map data = (Map) response.get(LinkisResponseKeyEnum.DATA.getKey());
        return new GeneralResponse<>("200", "Success to expire datasource", data);
    }

    @Override
    public GeneralResponse<Map> modifyDataSource(String clusterName, String authUser, Long dataSourceId, String jsonRequest)
        throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceModify()).toString().replace("{DATA_SOURCE_ID}", dataSourceId.toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", authUser);
        headers.add("Token-Code", clusterInfo.getLinkisToken());

        HttpEntity<Object> entity = new HttpEntity<>(jsonRequest, headers);
        LOGGER.info("Start to modify data source by user and cluster by linkis. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.PUT, entity);
        Map<String, Object> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Map.class).getBody();
        LOGGER.info("Finish to modify data source by user and cluster by linkis. response: {}", response);

        if (! checkResponse(response)) {
            String message = (String) response.get("message");
            LOGGER.error("Error! Can not get meta data from linkis, message: " + message);
            throw new MetaDataAcquireFailedException("Error! Can not get meta data from linkis, exception: " + message);
        }
        Map data = (Map) response.get(LinkisResponseKeyEnum.DATA.getKey());
        return new GeneralResponse<>("200", "Success to modify datasource", data);
    }

    @Override
    public GeneralResponse<Map> modifyDataSourceParam(String clusterName, String authUser, Long dataSourceId, String jsonRequest)
        throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceInitVersion()).toString().replace("{DATA_SOURCE_ID}", dataSourceId.toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", authUser);
        headers.add("Token-Code", clusterInfo.getLinkisToken());

        HttpEntity<Object> entity = new HttpEntity<>(jsonRequest, headers);
        LOGGER.info("Start to modify data source param by user and cluster by linkis. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.POST, entity);
        Map<String, Object> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class).getBody();
        LOGGER.info("Finish to modify data source param by user and cluster by linkis. response: {}", response);

        if (! checkResponse(response)) {
            String message = (String) response.get("message");
            LOGGER.error("Error! Can not get meta data from linkis, message: " + message);
            throw new MetaDataAcquireFailedException("Error! Can not get meta data from linkis, exception: " + message);
        }
        Map data = (Map) response.get(LinkisResponseKeyEnum.DATA.getKey());
        return new GeneralResponse<>("200", "Success to modify datasource connect params", data);
    }

    @Override
    public GeneralResponse<Map> createDataSource(String clusterName, String authUser, String jsonRequest)
        throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceCreate()).toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", authUser);
        headers.add("Token-Code", clusterInfo.getLinkisToken());

        HttpEntity<Object> entity = new HttpEntity<>(jsonRequest, headers);
        LOGGER.info("Start to create data source by user and cluster by linkis. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.POST, entity);
        Map<String, Object> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class).getBody();
        LOGGER.info("Finish to create data source by user and cluster by linkis. response: {}", response);

        if (! checkResponse(response)) {
            String message = (String) response.get("message");
            LOGGER.error("Error! Can not get meta data from linkis, message: " + message);
            throw new MetaDataAcquireFailedException("Error! Can not get meta data from linkis, exception: " + message);
        }
        Map data = (Map) response.get(LinkisResponseKeyEnum.DATA.getKey());
        return new GeneralResponse<>("200", "Success to create datasource", data);
    }

    @Override
    public Map getDbsByDataSource(String clusterName, String authUser, Long dataSourceId) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceDb()).queryParam("system", "Qualitis").toString().replace("{DATA_SOURCE_ID}", dataSourceId.toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", authUser);
        headers.add("Token-Code", clusterInfo.getLinkisToken());

        HttpEntity<Object> entity = new HttpEntity<>(headers);
        LOGGER.info("Start to get dbs by data source. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.GET, entity);
        Map<String, Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
        LOGGER.info("Finish to get dbs by data source. response: {}", response);

        if (! checkResponse(response)) {
            String message = (String) response.get("message");
            LOGGER.error("Error! Can not get meta data from linkis, message: " + message);
            throw new MetaDataAcquireFailedException("Error! Can not get meta data from linkis, exception: " + message);
        }
        Map data = (Map) response.get(LinkisResponseKeyEnum.DATA.getKey());
        List<Map> dbs = (List<Map>) data.get("dbs");
        if (CollectionUtils.isEmpty(dbs)) {
            LOGGER.info("No dbs with data source to be choosed.");
        }
        return data;
    }

    @Override
    public Map getTablesByDataSource(String clusterName, String authUser, Long dataSourceId, String dbName) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceTable()).queryParam("system", "Qualitis").toString()
            .replace("{DATA_SOURCE_ID}", dataSourceId.toString())
            .replace("{DATA_SOURCE_DB}", dbName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", authUser);
        headers.add("Token-Code", clusterInfo.getLinkisToken());

        HttpEntity<Object> entity = new HttpEntity<>(headers);
        LOGGER.info("Start to get tables by data source. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.GET, entity);
        Map<String, Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
        LOGGER.info("Finish to get tables by data source. response: {}", response);

        if (! checkResponse(response)) {
            String message = (String) response.get("message");
            LOGGER.error("Error! Can not get meta data from linkis, message: " + message);
            throw new MetaDataAcquireFailedException("Error! Can not get meta data from linkis, exception: " + message);
        }
        Map data = (Map) response.get(LinkisResponseKeyEnum.DATA.getKey());
        List<Map> tables = (List<Map>) data.get("tables");
        if (CollectionUtils.isEmpty(tables)) {
            LOGGER.info("No tables with data source to be choosed.");
        }
        return data;
    }

    @Override
    public DataInfo<ColumnInfoDetail> getColumnsByDataSource(String clusterName, String authUser, Long dataSourceId, String dbName, String tableName) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceColumn()).queryParam("system", "Qualitis").toString()
            .replace("{DATA_SOURCE_ID}", dataSourceId.toString())
            .replace("{DATA_SOURCE_DB}", dbName)
            .replace("{DATA_SOURCE_TABLE}", tableName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", authUser);
        headers.add("Token-Code", clusterInfo.getLinkisToken());

        HttpEntity<Object> entity = new HttpEntity<>(headers);
        LOGGER.info("Start to get columns by data source. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.GET, entity);
        Map<String, Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
        LOGGER.info("Finish to get columns by data source. response: {}", response);

        if (! checkResponse(response)) {
            String message = (String) response.get("message");
            LOGGER.error("Error! Can not get meta data from linkis, message: " + message);
            throw new MetaDataAcquireFailedException("Error! Can not get meta data from linkis, exception: " + message);
        }
        Map data = (Map) response.get(LinkisResponseKeyEnum.DATA.getKey());
        DataInfo<ColumnInfoDetail> result = new DataInfo<>();
        List<Map> tables = (List<Map>) data.get("columns");
        if (CollectionUtils.isEmpty(tables)) {
            LOGGER.info("No columns with data source to be choosed.");
        } else {
            List<ColumnInfoDetail> columnInfoDetailList = new ArrayList<>(tables.size());
            for (Map map : tables) {
                ColumnInfoDetail columnInfoDetail = new ColumnInfoDetail();
                columnInfoDetail.setFieldName((String) map.get("name"));
                columnInfoDetail.setDataType((String) map.get("type"));
                columnInfoDetailList.add(columnInfoDetail);
            }
            result.setTotalCount(columnInfoDetailList.size());
            result.setContent(columnInfoDetailList);
        }
        return result;
    }

    private ClusterInfo checkClusterNameExists(String clusterName) throws UnExpectedRequestException {
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
