package com.webank.wedatasphere.qualitis.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.webank.wedatasphere.qualitis.constant.RuleMetricBussCodeEnum;
import com.webank.wedatasphere.qualitis.constant.RuleMetricLevelEnum;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.dao.*;
import com.webank.wedatasphere.qualitis.dto.DataVisibilityPermissionDto;
import com.webank.wedatasphere.qualitis.dto.RoleDepartmentDto;
import com.webank.wedatasphere.qualitis.entity.*;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.response.DataInfo;
import com.webank.wedatasphere.qualitis.project.excel.ExcelProjectInfoListeners;
import com.webank.wedatasphere.qualitis.project.excel.ExcelRuleMetric;
import com.webank.wedatasphere.qualitis.project.excel.listener.ExcelRuleMetricListener;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.project.response.HiveRuleDetail;
import com.webank.wedatasphere.qualitis.project.service.ProjectBatchService;
import com.webank.wedatasphere.qualitis.request.*;
import com.webank.wedatasphere.qualitis.response.*;
import com.webank.wedatasphere.qualitis.rule.constant.MetricClassEnum;
import com.webank.wedatasphere.qualitis.rule.constant.RoleSystemTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TableDataTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.AlarmConfigDao;
import com.webank.wedatasphere.qualitis.rule.dao.MetricExtInfoDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.entity.*;
import com.webank.wedatasphere.qualitis.rule.exception.WriteExcelException;
import com.webank.wedatasphere.qualitis.service.*;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author allenzhou@webank.com
 * @date 2021/2/22 16:20
 */
@Service
public class RuleMetricServiceImpl implements RuleMetricService {
    @Autowired
    private RuleMetricTypeConfigDao ruleMetricTypeConfigDao;
    @Autowired
    private RuleMetricDao ruleMetricDao;
    @Autowired
    private RuleDao ruleDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserRoleDao userRoleDao;
    @Autowired
    private TaskResultDao taskResultDao;
    @Autowired
    private AlarmConfigDao alarmConfigDao;
    @Autowired
    private TaskRuleAlarmConfigDao taskRuleAlarmConfigDao;
    @Autowired
    private RuleMetricDepartmentUserDao ruleMetricDepartmentUserDao;

    @Autowired
    private SubDepartmentPermissionService subDepartmentPermissionService;
    @Autowired
    private DataVisibilityService dataVisibilityService;
    @Autowired
    private ProjectBatchService projectBatchService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private MetricExtInfoDao metricExtInfoDao;
    @Autowired
    private SystemConfigService systemConfigService;

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleMetricServiceImpl.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final FastDateFormat FILE_DATE_FORMATTER = FastDateFormat.getInstance("yyyyMMddHHmmss");

    private static final int MAX_RULE_METRIC_COUNT = 10000;

    private HttpServletRequest httpServletRequest;

    public RuleMetricServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleMetricResponse> addRuleMetricForOuter(AddRuleMetricRequest request, String loginUser) throws UnExpectedRequestException, PermissionDeniedRequestException {
        return addRuleMetricReal(request, loginUser);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleMetricResponse> addRuleMetric(AddRuleMetricRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        String userName = HttpUtils.getUserName(httpServletRequest);
        return addRuleMetricReal(request, userName);
    }

    private GeneralResponse<RuleMetricResponse> addRuleMetricReal(AddRuleMetricRequest request, String userName) throws UnExpectedRequestException, PermissionDeniedRequestException {
        LOGGER.info("Start to add rule metric, add request: [{}], user: [{}]", request.toString(), userName);
        User loginUser = userDao.findByUsername(userName);
        List<UserRole> userRoles = userRoleDao.findByUser(loginUser);
        Integer roleType = roleService.getRoleType(userRoles);
        subDepartmentPermissionService.checkEditablePermission(roleType, loginUser, null, request.getDevDepartmentId(), request.getOpsDepartmentId(), false);

        checkDuplicateName(request.getName());
        checkDuplicateCode(request.getEnCode());

        RuleMetric newRuleMetric = buildRuleMetric(request, userName);
        RuleMetric savedRuleMetric = ruleMetricDao.add(newRuleMetric);

        MetricExtInfo metricExtInfo = saveMetricExtInfo(request, savedRuleMetric.getId(), userName);

        RuleMetricResponse response = new RuleMetricResponse(savedRuleMetric, metricExtInfo);
        List<DepartmentSubInfoResponse> departmentInfoResponses = dataVisibilityService.saveBatch(savedRuleMetric.getId(), TableDataTypeEnum.RULE_METRIC, request.getVisibilityDepartmentList());
        response.setVisibilityDepartmentList(departmentInfoResponses);

        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&ADD_RULE_METRIC_SUCCESSFULLY}", response);
    }

    private RuleMetric buildRuleMetric(AddRuleMetricRequest request, String createUser) {
        RuleMetric newRuleMetric = new RuleMetric(request.getName(), request.getCnName(), request.getDesc(), request.getSubSystemId()
                , request.getSubSystemName(), request.getFullCnName(), request.getProductId(), request.getProductName(), request.getDepartmentCode()
                , request.getDepartmentName(), request.getDevDepartmentName(), request.getOpsDepartmentName(), request.getType(), request.getEnCode()
                , request.getFrequency(), request.getAvailable(), request.getBussCode(), request.getBussCustom(), request.getMultiEnv());
        newRuleMetric.setCreateUser(createUser);
        newRuleMetric.setCreateTime(QualitisConstants.PRINT_TIME_FORMAT.format(new Date()));
        newRuleMetric.setDevDepartmentId(request.getDevDepartmentId());
        newRuleMetric.setOpsDepartmentId(request.getOpsDepartmentId());
        return newRuleMetric;
    }

    private void checkDuplicateName(String name) throws UnExpectedRequestException {
        RuleMetric ruleMetricInDb = ruleMetricDao.findByName(name);

        if (ruleMetricInDb != null) {
            throw new UnExpectedRequestException("Rule metric name {&ALREADY_EXIST}");
        }
    }

    private void checkDuplicateCode(String enCode) throws UnExpectedRequestException {
        RuleMetric ruleMetricInDb = ruleMetricDao.findByEnCode(enCode);

        if (ruleMetricInDb != null) {
            throw new UnExpectedRequestException("Rule metric en code {&ALREADY_EXIST}");
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleMetricResponse> deleteRuleMetric(long id)
            throws UnExpectedRequestException, PermissionDeniedRequestException {
        if (id <= 0) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        // Check rule metric existence.
        RuleMetric ruleMetricInDb = ruleMetricDao.findById(id);
        if (ruleMetricInDb == null) {
            throw new UnExpectedRequestException("Rule Metric ID [" + id + "] {&DOES_NOT_EXIST}");
        }

        String userName = HttpUtils.getUserName(httpServletRequest);
        LOGGER.info("Start to delete rule metric, rule metric ID: [{}], user: [{}]", id, userName);

        User loginUser = userDao.findByUsername(userName);
        List<UserRole> userRoles = userRoleDao.findByUser(loginUser);

        Integer roleType = roleService.getRoleType(userRoles);
        subDepartmentPermissionService.checkEditablePermission(roleType, loginUser, ruleMetricInDb.getCreateUser(), ruleMetricInDb.getDevDepartmentId(), ruleMetricInDb.getOpsDepartmentId(), false);

        List<TaskRuleAlarmConfig> taskRuleAlarmConfigs = taskRuleAlarmConfigDao.findByRuleMetric(ruleMetricInDb);
        if (CollectionUtils.isNotEmpty(taskRuleAlarmConfigs)) {
            throw new UnExpectedRequestException("Cannot be deleted because task within six months related this rule metric");
        }
        ruleMetricDao.delete(ruleMetricInDb);
        dataVisibilityService.delete(ruleMetricInDb.getId(), TableDataTypeEnum.RULE_METRIC);
        metricExtInfoDao.delete(ruleMetricInDb.getId(), MetricClassEnum.RULE_METRIC);

        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&DELETE_RULE_METRIC_SUCCESSFULLY}", null);
    }

    @Override
    public GeneralResponse<RuleMetricResponse> modifyRuleMetricForOuter(ModifyRuleMetricRequest request, String loginUser)
            throws UnExpectedRequestException, PermissionDeniedRequestException {
        return modifyRuleMetricReal(request, loginUser);
    }

    private GeneralResponse<RuleMetricResponse> modifyRuleMetricReal(ModifyRuleMetricRequest request, String userName)
            throws UnExpectedRequestException, PermissionDeniedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        // Check rule metric existence.
        RuleMetric ruleMetricInDb = ruleMetricDao.findById(request.getId());
        if (ruleMetricInDb == null) {
            throw new UnExpectedRequestException("Rule Metric [ID=" + request.getId() + "] {&DOES_NOT_EXIST}");
        }
        LOGGER.info("Start to modify rule metric, modify request: [{}], user: [{}]", request.toString(), userName);
        if (!ruleMetricInDb.getName().equals(request.getName())) {
            checkDuplicateName(request.getName());
        }
        if (!ruleMetricInDb.getEnCode().equals(request.getEnCode())) {
            checkDuplicateCode(request.getEnCode());
        }
        User loginUser = userDao.findByUsername(userName);
        List<UserRole> userRoles = userRoleDao.findByUser(loginUser);

        Integer roleType = roleService.getRoleType(userRoles);
        subDepartmentPermissionService.checkEditablePermission(roleType, loginUser, ruleMetricInDb.getCreateUser(), request.getDevDepartmentId(), request.getOpsDepartmentId(), false);

        Integer bussCode = request.getBussCode();
        setRuleMetricInDb(request, userName, ruleMetricInDb, bussCode);

        MetricExtInfo metricExtInfo = saveMetricExtInfo(request, request.getId(), userName);

        RuleMetricResponse response = new RuleMetricResponse(ruleMetricDao.add(ruleMetricInDb), metricExtInfo);

        dataVisibilityService.delete(ruleMetricInDb.getId(), TableDataTypeEnum.RULE_METRIC);
        List<DepartmentSubInfoResponse> departmentInfoResponses = dataVisibilityService.saveBatch(ruleMetricInDb.getId(), TableDataTypeEnum.RULE_METRIC, request.getVisibilityDepartmentList());
        response.setVisibilityDepartmentList(departmentInfoResponses);

        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&MODIFY_RULE_METRIC_SUCCESSFULLY}", response);
    }

    private MetricExtInfo saveMetricExtInfo(MetricExtInfoRequest request, Long metricId, String username) {
        MetricExtInfo metricExtInfo = metricExtInfoDao.get(metricId, MetricClassEnum.RULE_METRIC);
        if (metricExtInfo == null) {
            metricExtInfo = new MetricExtInfo();
            metricExtInfo.setCreateTime(new Date());
            metricExtInfo.setCreateUser(username);
        } else {
            metricExtInfo.setModifyTime(new Date());
            metricExtInfo.setModifyUser(username);
        }
        metricExtInfo.setMetricClass(MetricClassEnum.RULE_METRIC.getCode());
        metricExtInfo.setMetricId(metricId);
        metricExtInfo.setBusinessDomain(request.getBusinessDomain());
        metricExtInfo.setMetricDefinition(request.getMetricDefinition());
        metricExtInfo.setBusinessModel(request.getBusinessModel());
        metricExtInfo.setBusinessStrategy(request.getBusinessStrategy());
        metricExtInfo.setBusinessSystem(request.getBusinessSystem());
        metricExtInfo.setCalculationMode(request.getCalculationMode());
        metricExtInfo.setMonitoringCapabilities(StringUtils.join(request.getMonitoringCapabilities(), SpecCharEnum.COMMA.getValue()));

        systemConfigService.addMonitoringCapabilities(request.getMonitoringCapabilities());
        return metricExtInfoDao.save(metricExtInfo);
    }


    private void setRuleMetricInDb(ModifyRuleMetricRequest request, String userName, RuleMetric ruleMetricInDb, Integer bussCode) {
        ruleMetricInDb.setName(request.getName());
        ruleMetricInDb.setCnName(request.getCnName());
        ruleMetricInDb.setMetricDesc(request.getDesc());
        ruleMetricInDb.setBussCode(bussCode);
        if (RuleMetricBussCodeEnum.SUBSYSTEM.getCode().equals(bussCode)) {
            ruleMetricInDb.setSubSystemId(request.getSubSystemId());
            ruleMetricInDb.setSubSystemName(request.getSubSystemName());
            ruleMetricInDb.setFullCnName(request.getFullCnName());
            // Empty them
            ruleMetricInDb.setProductId("");
            ruleMetricInDb.setBussCustom("");
            ruleMetricInDb.setProductName("");
        } else if (RuleMetricBussCodeEnum.PRODUCT.getCode().equals(bussCode)) {
            ruleMetricInDb.setProductId(request.getProductId());
            ruleMetricInDb.setProductName(request.getProductName());

            ruleMetricInDb.setSubSystemId(null);
            ruleMetricInDb.setSubSystemName("");
            ruleMetricInDb.setFullCnName("");
            ruleMetricInDb.setBussCustom("");
        } else if (RuleMetricBussCodeEnum.CUSTOM.getCode().equals(bussCode)) {
            ruleMetricInDb.setBussCustom(request.getBussCustom());

            ruleMetricInDb.setProductId("");
            ruleMetricInDb.setProductName("");
            ruleMetricInDb.setSubSystemId(null);
            ruleMetricInDb.setSubSystemName("");
            ruleMetricInDb.setFullCnName("");
        }

        ruleMetricInDb.setModifyUser(userName);
        ruleMetricInDb.setModifyTime(QualitisConstants.PRINT_TIME_FORMAT.format(new Date()));
        ruleMetricInDb.setType(request.getType());
        ruleMetricInDb.setEnCode(request.getEnCode());
        ruleMetricInDb.setFrequency(request.getFrequency());
        ruleMetricInDb.setDepartmentCode(request.getDepartmentCode());
        ruleMetricInDb.setDepartmentName(request.getDepartmentName());
        ruleMetricInDb.setDevDepartmentName(request.getDevDepartmentName());
        ruleMetricInDb.setOpsDepartmentName(request.getOpsDepartmentName());
        ruleMetricInDb.setDevDepartmentId(request.getDevDepartmentId());
        ruleMetricInDb.setOpsDepartmentId(request.getOpsDepartmentId());

        ruleMetricInDb.setAvailable(request.getAvailable());
        ruleMetricInDb.setMultiEnv(request.getMultiEnv());
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleMetricResponse> modifyRuleMetric(ModifyRuleMetricRequest request)
            throws UnExpectedRequestException, PermissionDeniedRequestException {
        String userName = HttpUtils.getUserName(httpServletRequest);
        return modifyRuleMetricReal(request, userName);
    }

    @Override
    public GeneralResponse<RuleMetricResponse> getRuleMetricDetail(long id)
            throws UnExpectedRequestException {
        if (id <= 0) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        // Check rule metric existence.
        RuleMetric ruleMetricInDb = ruleMetricDao.findById(id);
        if (ruleMetricInDb == null) {
            throw new UnExpectedRequestException("Rule Metric ID [" + id + "] {&DOES_NOT_EXIST}");
        }
        DataVisibilityPermissionDto dataVisibilityPermissionDto = new DataVisibilityPermissionDto.Builder()
                .createUser(ruleMetricInDb.getCreateUser())
                .devDepartmentId(ruleMetricInDb.getDevDepartmentId())
                .opsDepartmentId(ruleMetricInDb.getOpsDepartmentId())
                .build();
        subDepartmentPermissionService.checkAccessiblePermission(id, TableDataTypeEnum.RULE_METRIC, dataVisibilityPermissionDto);

        String userName = HttpUtils.getUserName(httpServletRequest);
        LOGGER.info("Start to get rule metric, rule metric ID: [{}], user: [{}]", id, userName);
        User loginUser = userDao.findByUsername(userName);
        List<UserRole> userRoles = userRoleDao.findByUser(loginUser);

        Integer roleType = roleService.getRoleType(userRoles);
        List<Long> devAndOpsInfoWithDeptList = Collections.emptyList();

        RuleMetricResponse ruleMetricResponse = new RuleMetricResponse(ruleMetricInDb, metricExtInfoDao.get(ruleMetricInDb.getId(), MetricClassEnum.RULE_METRIC));
        setVisibilityDepartment(ruleMetricResponse, ruleMetricInDb);
        boolean isEditable = subDepartmentPermissionService.isEditable(roleType, loginUser, ruleMetricInDb.getCreateUser(), ruleMetricInDb.getDevDepartmentId(), ruleMetricInDb.getOpsDepartmentId(), devAndOpsInfoWithDeptList);
        ruleMetricResponse.setEditable(isEditable);
        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_RULE_METRIC_SUCCESSFULLY}", ruleMetricResponse);
    }

    @Override
    public GeneralResponse<GetAllResponse<RuleMetricResponse>> getAllRuleMetric(RuleMetricQueryRequest request) throws UnExpectedRequestException {
        String userName = HttpUtils.getUserName(httpServletRequest);
        LOGGER.info("Start to get all rule metric, page request: [{}], user: [{}]", request.toString(), userName);
        User loginUser = userDao.findByUsername(userName);
        List<UserRole> userRoles = userRoleDao.findByUser(loginUser);
        Integer roleType = roleService.getRoleType(userRoles);

        List<RuleMetric> ruleMetrics = new ArrayList<>();
        List<RuleMetric> usedRuleMetrics = new ArrayList<>();
        if (roleType.equals(RoleSystemTypeEnum.ADMIN.getCode())) {
            LOGGER.info("SYS_ADMIN will get all rule metrics.");
            ruleMetrics.addAll(ruleMetricDao.findAllNotUsed(request.getRuleMetricName(), request.getPage(), request.getSize()));
            usedRuleMetrics.addAll(ruleMetricDao.findAllUsed(request.getRuleMetricName(), request.getPage(), request.getSize()));
        } else if (roleType.equals(RoleSystemTypeEnum.DEPARTMENT_ADMIN.getCode())) {
            LOGGER.info("DEPARTMENT_ADMIN will get rule metrics of all management departments and all projectors.");
            List<Department> departments = new ArrayList<>();
            for (UserRole temp : userRoles) {
                Department department = temp.getRole().getDepartment();
                if (department != null) {
                    departments.add(department);
                }
            }
            departments.add(loginUser.getDepartment());
            List<Long> departmentIds = departments.stream().filter(Objects::nonNull).map(Department::getId).collect(Collectors.toList());
            List<Long> devAndOpsInfoWithDeptList = subDepartmentPermissionService.getSubDepartmentIdList(departmentIds);
            List<RuleMetric> ruleMetricList = ruleMetricDao.findNotUsed(RuleMetricLevelEnum.DEFAULT_METRIC.getCode(), departments, null, loginUser.getUsername(), TableDataTypeEnum.RULE_METRIC.getCode(), devAndOpsInfoWithDeptList, request.getRuleMetricName(), request.getPage(), request.getSize());
            for (RuleMetric ruleMetric : ruleMetricList) {
                boolean isEditable = subDepartmentPermissionService.isEditable(roleType, loginUser, ruleMetric.getCreateUser(), ruleMetric.getDevDepartmentId(), ruleMetric.getOpsDepartmentId(), devAndOpsInfoWithDeptList);
                if (isEditable) {
                    ruleMetrics.add(ruleMetric);
                }
            }
            usedRuleMetrics.addAll(ruleMetricDao.findUsed(RuleMetricLevelEnum.DEFAULT_METRIC.getCode(), departments, null, loginUser.getUsername(), TableDataTypeEnum.RULE_METRIC.getCode(), devAndOpsInfoWithDeptList, request.getRuleMetricName(), request.getPage(), request.getSize()));
        } else {
            LOGGER.info("PROJECTOR will get rule metrics of department and own.");
            List<Department> departments = new ArrayList<>();
            departments.add(loginUser.getDepartment());
            List<RuleMetric> ruleMetricList = ruleMetricDao.findNotUsed(RuleMetricLevelEnum.DEFAULT_METRIC.getCode(), departments, loginUser, loginUser.getUsername(), TableDataTypeEnum.RULE_METRIC.getCode(), Arrays.asList(loginUser.getSubDepartmentCode()), request.getRuleMetricName(), request.getPage(), request.getSize());
            for (RuleMetric ruleMetric : ruleMetricList) {
                boolean isEditable = subDepartmentPermissionService.isEditable(roleType, loginUser, ruleMetric.getCreateUser(), ruleMetric.getDevDepartmentId(), ruleMetric.getOpsDepartmentId(), Collections.emptyList());
                if (isEditable) {
                    ruleMetrics.add(ruleMetric);
                }
            }
            usedRuleMetrics.addAll(ruleMetricDao.findUsed(RuleMetricLevelEnum.DEFAULT_METRIC.getCode(), departments, loginUser, loginUser.getUsername(), TableDataTypeEnum.RULE_METRIC.getCode(), Arrays.asList(loginUser.getSubDepartmentCode()), request.getRuleMetricName(), request.getPage(), request.getSize()));
        }

        GetAllResponse<RuleMetricResponse> response = new GetAllResponse<>();
        List<RuleMetricResponse> ruleMetricResponses = new ArrayList<>();

        List<RuleMetricResponse> unusedRuleMetricResponses = ruleMetrics.stream().filter(ruleMetric -> {
            if (request.getMultiEnvs() != null && !request.getMultiEnvs().equals(ruleMetric.getMultiEnv())) {
                return false;
            }
            return true;
        }).map(ruleMetric -> new RuleMetricResponse(ruleMetric, false)).collect(Collectors.toList());

        List<RuleMetricResponse> usedRuleMetricResponses = usedRuleMetrics.stream().filter(ruleMetric -> {
            if (request.getMultiEnvs() != null && !request.getMultiEnvs().equals(ruleMetric.getMultiEnv())) {
                return false;
            }
            return true;
        }).map(ruleMetric -> new RuleMetricResponse(ruleMetric, false)).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(unusedRuleMetricResponses)) {
            ruleMetricResponses.addAll(unusedRuleMetricResponses);
        }
        if (CollectionUtils.isNotEmpty(usedRuleMetricResponses)) {
            ruleMetricResponses.addAll(usedRuleMetricResponses);
        }

        response.setData(ruleMetricResponses);
        response.setTotal(ruleMetricResponses.size());

        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_RULE_METRIC_SUCCESSFULLY}", response);
    }

    private void fillMetricExtInfo(List<RuleMetricResponse> ruleMetricResponses) {
        if (CollectionUtils.isEmpty(ruleMetricResponses)) {
            return;
        }
        List<Long> ruleMetricIds = ruleMetricResponses.stream().map(RuleMetricResponse::getId).collect(Collectors.toList());
        List<MetricExtInfo> metricExtInfoList = metricExtInfoDao.list(ruleMetricIds, MetricClassEnum.RULE_METRIC);
        if (CollectionUtils.isNotEmpty(metricExtInfoList)) {
            Map<Long, MetricExtInfo> metricIdMap = metricExtInfoList.stream().collect(Collectors.toMap(MetricExtInfo::getMetricId, Function.identity(), (v1, v2) -> v1));
            ruleMetricResponses.forEach(ruleMetricResponse -> {
                if (metricIdMap.containsKey(ruleMetricResponse.getId())) {
                    MetricExtInfo metricExtInfo = metricIdMap.get(ruleMetricResponse.getId());
                    ruleMetricResponse.setCalculationMode(metricExtInfo.getCalculationMode());
                    if (StringUtils.isNotBlank(metricExtInfo.getMonitoringCapabilities())) {
                        ruleMetricResponse.setMonitoringCapabilities(Arrays.asList(StringUtils.split(metricExtInfo.getMonitoringCapabilities(), SpecCharEnum.COMMA.getValue())));
                    }
                    ruleMetricResponse.setBusinessDomain(metricExtInfo.getBusinessDomain());
                    ruleMetricResponse.setBusinessModel(metricExtInfo.getBusinessModel());
                    ruleMetricResponse.setBusinessStrategy(metricExtInfo.getBusinessStrategy());
                    ruleMetricResponse.setMetricDefinition(metricExtInfo.getMetricDefinition());
                    ruleMetricResponse.setBusinessSystem(metricExtInfo.getBusinessSystem());
                }
            });
        }
    }

    @Override
    public RuleMetricConditionResponse conditions() {
        String userName = HttpUtils.getUserName(httpServletRequest);
        LOGGER.info("Start to get rule metric condition, user: [{}]", userName);
        User loginUser = userDao.findByUsername(userName);

        RoleDepartmentDto roleDepartmentDto = roleService.getRoleAndDepartments(loginUser);
        List<RuleMetric> ruleMetrics = new ArrayList<>();
        if (roleDepartmentDto.getRoleSystemType().getCode().equals(RoleSystemTypeEnum.ADMIN.getCode())) {
            LOGGER.info("SYS_ADMIN will get all rule metrics with conditions.");
            ruleMetrics.addAll(ruleMetricDao.findAllRuleMetrics(0, Integer.MAX_VALUE));
        } else {
            LOGGER.info("PROJECTOR  will get rule metrics of department and own.");
            List<Department> departments = roleDepartmentDto.getDepartmentList();
            ruleMetrics.addAll(ruleMetricDao.findRuleMetrics(null,
                    CollectionUtils.isEmpty(departments) ? null : departments, loginUser, 0, Integer.MAX_VALUE));
        }

        Set<String> subSystemNameSet = ruleMetrics.stream().map(RuleMetric::getSubSystemName).filter(s -> StringUtils.isNotBlank(s)).collect(Collectors.toSet());
        List<RuleMetricTypeConfig> ruleMetricTypeConfigs = ruleMetricTypeConfigDao.findAllRuleMetricTypeConfig().stream().sorted(Comparator.comparing(RuleMetricTypeConfig::getId)).collect(Collectors.toList());

        RuleMetricConditionResponse response = new RuleMetricConditionResponse();
        response.setSubSystemNameCondition(subSystemNameSet);
        response.setRuleMetricType(ruleMetricTypeConfigs);

        return response;
    }

    @Override
    public List<String> queryEnCodeWithPage(String enCode, int page, int size) {
        String userName = HttpUtils.getUserName(httpServletRequest);
        User loginUser = userDao.findByUsername(userName);
        RoleDepartmentDto roleDepartmentDto = roleService.getRoleAndDepartments(loginUser);
        String enCodeCondition = null;
        if (StringUtils.isNotBlank(enCode)) {
            enCodeCondition = "%" + enCode + "%";
        }
        List<Department> departments = roleDepartmentDto.getDepartmentList();
        return ruleMetricDao.findEnCodeWithPage(CollectionUtils.isEmpty(departments) ? null : departments, loginUser, enCodeCondition, page, size);
    }

    @Override
    public GeneralResponse<GetAllResponse<RuleMetricResponse>> queryRuleMetric(RuleMetricQueryRequest request, boolean needVisibilityDepartmentList) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "Rule Metric query request");
        if (StringUtils.isBlank(request.getSubSystemName())) {
            request.setSubSystemName("");
        }
        if (StringUtils.isBlank(request.getRuleMetricName())) {
            request.setRuleMetricName("%");
        } else {
            request.setRuleMetricName("%" + request.getRuleMetricName() + "%");
        }

        if (StringUtils.isBlank(request.getDevDepartmentId())) {
            request.setDevDepartmentId("");
        }
        if (StringUtils.isBlank(request.getOpsDepartmentId())) {
            request.setOpsDepartmentId("");
        }
        if (StringUtils.isBlank(request.getCreateUser())) {
            request.setCreateUser("");
        }
        if (StringUtils.isBlank(request.getModifyUser())) {
            request.setModifyUser("");
        }
        Set<String> actionRangeSet = CollectionUtils.isEmpty(request.getActionRange()) ? null : request.getActionRange();

        String userName = HttpUtils.getUserName(httpServletRequest);
        LOGGER.info("Start to get all rule metric, page request: [{}], user: [{}]", request.toString(), userName);
        User loginUser = userDao.findByUsername(userName);
        List<UserRole> userRoles = userRoleDao.findByUser(loginUser);
        Integer roleType = roleService.getRoleType(userRoles);
        List<RuleMetric> ruleMetrics = new ArrayList<>();
        long total;
        if (roleType.equals(RoleSystemTypeEnum.ADMIN.getCode())) {
            LOGGER.info("SYS_ADMIN will get all rule metrics in query.");
            ruleMetrics.addAll(ruleMetricDao.queryAllRuleMetrics(request.getSubSystemName(), request.getRuleMetricName(), request.getEnCode(), request.getType()
                    , request.getAvailable(), request.getMultiEnvs(), request.getDevDepartmentId(), request.getOpsDepartmentId(), actionRangeSet, TableDataTypeEnum.RULE_METRIC.getCode(), request.getCreateUser(), request.getModifyUser(), request.getPage(), request.getSize()));
            total = ruleMetricDao.countQueryAllRuleMetrics(request.getSubSystemName(), request.getRuleMetricName(), request.getEnCode(), request.getType()
                    , request.getAvailable(), request.getMultiEnvs(), request.getDevDepartmentId(), request.getOpsDepartmentId(), actionRangeSet, TableDataTypeEnum.RULE_METRIC.getCode(), request.getCreateUser(), request.getModifyUser());
        } else if (roleType.equals(RoleSystemTypeEnum.DEPARTMENT_ADMIN.getCode())) {
            LOGGER.info("DEPARTMENT_ADMIN will get rule metrics of all management departments and all projectors.");
            List<Long> departmentIds = userRoles.stream().map(UserRole::getRole).filter(Objects::nonNull)
                    .map(Role::getDepartment).filter(Objects::nonNull)
                    .map(Department::getId).collect(Collectors.toList());
            if (Objects.nonNull(loginUser.getDepartment())) {
                departmentIds.add(loginUser.getDepartment().getId());
            }
            List<Long> devAndOpsInfoWithDeptList = subDepartmentPermissionService.getSubDepartmentIdList(departmentIds);
            ruleMetrics.addAll(ruleMetricDao.queryRuleMetrics(request.getSubSystemName(), request.getRuleMetricName(), request.getEnCode(), request.getType()
                    , request.getAvailable(), request.getAvailable(), request.getMultiEnvs(), TableDataTypeEnum.RULE_METRIC.getCode(), devAndOpsInfoWithDeptList.isEmpty() ? null : devAndOpsInfoWithDeptList, loginUser.getUsername(), request.getDevDepartmentId(), request.getOpsDepartmentId(), actionRangeSet, request.getCreateUser(), request.getModifyUser(), request.getPage(), request.getSize()));
            total = ruleMetricDao.countQueryRuleMetrics(request.getSubSystemName(), request.getRuleMetricName(), request.getEnCode(), request.getType()
                    , request.getAvailable(), request.getMultiEnvs(), TableDataTypeEnum.RULE_METRIC.getCode(), devAndOpsInfoWithDeptList.isEmpty() ? null : devAndOpsInfoWithDeptList, loginUser.getUsername(), request.getDevDepartmentId(), request.getOpsDepartmentId(), actionRangeSet, request.getCreateUser(), request.getModifyUser());
        } else {
            LOGGER.info("PROJECTOR will get rule metrics of department and own.");
            ruleMetrics.addAll(ruleMetricDao.queryRuleMetrics(request.getSubSystemName(), request.getRuleMetricName(), request.getEnCode(), request.getType(), request.getAvailable(), request.getMultiEnvs(), request.getMultiEnvs(),
                    TableDataTypeEnum.RULE_METRIC.getCode(), Arrays.asList(loginUser.getSubDepartmentCode()), loginUser.getUsername(), request.getDevDepartmentId(), request.getOpsDepartmentId(), actionRangeSet, request.getCreateUser(), request.getModifyUser(), request.getPage(), request.getSize()));
            total = ruleMetricDao.countQueryRuleMetrics(request.getSubSystemName(), request.getRuleMetricName(), request.getEnCode(), request.getType(), request.getAvailable(), request.getMultiEnvs(), TableDataTypeEnum.RULE_METRIC.getCode(), Arrays.asList(loginUser.getSubDepartmentCode()), loginUser.getUsername(), request.getDevDepartmentId(), request.getOpsDepartmentId(), actionRangeSet, request.getCreateUser(), request.getModifyUser());
        }

        GetAllResponse<RuleMetricResponse> response = new GetAllResponse<>();
        List<RuleMetricResponse> ruleMetricResponses = ruleMetrics.stream().map(ruleMetric -> {
            RuleMetricResponse ruleMetricResponse = new RuleMetricResponse(ruleMetric);
            if (needVisibilityDepartmentList) {
                setVisibilityDepartment(ruleMetricResponse, ruleMetric);
            }
            return ruleMetricResponse;
        }).collect(Collectors.toList());

        fillMetricExtInfo(ruleMetricResponses);

        response.setData(ruleMetricResponses);
        response.setTotal(total);

        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&RULE_METRIC_QUERY_SUCCESS}", response);
    }

    private void setVisibilityDepartment(RuleMetricResponse ruleMetricResponse, RuleMetric ruleMetricInDb) {
        List<DataVisibility> dataVisibilityList = dataVisibilityService.filter(ruleMetricInDb.getId(), TableDataTypeEnum.RULE_METRIC);
        if (CollectionUtils.isNotEmpty(dataVisibilityList)) {
            List<DepartmentSubInfoResponse> departmentInfoResponses = dataVisibilityList.stream().map(DepartmentSubInfoResponse::new).collect(Collectors.toList());
            ruleMetricResponse.setVisibilityDepartmentList(departmentInfoResponses);
        }
    }

    @Override
    public DataInfo<HiveRuleDetail> getRuleByRuleMetric(long id, int page, int size) throws UnExpectedRequestException {
        DataInfo<HiveRuleDetail> dataInfo = new DataInfo<>();
        if (id <= 0) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        // Check rule metric existence.
        RuleMetric ruleMetricInDb = ruleMetricDao.findById(id);
        if (ruleMetricInDb == null) {
            throw new UnExpectedRequestException("Rule Metric ID [" + id + "] {&DOES_NOT_EXIST}");
        }
        List<AlarmConfig> alarmConfigs = alarmConfigDao.getByRuleMetric(ruleMetricInDb);
        if (CollectionUtils.isEmpty(alarmConfigs)) {
            return dataInfo;
        }
        List<Rule> rules = alarmConfigs.stream().map(alarmConfig -> alarmConfig.getRule()).collect(Collectors.toList());
        int total = rules.size();
        List<HiveRuleDetail> result = new ArrayList<>(total);
        for (Rule ruleInDb : rules) {
            if (ruleInDb == null) {
                total--;
                continue;
            }
            HiveRuleDetail hiveRuleDetail = new HiveRuleDetail(ruleInDb);
            result.add(hiveRuleDetail);
        }
        dataInfo.setContent(result);
        dataInfo.setTotalCount(total);
        return dataInfo;
    }

    @Override
    public DataInfo<RuleMetricValueResponse> getResultsByRuleMetric(long ruleMetricId, String startTime, String endTime, String envName,
                                                                    int page, int size) throws UnExpectedRequestException {
        DataInfo<RuleMetricValueResponse> dataInfo = new DataInfo<>();
        int total = taskResultDao.countValuesByRuleMetric(ruleMetricId, startTime, endTime, envName);
        List<TaskResult> values = taskResultDao.findValuesByRuleMetric(ruleMetricId, startTime, endTime, envName, page, size);

        List<RuleMetricValueResponse> responses = new ArrayList<>(values.size());
        for (TaskResult taskResult : values) {
            if (taskResult == null) {
                continue;
            }

            RuleMetricValueResponse ruleMetricValueResponse = new RuleMetricValueResponse();
            ruleMetricValueResponse.setGenerateTime(taskResult.getCreateTime());
            Rule currentRule = ruleDao.findById(taskResult.getRuleId());
            if (currentRule != null) {
                HiveRuleDetail hiveRuleDetail = new HiveRuleDetail(currentRule);
                ruleMetricValueResponse.setHiveRuleDetail(hiveRuleDetail);
                Set<RuleDataSource> ruleDataSources = currentRule.getRuleDataSources();
                if (CollectionUtils.isNotEmpty(ruleDataSources)) {
                    List<String> datasourceNameList = ruleDataSources.stream().map(RuleDataSource::getLinkisDataSourceName).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
                    ruleMetricValueResponse.setDatasourceNames(datasourceNameList);
                }
            }
            ruleMetricValueResponse.setEnvName(taskResult.getEnvName());
            ruleMetricValueResponse.setRelatedRuleName(currentRule == null ? "Deleted" : currentRule.getName());
            ruleMetricValueResponse.setRuleMetricValue(StringUtils.isBlank(taskResult.getValue()) ? "0" : taskResult.getValue());
            responses.add(ruleMetricValueResponse);
        }
        GeneralResponse<GetAllResponse<EnvResponse>> allEnvs = getAllEnvs(ruleMetricId);
        if (allEnvs.getData().getData() != null) {
            List<String> envList = allEnvs.getData().getData().stream().map(o -> o.getEnvName()).distinct().collect(Collectors.toList());
            dataInfo.setEnvNames(envList);
        }
        dataInfo.setContent(responses);
        dataInfo.setTotalCount(total);
        return dataInfo;
    }

    @Override
    public GeneralResponse download(DownloadRuleMetricRequest request, HttpServletResponse response) throws UnExpectedRequestException, IOException
            , WriteExcelException, PermissionDeniedRequestException {
        // Check rule metric IDs permission.
        RuleMetricQueryRequest queryRequest = new RuleMetricQueryRequest(0, Integer.MAX_VALUE);
        List<RuleMetricResponse> ownRuleMetric = queryRuleMetric(queryRequest, true).getData().getData().stream().collect(Collectors.toList());

        if (ownRuleMetric.size() <= 0 || ownRuleMetric.size() >= MAX_RULE_METRIC_COUNT) {
            throw new UnExpectedRequestException("Unable to retriever metric results, please check your user roles.");
        }
        List<Long> downloadIds = request.getRuleMetricIds();
        List<Long> ownIds = ownRuleMetric.stream().map(RuleMetricResponse::getId).collect(Collectors.toList());

        if (ownIds.containsAll(downloadIds)) {
            List<RuleMetricResponse> downloadRuleMetric = ownRuleMetric.stream().filter(ruleMetricResponse ->
                    downloadIds.contains(ruleMetricResponse.getId())).collect(Collectors.toList());

            List<ExcelRuleMetric> excelRuleMetrics = new ArrayList<>(downloadRuleMetric.size());

            for (RuleMetricResponse ruleMetricResponse : downloadRuleMetric) {
                ExcelRuleMetric excelRuleMetric = new ExcelRuleMetric();
                excelRuleMetric.setRuleMetricJsonObject(objectMapper.writeValueAsString(ruleMetricResponse));

                excelRuleMetrics.add(excelRuleMetric);
            }
            String fileName = "batch_metrics_export_" + FILE_DATE_FORMATTER.format(new Date()) + QualitisConstants.SUPPORT_CSV_SUFFIX_NAME;
            fileName = URLEncoder.encode(fileName, "UTF-8");
            response.setContentType("application/octet-stream");

            response.addHeader("Content-Disposition", "attachment;filename*=UTF-8''" + fileName);
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            OutputStream outputStream = response.getOutputStream();
            writeExcelToOutput(excelRuleMetrics, outputStream);
            outputStream.flush();
        } else {
            throw new PermissionDeniedRequestException("HAS_NO_PERMISSION_TO_ACCESS", 403);
        }
        LOGGER.info("Succeed to download all rule metrics in type of excel");
        return new GeneralResponse<>(ResponseStatusConstants.OK, "SUCCESS", null);
    }

    private void writeExcelToOutput(List<ExcelRuleMetric> excelRuleMetrics, OutputStream outputStream) throws WriteExcelException, IOException {
        try {
            LOGGER.info("Start to write metric excel");
            EasyExcel.write(outputStream, ExcelRuleMetric.class)
                    .excelType(ExcelTypeEnum.CSV)
                    .sheet()
                    .doWrite(excelRuleMetrics);
            LOGGER.info("Finish to write metric excel");
        } catch (Exception e) {
            throw new WriteExcelException(e.getMessage());
        } finally {
            outputStream.close();
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse upload(InputStream inputStream, FormDataContentDisposition fileDisposition) throws UnExpectedRequestException, IOException {
        // Check Arguments
        if (inputStream == null || fileDisposition == null) {
            throw new UnExpectedRequestException("{&FILE_CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        // Check suffix name of file
        String fileName = fileDisposition.getFileName();
        String suffixName = fileName.substring(fileName.lastIndexOf('.'));
        if (!suffixName.equals(QualitisConstants.SUPPORT_EXCEL_SUFFIX_NAME) && !suffixName.equals(QualitisConstants.SUPPORT_CSV_SUFFIX_NAME)) {
            throw new UnExpectedRequestException("{&DO_NOT_SUPPORT_SUFFIX_NAME}: [" + suffixName + "]. {&ONLY_SUPPORT} [" + QualitisConstants.SUPPORT_EXCEL_SUFFIX_NAME + " and " + QualitisConstants.SUPPORT_CSV_SUFFIX_NAME + "]");
        }
        String userName = HttpUtils.getUserName(httpServletRequest);
        User user = userDao.findByUsername(userName);
        if (user == null) {
            return new GeneralResponse<>(ResponseStatusConstants.BAD_REQUEST, "{&PLEASE_LOGIN}", null);
        }

        LOGGER.info(userName + " start to upload rule metrics.");

        ExcelProjectInfoListeners projectListener = new ExcelProjectInfoListeners();
        ExcelRuleMetricListener ruleMetricListener = new ExcelRuleMetricListener(projectListener);
        EasyExcel.read(inputStream, ExcelRuleMetric.class, ruleMetricListener).doReadAll();

        try {
            projectBatchService.handleMetricData(user, projectListener.getExcelMetricContent());
            LOGGER.info("Succeed to add all rule metrics");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            inputStream.close();
        }
        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&SUCCESS_TO_UPLOAD_RULE_METRIC}", null);
    }

    @Override
    public GeneralResponse<List<RuleMetricTypeConfig>> types() {
        return new GeneralResponse<>(ResponseStatusConstants.OK, "", ruleMetricTypeConfigDao.findAllRuleMetricTypeConfig());
    }

    @Override
    public List<RuleMetricListValueResponse> getResultsByRuleMetricList(RuleMetricListValuesRequest ruleMetricListValuesRequest)
            throws UnExpectedRequestException {
        if (ruleMetricListValuesRequest == null || CollectionUtils.isEmpty(ruleMetricListValuesRequest.getRuleMetricIdList())) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        LOGGER.info("get results by rule metric list request detail: {}", ruleMetricListValuesRequest.toString());
        List<RuleMetricListValueResponse> responses = new ArrayList<>(ruleMetricListValuesRequest.getRuleMetricIdList().size());

        List<Long> ruleMetricIdList = ruleMetricListValuesRequest.getRuleMetricIdList();

        for (Long ruleMetricId : ruleMetricIdList) {
            RuleMetricListValueResponse ruleMetricListValueResponse = new RuleMetricListValueResponse();
            ruleMetricListValueResponse.setRuleMetricId(ruleMetricId);
            GeneralResponse<GetAllResponse<EnvResponse>> allEnvs = getAllEnvs(ruleMetricId);
            if (allEnvs.getData().getData() != null) {
                List<String> envList = allEnvs.getData().getData().stream().map(o -> o.getEnvName()).collect(Collectors.toList());
                ruleMetricListValueResponse.setEnvNames(envList);
            }
            List<TaskResult> values = taskResultDao.findValuesByRuleMetricWithTime(ruleMetricId, ruleMetricListValuesRequest.getStartTime(), ruleMetricListValuesRequest.getEndTime());
            for (TaskResult taskResult : values) {
                if (taskResult == null) {
                    continue;
                }

                RuleMetricValueResponse ruleMetricValueResponse = new RuleMetricValueResponse();
                ruleMetricValueResponse.setGenerateTime(taskResult.getCreateTime());

                Rule currentRule = ruleDao.findById(taskResult.getRuleId());
                if (currentRule != null) {
                    ruleMetricValueResponse.setHiveRuleDetail(new HiveRuleDetail(currentRule));
                }
                ruleMetricValueResponse.setRelatedRuleName(currentRule == null ? "Deleted" : currentRule.getName());
                ruleMetricValueResponse.setRuleMetricValue(StringUtils.isBlank(taskResult.getValue()) ? "0" : taskResult.getValue());

                ruleMetricListValueResponse.getRuleMetricValues().add(ruleMetricValueResponse);
            }
            responses.add(ruleMetricListValueResponse);
        }

        return responses;
    }

    @Override
    public GeneralResponse<GetAllResponse<EnvResponse>> getAllEnvs(long id) throws UnExpectedRequestException {
        // Check rule metric existence.
        GetAllResponse<EnvResponse> response = new GetAllResponse<>();
        RuleMetric ruleMetricInDb = ruleMetricDao.findById(id);
        if (ruleMetricInDb == null) {
            throw new UnExpectedRequestException("Rule Metric ID [" + id + "] {&DOES_NOT_EXIST}");
        }
        List<AlarmConfig> alarmConfigs = alarmConfigDao.getByRuleMetric(ruleMetricInDb);
        if (CollectionUtils.isEmpty(alarmConfigs)) {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "No envs with this rule metric", response);
        }
        List<Rule> rules = alarmConfigs.stream().map(alarmConfig -> alarmConfig.getRule()).collect(Collectors.toList());

        List<RuleDataSourceEnv> ruleDataSourceEnvList = rules.stream()
                .map(rule -> rule.getRuleDataSources())
                .flatMap(ruleDataSources -> ruleDataSources.stream())
                .filter(ruleDataSource -> CollectionUtils.isNotEmpty(ruleDataSource.getRuleDataSourceEnvs()))
                .map(ruleDataSource -> ruleDataSource.getRuleDataSourceEnvs()).flatMap(currRuleDataSourceEnvs -> currRuleDataSourceEnvs.stream()).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(ruleDataSourceEnvList)) {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "No envs with this rule metric", response);
        }

        List<EnvResponse> responses = new ArrayList<>(ruleDataSourceEnvList.size());
        for (RuleDataSourceEnv env : ruleDataSourceEnvList) {
            EnvResponse envResponse = new EnvResponse();
            envResponse.setEnvId(env.getEnvId());
            envResponse.setEnvName(env.getEnvName());
            responses.add(envResponse);
        }
        response.setData(responses);
        response.setTotal(responses.size());
        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_RULE_ENVIRONMENT_SUCCESS}", response);
    }
}
