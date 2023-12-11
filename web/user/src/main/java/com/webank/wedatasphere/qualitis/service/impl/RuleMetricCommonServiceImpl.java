package com.webank.wedatasphere.qualitis.service.impl;

import com.webank.wedatasphere.qualitis.constant.DepartmentSourceTypeEnum;
import com.webank.wedatasphere.qualitis.constant.RuleMetricBussCodeEnum;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.dao.RuleMetricDao;
import com.webank.wedatasphere.qualitis.dao.RuleMetricTypeConfigDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.dao.UserRoleDao;
import com.webank.wedatasphere.qualitis.entity.*;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.OperateCiService;
import com.webank.wedatasphere.qualitis.metadata.response.CmdbDepartmentResponse;
import com.webank.wedatasphere.qualitis.metadata.response.DepartmentSubResponse;
import com.webank.wedatasphere.qualitis.metadata.response.SubSystemResponse;
import com.webank.wedatasphere.qualitis.request.AddRuleMetricRequest;
import com.webank.wedatasphere.qualitis.request.ModifyRuleMetricRequest;
import com.webank.wedatasphere.qualitis.rule.constant.TableDataTypeEnum;
import com.webank.wedatasphere.qualitis.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.shaded.com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author v_gaojiedeng@webank.com
 */
@Service
public class RuleMetricCommonServiceImpl implements RuleMetricCommonService {

    @Autowired
    private OperateCiService operateCiService;
    @Autowired
    private RuleMetricTypeConfigDao ruleMetricTypeConfigDao;
    @Autowired
    private RuleMetricDao ruleMetricDao;

    @Autowired
    private UserDao userDao;
    @Autowired
    private UserRoleDao userRoleDao;
    @Autowired
    private SubDepartmentPermissionService subDepartmentPermissionService;
    @Autowired
    private DataVisibilityService dataVisibilityService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private RuleMetricCommonService ruleMetricCommonService;

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleMetricCommonServiceImpl.class);

    private static final Map<String, Integer> FREQUENCY_EN = new HashMap<String, Integer>(6);

    private static final Map<Integer, String> FREQUENCY_CN = new HashMap<Integer, String>(6);

    List<String> METRIC_EN_NAME_COLLECT = Arrays.asList("general-metric", "general-check-metric",
            "general-busi-check", "IT-metric", "1-level-metric", "2-level-metric", "custom-metric");

    private static final String IT_METRIC_EN_NAME = "it-metrics";
    private static final String IT_METRIC_CN_NAME = "IT数据监控指标";

    static {
        FREQUENCY_CN.put(1, "每日");
        FREQUENCY_CN.put(2, "月度");
        FREQUENCY_CN.put(3, "季度");
        FREQUENCY_CN.put(4, "半年度");
        FREQUENCY_CN.put(5, "年度");
        FREQUENCY_CN.put(6, "单次");
        FREQUENCY_EN.put("Daily", 1);
        FREQUENCY_EN.put("Monthly", 2);
        FREQUENCY_EN.put("Quarterly", 3);
        FREQUENCY_EN.put("HalfYear", 4);
        FREQUENCY_EN.put("Year", 5);
        FREQUENCY_EN.put("Single", 6);
    }

    @Value("${department.data_source_from: custom}")
    private String departmentSourceType;

    private HttpServletRequest httpServletRequest;

    public RuleMetricCommonServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public RuleMetric accordingRuleMetricNameAdd(String ruleMetricName, String loginUser, boolean multiEnv) throws UnExpectedRequestException, IOException, PermissionDeniedRequestException {
        RuleMetric ruleMetric = null;
        if (StringUtils.isBlank(ruleMetricName)) {
            throw new UnExpectedRequestException(ruleMetricName + " {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        checkDuplicateName(ruleMetricName);

        Map<String, Object> maps = ruleMetricCommonService.checkRuleMetricNameAndAddOrModify(ruleMetricName, loginUser, multiEnv, null);
        RuleMetric ruleMetricData = (RuleMetric) maps.get("rule_metric");
        AddRuleMetricRequest ruleMetricRequest = (AddRuleMetricRequest) maps.get("rule_metric_request");
        if (ruleMetricData == null) {
            ruleMetric = ruleMetricCommonService.addRuleMetricForObject(ruleMetricRequest, loginUser);
            LOGGER.info("Finish to create metric with name: {}", ruleMetricName);
        } else {
            ModifyRuleMetricRequest modifyRuleMetricRequest = new ModifyRuleMetricRequest();
            modifyRuleMetricRequest.setId(ruleMetricData.getId());
            BeanUtils.copyProperties(ruleMetricRequest, modifyRuleMetricRequest);
            ruleMetric = ruleMetricCommonService.modifyRuleMetricReal(modifyRuleMetricRequest, loginUser);
        }
        return ruleMetric;
    }

    @Override
    public Map<String, Object> checkRuleMetricNameAndAddOrModify(String ruleMetricName, String loginUser, Boolean multiEnv, String createUser) throws UnExpectedRequestException, IOException, PermissionDeniedRequestException {

        //判断指标名是否存在于 指标表   输入格式subSystemName_type_enCode_frequency  系统_指标分类_编号_指标频率 (无需对编号进行校验)
        LOGGER.info("Start to create metric with name: {}", ruleMetricName);
        String[] infos = ruleMetricName.split(SpecCharEnum.BOTTOM_BAR.getValue());
        String subSystemName, type, en, frequency = null;
        try {
            subSystemName = infos[0];
            type = infos[1];
            en = infos[2];
            frequency = infos[3];
        } catch (Exception e) {
            throw new UnExpectedRequestException("{&METRICS_FORMAT_ERROR}", 400);
        }

        //指标频率校验
        if (!FREQUENCY_EN.keySet().contains(frequency)) {
            throw new UnExpectedRequestException("Cannot recognize the rule metric type. Choose one {\"Daily\",\"Monthly\",\"Quarterly\",\"HalfYear\",\"Year\",\"Single\"}", 400);
        }
        String cnFrequency = FREQUENCY_CN.get(FREQUENCY_EN.get(frequency));

        //指标分类校验
        String finalType = type;
        List<RuleMetricTypeConfig> ruleMetricTypeConfig = ruleMetricTypeConfigDao.findAllRuleMetricTypeConfig().stream()
                .filter(config -> config.getEnName().equals(finalType)).collect(Collectors.toList());

        //指标分类对历史脚本做兼容处理
        List<String> collectType = METRIC_EN_NAME_COLLECT.stream()
                .filter(item -> item.equals(finalType)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(collectType)) {
            ruleMetricName = subSystemName + SpecCharEnum.BOTTOM_BAR.getValue() +
                    IT_METRIC_EN_NAME + SpecCharEnum.BOTTOM_BAR.getValue() + en +
                    SpecCharEnum.BOTTOM_BAR.getValue() + frequency;
        }

        if (CollectionUtils.isEmpty(collectType) && CollectionUtils.isEmpty(ruleMetricTypeConfig)) {
            throw new UnExpectedRequestException("Cannot recognize the rule metric type. Choose one {" + ruleMetricTypeConfig.stream()
                    .map(RuleMetricTypeConfig::getEnName).collect(Collectors.joining()) + "}", 400);
        }
        RuleMetricTypeConfig currentRuleMetricTypeConfig = new RuleMetricTypeConfig();
        String cnType = null;
        if (CollectionUtils.isNotEmpty(ruleMetricTypeConfig)) {
            currentRuleMetricTypeConfig = ruleMetricTypeConfig.iterator().next();
            cnType = currentRuleMetricTypeConfig.getCnName();
        } else {
            currentRuleMetricTypeConfig.setId(1L);
            cnType = IT_METRIC_CN_NAME;
        }

        //系统校验
        List<SubSystemResponse> subSystemResponses = operateCiService.getAllSubSystemInfo();
        List<CmdbDepartmentResponse> deptResponse;

        if (DepartmentSourceTypeEnum.CUSTOM.getValue().equals(departmentSourceType)) {
            List<Department> departmentResponses = departmentService.findAllDepartmentCodeAndName();
            deptResponse = departmentResponses.stream().map(departmentResponse -> {
                CmdbDepartmentResponse cmdbDepartmentResponse = new CmdbDepartmentResponse();
                cmdbDepartmentResponse.setCode(departmentResponse.getDepartmentCode());
                cmdbDepartmentResponse.setName(departmentResponse.getName());
                return cmdbDepartmentResponse;
            }).collect(Collectors.toList());
        } else {
            deptResponse = operateCiService.getAllDepartmetInfo();
        }

        List<SubSystemResponse> currentSubSystemResponses = subSystemResponses.stream()
                .filter(subSystemResponse -> subSystemResponse.getSubSystemName().equals(subSystemName)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(currentSubSystemResponses)) {
            throw new UnExpectedRequestException("Cannot recognize the sub system name.", 400);
        }

        SubSystemResponse currentSystem = currentSubSystemResponses.iterator().next();
        LOGGER.info("Find sub system from CMDB, current sub system is [ID=" + currentSystem.getSubSystemId() + ", name=" + currentSystem.getSubSystemName() + "]");

        String dept, devDept = null;
        if (StringUtils.isNotBlank(loginUser)) {
            User user = userDao.findByUsername(loginUser);
            if (user == null) {
                throw new UnExpectedRequestException(String.format("{&FAILED_TO_FIND_USER} %s", loginUser));
            }
            if (StringUtils.isEmpty(user.getDepartmentName())) {
                throw new UnExpectedRequestException("Current user has no department name.");
            }
            //数据格式 例如：基础科技产品部/大数据平台室
            String[] splitData = user.getDepartmentName().split("/");
            dept = splitData[0];
            devDept = splitData[1];
        } else {
            dept = currentSystem.getDepartmentName();
            if (StringUtils.isEmpty(dept)) {
                throw new UnExpectedRequestException("Current system has no dept data.");
            }
            if (StringUtils.isEmpty(currentSystem.getDevDepartmentName())) {
                throw new UnExpectedRequestException("Current system has no dept data.");
            }
            devDept = currentSystem.getDevDepartmentName();
        }

        String finalDept = dept;
        CmdbDepartmentResponse currentDept = deptResponse.stream().filter(map -> finalDept.equals(map.getName())).iterator().next();

        String deptCode = currentDept.getCode();
        String opsDept = devDept;

        List<DepartmentSubResponse> departmentSubResponses;
        if (DepartmentSourceTypeEnum.CUSTOM.getValue().equals(departmentSourceType)) {
            departmentSubResponses = departmentService.getSubDepartmentByDeptCode(Integer.parseInt(deptCode));
        } else {
            departmentSubResponses = operateCiService.getDevAndOpsInfo(Integer.parseInt(deptCode));
        }
        String finalDevDept = devDept;
        List<DepartmentSubResponse> conformCollect = departmentSubResponses.stream().filter(item -> finalDevDept.equals(item.getName())).collect(Collectors.toList());

        Long devDeptId = CollectionUtils.isNotEmpty(conformCollect) ? Long.parseLong(conformCollect.iterator().next().getId()) : null;
        Long opsDeptId = devDeptId;

        AddRuleMetricRequest ruleMetricRequest = new AddRuleMetricRequest();
        setBasicInfoForRequest(ruleMetricName, en, frequency, cnFrequency, currentRuleMetricTypeConfig, cnType, currentSystem, dept, deptCode, devDept, opsDept, devDeptId, opsDeptId, ruleMetricRequest);

        if (multiEnv) {
            ruleMetricRequest.setMultiEnv(true);
        } else {
            ruleMetricRequest.setMultiEnv(false);
        }

        RuleMetric ruleMetricInDb = ruleMetricDao.findByEnCode(en);

        Map<String, Object> map = Maps.newHashMap();
        map.put("rule_metric", ruleMetricInDb);
        map.put("rule_metric_request", ruleMetricRequest);
        return map;
    }

    @Override
    public RuleMetric modifyRuleMetricReal(ModifyRuleMetricRequest request, String userName) throws UnExpectedRequestException, PermissionDeniedRequestException {
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

        dataVisibilityService.delete(ruleMetricInDb.getId(), TableDataTypeEnum.RULE_METRIC);
        dataVisibilityService.saveBatch(ruleMetricInDb.getId(), TableDataTypeEnum.RULE_METRIC, request.getVisibilityDepartmentList());
        return ruleMetricInDb;
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
    public RuleMetric addRuleMetricForObject(AddRuleMetricRequest request, String userName) throws UnExpectedRequestException, PermissionDeniedRequestException {
        LOGGER.info("Start to add rule metric, add request: [{}], user: [{}]", request.toString(), userName);
        User loginUser = userDao.findByUsername(userName);
        List<UserRole> userRoles = userRoleDao.findByUser(loginUser);
        Integer roleType = roleService.getRoleType(userRoles);
        subDepartmentPermissionService.checkEditablePermission(roleType, loginUser, null, request.getDevDepartmentId(), request.getOpsDepartmentId(), false);

        RuleMetric newRuleMetric = buildRuleMetric(request, userName);
        RuleMetric savedRuleMetric = ruleMetricDao.add(newRuleMetric);

        dataVisibilityService.saveBatch(savedRuleMetric.getId(), TableDataTypeEnum.RULE_METRIC, request.getVisibilityDepartmentList());
        return savedRuleMetric;
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

    private void setBasicInfoForRequest(String ruleMetricName, String en, String frequency, String cnFrequency,
                                        RuleMetricTypeConfig currentRuleMetricTypeConfig, String cnType, SubSystemResponse currentSystem, String dept, String deptCode,
                                        String devDept, String opsDept, Long devDeptId, Long opsDeptId, AddRuleMetricRequest ruleMetricRequest) {
        ruleMetricRequest.setName(ruleMetricName);
        ruleMetricRequest.setDesc("Auto created.");
        ruleMetricRequest.setCnName(currentSystem.getSubSystemFullCnName()
                .concat(SpecCharEnum.BOTTOM_BAR.getValue())
                .concat(cnType)
                .concat(SpecCharEnum.BOTTOM_BAR.getValue())
                .concat(en)
                .concat(SpecCharEnum.BOTTOM_BAR.getValue())
                .concat(cnFrequency));

        ruleMetricRequest.setEnCode(en);
        ruleMetricRequest.setAvailable(true);
        ruleMetricRequest.setDepartmentName(dept);
        ruleMetricRequest.setDepartmentCode(deptCode);
        ruleMetricRequest.setDevDepartmentName(dept + "/" + devDept);
        ruleMetricRequest.setOpsDepartmentName(dept + "/" + opsDept);
        ruleMetricRequest.setDevDepartmentId(devDeptId);
        ruleMetricRequest.setOpsDepartmentId(opsDeptId);
        ruleMetricRequest.setFrequency(FREQUENCY_EN.get(frequency));
        ruleMetricRequest.setSubSystemId(currentSystem.getSubSystemId());
        ruleMetricRequest.setSubSystemName(currentSystem.getSubSystemName());
        ruleMetricRequest.setFullCnName(currentSystem.getSubSystemFullCnName());
        ruleMetricRequest.setBussCode(RuleMetricBussCodeEnum.SUBSYSTEM.getCode());
        ruleMetricRequest.setType(currentRuleMetricTypeConfig.getId().intValue());
    }
}
