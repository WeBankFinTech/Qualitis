package com.webank.wedatasphere.qualitis.client.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webank.wedatasphere.qualitis.client.config.DataMapConfig;
import com.webank.wedatasphere.qualitis.encoder.Sha256Encoder;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.RuleClient;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.response.DataInfo;
import com.webank.wedatasphere.qualitis.metadata.response.DataMapResultInfo;
import com.webank.wedatasphere.qualitis.metadata.response.table.TableMetadataInfo;
import com.webank.wedatasphere.qualitis.metadata.response.table.TableTagInfo;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.UriBuilder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author v_minminghe@webank.com
 * @date 2022-05-31 9:40
 * @description
 */
@Component
public class RuleClientImpl implements RuleClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleClientImpl.class);
    @Autowired
    private DataMapConfig dataMapConfig;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public TableTagInfo getTableTag(String sourceType, String clusterType, String dbName, String tableName, String loginUser) throws MetaDataAcquireFailedException, ResourceAccessException, UnExpectedRequestException {
        validateParameter(sourceType, clusterType, dbName, tableName);

        UriBuilder uriBuilder = UriBuilder.fromUri(dataMapConfig.getAddress())
                .path(dataMapConfig.getDatasetTagRelationsPath());
        uriBuilder.queryParam("sourceType", sourceType);
        uriBuilder.queryParam("clusterType", clusterType);
        uriBuilder.queryParam("dbCode", dbName);
        uriBuilder.queryParam("datasetName", tableName);
        constructUrlWithSignature(uriBuilder, loginUser);

        HttpHeaders headers = new HttpHeaders();
        headers.add("isAuth", String.valueOf(false));
        LOGGER.info("Start to get table tag by dms. url: {}, method: {},", uriBuilder, HttpMethod.GET);
        String response = restTemplate.getForObject(uriBuilder + "&confirmUser&tagCode", String.class);
        LOGGER.info("Finish to get table tag by dms. response: {}", response);
        if (StringUtils.isEmpty(response)) {
            throw new MetaDataAcquireFailedException("Error!network occurred an unexpected error", 200);
        }
        return convertTableTagInfo(response);
    }

    private TableTagInfo convertTableTagInfo(String response) throws MetaDataAcquireFailedException {
        Gson gson = new Gson();
        DataMapResultInfo<DataInfo<TableTagInfo>> dataMapResultInfo = gson.fromJson(response,
                new TypeToken<DataMapResultInfo<DataInfo<TableTagInfo>>>() {
                }.getType());
        if (!String.valueOf(HttpStatus.SC_OK).equals(dataMapResultInfo.getCode())) {
            throw new MetaDataAcquireFailedException("Error! Can not get table tag from DataMap, message: " + dataMapResultInfo.getMsg(), 200);
        }
        DataInfo<TableTagInfo> data = dataMapResultInfo.getData();
        if (Objects.nonNull(data)) {
            List<TableTagInfo> content = data.getContent();
            if (CollectionUtils.isNotEmpty(content)) {
                return content.get(0);
            }
        }
        return TableTagInfo.build();
    }

    @Override
    public TableMetadataInfo getMetaData(String sourceType, String clusterType, String dbName, String tableName, String loginUser) throws MetaDataAcquireFailedException, ResourceAccessException, UnExpectedRequestException {
        validateParameter(sourceType, clusterType, dbName, tableName);

        UriBuilder uriBuilder = UriBuilder.fromUri(dataMapConfig.getAddress())
                .path(dataMapConfig.getQueryAllPath());
        uriBuilder.queryParam("searchType", "TABLE");
        uriBuilder.queryParam("sourceType", sourceType.substring(0, 1).toUpperCase() + sourceType.substring(1));
        uriBuilder.queryParam("searchKey", tableName);
        uriBuilder.queryParam("clusterType", clusterType);
        uriBuilder.queryParam("pageNo", 1);
        uriBuilder.queryParam("pageSize", 10);
        constructUrlWithSignature(uriBuilder, loginUser);

        HttpHeaders headers = new HttpHeaders();
        headers.add("isAuth", String.valueOf(false));
        LOGGER.info("Start to get metaData table by dms. url: {}, method: {}", uriBuilder, HttpMethod.GET);
        String response = restTemplate.getForObject(uriBuilder.toString(), String.class);
        LOGGER.info("Finish to get metaData table by dms. response: {}", response);
        if (StringUtils.isEmpty(response)) {
            throw new MetaDataAcquireFailedException("Error!network occurred an unexpected error", 200);
        }
        return convertTableMetadataInfo(response, clusterType, dbName, tableName);
    }

    private TableMetadataInfo convertTableMetadataInfo(String response, String clusterType, String dbName, String tableName) throws MetaDataAcquireFailedException {
        Gson gson = new Gson();
        DataMapResultInfo<Map<String, Object>> dataMapResultInfo = gson.fromJson(response, new TypeToken<DataMapResultInfo<Map<String, Object>>>() {
        }.getType());
        if (!String.valueOf(HttpStatus.SC_OK).equals(dataMapResultInfo.getCode())) {
            throw new MetaDataAcquireFailedException("Error!Can not get metaData from DataMap, message: " + dataMapResultInfo.getMsg(), 200);
        }
        Map<String, Object> dataMap = dataMapResultInfo.getData();
        if (Objects.nonNull(dataMap)) {
            Object valList = dataMap.get("valList");
            List<TableMetadataInfo> deptInfoList = gson.fromJson(gson.toJson(valList), new TypeToken<List<TableMetadataInfo>>() {
            }.getType());
            if (CollectionUtils.isNotEmpty(deptInfoList)) {
                Optional<TableMetadataInfo> optional = deptInfoList.stream().filter(result -> verificationResult(result, clusterType, dbName, tableName)).findFirst();
                if (optional.isPresent()) {
                    return optional.get();
                }
            }
        }
        return TableMetadataInfo.build();
    }

    private boolean verificationResult(TableMetadataInfo result, String clusterType, String dbName, String tableName) {
        List<Map<String, String>> pathList = result.getPathList();
        if (CollectionUtils.isNotEmpty(pathList)) {
            List<String> pathItemList = pathList.stream().map(map -> map.get("name")).collect(Collectors.toList());
            if (StringUtils.isEmpty(dbName)) {
                return pathItemList.contains(clusterType) && tableName.equals(result.getRawName());
            }
            return pathItemList.contains(clusterType) && pathItemList.contains(dbName) && tableName.equals(result.getRawName());
        }
        return false;
    }

    @Override
    public DataInfo<TableTagInfo> getTagList(String loginUser, int page, int size) throws MetaDataAcquireFailedException {
        UriBuilder uriBuilder = UriBuilder.fromUri(dataMapConfig.getAddress())
                .path(dataMapConfig.getTagsPath());
        uriBuilder.queryParam("pageNo", page);
        uriBuilder.queryParam("pageSize", size);
        constructUrlWithSignature(uriBuilder, loginUser);

        HttpHeaders headers = new HttpHeaders();
        headers.add("isAuth", String.valueOf(false));
        LOGGER.info("Start to get table tag from dms. url: {}, method: {}", uriBuilder, HttpMethod.GET);
        String response = restTemplate.getForObject(uriBuilder.toString(), String.class);
        LOGGER.info("Finish to get table tag from dms. response: {}", response);
        if (StringUtils.isEmpty(response)) {
            throw new MetaDataAcquireFailedException("Error!network occurred an unexpected error", 200);
        }
        return convertTagInfo(response);
    }

    private DataInfo<TableTagInfo> convertTagInfo(String response) throws MetaDataAcquireFailedException {
        Gson gson = new Gson();
        DataMapResultInfo<DataInfo<TableTagInfo>> dataMapResultInfo = gson.fromJson(response,
                new TypeToken<DataMapResultInfo<DataInfo<TableTagInfo>>>() {
                }.getType());
        if (!String.valueOf(HttpStatus.SC_OK).equals(dataMapResultInfo.getCode())) {
            throw new MetaDataAcquireFailedException("Error! Can not get table tag from dms, message: " + dataMapResultInfo.getMsg(), 200);
        }
        return dataMapResultInfo.getData();
    }

    private void validateParameter(String sourceType, String clusterType, String dbName, String tableName) throws UnExpectedRequestException {
        if (StringUtils.isEmpty(sourceType)
                || StringUtils.isEmpty(clusterType)
                || StringUtils.isEmpty(dbName)
                || StringUtils.isEmpty(tableName)) {
            throw new UnExpectedRequestException("parameter must be not null");
        }
    }

    /**
     * if execute a large number of data, maybe nonce conflict, cause http 403
     * @param uriBuilder
     * @param loginUser
     */
    private void constructUrlWithSignature(UriBuilder uriBuilder, String loginUser) {
        String nonce = UuidGenerator.generateRandom(5);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = Sha256Encoder.encode(Sha256Encoder.encode(dataMapConfig.getAppId() + nonce + loginUser + timestamp) + dataMapConfig.getAppToken());
        uriBuilder.queryParam("appid", dataMapConfig.getAppId());
        uriBuilder.queryParam("nonce", nonce);
        uriBuilder.queryParam("timestamp", timestamp);
        uriBuilder.queryParam("loginUser", loginUser);
        uriBuilder.queryParam("signature", signature);
        uriBuilder.queryParam("isolateEnvFlag", dataMapConfig.getIsolateEnvFlag());
    }


}
