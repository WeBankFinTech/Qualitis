package com.webank.wedatasphere.qualitis.client.impl;

import com.google.common.collect.Maps;
import com.webank.wedatasphere.qualitis.config.LinkisConfig;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.LinkisMetaDataManager;
import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.request.LinkisConnectParamsRequest;
import com.webank.wedatasphere.qualitis.metadata.request.LinkisDataSourceEnvRequest;
import com.webank.wedatasphere.qualitis.metadata.request.LinkisDataSourceRequest;
import com.webank.wedatasphere.qualitis.metadata.request.ModifyDataSourceParameterRequest;
import com.webank.wedatasphere.qualitis.metadata.response.datasource.LinkisDataSourceInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.datasource.LinkisDataSourceParamsResponse;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.util.CryptoUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author v_minminghe@webank.com
 * @date 2023-05-12 10:59
 * @description 封装对metaDataClient的调用，主要用于处理请求参数的包装和转换
 */
@Service
public class LinkisMetaDataManagerImpl implements LinkisMetaDataManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkisMetaDataManagerImpl.class);

    @Autowired
    private MetaDataClient metaDataClient;
    @Autowired
    private LinkisConfig linkisConfig;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String CREATE_SYSTEM = "Qualitis";

    @Override
    public Long createDataSource(LinkisDataSourceRequest linkisDataSourceRequest, String cluster, String authUser) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        String dataSourceJson = createDatasourceJson(linkisDataSourceRequest);
        LOGGER.info("To create datasource to Linkis, request body: {}", dataSourceJson);
        GeneralResponse<Map<String, Object>> generalResponse;
        try {
            generalResponse = metaDataClient.createDataSource(cluster, authUser, dataSourceJson);
        } catch (JSONException e) {
            throw new UnExpectedRequestException("Failed to format request parameter of dataSource");
        }
        Map<String, Object> dataMap = generalResponse.getData();
        if (Objects.nonNull(dataMap) && dataMap.containsKey("insertId")) {
            return Long.valueOf((Integer) dataMap.get("insertId"));
        }
        throw new UnExpectedRequestException("Failed to create DataSource");
    }

    @Override
    public Long modifyDataSource(LinkisDataSourceRequest linkisDataSourceRequest, String cluster, String authUser) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        String dataSourceJson = createDatasourceJson(linkisDataSourceRequest);
        LOGGER.info("To modify datasource to Linkis, request body: {}", dataSourceJson);
        GeneralResponse<Map<String, Object>> generalResponse;
        try {
            generalResponse = metaDataClient.modifyDataSource(cluster, authUser, linkisDataSourceRequest.getLinkisDataSourceId(), dataSourceJson);
        } catch (JSONException e) {
            throw new UnExpectedRequestException("Failed to format request parameter of dataSource");
        }
        Map<String, Object> dataMap = generalResponse.getData();
        if (Objects.nonNull(dataMap) && dataMap.containsKey("updateId")) {
            return Long.valueOf((Integer) dataMap.get("updateId"));
        }
        throw new UnExpectedRequestException("Failed to modify DataSource");
    }

    @Override
    public List<LinkisDataSourceEnvRequest> createDataSourceEnvAndSetEnvId(Integer inputType, Integer verifyType, List<LinkisDataSourceEnvRequest> linkisDataSourceEnvRequestList, String clusterName, String authUser) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        String envJson = createDatasourceEnvJson(verifyType, linkisDataSourceEnvRequestList);
        GeneralResponse<Map<String, Object>> datasourceEnvResponse;
        try {
            LOGGER.info("createDataSourceEnv, request body: {}", envJson);
            datasourceEnvResponse = metaDataClient.createDataSourceEnvBatch(clusterName, authUser, CREATE_SYSTEM, envJson);
        } catch (JSONException e) {
            throw new UnExpectedRequestException("Failed to format request parameter of dataSource env");
        }
        if (ResponseStatusConstants.OK.equals(datasourceEnvResponse.getCode()) && Objects.nonNull(datasourceEnvResponse.getData())) {
            Map<String, Object> dataMap = datasourceEnvResponse.getData();
            if (dataMap.containsKey("envs")) {
                List<Map<String, Object>> envMapList = (List<Map<String, Object>>) dataMap.get("envs");
                Map<String, Map<String, Object>> nameAndEnvMap = envMapList.stream()
                        .filter(envMap -> envMap.containsKey("id"))
                        .collect(Collectors.toMap(envMap -> String.valueOf(envMap.get("envName")), Function.identity(), (oldVal, newVal) -> oldVal));
                linkisDataSourceEnvRequestList.forEach(linkisDataSourceEnvRequest -> {
                    if (nameAndEnvMap.containsKey(linkisDataSourceEnvRequest.getEnvName())) {
                        Map<String, Object> envMap = nameAndEnvMap.get(linkisDataSourceEnvRequest.getEnvName());
                        if (envMap.containsKey("id")) {
                            linkisDataSourceEnvRequest.setId(Long.valueOf(envMap.get("id").toString()));
                        }
                    }
                });
            }
        }
        return linkisDataSourceEnvRequestList;
    }

    @Override
    public List<LinkisDataSourceEnvRequest> modifyDataSourceEnv(Integer inputType, Integer verifyType, List<LinkisDataSourceEnvRequest> linkisDataSourceEnvRequestList, String clusterName, String authUser) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        String envJson = createDatasourceEnvJson(verifyType, linkisDataSourceEnvRequestList);
        GeneralResponse<Map<String, Object>> datasourceEnvResponse;
        try {
            LOGGER.info("modifyDataSourceEnv, request body: {}", envJson);
            datasourceEnvResponse = metaDataClient.modifyDataSourceEnvBatch(clusterName, authUser, CREATE_SYSTEM, envJson);
        } catch (JSONException e) {
            throw new UnExpectedRequestException("Failed to format request parameter of dataSource env");
        }
        if (ResponseStatusConstants.OK.equals(datasourceEnvResponse.getCode()) && Objects.nonNull(datasourceEnvResponse.getData())) {
            Map<String, Object> dataMap = datasourceEnvResponse.getData();
            if (dataMap.containsKey("envs")) {
                List<Map<String, Object>> envMapList = (List<Map<String, Object>>) dataMap.get("envs");
                Map<String, Map<String, Object>> nameAndEnvMap = envMapList.stream()
                        .filter(envMap -> envMap.containsKey("id"))
                        .collect(Collectors.toMap(envMap -> MapUtils.getString(envMap, "envName"), Function.identity(), (oldVal, newVal) -> oldVal));
                linkisDataSourceEnvRequestList.forEach(linkisDataSourceEnvRequest -> {
                    if (nameAndEnvMap.containsKey(linkisDataSourceEnvRequest.getEnvName())) {
                        Map<String, Object> envMap = nameAndEnvMap.get(linkisDataSourceEnvRequest.getEnvName());
                        if (envMap.containsKey("id")) {
                            linkisDataSourceEnvRequest.setId(MapUtils.getLong(envMap, "id"));
                        }
                    }
                });
            }
        }
        return linkisDataSourceEnvRequestList;
    }

    @Override
    public LinkisDataSourceParamsResponse modifyDataSourceParams(ModifyDataSourceParameterRequest modifyDataSourceParameterRequest, String clusterName, String authUser) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        Map<String, Object> parameterMap = Maps.newHashMapWithExpectedSize(2);
        parameterMap.put("connectParams", modifyDataSourceParameterRequest.getConnectParams());
        parameterMap.put("comment", modifyDataSourceParameterRequest.getComment());
        String parameterRequest;
        try {
            parameterRequest = objectMapper.writeValueAsString(parameterMap);
        } catch (IOException e) {
            throw new UnExpectedRequestException("Failed to format json parameter of dataSource");
        }
        GeneralResponse<Map<String, Object>> generalResponse;
        try {
            generalResponse = metaDataClient.modifyDataSourceParam(clusterName, authUser
                    , modifyDataSourceParameterRequest.getLinkisDataSourceId(), parameterRequest);
        } catch (JSONException e) {
            throw new UnExpectedRequestException("Failed to format json parameter of dataSource");
        }
        if (Objects.nonNull(generalResponse.getData())) {
            Object version = generalResponse.getData().get("version");
            Long versionId = Objects.nonNull(version) ? Long.valueOf(version.toString()) : null;
            return new LinkisDataSourceParamsResponse(versionId);
        }
        throw new UnExpectedRequestException("Failed to modify parameter of dataSource");
    }

    @Override
    public void deleteDataSource(Long linkisDataSourceId, String clusterName, String userName) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        GeneralResponse generalResponse = metaDataClient.deleteDataSource(clusterName, userName, linkisDataSourceId);
        if (!ResponseStatusConstants.OK.equals(generalResponse.getCode())) {
            throw new UnExpectedRequestException("Failed to delete DataSource to Linkis");
        }
    }


    private String createDatasourceEnvJson(Integer verifyType, List<LinkisDataSourceEnvRequest> dataSourceEnvList) throws UnExpectedRequestException {
        if (CollectionUtils.isEmpty(dataSourceEnvList)) {
            return StringUtils.EMPTY;
        }
        boolean isShared = isShared(verifyType);
        for (LinkisDataSourceEnvRequest dataSourceEnv : dataSourceEnvList) {
            if (Objects.isNull(dataSourceEnv.getConnectParamsRequest())) {
                LOGGER.warn("Lack of connect parameter, envName: {}", dataSourceEnv.getEnvName());
                continue;
            }
            LinkisConnectParamsRequest connectParamsRequest = dataSourceEnv.getConnectParamsRequest();
            Map<String, Object> connectParamMap = new HashMap<>();
            if (!isShared) {
                String authType = connectParamsRequest.getAuthType();
                connectParamMap.put("authType", authType);
                if (QualitisConstants.AUTH_TYPE_ACCOUNT_PWD.equals(authType)) {
                    connectParamMap.put("username", connectParamsRequest.getUsername());
                    if (StringUtils.isNotEmpty(connectParamsRequest.getPassword())) {
                        connectParamMap.put("password", CryptoUtils.encode(connectParamsRequest.getPassword()));
                    }
                } else if (QualitisConstants.AUTH_TYPE_DPM.equals(authType)) {
                    connectParamMap.put("appid", connectParamsRequest.getAppId());
                    connectParamMap.put("objectid", connectParamsRequest.getObjectId());
                    connectParamMap.put("dk", connectParamsRequest.getDk());
                }
            }
            connectParamMap.put("host", connectParamsRequest.getHost());
            connectParamMap.put("port", connectParamsRequest.getPort());
            connectParamMap.put("params", connectParamsRequest.getConnectParam());
            dataSourceEnv.setConnectParams(connectParamMap);
        }
        try {
            return objectMapper.writeValueAsString(dataSourceEnvList);
        } catch (IOException e) {
            throw new UnExpectedRequestException("Failed to format dataSource env json");
        }
    }

    private String createDatasourceJson(LinkisDataSourceRequest linkisDataSourceRequest) throws UnExpectedRequestException {
        if (MapUtils.isEmpty(linkisDataSourceRequest.getConnectParams())) {
            Map<String, Object> connectParams = new HashMap<>();
            boolean isShared = isShared(linkisDataSourceRequest.getVerifyType());
            if (isShared && Objects.nonNull(linkisDataSourceRequest.getSharedConnectParams())) {
                LinkisConnectParamsRequest connectParamsRequest = linkisDataSourceRequest.getSharedConnectParams();
                String authType = connectParamsRequest.getAuthType();
                connectParams.put("authType", authType);
                if (QualitisConstants.AUTH_TYPE_ACCOUNT_PWD.equals(authType)) {
                    connectParams.put("username", connectParamsRequest.getUsername());
                    connectParams.put("password", CryptoUtils.encode(connectParamsRequest.getPassword()));
                } else if (QualitisConstants.AUTH_TYPE_DPM.equals(authType)) {
                    connectParams.put("appid", connectParamsRequest.getAppId());
                    connectParams.put("objectid", connectParamsRequest.getObjectId());
                    connectParams.put("mkPrivate", connectParamsRequest.getMkPrivate());
                }
            }
            connectParams.put("subSystem", linkisDataSourceRequest.getSubSystem());
            connectParams.put("share", isShared);
            connectParams.put("dcn", isAutoInput(linkisDataSourceRequest.getInputType()));
            connectParams.put("multi_env", true);

            linkisDataSourceRequest.setConnectParams(connectParams);
        }

        validateConnectParams(linkisDataSourceRequest.getConnectParams());

        try {
            return objectMapper.writeValueAsString(linkisDataSourceRequest);
        } catch (IOException e) {
            throw new UnExpectedRequestException("Failed to format dataSource json");
        }
    }

    @Override
    public Map<String, Long> getDataSourceTypeNameAndIdMap() {
        Map<String, Long> typeNameAndIdMap = new HashMap<>();
        try {
            GeneralResponse<Map<String, Object>> generalResponse = metaDataClient.getAllDataSourceTypes(linkisConfig.getDatasourceCluster(), linkisConfig.getDatasourceAdmin());
            if (ResponseStatusConstants.OK.equals(generalResponse.getCode())
                    && generalResponse.getData() != null
                    && generalResponse.getData().containsKey("typeList")) {
                List<Map<String, Object>> typeListMap = (List<Map<String, Object>>) generalResponse.getData().get("typeList");
                typeListMap.forEach(typeMap -> {
                    typeNameAndIdMap.put(MapUtils.getString(typeMap, "name"), Long.valueOf(MapUtils.getString(typeMap, "id")));
                });
            }
        } catch (UnExpectedRequestException e) {
            LOGGER.error("Failed to query all dataSource types. ", e);
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error("Failed to query all dataSource types. ", e);
        }
        return typeNameAndIdMap;
    }

    @Override
    public GeneralResponse connect(Long linkisDataSourceId, Long versionId) throws Exception {
        LinkisDataSourceInfoDetail linkisDataSourceInfoDetail = metaDataClient.getDataSourceInfoById(linkisConfig.getDatasourceCluster(), linkisConfig.getDatasourceAdmin(), linkisDataSourceId, versionId);
        String dataSourceJson = objectMapper.writeValueAsString(linkisDataSourceInfoDetail);

        try {
            GeneralResponse<Map<String, Object>> resultMap = metaDataClient.connectDataSource(linkisConfig.getDatasourceCluster(), linkisConfig.getDatasourceAdmin(), dataSourceJson);
            if (!resultMap.getData().containsKey("ok")) {
                return resultMap;
            }
        } catch (MetaDataAcquireFailedException e) {
            String errorMsg = "环境连接失败";
            throw new MetaDataAcquireFailedException(errorMsg, 500);
        }
        return new GeneralResponse(ResponseStatusConstants.OK, "Connected!", null);
    }

    private void validateConnectParams(Map<String, Object> connectParams) throws UnExpectedRequestException {
        validateKey(connectParams, "subSystem");
        validateKey(connectParams, "share");
        validateKey(connectParams, "dcn");
        if (Boolean.TRUE.toString().equals(connectParams.getOrDefault("share", Boolean.FALSE.toString()))) {
            validateKey(connectParams, "authType");
            String authType = (String) connectParams.get("authType");
            if (QualitisConstants.AUTH_TYPE_ACCOUNT_PWD.equals(authType)) {
                validateKey(connectParams, "username");
                validateKey(connectParams, "password");
            } else if (QualitisConstants.AUTH_TYPE_DPM.equals(authType)) {
                validateKey(connectParams, "appid");
                validateKey(connectParams, "objectid");
                validateKey(connectParams, "mkPrivate");
            }
        }
    }

    private void validateKey(Map<String, Object> map, String key) throws UnExpectedRequestException {
        if (!map.containsKey(key)) {
            throw new UnExpectedRequestException("Parameter must be null: " + key);
        }
    }

    private boolean isAutoInput(Integer inputType) {
        return Integer.valueOf(QualitisConstants.DATASOURCE_MANAGER_INPUT_TYPE_AUTO).equals(inputType);
    }

    private boolean isShared(Integer verifyType) {
        return Integer.valueOf(QualitisConstants.DATASOURCE_MANAGER_VERIFY_TYPE_SHARE).equals(verifyType);
    }

}