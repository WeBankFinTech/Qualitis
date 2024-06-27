package com.webank.wedatasphere.qualitis.rule.service.impl;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.dao.UserRoleDao;
import com.webank.wedatasphere.qualitis.dto.DataVisibilityPermissionDto;
import com.webank.wedatasphere.qualitis.entity.Department;
import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.entity.UserRole;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.DataStandardClient;
import com.webank.wedatasphere.qualitis.metadata.client.OperateCiService;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.query.request.EditionRequest;
import com.webank.wedatasphere.qualitis.query.request.StandardValueRequest;
import com.webank.wedatasphere.qualitis.request.DepartmentSubInfoRequest;
import com.webank.wedatasphere.qualitis.response.DepartmentSubInfoResponse;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.rule.constant.RoleSystemTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.StandardApproveEnum;
import com.webank.wedatasphere.qualitis.rule.constant.StandardSourceEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TableDataTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.dao.StandardValueDao;
import com.webank.wedatasphere.qualitis.rule.dao.StandardValueDepartmentVersionDao;
import com.webank.wedatasphere.qualitis.rule.dao.StandardValueLabelVersionDao;
import com.webank.wedatasphere.qualitis.rule.dao.StandardValueUserVersionDao;
import com.webank.wedatasphere.qualitis.rule.dao.StandardValueVersionDao;
import com.webank.wedatasphere.qualitis.rule.entity.DataVisibility;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.StandardValueDepartmentVersion;
import com.webank.wedatasphere.qualitis.rule.entity.StandardValueLabelVersion;
import com.webank.wedatasphere.qualitis.rule.entity.StandardValueUserVersion;
import com.webank.wedatasphere.qualitis.rule.entity.StandardValueVersion;
import com.webank.wedatasphere.qualitis.rule.request.AddStandardValueRequest;
import com.webank.wedatasphere.qualitis.rule.request.DeleteStandardValueVersionRequest;
import com.webank.wedatasphere.qualitis.rule.request.DmsQueryRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyStandardValueRequest;
import com.webank.wedatasphere.qualitis.rule.response.StandardValueResponse;
import com.webank.wedatasphere.qualitis.rule.service.StandardValueService;
import com.webank.wedatasphere.qualitis.service.DataVisibilityService;
import com.webank.wedatasphere.qualitis.service.RoleService;
import com.webank.wedatasphere.qualitis.service.SubDepartmentPermissionService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author v_gaojiedeng@webank.com
 */
@Service
public class StandardValueServiceImpl implements StandardValueService {

    @Autowired
    private StandardValueDao standardValueDao;
    @Autowired
    private StandardValueVersionDao standardValueVersionDao;
    @Autowired
    private StandardValueDepartmentVersionDao standardValueDepartmentVersionDao;
    @Autowired
    private StandardValueUserVersionDao standardValueUserVersionDao;
    @Autowired
    private StandardValueLabelVersionDao standardValueLabelVersionDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserRoleDao userRoleDao;
    @Autowired
    private RoleService roleService;
    @Autowired
    private RuleDao ruleDao;
    @Autowired
    private DataVisibilityService dataVisibilityService;
    @Autowired
    private OperateCiService operateCiService;
    @Autowired
    private SubDepartmentPermissionService subDepartmentPermissionService;

    @Autowired
    private DataStandardClient dataStandardClient;

    private final Long ALL_DEPARTMENT_VISIBILITY = 0L;

    private static final Logger LOGGER = LoggerFactory.getLogger(StandardValueServiceImpl.class);

    public static final FastDateFormat PRINT_TIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    private HttpServletRequest httpServletRequest;

    public StandardValueServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class, PermissionDeniedRequestException.class})
    public StandardValueResponse addStandardValue(AddStandardValueRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        AddStandardValueRequest.checkRequest(request);

        User userInDb = userDao.findById(HttpUtils.getUserId(httpServletRequest));
        List<UserRole> userRoles = userRoleDao.findByUser(userInDb);
        Integer roleType = roleService.getRoleType(userRoles);
        List<DepartmentSubInfoRequest> departmentSubInfoRequests = request.getVisibilityDepartmentList();
        boolean allVisibility = false;
        if (CollectionUtils.isNotEmpty(departmentSubInfoRequests)) {
            allVisibility = departmentSubInfoRequests.stream().map(DepartmentSubInfoRequest::getId).anyMatch(ALL_DEPARTMENT_VISIBILITY::equals);
        }
        subDepartmentPermissionService.checkEditablePermission(roleType, userInDb, null, request.getDevDepartmentId(), request.getOpsDepartmentId(), allVisibility);

        List<StandardValueVersion> list = standardValueVersionDao.findAllData(request.getEnName());
        if (CollectionUtils.isNotEmpty(list)) {
            throw new UnExpectedRequestException("{&STANDARD_VALUE_EN_NAME}: [" + request.getEnName() + "] {&ALREADY_EXIST}");
        }

        StandardValueVersion newStandardValueVersion = insertStandardValueVersion(request);

        StandardValueResponse standardValueResponse = new StandardValueResponse(newStandardValueVersion);
        List<DepartmentSubInfoResponse> departmentInfoResponses = dataVisibilityService.saveBatch(newStandardValueVersion.getId(), TableDataTypeEnum.STANDARD_VALUE, request.getVisibilityDepartmentList());
        standardValueResponse.setVisibilityDepartmentList(departmentInfoResponses);
        return standardValueResponse;
    }

    private StandardValueVersion insertStandardValueVersion(AddStandardValueRequest request) {
        StandardValueVersion standardValueVersion = new StandardValueVersion();
        BeanUtils.copyProperties(request, standardValueVersion);

        standardValueVersion.setAvailable(true);
        standardValueVersion.setCreateUser(HttpUtils.getUserName(httpServletRequest));
        standardValueVersion.setCreateTime(StandardValueServiceImpl.PRINT_TIME_FORMAT.format(new Date()));

        StandardValueVersion newStandardValueVersion = standardValueVersionDao.saveStandardValueVersion(standardValueVersion);

        return newStandardValueVersion;
    }


    public void addStandardValueLabels(Set<String> labels, StandardValueVersion standardValueVersion) {
        Map<String, StandardValueLabelVersion> map = new HashMap<>(2);
        if (standardValueVersion.getStandardValueLabelVersion() != null) {
            for (StandardValueLabelVersion projectLabel : standardValueVersion.getStandardValueLabelVersion()) {
                map.put(projectLabel.getLabelName(), projectLabel);
            }
        } else {
            standardValueVersion.setStandardValueLabelVersion(new HashSet<>());
        }
        if (labels != null && !labels.isEmpty()) {
            Set<StandardValueLabelVersion> standardValueLabels = new HashSet<>();
            for (String labelName : labels) {
                if (map.keySet().contains(labelName)) {
                    continue;
                }
                StandardValueLabelVersion standardValueLabelVersion = new StandardValueLabelVersion();
                standardValueLabelVersion.setStandardValueVersion(standardValueVersion);
                standardValueLabelVersion.setLabelName(labelName);
                standardValueLabels.add(standardValueLabelVersion);
            }
            LOGGER.info("Start to save StandardValue. Labels: {}", Arrays.toString(standardValueLabels.toArray()));
            standardValueLabelVersionDao.saveAll(standardValueLabels);
            for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext(); ) {
                if (!labels.contains(iterator.next())) {
                    iterator.remove();
                }
            }
            standardValueVersion.setStandardValueLabelVersion(new HashSet<>(map.values()));
            LOGGER.info("Succeed to save standardValueLabel.");
        } else {
            standardValueVersion.setStandardValueLabelVersion(null);
        }
    }

    private void checkStandardValueVersionName(String standardValueName) throws UnExpectedRequestException {
        List<StandardValueVersion> list = standardValueVersionDao.findAll();
        LOGGER.info("Number of StandardValueVersion in database is {}", list.size());
        for (StandardValueVersion standardValueVersion : list) {
            if (standardValueName.equals(standardValueVersion.getEnName())) {
                throw new UnExpectedRequestException("{&STANDARD_VALUE_EN_NAME}: [" + standardValueVersion.getEnName() + "] {&ALREADY_EXIST}");
            }
        }
    }

    public StandardValueVersion checkStandardValueVersion(Long id) throws UnExpectedRequestException {
        StandardValueVersion standardValueVersion = standardValueVersionDao.findById(id);
        if (standardValueVersion == null) {
            throw new UnExpectedRequestException("standardValueVersion {&DOES_NOT_EXIST}");
        }
        return standardValueVersion;
    }

    private void clearStandardValueUser(Long id) {
        List<StandardValueUserVersion> list = standardValueUserVersionDao.findListStandardValueUserVersion(id);
        if (CollectionUtils.isNotEmpty(list)) {
            for (StandardValueUserVersion standardValueUserVersion : list) {
                standardValueUserVersionDao.deleteStandardValueUserVersion(standardValueUserVersion);
            }
        }
    }

    private void clearStandardVauleDeptment(Long id) {
        List<StandardValueDepartmentVersion> list = standardValueDepartmentVersionDao.findListStandardValueDepartmentVersion(id);
        if (CollectionUtils.isNotEmpty(list)) {
            for (StandardValueDepartmentVersion standardValueDepartmentVersion : list) {
                standardValueDepartmentVersionDao.deleteStandardValueDepartmentVersion(standardValueDepartmentVersion);
            }
        }
    }


    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public StandardValueResponse modifyStandardValue(ModifyStandardValueRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        ModifyStandardValueRequest.checkRequest(request);
        StandardValueVersion standardValueVersion = checkStandardValueVersion(request.getEditionId());
        // Check user info.
        User userInDb = userDao.findById(HttpUtils.getUserId(httpServletRequest));
        List<UserRole> userRoles = userRoleDao.findByUser(userInDb);
        Integer roleType = roleService.getRoleType(userRoles);

        List<DepartmentSubInfoRequest> visibilityDepartmentNameList = request.getVisibilityDepartmentList();
        boolean allVisibility = false;
        if (CollectionUtils.isNotEmpty(visibilityDepartmentNameList)) {
            allVisibility = visibilityDepartmentNameList.stream().map(DepartmentSubInfoRequest::getId).anyMatch(ALL_DEPARTMENT_VISIBILITY::equals);
        }
        subDepartmentPermissionService.checkEditablePermission(roleType, userInDb, standardValueVersion.getCreateUser(), request.getDevDepartmentId(), request.getOpsDepartmentId(), allVisibility);

        if (!standardValueVersion.getEnName().equals(request.getEnName())) {
            List<StandardValueVersion> list = standardValueVersionDao.findAllData(request.getEnName());
            if (CollectionUtils.isNotEmpty(list)) {
                throw new UnExpectedRequestException("{&STANDARD_VALUE_EN_NAME}: [" + request.getEnName() + "] {&ALREADY_EXIST}");
            }
        }

        BeanUtils.copyProperties(request, standardValueVersion);

        standardValueVersion.setModifyUser(userInDb.getUsername());
        standardValueVersion.setModifyTime(StandardValueServiceImpl.PRINT_TIME_FORMAT.format(new Date()));
        StandardValueVersion oldValueVersion = standardValueVersionDao.saveStandardValueVersion(standardValueVersion);

        StandardValueResponse standardValueResponse = new StandardValueResponse(oldValueVersion);

        dataVisibilityService.delete(oldValueVersion.getId(), TableDataTypeEnum.STANDARD_VALUE);
        List<DepartmentSubInfoResponse> departmentInfoResponses = dataVisibilityService.saveBatch(oldValueVersion.getId(), TableDataTypeEnum.STANDARD_VALUE, request.getVisibilityDepartmentList());
        standardValueResponse.setVisibilityDepartmentList(departmentInfoResponses);
        return standardValueResponse;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public void deleteStandardValue(DeleteStandardValueVersionRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        DeleteStandardValueVersionRequest.checkRequest(request);
        StandardValueVersion standardValueVersion = checkStandardValueVersion(request.getEditionId());

        // Check operator permission
        User userInDb = userDao.findById(HttpUtils.getUserId(httpServletRequest));
        List<UserRole> userRoles = userRoleDao.findByUser(userInDb);
        Integer roleType = roleService.getRoleType(userRoles);

        List<DataVisibility> dataVisibilityList = dataVisibilityService.filter(standardValueVersion.getId(), TableDataTypeEnum.STANDARD_VALUE);
        boolean allVisibility = false;
        if (CollectionUtils.isNotEmpty(dataVisibilityList)) {
            allVisibility = dataVisibilityList.stream().map(DataVisibility::getDepartmentSubId).anyMatch(ALL_DEPARTMENT_VISIBILITY::equals);
        }
        subDepartmentPermissionService.checkEditablePermission(roleType, userInDb, standardValueVersion.getCreateUser(), standardValueVersion.getDevDepartmentId(), standardValueVersion.getOpsDepartmentId(), allVisibility);

        //校验规则是否存在有配置了该标准版本的id    1标准值列表2版本管理列表
        if (null != standardValueVersion.getStandardValue()) {
            List<StandardValueVersion> standardValueVersions = standardValueVersionDao.selectWhereStandardValueId(standardValueVersion.getStandardValue().getId());
            List<Long> idList = standardValueVersions.stream().map(u -> u.getId()).collect(Collectors.toList());
            List<Rule> ruleList = ruleDao.getDeployStandardVersionIdList(idList);
            if (CollectionUtils.isNotEmpty(ruleList)) {
                LOGGER.info("This is ruleList", Arrays.toString(ruleList.toArray()));
                throw new UnExpectedRequestException("{&STANDARD_VALUE_VERSION_ALREADY_CONFIGURED_IN_THE_RULE}.");
            } else {
                //删除关联数据
                standardValueDao.deleteStandardValue(standardValueVersion.getStandardValue());
                clearStandardValueUser(standardValueVersion.getId());
                clearStandardVauleDeptment(standardValueVersion.getId());
            }
        } else {
            List<Long> standardValueVersionIds = Lists.newArrayList();
            standardValueVersionIds.add(standardValueVersion.getId());
            List<Rule> ruleList = ruleDao.getDeployStandardVersionIdList(standardValueVersionIds);
            if (CollectionUtils.isNotEmpty(ruleList)) {
                LOGGER.info("This is ruleList", Arrays.toString(ruleList.toArray()));
                throw new UnExpectedRequestException("{&STANDARD_VALUE_VERSION_ALREADY_CONFIGURED_IN_THE_RULE}.");
            } else {
                //删除关联数据
                standardValueVersionDao.deleteStandardValueVersion(standardValueVersion);
                clearStandardValueUser(standardValueVersion.getId());
                clearStandardVauleDeptment(standardValueVersion.getId());
            }
        }
        dataVisibilityService.delete(standardValueVersion.getId(), TableDataTypeEnum.STANDARD_VALUE);
    }

    @Override
    public GeneralResponse<GetAllResponse<StandardValueResponse>> getAllStandardValue(StandardValueRequest request) throws UnExpectedRequestException {
        StandardValueRequest.checkRequest(request);
        LOGGER.info("Output input parameter log, editionId: {}, cnName: {}, enName: {}, createUser: {}," +
                " devDepartmentId: {}, opsDepartmentId: {}, sourceType: {}, page: {}, size: {}", request.getEditionId(), request.getCnName(),
                request.getEnName(), request.getCreateUser(), request.getDevDepartmentId(), request.getOpsDepartmentId(), request.getSourceType(), request.getPage(), request.getSize());
        List<StandardValueVersion> standardValueList;
        User userInDb = userDao.findById(HttpUtils.getUserId(httpServletRequest));
        List<UserRole> userRoles = userRoleDao.findByUser(userInDb);
        Integer roleType = roleService.getRoleType(userRoles);
        String cnName = null, enName = null;
        if (StringUtils.isNotBlank(request.getCnName())) {
            cnName = SpecCharEnum.PERCENT.getValue() + request.getCnName() + SpecCharEnum.PERCENT.getValue();
        }
        if (StringUtils.isNotBlank(request.getEnName())) {
            enName = SpecCharEnum.PERCENT.getValue() + request.getEnName() + SpecCharEnum.PERCENT.getValue();
        }

        Set<String> actionRangeSet = CollectionUtils.isEmpty(request.getActionRange()) ? null : request.getActionRange();

        long total;
        if (roleType.equals(RoleSystemTypeEnum.ADMIN.getCode())) {
            standardValueList = standardValueVersionDao.findAllStandardValue(cnName, enName, request.getEditionId(), request.getCreateUser(), request.getDevDepartmentId(), request.getOpsDepartmentId(), actionRangeSet, TableDataTypeEnum.STANDARD_VALUE.getCode(), request.getSourceType(), request.getModifyUser(), request.getPage(), request.getSize());
            total = standardValueVersionDao.countAllStandardValue(cnName, enName, request.getEditionId(), request.getCreateUser(), request.getDevDepartmentId(), request.getOpsDepartmentId(), actionRangeSet, TableDataTypeEnum.STANDARD_VALUE.getCode(), request.getSourceType(), request.getModifyUser());
        } else if (roleType.equals(RoleSystemTypeEnum.DEPARTMENT_ADMIN.getCode())) {
            List<Long> departmentIds = userRoles.stream().map(UserRole::getRole).filter(Objects::nonNull)
                    .map(Role::getDepartment).filter(Objects::nonNull)
                    .map(Department::getId).collect(Collectors.toList());
            if (Objects.nonNull(userInDb.getDepartment())) {
                departmentIds.add(userInDb.getDepartment().getId());
            }
            List<Long> devAndOpsInfoWithDeptList = subDepartmentPermissionService.getSubDepartmentIdList(departmentIds);
            standardValueList = standardValueVersionDao.findStandardValue(cnName, enName, request.getEditionId(), request.getCreateUser(), request.getDevDepartmentId(), request.getOpsDepartmentId(), actionRangeSet, TableDataTypeEnum.STANDARD_VALUE.getCode(), devAndOpsInfoWithDeptList.isEmpty() ? null : devAndOpsInfoWithDeptList, userInDb.getUsername(), request.getSourceType(),request.getModifyUser(), request.getPage(), request.getSize());
            total = standardValueVersionDao.countStandardValue(cnName, enName, request.getEditionId(), request.getCreateUser(), request.getDevDepartmentId(), request.getOpsDepartmentId(), actionRangeSet
                    , TableDataTypeEnum.STANDARD_VALUE.getCode(), devAndOpsInfoWithDeptList.isEmpty() ? null : devAndOpsInfoWithDeptList, userInDb.getUsername(), request.getSourceType(),request.getModifyUser());
        } else {
            standardValueList = standardValueVersionDao.findStandardValue(cnName, enName, request.getEditionId(), request.getCreateUser(), request.getDevDepartmentId(), request.getOpsDepartmentId(), actionRangeSet, TableDataTypeEnum.STANDARD_VALUE.getCode(), Arrays.asList(userInDb.getSubDepartmentCode()), userInDb.getUsername(), request.getSourceType(),request.getModifyUser(), request.getPage(), request.getSize());
            total = standardValueVersionDao.countStandardValue(cnName, enName, request.getEditionId(), request.getCreateUser(), request.getDevDepartmentId(), request.getOpsDepartmentId(), actionRangeSet
                    , TableDataTypeEnum.STANDARD_VALUE.getCode(), Arrays.asList(userInDb.getSubDepartmentCode()), userInDb.getUsername(), request.getSourceType(),request.getModifyUser());
        }

        GetAllResponse<StandardValueResponse> response = new GetAllResponse<>();
        List<StandardValueResponse> responseList = standardValueList.stream().map(StandardValueResponse::new).collect(Collectors.toList());

        setDataVisibilityToResp(responseList);

        response.setTotal(total);
        response.setData(responseList);

        LOGGER.info("Succeed to find standard_value. response: {}", response);
        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_STANDARD_VALUE_SUCCESSFULLY}", response);
    }

    private void setDataVisibilityToResp(List<StandardValueResponse> responseList) {
        List<Long> editionIds = responseList.stream().map(StandardValueResponse::getEditionId).collect(Collectors.toList());
        List<DataVisibility> dataVisibilityList = dataVisibilityService.filterByIds(editionIds, TableDataTypeEnum.STANDARD_VALUE);
        if (CollectionUtils.isEmpty(dataVisibilityList)) {
            return;
        }
        Map<Long, List<DataVisibility>> dataVisibilityListMap = dataVisibilityList.stream().collect(Collectors.groupingBy(DataVisibility::getTableDataId));
        for (StandardValueResponse standardValueResponse : responseList) {
            List<DataVisibility> subDataVisibility = dataVisibilityListMap.get(standardValueResponse.getEditionId());
            if (CollectionUtils.isNotEmpty(subDataVisibility)) {
                standardValueResponse.setActionRange(subDataVisibility.stream().map(DataVisibility::getDepartmentSubName).collect(Collectors.toSet()));
                List<DepartmentSubInfoResponse> departmentSubInfoResponseList = subDataVisibility.stream().map(DepartmentSubInfoResponse::new).collect(Collectors.toList());
                standardValueResponse.setVisibilityDepartmentList(departmentSubInfoResponseList);
            }
        }
    }

    @Override
    public GeneralResponse<GetAllResponse<StandardValueResponse>> getAccordStandardValue(EditionRequest request) throws UnExpectedRequestException {
        EditionRequest.checkRequest(request);
        StandardValueVersion standardValueVersion = checkStandardValueVersion(request.getEditionId());

        int size = request.getSize();
        int page = request.getPage();

        List<StandardValueVersion> standardValueList = null;
        List<Long> departmentId = Lists.newArrayList();
        long total = 0;
        User userInDb = userDao.findById(HttpUtils.getUserId(httpServletRequest));
        List<UserRole> userRoles = userRoleDao.findByUser(userInDb);
        Integer roleType = roleService.getRoleType(userRoles);

        if (roleType.equals(RoleSystemTypeEnum.ADMIN.getCode())) {
            standardValueList = standardValueVersionDao.selectStandardValue(standardValueVersion.getStandardValue().getId(), page, size);
            total = standardValueVersionDao.countStandardValue(standardValueVersion.getStandardValue().getId());
        } else if (roleType.equals(RoleSystemTypeEnum.DEPARTMENT_ADMIN.getCode())) {
            List<Long> departmentIds = userRoles.stream().map(UserRole::getRole).filter(Objects::nonNull)
                    .map(Role::getDepartment).filter(Objects::nonNull)
                    .map(Department::getId).collect(Collectors.toList());
            if (Objects.nonNull(userInDb.getDepartment())) {
                departmentIds.add(userInDb.getDepartment().getId());
            }
            List<Long> devAndOpsInfoWithDeptList = subDepartmentPermissionService.getSubDepartmentIdList(departmentId);
            standardValueList = standardValueVersionDao.selectSuit(standardValueVersion.getStandardValue().getId(), TableDataTypeEnum.STANDARD_VALUE.getCode(), devAndOpsInfoWithDeptList.isEmpty() ? null : devAndOpsInfoWithDeptList, userInDb.getUsername(), page, size);
            total = standardValueVersionDao.countSuitSome(standardValueVersion.getStandardValue().getId(), TableDataTypeEnum.STANDARD_VALUE.getCode(), devAndOpsInfoWithDeptList.isEmpty() ? null : devAndOpsInfoWithDeptList, userInDb.getUsername());
        } else {
            standardValueList = standardValueVersionDao.selectSuit(standardValueVersion.getStandardValue().getId(), TableDataTypeEnum.STANDARD_VALUE.getCode(), Arrays.asList(userInDb.getSubDepartmentCode()), userInDb.getUsername(), page, size);
            total = standardValueVersionDao.countSuitSome(standardValueVersion.getStandardValue().getId(), TableDataTypeEnum.STANDARD_VALUE.getCode(), Arrays.asList(userInDb.getSubDepartmentCode()), userInDb.getUsername());
        }

        GetAllResponse<StandardValueResponse> response = new GetAllResponse<>();
        response.setTotal(total);
        List<StandardValueResponse> responseList = new ArrayList<>();
        for (StandardValueVersion temp : standardValueList) {
            StandardValueResponse editionResponse = new StandardValueResponse(temp);
            responseList.add(editionResponse);
        }

        response.setData(responseList);

        LOGGER.info("Succeed to find standard_value. response: {}", response);
        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_STANDARD_VALUE_VERSION_SUCCESSFULLY}", response);
    }


    @Override
    public StandardValueResponse geStandardValueDetail(Long editionId) throws UnExpectedRequestException {
        // Check existence of standardValueVersion
        StandardValueVersion standardValueVersion = standardValueVersionDao.findById(editionId);
        if (standardValueVersion == null) {
            throw new UnExpectedRequestException("editionId [" + editionId + "] {&DOES_NOT_EXIST}");
        }
        DataVisibilityPermissionDto dataVisibilityPermissionDto = new DataVisibilityPermissionDto.Builder()
                .createUser(standardValueVersion.getCreateUser())
                .devDepartmentId(standardValueVersion.getDevDepartmentId())
                .opsDepartmentId(standardValueVersion.getOpsDepartmentId())
                .build();
        subDepartmentPermissionService.checkAccessiblePermission(editionId, TableDataTypeEnum.STANDARD_VALUE, dataVisibilityPermissionDto);

        StandardValueResponse standardValueResponse = new StandardValueResponse(standardValueVersion);

        List<DataVisibility> dataVisibilityList = dataVisibilityService.filter(standardValueVersion.getId(), TableDataTypeEnum.STANDARD_VALUE);
        if (CollectionUtils.isNotEmpty(dataVisibilityList)) {
            List<DepartmentSubInfoResponse> departmentInfoResponses = dataVisibilityList.stream().map(DepartmentSubInfoResponse::new).collect(Collectors.toList());
            standardValueResponse.setVisibilityDepartmentList(departmentInfoResponses);
        }

        boolean isEditable = subDepartmentPermissionService.isEditable(standardValueVersion.getCreateUser(), standardValueVersion.getDevDepartmentId(), standardValueVersion.getOpsDepartmentId());
        standardValueResponse.setEditable(isEditable);
        LOGGER.info("Succeed to get standardValueVersion detail. editionId: {}", editionId);
        return standardValueResponse;
    }

    @Override
    public List<Map<String, Object>> getAllApproveEnum() {
        return StandardApproveEnum.getStandardEnumList();
    }

    @Override
    public List<Long> getVersionList(Long standardValueId) {
        return standardValueVersionDao.getVersionList(standardValueId);
    }

    @Override
    public List<Map<String, Object>> getAllSourceEnum() {
        return StandardSourceEnum.getStandardSourceList();
    }

    @Override
    public Map<String, Object> getDmsStandardCategory(DmsQueryRequest request) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        return dataStandardClient.getDataStandardCategory(request.getPage(), request.getSize(), loginUser, request.getStdSubName());
    }

    @Override
    public Map<String, Object> getDmsStandardBigCategory(DmsQueryRequest request) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        return dataStandardClient.getDataStandardBigCategory(request.getPage(), request.getSize(), loginUser, request.getStdSubName(), request.getStdBigCategoryName());
    }

    @Override
    public Map<String, Object> getDmsStandardSmallCategory(DmsQueryRequest request) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        return dataStandardClient.getDataStandardSmallCategory(request.getPage(), request.getSize(), loginUser, request.getStdSubName(), request.getStdBigCategoryName(), request.getSmallCategoryName());
    }

    @Override
    public Map<String, Object> getDmsStandardUrn(DmsQueryRequest request) throws UnExpectedRequestException, MetaDataAcquireFailedException, URISyntaxException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        return dataStandardClient.getDataStandard(request.getPage(), request.getSize(), loginUser, request.getStdSmallCategoryUrn(), request.getStdCnName());
    }

    @Override
    public Map<String, Object> getDmsStandardCodeName(DmsQueryRequest request) throws UnExpectedRequestException, MetaDataAcquireFailedException, URISyntaxException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        return dataStandardClient.getStandardCode(request.getPage(), request.getSize(), loginUser, request.getStdUrn());
    }

    @Override
    public Map<String, Object> getDmsStandardCodeTable(DmsQueryRequest request) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        return dataStandardClient.getStandardCodeTable(request.getPage(), request.getSize(), loginUser, request.getStdCode());
    }

}
