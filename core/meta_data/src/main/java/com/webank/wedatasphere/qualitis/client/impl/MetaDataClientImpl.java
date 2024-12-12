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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.webank.wedatasphere.qualitis.client.RequestLinkis;
import com.webank.wedatasphere.qualitis.client.request.AskLinkisParameter;
import com.webank.wedatasphere.qualitis.config.LinkisConfig;
import com.webank.wedatasphere.qualitis.constant.LinkisResponseKeyEnum;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.request.GetClusterByUserRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetColumnByUserAndTableRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetDbByUserAndClusterRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetTableByUserAndDbRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetUserColumnByCsRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetUserTableByCsIdRequest;
import com.webank.wedatasphere.qualitis.metadata.response.DataInfo;
import com.webank.wedatasphere.qualitis.metadata.response.cluster.ClusterInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.column.ColumnInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.datasource.LinkisDataSourceInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.db.DbInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.table.CsTableInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.table.PartitionStatisticsInfo;
import com.webank.wedatasphere.qualitis.metadata.response.table.TableInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.table.TableStatisticsInfo;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.ws.rs.core.UriBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * @author howeye
 * @date 2019-04-22
 */
@Component
public class MetaDataClientImpl implements MetaDataClient {
    private final static String QUERY_WORKFLOW_TABLE_PATH = "/dss/workflow/tables";
    private final static String QUERY_WORKFLOW_COLUMN_PATH = "/dss/workflow/columns";

    private static final String STATUS = "status";
    private static final String COLUMNS = "columns";
    private static final String INFO = "info";

    @Autowired
    private ClusterInfoDao clusterInfoDao;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LinkisConfig linkisConfig;

    @Autowired
    private RequestLinkis requestLinkis;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final Logger LOGGER = LoggerFactory.getLogger(MetaDataClientImpl.class);

    /**
     * key: cluster name, value: cluster object
     */
    private Cache<String, ClusterInfo> clusterInfoCache = CacheBuilder.newBuilder()
        .expireAfterAccess(5, TimeUnit.MINUTES)
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .build();

    @Override
    public DataInfo<ClusterInfoDetail> getClusterByUser(GetClusterByUserRequest request) {
        Long total = clusterInfoDao.countAll();
        List<ClusterInfo> allCluster = clusterInfoDao.findAllClusterInfo(request.getStartIndex(), request.getPageSize());

        DataInfo<ClusterInfoDetail> dataInfo = new DataInfo<>();
        if (CollectionUtils.isEmpty(allCluster)) {
            return dataInfo;
        }
        // Remove datasource cluster
        allCluster = allCluster.stream().filter(clusterInfo -> ! clusterInfo.getClusterName().equals(linkisConfig.getDatasourceCluster())).collect(Collectors.toList());
        total -= 1;

        List<ClusterInfoDetail> details = new ArrayList<>();
        for (ClusterInfo clusterInfo : allCluster) {
            ClusterInfoDetail detail = new ClusterInfoDetail(clusterInfo.getClusterName());
            details.add(detail);
        }
        dataInfo.setTotalCount(total.intValue());
        dataInfo.setContent(details);
        return dataInfo;
    }

    @Override
    public DataInfo<DbInfoDetail> getDbByUserAndCluster(GetDbByUserAndClusterRequest request)
            throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(request.getClusterName());
        String authUser = request.getLoginUser();

        // send request to get dbs
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDbPath()).toString();

        Map<String, Object> response = gainResponseLinkisByGet(clusterInfo, authUser, url, "get db by user and cluster by linkis.");

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

        Map<String, Object> response = gainResponseLinkisByGet(clusterInfo, authUser, url, "get table by user and cluster and db by linkis.");

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

        Map<String, Object> response = gainResponseLinkisByGet(clusterInfo, authUser, url, "get column by user and cluster and db and table by linkis.");

        Map<String, Object> dataMap = (Map<String, Object>) response.get("data");
        if (!dataMap.containsKey(COLUMNS) || Objects.isNull(dataMap.get(COLUMNS))) {
            return new DataInfo<>();
        }
        List<Map<String, String>> allTables = ((List<Map<String, String>>) dataMap.get(COLUMNS));

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

    private Map<String, Object> gainResponseLinkisByGet(ClusterInfo clusterInfo, String authUser, String url, String logMessage) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        return requestLinkis.getLinkisResponseByGet(new AskLinkisParameter(url, clusterInfo.getLinkisToken(), authUser, logMessage));
    }

    private Map<String, Object> gainResponseLinkisByDelete(ClusterInfo clusterInfo, String authUser, String url, String logMessage) throws MetaDataAcquireFailedException {
        return requestLinkis.removeLinkisResponseByDelete(new AskLinkisParameter(url, clusterInfo.getLinkisToken(), authUser, logMessage));
    }

    private Map<String, Object> gainResponseLinkisByGetRetry(ClusterInfo clusterInfo, String authUser, String url, String logMessage) throws Exception {
        return requestLinkis.getLinkisResponseByGetRetry(new AskLinkisParameter(url, clusterInfo.getLinkisToken(), authUser, logMessage));
    }

    private Map<String, Object> gainResponseLinkisByPost(ClusterInfo clusterInfo, String authUser, String url, String logMessage) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        return requestLinkis.getLinkisResponseByPost(new AskLinkisParameter(url, clusterInfo.getLinkisToken(), authUser, logMessage));
    }

    private Map<String, Object> gainResponseLinkisByPostBringJson(ClusterInfo clusterInfo, String authUser, String url, String logMessage, JSONObject jsonObject) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        return requestLinkis.getLinkisResponseByPostBringJson(new AskLinkisParameter(url, clusterInfo.getLinkisToken(), authUser, logMessage), jsonObject);
    }

    private Map<String, Object> gainResponseLinkisByPostBringJsonArray(ClusterInfo clusterInfo, String authUser, String url, String logMessage, JSONArray jsonArray) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        return requestLinkis.getLinkisResponseByPostBringJsonArray(new AskLinkisParameter(url, clusterInfo.getLinkisToken(), authUser, logMessage), jsonArray);
    }

    private Map<String, Object> gainResponseLinkisByPostBringJsonRetry(ClusterInfo clusterInfo, String authUser, String url, String logMessage, JSONObject jsonObject) throws Exception {
        return requestLinkis.getLinkisResponseByPostBringJsonRetry(new AskLinkisParameter(url, clusterInfo.getLinkisToken(), authUser, logMessage), jsonObject);
    }

    private Map<String, Object> gainResponseLinkisByPut(ClusterInfo clusterInfo, String authUser, String url, String logMessage) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        return requestLinkis.getLinkisResponseByPut(new AskLinkisParameter(url, clusterInfo.getLinkisToken(), authUser, logMessage));
    }

    private Map<String, Object> gainResponseLinkisByPutBringJson(ClusterInfo clusterInfo, String authUser, String url, String logMessage, JSONObject jsonObject) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        return requestLinkis.getLinkisResponseByPutBringJson(new AskLinkisParameter(url, clusterInfo.getLinkisToken(), authUser, logMessage),jsonObject);
    }

    private Map<String, Object> gainResponseLinkisByPutBringJsonArray(ClusterInfo clusterInfo, String authUser, String url, String logMessage, JSONArray jsonArray) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        return requestLinkis.getLinkisResponseByPutBringJsonArray(new AskLinkisParameter(url, clusterInfo.getLinkisToken(), authUser, logMessage), jsonArray);
    }


    @Override
    public String getTableBasicInfo(String clusterName, String dbName, String tableName, String userName)
            throws MetaDataAcquireFailedException, UnExpectedRequestException {
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get table comment.
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getTableInfo())
                .queryParam("database", dbName).queryParam("tableName", tableName).toString();

        Map<String, Object> response = gainResponseLinkisByGet(clusterInfo, userName, url, "get table comment by user and cluster and db by linkis.");
        Object result = ((Map<String, Object>) ((Map<String, Object>) ((Map<String, Object>) response.get("data")).get("tableBaseInfo")).get("base"))
                .get("comment");
        String comment = result == null ? "no comment" : result.toString();
        return comment;
    }


    @Override
    public DataInfo<CsTableInfoDetail> getTableByCsId(GetUserTableByCsIdRequest request)
            throws Exception {
        DataInfo<CsTableInfoDetail> result = new DataInfo<>();
        List<CsTableInfoDetail> csTableInfoDetailList = new ArrayList<>();
        try {
            LOGGER.info("Start to get tables with context service ID and node name by restful API. csId: {}, nodeName: {}", request.getCsId(), request.getNodeName());
            ClusterInfo clusterInfo = checkClusterNameExists(request.getClusterName());
            String authUser = request.getLoginUser();

            // send request
            String url = getPath(clusterInfo.getLinkisAddress()).path(QUERY_WORKFLOW_TABLE_PATH).toString();


            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("contextID", request.getCsId());
                jsonObject.put("nodeName", request.getNodeName());
            } catch (JSONException e) {
                LOGGER.error(e.getMessage(), e);
                throw new UnExpectedRequestException("Failed to construct http body json with context ID and node name", 500);
            }
            // Retry
            Map<String, Object> response = gainResponseLinkisByPostBringJsonRetry(clusterInfo, authUser, url, "get table with context service ID and node name by restful API.",jsonObject);
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
    public List<ColumnInfoDetail> getColumnInfo(String clusterName, String dbName, String tableName, String userName) throws Exception {
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get table comment.
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getColumnInfo())
                .queryParam("database", dbName).queryParam("tableName", tableName).toString();
        // Retry
        Map<String, Object> response = gainResponseLinkisByGetRetry(clusterInfo, userName, url, "get column info by user and cluster and db and table by linkis.");

        List<Map<String, Object>> tableFieldInfo = (List<Map<String, Object>>) ((Map<String, Object>) response.get("data")).get("tableFieldsInfo");
        List<ColumnInfoDetail> result = new ArrayList<>();
        for (Map<String, Object> map : tableFieldInfo) {
            ColumnInfoDetail columnInfoDetail = new ColumnInfoDetail();
            columnInfoDetail.setFieldName(map.get("name").toString());
            columnInfoDetail.setDataType(map.get("type").toString());
            if (map.get("length") != null && !"".equals(map.get("length").toString())) {
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
            throws Exception {
        DataInfo<ColumnInfoDetail> result = new DataInfo<>();
        List<ColumnInfoDetail> list = new ArrayList<>();

        try {
            LOGGER.info("Start to get columns with context service ID and table's context key. csId: {}, contextKey: {}", request.getCsId(),
                    request.getContextKey());
            ClusterInfo clusterInfo = checkClusterNameExists(request.getClusterName());
            String authUser = request.getLoginUser();
            // send request
            String url = getPath(clusterInfo.getLinkisAddress()).path(QUERY_WORKFLOW_COLUMN_PATH).toString();

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("contextID", request.getCsId());
                jsonObject.put("contextKey", request.getContextKey());
            } catch (JSONException e) {
                LOGGER.error("Failed to construct http body json, exception is : {}", e);
            }
            Map<String, Object> response = gainResponseLinkisByPostBringJsonRetry(clusterInfo, authUser, url, "get column with context service ID and table's context key by restful API.", jsonObject);

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
            throws Exception {
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);

        // send request to get dbs
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getTableStatistics())
                .queryParam("database", dbName)
                .queryParam("tableName", tableName).toString();

        Map<String, Object> response = gainResponseLinkisByGetRetry(clusterInfo, userName, url, "get table info by linkis");
        Map<String, Object> result = (Map<String, Object>) ((Map<String, Object>) response.get("data")).get("tableStatisticInfo");
        TableStatisticsInfo tableStatisticsInfo = new TableStatisticsInfo();
        tableStatisticsInfo.setTableFileCount(Integer.parseInt(result.get("fileNum").toString()));
        tableStatisticsInfo.setTableSize(result.get("tableSize").toString());
        tableStatisticsInfo.setPartitions((List<Map<String, Object>>) result.get("partitions"));

        return tableStatisticsInfo;
    }

    @Override
    public PartitionStatisticsInfo getPartitionStatisticsInfo(String clusterName, String dbName, String tableName, String partitionPath, String userName)
            throws Exception {
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

        Map<String, Object> response = gainResponseLinkisByGetRetry(clusterInfo, userName, url, "get partition info by linkis.");
        Map<String, Object> result = (Map<String, Object>) ((Map<String, Object>) response.get("data")).get("partitionStatisticInfo");
        PartitionStatisticsInfo partitionStatisticsInfo = new PartitionStatisticsInfo();
        partitionStatisticsInfo.setPartitionChildCount(Integer.parseInt(result.get("fileNum").toString()));
        partitionStatisticsInfo.setModificationTime((Long) result.get("modificationTime"));
        partitionStatisticsInfo.setPartitionSize(result.get("partitionSize").toString());
        partitionStatisticsInfo.setPartitions((List<Map<String, Object>>) result.get("childrens"));
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
            if (col.equals(SpecCharEnum.STAR.getValue())) {
                return cols.size() > 0;
            }
            String[] colsInfo = col.split("\\|");
            int diff = colsInfo.length;
            for (String column : colsInfo) {
                for (ColumnInfoDetail columnInfoDetail : cols) {
                    String realNameWithType = columnInfoDetail.getFieldName() + ":" + columnInfoDetail.getDataType();
                    if (realNameWithType.equals(column)) {
                        diff--;
                        break;
                    }
                }

            }

            return diff == 0;
        } else {// table level check or multi
            if (mappingCols != null && mappingCols.size() > 0) {
                int diff = mappingCols.size();
                for (Map.Entry<String,String> entry : mappingCols.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    for (ColumnInfoDetail columnInfoDetail : cols) {
                        if (columnInfoDetail.getFieldName().equals(key) && columnInfoDetail.getDataType().equals(value)) {
                            diff--;
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
    public GeneralResponse<Map<String, Object>> getAllDataSourceTypes(String clusterName, String authUser) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceTypes()).toString();

        Map<String, Object> response = gainResponseLinkisByGet(clusterInfo, authUser, url, "get data source types by user and cluster by linkis.");

        Map<String, Object> data = (Map<String, Object>) response.get(LinkisResponseKeyEnum.DATA.getKey());

        return new GeneralResponse<>("200", "Success to get all datasource types", data);
    }

    @Override
    public GeneralResponse<Map<String, Object>> getDataSourceEnv(String clusterName, String authUser)
            throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceEnv()).toString();

        Map<String, Object> response = gainResponseLinkisByGet(clusterInfo, authUser, url, "get data source env by user and cluster by linkis.");

        Map<String, Object> data = (Map<String, Object>) response.get(LinkisResponseKeyEnum.DATA.getKey());
        return new GeneralResponse<>("200", "Success to get datasource env", data);
    }

    @Override
    public GeneralResponse<Map<String, Object>> createDataSourceEnvBatch(String clusterName, String authUser, String createSystem, String datasourceEnvs) throws UnExpectedRequestException, MetaDataAcquireFailedException, JSONException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceEnvCreateBatch())
                .queryParam("system", "Qualitis")
                .toString();
        Map<String, Object> response = gainResponseLinkisByPostBringJsonArray(clusterInfo, authUser, url, "batch create data source env param by user and cluster by linkis."
                , new JSONArray(datasourceEnvs));
        Map<String, Object> data = (Map<String, Object>) response.get(LinkisResponseKeyEnum.DATA.getKey());
        return new GeneralResponse<>("200", "Success to create datasource env connect params", data);
    }

    @Override
    public GeneralResponse<Map<String, Object>> modifyDataSourceEnvBatch(String clusterName, String authUser, String createSystem, String datasourceEnvs) throws UnExpectedRequestException, MetaDataAcquireFailedException, JSONException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceEnvModifyBatch())
                .queryParam("system", "Qualitis")
                .toString();

        Map<String, Object> response = gainResponseLinkisByPutBringJsonArray(clusterInfo, authUser, url, "modify data source env by user and cluster by linkis."
                ,new JSONArray(datasourceEnvs));
        Map<String, Object> data = (Map<String, Object>) response.get(LinkisResponseKeyEnum.DATA.getKey());
        return new GeneralResponse<>("200", "Success to modify datasource", data);
    }

    @Override
    public GeneralResponse<Map<String, Object>> getDatasourceEnvById(String clusterName, String authUser, Long envId) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceEnvDetail()).toString()
                .replace("{ENV_ID}", envId.toString());

        Map<String, Object> response = gainResponseLinkisByGet(clusterInfo, authUser, url, "get data source env by user and cluster by linkis.");

        Map<String, Object> data = (Map<String, Object>) response.get(LinkisResponseKeyEnum.DATA.getKey());
        return new GeneralResponse<>("200", "Success to get datasource version", data);
    }

    @Override
    public GeneralResponse<Map<String, Object>> getDataSourceInfoPage(String clusterName, String authUser, int page, int size, String searchName,
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

        Map<String, Object> response = gainResponseLinkisByGet(clusterInfo, authUser, url, "get data source info by user and cluster by linkis.");

        Map<String, Object> data = (Map<String, Object>) response.get(LinkisResponseKeyEnum.DATA.getKey());
        return new GeneralResponse<>("200", "Success to get datasource info", data);
    }

    @Override
    public GeneralResponse<Map<String, Object>> getDataSourceInfoByIds(String clusterName, String userName, List<Long> dataSourceIds) throws UnExpectedRequestException, MetaDataAcquireFailedException, IOException {
        if (CollectionUtils.isEmpty(dataSourceIds)) {
            return new GeneralResponse<>("200", "Success to get datasource info by ids", null);
        }
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        UriBuilder uriBuilder = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceInfoIds())
                .queryParam("ids", new ObjectMapper().writeValueAsString(dataSourceIds));
        String url = uriBuilder.toString();
        url = URLDecoder.decode(url, "UTF-8");
        Map<String, Object> response = gainResponseLinkisByGet(clusterInfo, userName, url, "get data source info by user and cluster by linkis.");

        Map<String, Object> data = (Map<String, Object>) response.get(LinkisResponseKeyEnum.DATA.getKey());
        return new GeneralResponse<>("200", "Success to get datasource info by ids", data);
    }

    @Override
    public GeneralResponse<Map<String, Object>> getDataSourceVersions(String clusterName, String authUser, Long dataSourceId) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceVersions()).toString().replace("{DATA_SOURCE_ID}", dataSourceId.toString());

        Map<String, Object> response = gainResponseLinkisByGet(clusterInfo, authUser, url, "get data source versions by user and cluster by linkis.");

        Map<String, Object> data = (Map<String, Object>) response.get(LinkisResponseKeyEnum.DATA.getKey());
        return new GeneralResponse<>("200", "Success to get datasource version", data);
    }

    @Override
    public GeneralResponse<Map<String, Object>> getDataSourceInfoDetail(String clusterName, String authUser, Long dataSourceId, Long versionId) throws Exception {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        UriBuilder uriBuilder = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceInfo()).path(dataSourceId.toString());
        if (versionId != null) {
            uriBuilder.path(versionId.toString());
        }
        String url = uriBuilder.toString();
        Map<String, Object> response = gainResponseLinkisByGetRetry(clusterInfo, authUser, url, "get data source info detail by user and cluster by linkis.");

        Map<String, Object> data = (Map<String, Object>) response.get(LinkisResponseKeyEnum.DATA.getKey());
        return new GeneralResponse<>("200", "Success to get datasource detail info", data);
    }

    @Override
    public GeneralResponse<Map<String, Object>> getDataSourceInfoDetailByName(String clusterName, String authUser, String dataSourceName) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        UriBuilder uriBuilder = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceInfoName()).path(dataSourceName);

        String url = uriBuilder.toString();
        Map<String, Object> response = gainResponseLinkisByGet(clusterInfo, authUser, url, "get data source info detail by user and cluster and name by linkis.");

        Map<String, Object> data = (Map<String, Object>) response.get(LinkisResponseKeyEnum.DATA.getKey());
        return new GeneralResponse<>("200", "Success to get datasource info detail by datasource name", data);
    }

    @Override
    public GeneralResponse<Map<String, Object>> getDataSourceKeyDefine(String clusterName, String authUser, Long keyId) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceKeyDefine()).path(keyId.toString()).toString();

        Map<String, Object> response = gainResponseLinkisByGet(clusterInfo, authUser, url, "get data source key define by user and cluster by linkis.");

        Map<String, Object> data = (Map<String, Object>) response.get(LinkisResponseKeyEnum.DATA.getKey());
        return new GeneralResponse<>("200", "Success to get datasource key define", data);
    }

    @Override
    public GeneralResponse<Map<String, Object>> connectDataSource(String clusterName, String authUser, String jsonRequest) throws UnExpectedRequestException, MetaDataAcquireFailedException, JSONException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceConnect()).toString();

        Map<String, Object> response = gainResponseLinkisByPostBringJson(clusterInfo, authUser, url, "connect data source by user and cluster by linkis.",new JSONObject(jsonRequest));

        Map<String, Object> data = (Map<String, Object>) response.get(LinkisResponseKeyEnum.DATA.getKey());
        return new GeneralResponse<>("200", "{&CONNECT_SUCCESS}", data);
    }


    @Override
    public GeneralResponse<Map<String, Object>> getDataSourceConnectParams(String clusterName, String authUser, Long dataSourceId, Long versionId) throws Exception {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        UriBuilder uriBuilder = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceConnectParam());
        if (versionId != null) {
            uriBuilder.path(versionId.toString());
        }
        String url = uriBuilder.toString().replace("{DATA_SOURCE_ID}", dataSourceId.toString());
        Map<String, Object> response = gainResponseLinkisByGetRetry(clusterInfo, authUser, url, "get data source connect params by user and cluster by linkis.");

        Map<String, Object> data = (Map<String, Object>) response.get(LinkisResponseKeyEnum.DATA.getKey());

        return new GeneralResponse<>("200", "Success to get datasource connect params", data);
    }

    @Override
    public GeneralResponse<Map<String, Object>> publishDataSource(String clusterName, String authUser, Long dataSourceId, Long versionId) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourcePublish()).path(dataSourceId.toString()).path(versionId.toString()).toString();

        Map<String, Object> response = gainResponseLinkisByPost(clusterInfo, authUser, url, "publish data source by user and cluster by linkis.");

        Map<String, Object> data = (Map<String, Object>) response.get(LinkisResponseKeyEnum.DATA.getKey());
        return new GeneralResponse<>("200", "Success to publish datasource", data);
    }

    @Override
    public GeneralResponse<Map<String, Object>> expireDataSource(String clusterName, String authUser, Long dataSourceId) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceExpire()).toString().replace("{DATA_SOURCE_ID}", dataSourceId.toString());

        Map<String, Object> response = gainResponseLinkisByPut(clusterInfo, authUser, url, "expire data source by user and cluster by linkis.");

        Map<String, Object> data = (Map<String, Object>) response.get(LinkisResponseKeyEnum.DATA.getKey());
        return new GeneralResponse<>("200", "Success to expire datasource", data);
    }

    @Override
    public GeneralResponse<Map<String, Object>> modifyDataSource(String clusterName, String authUser, Long dataSourceId, String jsonRequest)
            throws UnExpectedRequestException, MetaDataAcquireFailedException, JSONException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceModify()).toString().replace("{DATA_SOURCE_ID}", dataSourceId.toString());

        Map<String, Object> response = gainResponseLinkisByPutBringJson(clusterInfo, authUser, url, "modify data source by user and cluster by linkis.",new JSONObject(jsonRequest));
        Map<String, Object> data = (Map<String, Object>) response.get(LinkisResponseKeyEnum.DATA.getKey());
        return new GeneralResponse<>("200", "Success to modify datasource", data);
    }

    @Override
    public GeneralResponse<Map<String, Object>> modifyDataSourceParam(String clusterName, String authUser, Long dataSourceId, String jsonRequest)
            throws UnExpectedRequestException, MetaDataAcquireFailedException, JSONException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceInitVersion()).toString().replace("{DATA_SOURCE_ID}", dataSourceId.toString());

        Map<String, Object> response = gainResponseLinkisByPostBringJson(clusterInfo, authUser, url, "modify data source param by user and cluster by linkis.",new JSONObject(jsonRequest));

        Map<String, Object> data = (Map<String, Object>) response.get(LinkisResponseKeyEnum.DATA.getKey());
        return new GeneralResponse<>("200", "Success to modify datasource connect params", data);
    }

    @Override
    public GeneralResponse<Map<String, Object>> createDataSource(String clusterName, String authUser, String jsonRequest)
            throws UnExpectedRequestException, MetaDataAcquireFailedException, JSONException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceCreate()).toString();

        Map<String, Object> response = gainResponseLinkisByPostBringJson(clusterInfo, authUser, url, "create data source by user and cluster by linkis.", new JSONObject(jsonRequest));
        Map<String, Object> data = (Map<String, Object>) response.get(LinkisResponseKeyEnum.DATA.getKey());
        return new GeneralResponse<>("200", "Success to create datasource", data);
    }

    @Override
    public GeneralResponse<Map<String, Object>> deleteDataSource(String clusterName, String userName, Long dataSourceId) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceDelete()).toString().replace("{DATA_SOURCE_ID}", dataSourceId.toString());
        Map<String, Object> response = gainResponseLinkisByDelete(clusterInfo, userName, url, "delete data source by user and cluster by linkis.");

        Map<String, Object> data = (Map<String, Object>) response.get(LinkisResponseKeyEnum.DATA.getKey());
        return new GeneralResponse<>("200", "Success to delete datasource", data);
    }

    @Override
    public Map<String, Object> getDbsByDataSourceName(String clusterName, String authUser, String dataSourceName, Long envId) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        UriBuilder url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceQueryDb()).queryParam("system", "Qualitis")
                .queryParam("dataSourceName", dataSourceName);
        if (Objects.nonNull(envId)) {
            url.queryParam("envId", envId);
        }
        Map<String, Object> response = gainResponseLinkisByGet(clusterInfo, authUser, url.toString(), "get dbs by data source.");

        Map<String, Object> data = (Map<String, Object>) response.get(LinkisResponseKeyEnum.DATA.getKey());
        List<Map<String, Object>> dbs = (List<Map<String, Object>>) data.get("dbs");
        if (CollectionUtils.isEmpty(dbs)) {
            LOGGER.info("No dbs with data source to be choosed.");
        }
        return data;
    }

    @Override
    public Map<String, Object> getTablesByDataSourceName(String clusterName, String authUser, String dataSourceName, String dbName, Long envId) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        UriBuilder url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceQueryTable())
                .queryParam("system", "Qualitis")
                .queryParam("dataSourceName", dataSourceName)
                .queryParam("database", dbName);
        if (Objects.nonNull(envId)) {
            url.queryParam("envId", envId);
        }

        Map<String, Object> response = gainResponseLinkisByGet(clusterInfo, authUser, url.toString(), "get tables by data source.");

        Map<String, Object> data = (Map<String, Object>) response.get(LinkisResponseKeyEnum.DATA.getKey());
        List<Map<String, Object>> tables = (List<Map<String, Object>>) data.get("tables");
        if (CollectionUtils.isEmpty(tables)) {
            LOGGER.info("No tables with data source to be choosed.");
        }
        return data;
    }

    @Override
    public DataInfo<ColumnInfoDetail> getColumnsByDataSource(String clusterName, String authUser, Long dataSourceId, String dbName, String tableName) throws Exception {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceColumn()).queryParam("system", "Qualitis").toString()
                .replace("{DATA_SOURCE_ID}", dataSourceId.toString())
                .replace("{DATA_SOURCE_DB}", dbName)
                .replace("{DATA_SOURCE_TABLE}", tableName);

        Map<String, Object> response = gainResponseLinkisByGetRetry(clusterInfo, authUser, url, "get columns by data source.");

        Map<String, Object> data = (Map) response.get(LinkisResponseKeyEnum.DATA.getKey());
        DataInfo<ColumnInfoDetail> result = new DataInfo<>();
        List<Map<String, Object>> tables = (List<Map<String, Object>>) data.get("columns");
        if (CollectionUtils.isEmpty(tables)) {
            LOGGER.info("No columns with data source to be choosed.");
        } else {
            List<ColumnInfoDetail> columnInfoDetailList = new ArrayList<>(tables.size());
            for (Map<String, Object> map : tables) {
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

    @Override
    public DataInfo<ColumnInfoDetail> getColumnsByDataSourceName(String clusterName, String authUser, String dataSourceName, String dbName, String tableName, Long envId) throws Exception {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        UriBuilder url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDatasourceQueryColumn())
                .queryParam("system", "Qualitis")
                .queryParam("dataSourceName", dataSourceName)
                .queryParam("database", dbName)
                .queryParam("table", tableName);
        if (Objects.nonNull(envId)) {
            url.queryParam("envId", envId);
        }

        Map<String, Object> response = gainResponseLinkisByGetRetry(clusterInfo, authUser, url.toString(), "get columns by data source.");

        Map<String, Object> data = (Map<String, Object>) response.get(LinkisResponseKeyEnum.DATA.getKey());
        DataInfo<ColumnInfoDetail> result = new DataInfo<>();
        List<Map<String, Object>> tables = (List<Map<String, Object>>) data.get("columns");
        if (CollectionUtils.isEmpty(tables)) {
            LOGGER.info("No columns with data source to be choosed.");
        } else {
            List<ColumnInfoDetail> columnInfoDetailList = new ArrayList<>(tables.size());
            for (Map<String, Object> map : tables) {
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

    @Override
    public int getUndoneTaskTotal(String clusterName, String executionUser) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        long curSysTime = System.currentTimeMillis();

        long deadtime = curSysTime - linkisConfig.getUnDoneDays()*24*60*60*1000L;

        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getUnDone())
            .queryParam("engineType", linkisConfig.getEngineName())
            .queryParam("creator", linkisConfig.getAppName())
            .queryParam("startDate", deadtime)
            .queryParam("endDate", curSysTime)
            .toString();

        Map<String, Object> response = gainResponseLinkisByGet(clusterInfo, executionUser, url, "get undone task total.");

        Integer undoneTaskTotal = ((Integer) ((Map<String, Object>) response.get("data")).get("totalPage"));

        if (undoneTaskTotal != null) {
            LOGGER.info("Curent undone task num: " + undoneTaskTotal.intValue());
            return undoneTaskTotal.intValue();
        }
        return Integer.parseInt("0");
    }

    @Override
    public LinkisDataSourceInfoDetail getDataSourceInfoById(String clusterName, String userName, Long dataSourceId) throws Exception {
        GeneralResponse<Map<String, Object>> generalResponse = getDataSourceInfoDetail(clusterName, userName, dataSourceId, null);
        if (MapUtils.isEmpty(generalResponse.getData()) || !generalResponse.getData().containsKey(INFO)) {
            throw new MetaDataAcquireFailedException("Failed to acquire data source by id");
        }
        Map<String, Object> infoMap = (Map<String, Object>) generalResponse.getData().get("info");
        ObjectMapper objectMapper = new ObjectMapper();
        String infoJson = objectMapper.writeValueAsString(infoMap);
        return objectMapper.readValue(infoJson, LinkisDataSourceInfoDetail.class);
    }

    @Override
    public Long addUdf(String currentCluster, String userName, Map<String, Object> requestBody) throws UnExpectedRequestException, IOException, JSONException, MetaDataAcquireFailedException {
        ClusterInfo clusterInfo = checkClusterNameExists(currentCluster);
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getUdfAdd()).toString();
        Map<String, Object> response = gainResponseLinkisByPostBringJson(clusterInfo, userName, url, "add udf with linkis api", new JSONObject(OBJECT_MAPPER.writeValueAsString(requestBody)));

        if (checkResponse(response)) {
            // Get real ID
            return ((Integer) ((Map) response.get("data")).get("udfId")).longValue();
        }
        return null;
    }

    @Override
    public void modifyUdf(String currentCluster, String userName, Map<String, Object> requestBody) throws UnExpectedRequestException, IOException, JSONException, MetaDataAcquireFailedException {
        ClusterInfo clusterInfo = checkClusterNameExists(currentCluster);
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getUdfModify()).toString();
        Map<String, Object> response = gainResponseLinkisByPostBringJson(clusterInfo, userName, url, "modify udf with linkis api", new JSONObject(OBJECT_MAPPER.writeValueAsString(requestBody)));

        if (checkResponse(response)) {
            LOGGER.info("");
        }
        LOGGER.info("");
    }

    @Override
    public String checkFilePathExistsAndUploadToWorkspace(String currentCluster, String userName, File uploadFile, Boolean needUpload) throws UnExpectedRequestException, MetaDataAcquireFailedException, IOException, JSONException {
        ClusterInfo clusterInfo = checkClusterNameExists(currentCluster);
        String targetFilePath = new StringBuffer(linkisConfig.getUploadWorkspacePrefix())
            .append(File.separator).append(userName).append(File.separator).append("qualitis").toString();
        String getPathUrl = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getUploadDir()).queryParam("path", targetFilePath).toString();
        try {
            getPathUrl = URLDecoder.decode(getPathUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage(), e);
            throw new UnExpectedRequestException("Decode get path url exception", 500);
        }
        Map<String, Object> responseMap = gainResponseLinkisByGet(clusterInfo, userName, getPathUrl, "Check file path.");

        Map<String, Object> dirFileTrees = (Map) ((Map) responseMap.get("data")).get("dirFileTrees");
        if (dirFileTrees == null) {
            Map<String, Object> requestBody = Maps.newHashMapWithExpectedSize(1);
            requestBody.put("path", targetFilePath);
            String createPathUrl = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getUploadCreateDir()).toString();

            Map<String, Object> createResponse = gainResponseLinkisByPostBringJson(clusterInfo, userName, createPathUrl, "Create file path.", new JSONObject(OBJECT_MAPPER.writeValueAsString(requestBody)));
            if (checkResponse(createResponse)) {
                LOGGER.info("Succeed to create file path.");
            } else {
                throw new UnExpectedRequestException("Failed to create file path.");
            }
        }

        if (! needUpload) {
            return targetFilePath;
        }

        // Upload
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getUpload()).toString();
        HttpPost httppost = new HttpPost(url);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        multipartEntityBuilder.setContentType(ContentType.MULTIPART_FORM_DATA);
        multipartEntityBuilder.setCharset(Charset.forName("UTF-8"));
        multipartEntityBuilder.addBinaryBody("file", uploadFile);
        multipartEntityBuilder.addTextBody("path", "file://" + targetFilePath);

        httppost.addHeader("Token-User", userName);
        httppost.addHeader("Token-Code", clusterInfo.getLinkisToken());

        httppost.setEntity(multipartEntityBuilder.build());
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
            int code = response.getStatusLine().getStatusCode();
            if (code != HttpStatus.SC_OK) {
                throw new UnExpectedRequestException("{&FAILED_TO_CALL_UPLOAD_API}");
            }
        } catch (IOException e) {
            throw new UnExpectedRequestException("{&FAILED_TO_CALL_UPLOAD_API}");
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }

            try {
                httpclient.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        return targetFilePath;
    }

    @Override
    public Long clientAdd(String currentCluster, String targetFilePath, File uploadFile, String fileName, String udfDesc, String udfName, String returnType
        , String enter, String registerName, Boolean status, String dir) throws MetaDataAcquireFailedException, UnExpectedRequestException, JSONException, IOException {
        boolean commonJar = fileName.endsWith(".jar");
        boolean pythonScript = fileName.endsWith(".py");

        Map<String, Object> requestBody = new HashMap<>(1);
        Map<String, Object> requestInBody = new HashMap<>(10);
        requestInBody.put("path", targetFilePath + File.separator + uploadFile.getName());
        requestInBody.put("description", udfDesc);
        requestInBody.put("udfName", udfName);

        // Support script type. 1 for spark python, 2 for spark scala.
        if (commonJar) {
            requestInBody.put("udfType", 0);
            requestInBody.put("useFormat", returnType + " " + udfName + "(" + enter + ")");
            requestInBody.put("registerFormat", "create temporary function " + udfName + " as \"" + registerName + "\"");
        } else if (pythonScript) {
            requestInBody.put("udfType", 1);
            requestInBody.put("useFormat", returnType + " " + udfName + "(" + enter + ")");
            requestInBody.put("registerFormat", "udf.register(\"" + udfName + "\", " + registerName + ")");
        } else {
            requestInBody.put("udfType", 2);
            requestInBody.put("useFormat", udfName + "()");
            requestInBody.put("registerFormat", "sqlContext.udf.register[" + returnType + "," + enter + "](\"" + udfName + "\"," + registerName + ")");
        }

        requestInBody.put("clusterName", "All");
        requestInBody.put("load", status);
        requestInBody.put("directory", dir);
        requestInBody.put("sys", linkisConfig.getAppName());

        requestBody.put("udfAddVo", requestInBody);
        Long udfId = addUdf(currentCluster, linkisConfig.getUdfAdmin(), requestBody);

        return udfId;
    }

    @Override
    public void clientModify(String targetFilePath, File uploadFile, String currentCluster, Map<String, Long> clusterIdMaps, String fileName, String udfDesc
        , String udfName, String returnType, String enter, String registerName) throws MetaDataAcquireFailedException, UnExpectedRequestException, JSONException, IOException {
        boolean commonJar = fileName.endsWith(".jar");
        boolean pythonScript = fileName.endsWith(".py");

        Map<String, Object> requestBody = new HashMap<>(1);
        Map<String, Object> requestInBody = new HashMap<>(7);
        requestInBody.put("path", targetFilePath + File.separator + uploadFile.getName());
        requestInBody.put("id", clusterIdMaps.get(currentCluster));
        requestInBody.put("description", udfDesc);
        requestInBody.put("udfName", udfName);
        if (commonJar) {
            requestInBody.put("udfType", 0);
            requestInBody.put("useFormat", returnType + " " + udfName + "(" + enter + ")");
            requestInBody.put("registerFormat", "create temporary function " + udfName + " as \"" + registerName + "\"");
        } else if (pythonScript) {
            requestInBody.put("udfType", 1);
            requestInBody.put("useFormat", returnType + " " + udfName + "(" + enter + ")");
            requestInBody.put("registerFormat", "udf.register(\"" + udfName + "\", " + registerName + ")");
        } else {
            requestInBody.put("udfType", 2);
            requestInBody.put("useFormat", udfName + "()");
            requestInBody.put("registerFormat", "sqlContext.udf.register[" + returnType + "," + enter + "](\"" + udfName + "\"," + registerName + ")");
        }
        requestBody.put("udfUpdateVo", requestInBody);
        modifyUdf(currentCluster, linkisConfig.getUdfAdmin(), requestBody);
    }

    @Override
    public void shareAndDeploy(Long udfId, String currentCluster, List<String> proxyUserNames, String udfName) {
        if (CollectionUtils.isNotEmpty(proxyUserNames)) {
            try {
                // Share to proxy user.
                LOGGER.info("Start to share udf to proxy users");
                proxyUserNames = proxyUserNames.stream().filter(proxyUserName -> ! proxyUserName.equals(linkisConfig.getUdfAdmin())).collect(Collectors.toList());
                shareUdfToProxyUsers(currentCluster, linkisConfig.getUdfAdmin(), proxyUserNames, udfId);
                LOGGER.info("Finish to share udf to proxy users");

                // Deploy new version.
                LOGGER.info("Start to get udf new version");
                String version = getUdfNewVersion(currentCluster, linkisConfig.getUdfAdmin(), udfName);
                LOGGER.info("Finish to get udf new version: {}", version);

                LOGGER.info("Start to deploy udf new version");
                deployUdfNewVersion(currentCluster, linkisConfig.getUdfAdmin(), udfId, version);
                LOGGER.info("Finish to deploy udf new version");

                LOGGER.info("Start to open udf with every proxy user");
                for (String userName : proxyUserNames) {
                    switchUdfStatus(currentCluster, udfId, userName, Boolean.TRUE);
                }
                LOGGER.info("Finish to open udf with every proxy user");
            } catch (Exception e) {
                LOGGER.error("Udf operation in backend failed. Please check udf status in linkis console.");
            }
        }
    }

    @Override
    public Map<String, Object> getUdfDetail(String currentCluster, String userName, Long linkisUdfId) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        ClusterInfo clusterInfo = checkClusterNameExists(currentCluster);
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getUdfDetail()).queryParam("udfId", linkisUdfId).toString();
        Map<String, Object> response = gainResponseLinkisByGet(clusterInfo, userName, url, "get udf detail with linkis api");

        if (checkResponse(response)) {
            return response;
        }
        LOGGER.error("");
        return null;
    }

    @Override
    public List<String> getDirectory(String category, String clusterName, String userName) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getUdfDirectory()).queryParam("category", category).toString();
        Map<String, Object> response = gainResponseLinkisByGet(clusterInfo, userName, url, "get udf detail with linkis api");

        if (checkResponse(response)) {
            return (List<String>) ((Map) response.get("data")).get("userDirectory");
        }
        LOGGER.error("Get directory failed.");
        return new ArrayList<>();
    }

    @Override
    public void shareUdfToProxyUsers(String clusterName, String userName, List<String> proxyUserNames, Long udfId) throws IOException, JSONException, UnExpectedRequestException, MetaDataAcquireFailedException {
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getUdfShare()).toString();
        Map<String, Object> requestBody = new HashMap<>(2);
        Map<String, Object> innerRequestBody = new HashMap<>(1);
        innerRequestBody.put("id", udfId);
        requestBody.put("udfInfo", innerRequestBody);
        requestBody.put("sharedUsers", proxyUserNames);


        Map<String, Object> response = gainResponseLinkisByPostBringJson(clusterInfo, userName, url, "share udf with linkis api", new JSONObject(OBJECT_MAPPER.writeValueAsString(requestBody)));

        if (checkResponse(response)) {
            LOGGER.info("");
            return;
        }
        LOGGER.error("");
    }

    @Override
    public void deleteUdf(String clusterName, Long linkisUdfId, String userName, String fileName) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Step 1
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getUdfDelete()).toString().replace("{UDF_ID}", linkisUdfId.toString());

        Map<String, Object> response = gainResponseLinkisByPost(clusterInfo, userName, url, "delete udf with linkis api");

        if (checkResponse(response)) {
            LOGGER.info("Succeed to delete udf, start to delete the udf file.");
            // Step 2
            String path = new StringBuffer(linkisConfig.getUploadWorkspacePrefix()).append(File.separator).append(userName)
                                                                                   .append(File.separator).append("qualitis")
                                                                                   .append(File.separator).append(fileName).toString();
            String deleteUrl = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getDeleteDir()).toString();
            Map<String, Object> requestBody = Maps.newHashMapWithExpectedSize(1);
            requestBody.put("path", path);

            try {
                Map<String, Object> deleteResponse = gainResponseLinkisByPostBringJson(clusterInfo, userName, deleteUrl, "delete udf file with linkis api",
                    new JSONObject(OBJECT_MAPPER.writeValueAsString(requestBody)));

                if (checkResponse(deleteResponse)) {
                    LOGGER.info("Succeed to delete the udf file.");
                    return;
                }
            } catch (Exception e) {
                LOGGER.error("Failed to delete the udf file.");
            }
        }
        LOGGER.error("Failed to delete the udf.");
    }

    @Override
    public void switchUdfStatus(String clusterName, Long linkisUdfId, String userName, Boolean isLoad) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getUdfSwitchStatus()).queryParam("udfId", linkisUdfId).queryParam("isLoad", isLoad).toString();

        Map<String, Object> response = gainResponseLinkisByGet(clusterInfo, userName, url, "switch udf status with linkis api");

        if (checkResponse(response)) {
            LOGGER.info("");
            return;
        }
        LOGGER.error("");
    }

    @Override
    public String getUdfNewVersion(String clusterName, String userName, String udfName) throws UnExpectedRequestException, IOException, JSONException, MetaDataAcquireFailedException {
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getUdfNewVersion()).queryParam("udfName", udfName).queryParam("createUser", userName).toString();

        Map<String, Object> response = gainResponseLinkisByGet(clusterInfo, userName, url, "get new version with linkis api");

        if (checkResponse(response)) {
            LOGGER.info("");
            return (String) ((Map) ((Map) response.get("data")).get("versionInfo")).get("bmlResourceVersion");
        }
        LOGGER.error("");
        return "";
    }

    @Override
    public void deployUdfNewVersion(String clusterName, String userName, Long udfId, String version)
        throws UnExpectedRequestException, IOException, JSONException, MetaDataAcquireFailedException {
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getUdfPublish()).toString();
        Map<String, Object> requestBody = new HashMap<>(2);
        requestBody.put("udfId", udfId);
        requestBody.put("version", version);
        Map<String, Object> response = gainResponseLinkisByPostBringJson(clusterInfo, userName, url, "deploy new version with linkis api", new JSONObject(OBJECT_MAPPER.writeValueAsString(requestBody)));

        if (checkResponse(response)) {
            LOGGER.info("");
            return;
        }
        LOGGER.error("");
        return;
    }

    @Override
    public GeneralResponse<Map<String, Object>> deleteEnv(String clusterName, String userName, Long envId) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check existence of cluster name
        ClusterInfo clusterInfo = checkClusterNameExists(clusterName);
        // send request to get dbs
        String url = getPath(clusterInfo.getLinkisAddress()).path(linkisConfig.getEnvDelete()).toString().replace("{ENV_ID}", envId.toString());
        Map<String, Object> response = gainResponseLinkisByDelete(clusterInfo, userName, url, "delete env by user and cluster by linkis.");

        Map<String, Object> data = (Map<String, Object>) response.get(LinkisResponseKeyEnum.DATA.getKey());
        return new GeneralResponse<>("200", "Success to delete env", data);
    }

    private ClusterInfo checkClusterNameExists(String clusterName) throws UnExpectedRequestException {
        ClusterInfo currentClusterInfo = clusterInfoCache.getIfPresent(clusterName);

        if (currentClusterInfo != null) {
            LOGGER.info("Getting cluster from local cache, key: {}", clusterName);
        } else {
            ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(clusterName);
            if (clusterInfo == null) {
                throw new UnExpectedRequestException(String.format("%s 集群名称不存在", clusterName));
            }
            clusterInfoCache.put(clusterName, clusterInfo);
            return clusterInfo;
        }
        return currentClusterInfo;
    }

    private UriBuilder getPath(String linkisAddress) {
        return UriBuilder.fromUri(linkisAddress).path(linkisConfig.getPrefix());
    }

    private boolean checkResponse(Map<String, Object> response) {
        if (null == response.get(STATUS)) {
            return false;
        }
        Integer responseStatus = (Integer) response.get(STATUS);
        return responseStatus == 0;
    }

}
