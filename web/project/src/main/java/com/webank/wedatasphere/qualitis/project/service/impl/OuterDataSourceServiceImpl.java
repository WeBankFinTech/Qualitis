package com.webank.wedatasphere.qualitis.project.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.webank.wedatasphere.qualitis.config.LinkisConfig;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.dao.DepartmentDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.dao.UserRoleDao;
import com.webank.wedatasphere.qualitis.dto.DataVisibilityPermissionDto;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.entity.UserRole;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.LinkisMetaDataManager;
import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import com.webank.wedatasphere.qualitis.metadata.client.OperateCiService;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.request.LinkisConnectParamsRequest;
import com.webank.wedatasphere.qualitis.metadata.request.LinkisDataSourceEnvRequest;
import com.webank.wedatasphere.qualitis.metadata.request.LinkisDataSourceRequest;
import com.webank.wedatasphere.qualitis.metadata.request.ModifyDataSourceParameterRequest;
import com.webank.wedatasphere.qualitis.metadata.response.SubSystemResponse;
import com.webank.wedatasphere.qualitis.metadata.response.datasource.LinkisDataSourceInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.datasource.LinkisDataSourceParamsResponse;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.project.request.OuterDataSourceDcnRequest;
import com.webank.wedatasphere.qualitis.project.request.OuterDataSourceEnvRequest;
import com.webank.wedatasphere.qualitis.project.request.OuterDataSourceRequest;
import com.webank.wedatasphere.qualitis.project.response.OuterDataSourceDetailResponse;
import com.webank.wedatasphere.qualitis.project.response.OuterDataSourceEnvResponse;
import com.webank.wedatasphere.qualitis.project.response.OuterDataSourceVersionResponse;
import com.webank.wedatasphere.qualitis.project.service.OuterDataSourceService;
import com.webank.wedatasphere.qualitis.response.DepartmentSubInfoResponse;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.constant.TableDataTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.LinkisDataSourceDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceDao;
import com.webank.wedatasphere.qualitis.rule.entity.*;
import com.webank.wedatasphere.qualitis.rule.service.LinkisDataSourceEnvService;
import com.webank.wedatasphere.qualitis.rule.service.LinkisDataSourceService;
import com.webank.wedatasphere.qualitis.service.DataVisibilityService;
import com.webank.wedatasphere.qualitis.service.RoleService;
import com.webank.wedatasphere.qualitis.service.SubDepartmentPermissionService;
import com.webank.wedatasphere.qualitis.service.UserService;
import com.webank.wedatasphere.qualitis.util.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.RoleNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author v_minminghe@webank.com
 * @date 2023-10-17 14:44
 * @description
 */
@Service
public class OuterDataSourceServiceImpl implements OuterDataSourceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OuterDataSourceServiceImpl.class);

    @Autowired
    private LinkisMetaDataManager linkisMetaDataManager;
    @Autowired
    private LinkisDataSourceService linkisDataSourceService;
    @Autowired
    private RuleDataSourceDao ruleDataSourceDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserRoleDao userRoleDao;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserService userService;
    @Autowired
    private LinkisConfig linkisConfig;
    @Autowired
    private DepartmentDao departmentDao;
    @Autowired
    private MetaDataClient metaDataClient;
    @Autowired
    private SubDepartmentPermissionService subDepartmentPermissionService;
    @Autowired
    private DataVisibilityService dataVisibilityService;
    @Autowired
    private OperateCiService operateCiService;
    @Autowired
    private LinkisDataSourceEnvService linkisDataSourceEnvService;
    @Autowired
    private LinkisDataSourceDao linkisDataSourceDao;

    private HttpServletRequest httpServletRequest;

    public OuterDataSourceServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    private void buildEnvRequestByDcnRange(OuterDataSourceRequest outerDataSourceRequest, String subSystemId) throws UnExpectedRequestException {
        if (StringUtils.isBlank(outerDataSourceRequest.getDcnRangeType())) {
            return;
        }
        List<Map<String, Object>> filteredDcnList = new ArrayList<>();
        try {
            GeneralResponse generalResponse = operateCiService.getDcn(subSystemId, outerDataSourceRequest.getDcnRangeType(), Collections.emptyList());
            if (ResponseStatusConstants.OK.equals(generalResponse.getCode())) {
                boolean isRangeQuery = Arrays.asList(QualitisConstants.CMDB_KEY_DCN_NUM, QualitisConstants.CMDB_KEY_LOGIC_AREA).contains(outerDataSourceRequest.getDcnRangeType());
                if (isRangeQuery) {
                    Map<Object, List<Map<String, Object>>> res = (Map<Object, List<Map<String, Object>>>) generalResponse.getData();
                    for (String dcnRangeValue : outerDataSourceRequest.getDcnRangeValues()) {
                        if (res.containsKey(dcnRangeValue)) {
                            filteredDcnList.addAll(res.get(dcnRangeValue));
                        }
                    }
                } else {
                    filteredDcnList.addAll((List<Map<String, Object>>) generalResponse.getData());
                }
            }
        } catch (UnExpectedRequestException e) {
            LOGGER.error("Failed to fetch dcn from cmdb.", e);
        }

        List<OuterDataSourceEnvRequest> dataSourceEnvs = filteredDcnList.stream().map(entry -> {
            OuterDataSourceEnvRequest envRequest = new OuterDataSourceEnvRequest();
            envRequest.setEnvName(MapUtils.getString(entry, QualitisConstants.CMDB_KEY_DCN_NUM));
            envRequest.setDcnNum(MapUtils.getString(entry, QualitisConstants.CMDB_KEY_DCN_NUM));
            envRequest.setLogicArea(MapUtils.getString(entry, QualitisConstants.CMDB_KEY_LOGIC_AREA));
            envRequest.setDatabaseInstance(MapUtils.getString(entry, "dbinstance_name"));
            LinkisConnectParamsRequest linkisConnectParamsRequest = new LinkisConnectParamsRequest();
            linkisConnectParamsRequest.setHost(MapUtils.getString(entry, "vip"));
            linkisConnectParamsRequest.setPort(MapUtils.getString(entry, "gwport"));
            envRequest.setConnectParams(linkisConnectParamsRequest);
            return envRequest;
        }).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(dataSourceEnvs)) {
            throw new UnExpectedRequestException("Failed to fetch dcn from cmdb!");
        }
        outerDataSourceRequest.setDataSourceEnvs(dataSourceEnvs);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long createDataSource(OuterDataSourceRequest outerDataSourceRequest) throws RoleNotFoundException, UnExpectedRequestException, MetaDataAcquireFailedException, JsonProcessingException {
        String subSystemId = outerDataSourceRequest.getSubSystemId();
        if (null == subSystemId) {
            List<SubSystemResponse> subSystemResponseList = operateCiService.getAllSubSystemInfo();
            Optional<SubSystemResponse> subSystemResponseOptional = subSystemResponseList.stream().filter(subSystemResponse -> subSystemResponse.getSubSystemName().equals(outerDataSourceRequest.getSubSystemName())).findFirst();
            if (!subSystemResponseOptional.isPresent()) {
                throw new UnExpectedRequestException("Inviable sub_system: " + outerDataSourceRequest.getSubSystemName());
            }
            subSystemId = subSystemResponseOptional.get().getSubSystemId();
        }
        buildEnvRequestByDcnRange(outerDataSourceRequest, subSystemId);

        String username = outerDataSourceRequest.getUsername();
        User userInDb = userDao.findByUsername(username);
        if (userInDb == null) {
            throw new UnExpectedRequestException("User doesn't exist, username: " + username);
        }
        LinkisDataSource linkisDataSourceInDb = linkisDataSourceService.getByLinkisDataSourceName(outerDataSourceRequest.getDataSourceName());
        if (linkisDataSourceInDb != null) {
            throw new UnExpectedRequestException("DataSourceName already existed!");
        }

//        convert data_type_name to data_type_id
        Map<String, Long> typeNameAndIdMap = linkisMetaDataManager.getDataSourceTypeNameAndIdMap();
        if (!typeNameAndIdMap.containsKey(outerDataSourceRequest.getDataSourceTypeName())) {
            throw new UnExpectedRequestException("DataSourceTypeName is not existed!");
        }
        Long dataSourceTypeId = typeNameAndIdMap.get(outerDataSourceRequest.getDataSourceTypeName());

//        submit dataSource to Linkis
        LinkisDataSourceRequest linkisDataSourceRequest = new LinkisDataSourceRequest();
        BeanUtils.copyProperties(outerDataSourceRequest, linkisDataSourceRequest);
        linkisDataSourceRequest.setSubSystem(outerDataSourceRequest.getSubSystemName());
        linkisDataSourceRequest.setDataSourceTypeId(dataSourceTypeId);
        Long linkisDataSourceId = linkisMetaDataManager.createDataSource(linkisDataSourceRequest, linkisConfig.getDatasourceCluster(), linkisConfig.getDatasourceAdmin());

//        submit envs of dataSource to Linkis
        List<OuterDataSourceEnvRequest> dataSourceEnvs = outerDataSourceRequest.getDataSourceEnvs();
        validateAndResetEnvName(linkisDataSourceId, outerDataSourceRequest.getInputType(), dataSourceEnvs);
        List<LinkisDataSourceEnvRequest> linkisDataSourceEnvRequestList = dataSourceEnvs.stream().map(dataSourceEnv -> {
            LinkisDataSourceEnvRequest linkisDataSourceEnvRequest = new LinkisDataSourceEnvRequest();
            linkisDataSourceEnvRequest.setDataSourceTypeId(dataSourceTypeId);
            linkisDataSourceEnvRequest.setConnectParamsRequest(dataSourceEnv.getConnectParams());
            BeanUtils.copyProperties(dataSourceEnv, linkisDataSourceEnvRequest);
            return linkisDataSourceEnvRequest;
        }).collect(Collectors.toList());
        linkisDataSourceRequest.setDataSourceEnvs(linkisDataSourceEnvRequestList);
        try {
            linkisMetaDataManager.createDataSourceEnvAndSetEnvId(outerDataSourceRequest.getInputType(), outerDataSourceRequest.getVerifyType(), linkisDataSourceEnvRequestList, linkisConfig.getDatasourceCluster(), linkisConfig.getDatasourceAdmin());
        } catch (Exception e) {
            LOGGER.warn("Failed to create DataSourceEnv, preparing to rollback.");
            linkisMetaDataManager.deleteDataSource(linkisDataSourceId, linkisConfig.getDatasourceCluster(), linkisConfig.getDatasourceAdmin());
            throw e;
        }

//        save dataSource and its envs to local database
        LinkisDataSource linkisDataSource = linkisDataSourceService.save(linkisDataSourceId, dataSourceTypeId, linkisDataSourceRequest, userInDb);
        linkisDataSourceEnvService.createBatch(linkisDataSourceId, linkisDataSourceEnvRequestList);

        dataVisibilityService.saveBatch(linkisDataSource.getId(), TableDataTypeEnum.LINKIS_DATA_SOURCE, outerDataSourceRequest.getVisibilityDepartmentList());

//        submit param of dataSource to Linkis
        List<String> envIdArray = linkisDataSourceEnvRequestList.stream().map(LinkisDataSourceEnvRequest::getId).map(String::valueOf).collect(Collectors.toList());
        modifyDataSourceParam(outerDataSourceRequest, envIdArray, "Init");

        return linkisDataSourceId;
    }

    private void modifyDataSourceParam(OuterDataSourceRequest request, List<String> envIdArray, String comment) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        LinkisDataSource linkisDataSource = linkisDataSourceService.getByLinkisDataSourceName(request.getDataSourceName());
        if (linkisDataSource == null) {
            throw new UnExpectedRequestException("DataSource doesn't exist!");
        }
        Map<String, Object> connectMap = new HashMap<>();
        connectMap.put("envIdArray", envIdArray);
        if (QualitisConstants.DATASOURCE_MANAGER_VERIFY_TYPE_SHARE == request.getVerifyType()) {
            LinkisConnectParamsRequest sharedConnectParams = request.getConnectParams();
            String authType = sharedConnectParams.getAuthType();
            connectMap.put("authType", authType);
            if (QualitisConstants.AUTH_TYPE_ACCOUNT_PWD.equals(authType)) {
                connectMap.put("username", sharedConnectParams.getUsername());
                connectMap.put("password", sharedConnectParams.getPassword());
            } else if (QualitisConstants.AUTH_TYPE_DPM.equals(authType)) {
                connectMap.put("appid", sharedConnectParams.getAppId());
                connectMap.put("objectid", sharedConnectParams.getObjectId());
                connectMap.put("mkPrivate", sharedConnectParams.getMkPrivate());
            }
        }
        ModifyDataSourceParameterRequest modifyDataSourceParameterRequest = new ModifyDataSourceParameterRequest();
        modifyDataSourceParameterRequest.setLinkisDataSourceId(linkisDataSource.getLinkisDataSourceId());
        modifyDataSourceParameterRequest.setConnectParams(connectMap);
        modifyDataSourceParameterRequest.setComment(comment);
        LinkisDataSourceParamsResponse linkisDataSourceParamsResponse = linkisMetaDataManager.modifyDataSourceParams(modifyDataSourceParameterRequest, linkisConfig.getDatasourceCluster(), linkisConfig.getDatasourceAdmin());

        linkisDataSource.setVersionId(linkisDataSourceParamsResponse.getVersionId());
        linkisDataSourceService.save(linkisDataSource);
    }

    @Transactional
    @Override
    public GeneralResponse<Long> modifyDataSource(OuterDataSourceRequest request) throws UnExpectedRequestException, MetaDataAcquireFailedException, JsonProcessingException, PermissionDeniedRequestException {
        LinkisDataSource linkisDataSource = linkisDataSourceService.getByLinkisDataSourceName(request.getDataSourceName());
        if (null == linkisDataSource) {
            throw new UnExpectedRequestException("DataSource doesn't exist!");
        }
//        Checking if the DataSource was related to rules
        List<Rule> rules = getRulesRelatedTo(linkisDataSource.getLinkisDataSourceId());
        if (CollectionUtils.isNotEmpty(rules)) {
            List<String> ruleNameList = rules.stream().map(Rule::getName).collect(Collectors.toList());
            return new GeneralResponse(ResponseStatusConstants.SERVER_ERROR, "There are some rules related to the DataSource. Before you going on, you need to delete or ban these rules. ", ruleNameList);
        }

        String subSystemId = request.getSubSystemId();
        if (null == subSystemId) {
            List<SubSystemResponse> subSystemResponseList = operateCiService.getAllSubSystemInfo();
            Optional<SubSystemResponse> subSystemResponseOptional = subSystemResponseList.stream().filter(subSystemResponse -> subSystemResponse.getSubSystemName().equals(request.getSubSystemName())).findFirst();
            if (!subSystemResponseOptional.isPresent()) {
                throw new UnExpectedRequestException("Inviable sub_system: " + request.getSubSystemName());
            }
            subSystemId = subSystemResponseOptional.get().getSubSystemId();
        }
        buildEnvRequestByDcnRange(request, subSystemId);

        String username = request.getUsername();
        User userInDb = userDao.findByUsername(username);
        if (userInDb == null) {
            throw new UnExpectedRequestException("User doesn't exist!, username: " + username);
        }
        List<UserRole> userRoles = userRoleDao.findByUser(userInDb);
        Integer roleType = roleService.getRoleType(userRoles);
        subDepartmentPermissionService.checkEditablePermission(roleType, userInDb, linkisDataSource.getCreateUser(), linkisDataSource.getDevDepartmentId(), linkisDataSource.getOpsDepartmentId(), false);

//        Convert name of dataSourceType to id
        Map<String, Long> typeNameAndIdMap = linkisMetaDataManager.getDataSourceTypeNameAndIdMap();
        if (!typeNameAndIdMap.containsKey(request.getDataSourceTypeName())) {
            throw new UnExpectedRequestException("DataSourceTypeName doesn't exist!");
        }
        Long dataSourceTypeId = typeNameAndIdMap.get(request.getDataSourceTypeName());

        String clusterName = linkisConfig.getDatasourceCluster();
//        modify datasource to linkis
        LinkisDataSourceRequest linkisDataSourceRequest = new LinkisDataSourceRequest();
        BeanUtils.copyProperties(request, linkisDataSourceRequest);
        if (CollectionUtils.isNotEmpty(request.getDataSourceEnvs())) {
            LinkisConnectParamsRequest connectParamsRequest = new LinkisConnectParamsRequest();
            if (null != request.getConnectParams()) {
                BeanUtils.copyProperties(request.getConnectParams(), connectParamsRequest);
            }
            linkisDataSourceRequest.setSharedConnectParams(connectParamsRequest);
        }
        linkisDataSourceRequest.setSubSystem(request.getSubSystemName());
        linkisDataSourceRequest.setDataSourceTypeId(dataSourceTypeId);
        linkisDataSourceRequest.setLabels(request.getLabels());
        linkisDataSourceRequest.setLinkisDataSourceId(linkisDataSource.getLinkisDataSourceId());
        linkisMetaDataManager.modifyDataSource(linkisDataSourceRequest, clusterName, linkisConfig.getDatasourceAdmin());

        List<OuterDataSourceEnvRequest> dataSourceEnvList = request.getDataSourceEnvs();
        validateAndResetEnvName(linkisDataSource.getLinkisDataSourceId(), request.getInputType(), dataSourceEnvList);


        // delete env if exists deleted envs on page
        List<LinkisDataSourceEnv> linkisDataSourceEnvList = linkisDataSourceEnvService.queryAllEnvs(linkisDataSource.getLinkisDataSourceId());
        if (CollectionUtils.isNotEmpty(linkisDataSourceEnvList)) {
            Collection<Long> envIdsInDb = linkisDataSourceEnvList.stream().map(LinkisDataSourceEnv::getEnvId).collect(Collectors.toList());
            List<Long> envIdsInRequest = dataSourceEnvList.stream().map(dataSourceEnv -> dataSourceEnv.getId()).collect(Collectors.toList());
            List<Long> envIdsInDeleted = envIdsInDb.stream().filter(envIdInDb -> !envIdsInRequest.contains(envIdInDb)).collect(Collectors.toList());
            for (Long envIdInDeleted : envIdsInDeleted) {
                try {
                    metaDataClient.deleteEnv(clusterName, QualitisConstants.FPS_DEFAULT_USER, envIdInDeleted);
                } catch (UnExpectedRequestException | MetaDataAcquireFailedException e) {
                    LOGGER.error("Failed to delete env deleted on page, envId: {}", envIdInDeleted, e);
                }
            }
            linkisDataSourceEnvService.deleteBatch(envIdsInDeleted);
        }

        List<LinkisDataSourceEnvRequest> modifyDatasourceEnvList = dataSourceEnvList.stream().filter(dataSourceEnv -> Objects.nonNull(dataSourceEnv.getId()))
                .map(dataSourceEnv -> {
                    LinkisDataSourceEnvRequest linkisDataSourceEnvRequest = new LinkisDataSourceEnvRequest();
                    linkisDataSourceEnvRequest.setId(dataSourceEnv.getId());
                    linkisDataSourceEnvRequest.setDataSourceTypeId(dataSourceTypeId);
                    linkisDataSourceEnvRequest.setConnectParamsRequest(dataSourceEnv.getConnectParams());
                    BeanUtils.copyProperties(dataSourceEnv, linkisDataSourceEnvRequest);
                    return linkisDataSourceEnvRequest;
                }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(modifyDatasourceEnvList)) {
            linkisMetaDataManager.modifyDataSourceEnv(request.getInputType(), request.getVerifyType(), modifyDatasourceEnvList, clusterName, linkisConfig.getDatasourceAdmin());
        }

        List<LinkisDataSourceEnvRequest> createDatasourceEnvList = dataSourceEnvList.stream().filter(dataSourceEnv -> Objects.isNull(dataSourceEnv.getId()))
                .map(dataSourceEnv -> {
                    LinkisDataSourceEnvRequest linkisDataSourceEnvRequest = new LinkisDataSourceEnvRequest();
                    linkisDataSourceEnvRequest.setDataSourceTypeId(dataSourceTypeId);
                    linkisDataSourceEnvRequest.setConnectParamsRequest(dataSourceEnv.getConnectParams());
                    BeanUtils.copyProperties(dataSourceEnv, linkisDataSourceEnvRequest);
                    return linkisDataSourceEnvRequest;
                })
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(createDatasourceEnvList)) {
            linkisMetaDataManager.createDataSourceEnvAndSetEnvId(request.getInputType(), request.getVerifyType(), createDatasourceEnvList, clusterName, linkisConfig.getDatasourceAdmin());
        }

        List<LinkisDataSourceEnvRequest> linkisDataSourceEnvRequestList = Lists.newArrayListWithExpectedSize(dataSourceEnvList.size());
        linkisDataSourceEnvRequestList.addAll(createDatasourceEnvList);
        linkisDataSourceEnvRequestList.addAll(modifyDatasourceEnvList);

        linkisDataSourceService.modify(linkisDataSource, dataSourceTypeId, linkisDataSourceRequest, userInDb);

        if (CollectionUtils.isNotEmpty(modifyDatasourceEnvList)) {
            Map<String, LinkisDataSourceEnv> envNameMapInDb = linkisDataSourceEnvList.stream().collect(Collectors.toMap(LinkisDataSourceEnv::getEnvName, Function.identity(), (k1, k2) -> k1));
            List<LinkisDataSourceEnv> modifyDatasourceEnvListInDb = modifyDatasourceEnvList.stream().map(dataSourceEnvRequest -> {
                LinkisDataSourceEnv linkisDataSourceEnv = new LinkisDataSourceEnv();
                linkisDataSourceEnv.setId(envNameMapInDb.get(dataSourceEnvRequest.getEnvName()).getId());
                linkisDataSourceEnv.setLinkisDataSourceId(linkisDataSource.getLinkisDataSourceId());
                linkisDataSourceEnv.setEnvId(dataSourceEnvRequest.getId());
                linkisDataSourceEnv.setEnvName(dataSourceEnvRequest.getEnvName());
                linkisDataSourceEnv.setDcnNum(dataSourceEnvRequest.getDcnNum());
                linkisDataSourceEnv.setLogicArea(dataSourceEnvRequest.getLogicArea());
                return linkisDataSourceEnv;
            }).filter(Objects::nonNull).collect(Collectors.toList());
            linkisDataSourceEnvService.modifyBatch(modifyDatasourceEnvListInDb);
        }
        linkisDataSourceEnvService.createBatch(linkisDataSource.getLinkisDataSourceId(), createDatasourceEnvList);

        dataVisibilityService.delete(linkisDataSource.getId(), TableDataTypeEnum.LINKIS_DATA_SOURCE);
        dataVisibilityService.saveBatch(linkisDataSource.getId(), TableDataTypeEnum.LINKIS_DATA_SOURCE, request.getVisibilityDepartmentList());

//        update rule_datasource.linkis_datasource_name if datasourceName was updated
        ruleDataSourceDao.updateLinkisDataSourceName(linkisDataSource.getLinkisDataSourceId(), request.getDataSourceName());

        List<String> envIdArray = linkisDataSourceEnvRequestList.stream().map(LinkisDataSourceEnvRequest::getId).map(String::valueOf).collect(Collectors.toList());
        modifyDataSourceParam(request, envIdArray, "Updated");

        return new GeneralResponse<>(ResponseStatusConstants.OK, "success", linkisDataSource.getLinkisDataSourceId());
    }

    public List<Rule> getRulesRelatedTo(Long linkisDataSourceId) {
        List<RuleDataSource> ruleDataSources = ruleDataSourceDao.findByLinkisDataSourceId(linkisDataSourceId);
        return ruleDataSources.stream().filter(ruleDataSource -> {
            Rule rule = ruleDataSource.getRule();
            if (null != rule && rule.getEnable()) {
                return true;
            }
            return false;
        }).map(RuleDataSource::getRule).collect(Collectors.toList());
    }

    @Override
    public OuterDataSourceDetailResponse getDataSourceDetail(String linkisDataSourceName, Long versionId, String username) throws Exception {
        LinkisDataSource linkisDataSource = linkisDataSourceService.getByLinkisDataSourceName(linkisDataSourceName);
        if (null == linkisDataSource) {
            throw new UnExpectedRequestException("DataSource doesn't exist!");
        }
        if (null != versionId && linkisDataSource.getVersionId() < versionId) {
            throw new UnExpectedRequestException("The version_id doesn't exist!");
        }
        User userInDb = userDao.findByUsername(username);
        if (userInDb == null) {
            throw new UnExpectedRequestException("User doesn't exist!, username: " + username);
        }

        DataVisibilityPermissionDto dataVisibilityPermissionDto = new DataVisibilityPermissionDto.Builder()
                .createUser(linkisDataSource.getCreateUser())
                .devDepartmentId(linkisDataSource.getDevDepartmentId())
                .opsDepartmentId(linkisDataSource.getOpsDepartmentId())
                .build();
        subDepartmentPermissionService.checkAccessiblePermission(userInDb, linkisDataSource.getId(), TableDataTypeEnum.LINKIS_DATA_SOURCE, dataVisibilityPermissionDto);

        LinkisDataSourceInfoDetail linkisDataSourceInfoDetail = metaDataClient.getDataSourceInfoById(linkisConfig.getDatasourceCluster(), linkisConfig.getDatasourceAdmin(), linkisDataSource.getLinkisDataSourceId(), versionId);
        OuterDataSourceDetailResponse linkisDataSourceResponse = new OuterDataSourceDetailResponse(linkisDataSource);
        BeanUtils.copyProperties(linkisDataSourceInfoDetail, linkisDataSourceResponse);
        linkisDataSourceResponse.setDataSourceTypeName(linkisDataSourceInfoDetail.getDataSourceType().getName());
        linkisDataSourceResponse.setDataSourceEnvs(convertDataSourceEnvs(linkisDataSource, linkisDataSourceInfoDetail));
        linkisDataSourceResponse.setSubSystem(MapUtils.getString(linkisDataSourceInfoDetail.getConnectParams(), "subSystem"));
        if (linkisDataSource.getVerifyType() == QualitisConstants.DATASOURCE_MANAGER_VERIFY_TYPE_SHARE) {
            LinkisConnectParamsRequest connectParamsRequest = createConnectParams(linkisDataSourceInfoDetail.getConnectParams());
            linkisDataSourceResponse.setConnectParams(connectParamsRequest);
        }

        List<DepartmentSubInfoResponse> departmentInfoResponses = new ArrayList<>();
        List<DataVisibility> dataVisibilityList = dataVisibilityService.filter(linkisDataSource.getId(), TableDataTypeEnum.LINKIS_DATA_SOURCE);
        if (CollectionUtils.isNotEmpty(dataVisibilityList)) {
            departmentInfoResponses = dataVisibilityList.stream().map(dataVisibility -> {
                DepartmentSubInfoResponse departmentInfoResponse = new DepartmentSubInfoResponse();
                departmentInfoResponse.setId(dataVisibility.getDepartmentSubId());
                departmentInfoResponse.setName(dataVisibility.getDepartmentSubName());
                return departmentInfoResponse;
            }).collect(Collectors.toList());
        }
        linkisDataSourceResponse.setVisibilityDepartmentList(departmentInfoResponses);
        linkisDataSourceResponse.setVersionId(versionId);
        return linkisDataSourceResponse;
    }

    @Override
    public GeneralResponse publish(String linkisDataSourceName, Long versionId, String username) throws Exception {
        LinkisDataSource linkisDataSource = linkisDataSourceService.getByLinkisDataSourceName(linkisDataSourceName);
        if (null == linkisDataSource) {
            throw new UnExpectedRequestException("DataSource doesn't exist!");
        }
        User userInDb = userDao.findByUsername(username);
        if (userInDb == null) {
            throw new UnExpectedRequestException("User doesn't exist, username: " + username);
        }
        List<UserRole> userRoles = userRoleDao.findByUser(userInDb);
        Integer roleType = roleService.getRoleType(userRoles);
        subDepartmentPermissionService.checkEditablePermission(roleType, userInDb, linkisDataSource.getCreateUser(), linkisDataSource.getDevDepartmentId(), linkisDataSource.getOpsDepartmentId(), false);

        return metaDataClient.publishDataSource(linkisConfig.getDatasourceCluster(), linkisConfig.getDatasourceAdmin(), linkisDataSource.getLinkisDataSourceId(), versionId);
    }

    @Override
    public GeneralResponse connect(String linkisDataSourceName, Long versionId, String username) throws Exception {
        LinkisDataSource linkisDataSource = linkisDataSourceService.getByLinkisDataSourceName(linkisDataSourceName);
        if (null == linkisDataSource) {
            throw new UnExpectedRequestException("DataSource doesn't exist!");
        }
        OuterDataSourceDetailResponse linkisDataSourceResponse = getDataSourceDetail(linkisDataSourceName, versionId, username);
        LinkisDataSourceInfoDetail linkisDataSourceInfoDetail = new LinkisDataSourceInfoDetail();
        BeanUtils.copyProperties(linkisDataSourceResponse, linkisDataSourceInfoDetail);
        linkisDataSourceInfoDetail.setId(linkisDataSource.getLinkisDataSourceId());
        linkisDataSourceInfoDetail.setDataSourceTypeId(linkisDataSource.getDataSourceTypeId());

        ObjectMapper objectMapper = new ObjectMapper();
        String dataSourceJson = objectMapper.writeValueAsString(linkisDataSourceInfoDetail);
        Map<String, Object> dataSourceMap = objectMapper.readValue(dataSourceJson, Map.class);
        Map<String, Object> connectMap = new HashMap<>();

        boolean isShare = QualitisConstants.DATASOURCE_MANAGER_VERIFY_TYPE_SHARE == linkisDataSourceResponse.getVerifyType();
        if (isShare) {
            LinkisConnectParamsRequest connectParams = linkisDataSourceResponse.getConnectParams();
            String authType = connectParams.getAuthType();
            connectMap.put("authType", authType);
            if (QualitisConstants.AUTH_TYPE_DPM.equals(authType)) {
                connectMap.put("objectid", connectParams.getObjectId());
                connectMap.put("mkPrivate", connectParams.getMkPrivate());
                connectMap.put("appid", connectParams.getAppId());
                String dk = connectParams.getDk();
                if (StringUtils.isNotEmpty(dk)) {
                    connectMap.put("dk", dk);
                }
            } else if (QualitisConstants.AUTH_TYPE_ACCOUNT_PWD.equals(authType)) {
                connectMap.put("username", connectParams.getUsername());
                connectMap.put("password", connectParams.getPassword());
            }
            connectMap.put("timestamp", connectParams.getTimeStamp());
        }

        dataSourceMap.put("connectParams", connectMap);
        List<OuterDataSourceEnvResponse> dataSourceEnvs = linkisDataSourceResponse.getDataSourceEnvs();
        if (CollectionUtils.isNotEmpty(dataSourceEnvs)) {
            for (OuterDataSourceEnvResponse dataSourceEnv : dataSourceEnvs) {
                LinkisConnectParamsRequest connectParams = dataSourceEnv.getConnectParams();
                if (!isShare) {
                    String authType = connectParams.getAuthType();
                    connectMap.put("authType", authType);
                    if (QualitisConstants.AUTH_TYPE_DPM.equals(authType)) {
                        connectMap.put("objectid", connectParams.getObjectId());
                        connectMap.put("mkPrivate", connectParams.getMkPrivate());
                        connectMap.put("appid", connectParams.getAppId());
                        String dk = connectParams.getDk();
                        if (StringUtils.isNotEmpty(dk)) {
                            connectMap.put("dk", dk);
                        }
                    } else if (QualitisConstants.AUTH_TYPE_ACCOUNT_PWD.equals(authType)) {
                        connectMap.put("username", connectParams.getUsername());
                        connectMap.put("password", connectParams.getPassword());
                    }
                    connectMap.put("timestamp", connectParams.getTimeStamp());
                }
                connectMap.put("host", connectParams.getHost());
                connectMap.put("port", connectParams.getPort());
                connectMap.put("params", connectParams.getConnectParam());
                dataSourceJson = objectMapper.writeValueAsString(dataSourceMap);
                try {
                    GeneralResponse<Map<String, Object>> resultMap = metaDataClient.connectDataSource(linkisConfig.getDatasourceCluster(), linkisConfig.getDatasourceAdmin(), dataSourceJson);
                    if (!resultMap.getData().containsKey("ok")) {
                        return resultMap;
                    }
                } catch (MetaDataAcquireFailedException e) {
                    String errorMsg = String.format("环境{%s}连接失败", dataSourceEnv.getEnvName());
                    throw new MetaDataAcquireFailedException(errorMsg, 500);
                }
            }
        }
        return new GeneralResponse(ResponseStatusConstants.OK, "Connected!", null);
    }

    @Override
    public GeneralResponse expire(String linkisDataSourceName, String username) throws Exception {
        LinkisDataSource linkisDataSource = linkisDataSourceService.getByLinkisDataSourceName(linkisDataSourceName);
        if (null == linkisDataSource) {
            throw new UnExpectedRequestException("DataSource doesn't exist");
        }
        User userInDb = userDao.findByUsername(username);
        if (userInDb == null) {
            throw new UnExpectedRequestException("User doesn't exist, username: " + username);
        }
        List<UserRole> userRoles = userRoleDao.findByUser(userInDb);
        Integer roleType = roleService.getRoleType(userRoles);
        subDepartmentPermissionService.checkEditablePermission(roleType, userInDb, linkisDataSource.getCreateUser(), linkisDataSource.getDevDepartmentId(), linkisDataSource.getOpsDepartmentId(), false);

        return metaDataClient.expireDataSource(linkisConfig.getDatasourceCluster(), linkisConfig.getDatasourceAdmin(), linkisDataSource.getLinkisDataSourceId());
    }

    @Override
    public List<OuterDataSourceVersionResponse> versions(String linkisDataSourceName, String username) throws Exception {
        LinkisDataSource linkisDataSource = linkisDataSourceService.getByLinkisDataSourceName(linkisDataSourceName);
        if (null == linkisDataSource) {
            throw new UnExpectedRequestException("DataSource doesn't exist");
        }
        User userInDb = userDao.findByUsername(username);
        if (userInDb == null) {
            throw new UnExpectedRequestException("User doesn't exist, username: " + username);
        }

        DataVisibilityPermissionDto dataVisibilityPermissionDto = new DataVisibilityPermissionDto.Builder()
                .createUser(linkisDataSource.getCreateUser())
                .devDepartmentId(linkisDataSource.getDevDepartmentId())
                .opsDepartmentId(linkisDataSource.getOpsDepartmentId())
                .build();
        subDepartmentPermissionService.checkAccessiblePermission(userInDb, linkisDataSource.getId(), TableDataTypeEnum.LINKIS_DATA_SOURCE, dataVisibilityPermissionDto);

        GeneralResponse generalResponse = metaDataClient.getDataSourceVersions(linkisConfig.getDatasourceCluster(), linkisConfig.getDatasourceAdmin(), linkisDataSource.getLinkisDataSourceId());
        if (ResponseStatusConstants.OK.equals(generalResponse.getCode()) && Objects.nonNull(generalResponse.getData())) {
            Map<String, Object> dataMap = (Map<String, Object>) generalResponse.getData();
            if (dataMap.containsKey("versions")) {
                List<Map<String, Object>> versionList = (List) dataMap.getOrDefault("versions", Collections.emptyList());
                return versionList.stream().map(versionMap -> {
                    OuterDataSourceVersionResponse versionResponse = new OuterDataSourceVersionResponse();
                    versionResponse.setVersionId(Long.valueOf(versionMap.get("versionId").toString()));
                    versionResponse.setComment((String) versionMap.get("comment"));
                    Map<String, Object> connectMap = MapUtils.getMap(versionMap, "connectParams");
                    if (MapUtils.isNotEmpty(connectMap)) {
                        LinkisConnectParamsRequest linkisConnectParamsRequest = new LinkisConnectParamsRequest();
                        linkisConnectParamsRequest.setAuthType(MapUtils.getString(connectMap, "authType"));
                        linkisConnectParamsRequest.setUsername(MapUtils.getString(connectMap, "username"));
                        linkisConnectParamsRequest.setPassword(MapUtils.getString(connectMap, "password"));
                        linkisConnectParamsRequest.setAppId(MapUtils.getString(connectMap, "appid"));
                        linkisConnectParamsRequest.setObjectId(MapUtils.getString(connectMap, "objectid"));
                        linkisConnectParamsRequest.setMkPrivate(MapUtils.getString(connectMap, "mkPrivate"));
                        versionResponse.setConnectParams(linkisConnectParamsRequest);
                    }
                    return versionResponse;
                }).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    @Override
    public void updateDcn(OuterDataSourceDcnRequest request) throws UnExpectedRequestException {
        CommonChecker.checkString(request.getUsername(), "username");
        CommonChecker.checkListMinSize(request.getDatasourceNameList(), 1, "datasource_name_list");
        List<LinkisDataSource> linkisDataSourceList = linkisDataSourceDao.getByLinkisDataSourceNameList(request.getDatasourceNameList());
        if (CollectionUtils.isEmpty(linkisDataSourceList)) {
            LOGGER.info("Stopped to sync dcn cause have no data source from database.");
            return;
        }
        List<String> dcnRangeTypes = Arrays.asList(QualitisConstants.CMDB_KEY_DCN_NUM, QualitisConstants.CMDB_KEY_LOGIC_AREA);

        List<SubSystemResponse> subSystemResponseList = operateCiService.getAllSubSystemInfo();
        Map<String, String> subSystemNameAndIdMap = subSystemResponseList.stream().collect(Collectors.toMap(SubSystemResponse::getSubSystemName, SubSystemResponse::getSubSystemId, (k1, k2) -> k1));

        for (LinkisDataSource linkisDataSource : linkisDataSourceList) {
//            To process only the data what the value of dcn_range_type is dcn_num or logic_area
            if (!dcnRangeTypes.contains(linkisDataSource.getDcnRangeType())) {
                continue;
            }
            if (!subSystemNameAndIdMap.containsKey(linkisDataSource.getSubSystem())) {
                LOGGER.warn("Failed to sync DCN from cmdb cause sub_system_id is null. datasource name is: {}", linkisDataSource.getLinkisDataSourceName());
                continue;
            }
//            To retrieve dcn_num or logic_area what need to sync update
            List<LinkisDataSourceEnv> linkisDataSourceEnvList = linkisDataSourceEnvService.queryAllEnvs(linkisDataSource.getLinkisDataSourceId());
            List<String> dcnRangeValues = linkisDataSourceEnvList.stream().map(linkisDataSourceEnv -> {
                if (QualitisConstants.CMDB_KEY_DCN_NUM.equals(linkisDataSource.getDcnRangeType())) {
                    return linkisDataSourceEnv.getDcnNum();
                } else {
                    return linkisDataSourceEnv.getLogicArea();
                }
            }).distinct().collect(Collectors.toList());
            if (CollectionUtils.isEmpty(dcnRangeValues)) {
                continue;
            }

//            To retrieve DCNs from cmdb by sub-system and dcn_num(or logic_area)
            GeneralResponse generalResponse = operateCiService.getDcn(subSystemNameAndIdMap.get(linkisDataSource.getSubSystem()), linkisDataSource.getDcnRangeType(), dcnRangeValues);
            if (!ResponseStatusConstants.OK.equals(generalResponse.getCode())) {
                LOGGER.error("Failed to fetch DCN from cmdb: server error.");
                continue;
            }
            Map<Object, List<Map<String, Object>>> dcnRangeTypeAndDCNMap = (Map<Object, List<Map<String, Object>>>) generalResponse.getData();
            if (MapUtils.isEmpty(dcnRangeTypeAndDCNMap)) {
                LOGGER.error("Failed to fetch DCN from cmdb: the data of response is empty.");
                continue;
            }
            List<Map<String, Object>> lastedDcnList = dcnRangeValues.stream().map(dcnRangeTypeAndDCNMap::get).filter(Objects::nonNull).flatMap(List::stream).collect(Collectors.toList());

//            To check if there are new DCNs from lastedDcnList by comparing to env_name stored in the local database
            List<String> envNamesInDb = linkisDataSourceEnvList.stream().map(LinkisDataSourceEnv::getEnvName).collect(Collectors.toList());
            List<LinkisDataSourceEnvRequest> newDataSourceEnvRequestList = lastedDcnList.stream().filter(entry -> {
                String linkisEnvName = linkisDataSourceService.convertOriginalEnvNameToLinkis(linkisDataSource.getLinkisDataSourceId(), MapUtils.getString(entry, QualitisConstants.CMDB_KEY_DCN_NUM), linkisDataSource.getInputType()
                        , MapUtils.getString(entry, "vip"), MapUtils.getString(entry, "gwport"), MapUtils.getString(entry, "dbinstance_name"));
                return !envNamesInDb.contains(linkisEnvName);
            }).map(entry -> {
                LinkisDataSourceEnvRequest envRequest = new LinkisDataSourceEnvRequest();
                envRequest.setDcnNum(MapUtils.getString(entry, QualitisConstants.CMDB_KEY_DCN_NUM));
                envRequest.setLogicArea(MapUtils.getString(entry, QualitisConstants.CMDB_KEY_LOGIC_AREA));
                envRequest.setDatabaseInstance(MapUtils.getString(entry, "dbinstance_name"));
                envRequest.setDataSourceTypeId(linkisDataSource.getDataSourceTypeId());
                LinkisConnectParamsRequest linkisConnectParamsRequest = new LinkisConnectParamsRequest();
                linkisConnectParamsRequest.setHost(MapUtils.getString(entry, "vip"));
                linkisConnectParamsRequest.setPort(MapUtils.getString(entry, "gwport"));
                envRequest.setConnectParamsRequest(linkisConnectParamsRequest);
                String linkisEnvName = linkisDataSourceService.convertOriginalEnvNameToLinkis(linkisDataSource.getLinkisDataSourceId()
                        , MapUtils.getString(entry, QualitisConstants.CMDB_KEY_DCN_NUM), linkisDataSource.getInputType()
                        , linkisConnectParamsRequest.getHost(), linkisConnectParamsRequest.getPort(), envRequest.getDatabaseInstance());
                envRequest.setEnvName(linkisEnvName);
                return envRequest;
            }).collect(Collectors.toList());

            if (CollectionUtils.isEmpty(newDataSourceEnvRequestList)) {
                continue;
            }

//            To wrap new DCNs as dataSourceEnv and push them to Linkis
            try {
                linkisMetaDataManager.createDataSourceEnvAndSetEnvId(linkisDataSource.getInputType(), linkisDataSource.getVerifyType(), newDataSourceEnvRequestList, linkisConfig.getDatasourceCluster(), linkisConfig.getDatasourceAdmin());
            } catch (MetaDataAcquireFailedException e) {
                LOGGER.error("Failed to push new env to Linkis.", e);
                continue;
            }
            linkisDataSource.setModifyTime(DateUtils.now());
            linkisDataSource.setModifyUser(request.getUsername());
            linkisDataSourceService.save(linkisDataSource);
//            To save new DCNs wrapped as dataSourceEnv into the local database
            linkisDataSourceEnvService.createBatch(linkisDataSource.getLinkisDataSourceId(), newDataSourceEnvRequestList);
        }
    }

    @Override
    public List<String> getDataSourceNames(String username) {
        Page<LinkisDataSource> linkisDataSourcePage = linkisDataSourceDao.filterWithPage(null,null,null,null, username, null,null,null,null,false,null,0,Integer.MAX_VALUE);
        if (linkisDataSourcePage.isEmpty()) {
            return Collections.emptyList();
        }
        return linkisDataSourcePage.getContent().stream().map(LinkisDataSource::getLinkisDataSourceName).collect(Collectors.toList());
    }

    private List<OuterDataSourceEnvResponse> convertDataSourceEnvs(LinkisDataSource linkisDataSource, LinkisDataSourceInfoDetail linkisDataSourceInfoDetail) throws UnExpectedRequestException, MetaDataAcquireFailedException, JsonProcessingException {
        List<Object> envIdArray = Collections.emptyList();
        if (MapUtils.isNotEmpty(linkisDataSourceInfoDetail.getConnectParams())) {
            Map<String, Object> connectParams = linkisDataSourceInfoDetail.getConnectParams();
            if (connectParams.containsKey("envIdArray")) {
                envIdArray = (List<Object>) connectParams.get("envIdArray");
            }
        }
        List<LinkisDataSourceEnv> linkisDataSourceEnvList = linkisDataSourceEnvService.queryAllEnvs(linkisDataSource.getLinkisDataSourceId());

        List<String> envNameListFromLinkis = Lists.newArrayListWithExpectedSize(envIdArray.size());
        List<OuterDataSourceEnvResponse> dataSourceEnvs = Lists.newArrayListWithExpectedSize(envIdArray.size());
        for (Object envId : envIdArray) {
            GeneralResponse<Map<String, Object>> envResponse = metaDataClient.getDatasourceEnvById(linkisConfig.getDatasourceCluster(), linkisConfig.getDatasourceAdmin(), Long.valueOf(envId.toString()));
            if (!ResponseStatusConstants.OK.equals(envResponse.getCode())) {
                continue;
            }
            Map<String, Object> envResponseData = envResponse.getData();
            if (MapUtils.isEmpty(envResponseData)) {
                continue;
            }
            if (envResponseData.containsKey("env")) {
                Map<String, Object> dataSourceEnv = (Map<String, Object>) envResponseData.get("env");
                if (Objects.isNull(dataSourceEnv)) {
                    continue;
                }
                OuterDataSourceEnvResponse linkisDataSourceEnvRes= new OuterDataSourceEnvResponse();
                if (dataSourceEnv.containsKey("connectParams")) {
                    Map<String, Object> connectParams = (Map<String, Object>) dataSourceEnv.get("connectParams");
                    LinkisConnectParamsRequest connectParamsRequest = createConnectParams(connectParams);
                    linkisDataSourceEnvRes.setConnectParams(connectParamsRequest);
                }

                String linkisEnvName = MapUtils.getString(dataSourceEnv, "envName");
                envNameListFromLinkis.add(linkisEnvName);
                if (Objects.nonNull(linkisDataSource)) {
                    String originalEnvName = linkisEnvName.replace(linkisDataSource.getLinkisDataSourceId() + SpecCharEnum.BOTTOM_BAR.getValue(), "");
                    dataSourceEnv.put("envName", originalEnvName);
                }

                linkisDataSourceEnvRes.setId(Long.valueOf(envId.toString()));
                linkisDataSourceEnvRes.setEnvName(MapUtils.getString(dataSourceEnv, "envName"));
                linkisDataSourceEnvRes.setEnvDesc(MapUtils.getString(dataSourceEnv, "envDesc"));

                Optional<LinkisDataSourceEnv> linkisDataSourceEnvInDb = linkisDataSourceEnvList.stream().filter(linkisDataSourceEnv -> linkisDataSourceEnv.getEnvId().equals(linkisDataSourceEnvRes.getId())).findFirst();
                if (linkisDataSourceEnvInDb.isPresent()) {
                    linkisDataSourceEnvRes.setDcnNum(linkisDataSourceEnvInDb.get().getDcnNum());
                    linkisDataSourceEnvRes.setLogicArea(linkisDataSourceEnvInDb.get().getLogicArea());
                }

                dataSourceEnvs.add(linkisDataSourceEnvRes);
            }
        }

        return dataSourceEnvs;
    }

    private LinkisConnectParamsRequest createConnectParams(Map<String, Object> connectParams) {
        LinkisConnectParamsRequest connectParamsRequest = new LinkisConnectParamsRequest();
        connectParamsRequest.setAppId(MapUtils.getString(connectParams, "appid"));
        connectParamsRequest.setObjectId(MapUtils.getString(connectParams, "objectid"));
        connectParamsRequest.setUsername(MapUtils.getString(connectParams, "username"));
        connectParamsRequest.setPassword(MapUtils.getString(connectParams, "password"));
        connectParamsRequest.setAuthType(MapUtils.getString(connectParams, "authType"));
        connectParamsRequest.setHost(MapUtils.getString(connectParams, "host"));
        connectParamsRequest.setPort(MapUtils.getString(connectParams, "port"));
        return connectParamsRequest;
    }

    private void validateAndResetEnvName(Long dataSourceId, Integer inputType, List<OuterDataSourceEnvRequest> dataSourceEnvList) throws UnExpectedRequestException {
//        add prefix to env_name
        int totalLength = QualitisConstants.ACTUAL_ENV_NAME_LENGTH;
        for (OuterDataSourceEnvRequest dataSourceEnv : dataSourceEnvList) {
            String linkisEnvName = linkisDataSourceService.convertOriginalEnvNameToLinkis(dataSourceId, dataSourceEnv.getEnvName(), inputType
                    , dataSourceEnv.getConnectParams().getHost(), dataSourceEnv.getConnectParams().getPort(), dataSourceEnv.getDatabaseInstance());
            if (linkisEnvName.length() > totalLength) {
                int maxLength = totalLength - String.valueOf(dataSourceId).length() - 1;
                throw new UnExpectedRequestException("env_name {&EXCEED_MAX_LENGTH}: " + maxLength);
            }
            dataSourceEnv.setEnvName(linkisEnvName);
        }
        boolean isDuplicateEnvName = dataSourceEnvList.stream().map(OuterDataSourceEnvRequest::getEnvName).distinct().count() < dataSourceEnvList.size();
        if (isDuplicateEnvName) {
            throw new UnExpectedRequestException("Duplicate env name!");
        }
    }
}
