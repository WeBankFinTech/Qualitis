package com.webank.wedatasphere.qualitis.service.impl;

import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.webank.wedatasphere.qualitis.constant.RuleMetricBussCodeEnum;
import com.webank.wedatasphere.qualitis.constant.RuleMetricLevelEnum;
import com.webank.wedatasphere.qualitis.dao.RuleMetricDao;
import com.webank.wedatasphere.qualitis.dao.RuleMetricDepartmentUserDao;
import com.webank.wedatasphere.qualitis.dao.RuleMetricTypeConfigDao;
import com.webank.wedatasphere.qualitis.dao.TaskResultDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.dao.UserRoleDao;
import com.webank.wedatasphere.qualitis.entity.Department;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.entity.RuleMetricDepartmentUser;
import com.webank.wedatasphere.qualitis.entity.RuleMetricTypeConfig;
import com.webank.wedatasphere.qualitis.entity.TaskResult;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.entity.UserRole;
import com.webank.wedatasphere.qualitis.excel.ExcelRuleMetricListener;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.response.DataInfo;
import com.webank.wedatasphere.qualitis.project.constant.ExcelSheetName;
import com.webank.wedatasphere.qualitis.project.excel.ExcelRuleMetric;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.project.response.HiveRuleDetail;
import com.webank.wedatasphere.qualitis.request.AddRuleMetricRequest;
import com.webank.wedatasphere.qualitis.request.DownloadRuleMetricRequest;
import com.webank.wedatasphere.qualitis.request.ModifyRuleMetricRequest;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.request.RuleMetricQueryRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.response.RuleMetricConditionResponse;
import com.webank.wedatasphere.qualitis.response.RuleMetricResponse;
import com.webank.wedatasphere.qualitis.response.RuleMetricValueResponse;
import com.webank.wedatasphere.qualitis.rule.constant.RoleDefaultTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmConfig;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.exception.WriteExcelException;
import com.webank.wedatasphere.qualitis.service.RoleService;
import com.webank.wedatasphere.qualitis.service.RuleMetricService;
import com.webank.wedatasphere.qualitis.submitter.impl.ExecutionManagerImpl;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
  private RoleService roleService;

  @Autowired
  private RuleMetricDepartmentUserDao ruleMetricDepartmentUserDao;

  private static final Logger LOGGER = LoggerFactory.getLogger(RuleMetricServiceImpl.class);

  private final FastDateFormat FILE_DATE_FORMATTER = FastDateFormat.getInstance("yyyyMMddHHmmss");

  private static final String SUPPORT_EXCEL_SUFFIX_NAME = ".xlsx";

  private static final int MAX_RULE_METRIC_COUNT = 10000;

  private HttpServletRequest httpServletRequest;
  public RuleMetricServiceImpl(@Context HttpServletRequest httpServletRequest) {
    this.httpServletRequest = httpServletRequest;
  }

  @Override
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class, UnExpectedRequestException.class})
  public GeneralResponse<RuleMetricResponse> addRuleMetricForOuter(AddRuleMetricRequest request, String loginUser) throws UnExpectedRequestException {
    return addRuleMetricReal(request, loginUser);
  }

  private GeneralResponse<RuleMetricResponse> addRuleMetricReal(AddRuleMetricRequest request, String userName) throws UnExpectedRequestException {
    checkDuplicateName(request.getName());
    checkDuplicateCode(request.getEnCode());
    LOGGER.info("Start to add rule metric, add request: [{}], user: [{}]",request.toString(), userName);
    User loginUser = userDao.findByUsername(userName);
    List<UserRole> userRoles =  userRoleDao.findByUser(loginUser);
    Integer roleType = roleService.getRoleType(userRoles);

    RuleMetric newRuleMetric = new RuleMetric(request.getName(), request.getCnName(), request.getDesc(), request.getSubSystemName(), request.getFullCnName()
        , request.getProductName(), request.getDepartmentName(), request.getDevDepartmentName(), request.getOpsDepartmentName(), request.getType()
        , request.getEnCode(), request.getFrequency(), request.getAvailable(), request.getBussCode(), request.getBussCustom());
    newRuleMetric.setCreateUser(userName);
    newRuleMetric.setCreateTime(ExecutionManagerImpl.PRINT_TIME_FORMAT.format(new Date()));
    RuleMetric savedRuleMetric = ruleMetricDao.add(newRuleMetric);
    if (roleType.equals(RoleDefaultTypeEnum.ADMIN.getCode())) {
      LOGGER.info("First level(created by SYS_ADMIN) indicator will be created soon.");
      savedRuleMetric.setLevel(RuleMetricLevelEnum.DEFAULT_METRIC.getCode());
    } else if (roleType.equals(RoleDefaultTypeEnum.DEPARTMENT_ADMIN.getCode())){
      LOGGER.info("Second level(created by DEPARTMENT_ADMIN) indicator will be created soon.");
      savedRuleMetric.setLevel(RuleMetricLevelEnum.DEPARTMENT_METRIC.getCode());
      for (UserRole temp : userRoles) {
        Department department = temp.getRole().getDepartment();
        if (department != null) {
          RuleMetricDepartmentUser ruleMetricDepartmentUser = new RuleMetricDepartmentUser();
          ruleMetricDepartmentUser.setDepartment(department);
          ruleMetricDepartmentUser.setRuleMetric(savedRuleMetric);
          ruleMetricDepartmentUserDao.add(ruleMetricDepartmentUser);
          LOGGER.info("Succeed to save rule metric department user.");
        }
      }
    } else {
      LOGGER.info("Third level(created by PROJECTOR) indicator will be created soon.");
      savedRuleMetric.setLevel(RuleMetricLevelEnum.PERSONAL_METRIC.getCode());
      RuleMetricDepartmentUser ruleMetricDepartmentUser = new RuleMetricDepartmentUser();
      ruleMetricDepartmentUser.setDepartment(loginUser.getDepartment());
      ruleMetricDepartmentUser.setUser(loginUser);
      ruleMetricDepartmentUser.setRuleMetric(savedRuleMetric);
      ruleMetricDepartmentUserDao.add(ruleMetricDepartmentUser);
      LOGGER.info("Succeed to save rule metric department user.");
    }
    RuleMetricResponse response = new RuleMetricResponse(ruleMetricDao.add(savedRuleMetric));
    return new GeneralResponse<>("200", "{&ADD_RULE_METRIC_SUCCESSFULLY}", response);
  }

  @Override
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class, UnExpectedRequestException.class})
  public GeneralResponse<RuleMetricResponse> addRuleMetric(AddRuleMetricRequest request) throws UnExpectedRequestException {
    if (request == null) {
      throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
    }
    String userName = HttpUtils.getUserName(httpServletRequest);
    return addRuleMetricReal(request, userName);
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

    if (roleType.equals(RoleDefaultTypeEnum.ADMIN.getCode())) {
      LOGGER.info("SYS_ADMIN will delete rule metric.");
      RuleMetricDepartmentUser ruleMetricDepartmentUser = ruleMetricDepartmentUserDao.findByRuleMetric(ruleMetricInDb);
      if (ruleMetricDepartmentUser != null) {
        LOGGER.info("Rule metric[{}] will be delete.", ruleMetricInDb.toString());
        ruleMetricDepartmentUserDao.delete(ruleMetricDepartmentUser);
      }
    } else if (roleType.equals(RoleDefaultTypeEnum.DEPARTMENT_ADMIN.getCode())) {
      LOGGER.info("DEPARTMENT_ADMIN will delete rule metric.");

      if (ruleMetricInDb.getLevel().equals(RuleMetricLevelEnum.DEFAULT_METRIC.getCode())) {
        throw new PermissionDeniedRequestException("User {&HAS_NO_PERMISSION_TO_ACCESS}", 403);
      }
      List<Department> managedDepartment = new ArrayList<>();
      for (UserRole userRole : userRoles) {
        Department department = userRole.getRole().getDepartment();
        if (department != null) {
          managedDepartment.add(department);
        }
      }
      RuleMetricDepartmentUser ruleMetricDepartmentUser = ruleMetricDepartmentUserDao.findByRuleMetric(ruleMetricInDb);
      if (ruleMetricDepartmentUser != null && managedDepartment.contains(ruleMetricDepartmentUser.getDepartment())) {
        LOGGER.info("Rule metric[{}] will be delete.", ruleMetricInDb.toString());
        ruleMetricDepartmentUserDao.delete(ruleMetricDepartmentUser);
      } else {
        throw new PermissionDeniedRequestException("User {&HAS_NO_PERMISSION_TO_ACCESS}", 403);
      }
    } else {
      LOGGER.info("PROJECTOR will delete rule metric.");

      if (! ruleMetricInDb.getLevel().equals(RuleMetricLevelEnum.PERSONAL_METRIC.getCode())
          || ! ruleMetricInDb.getCreateUser().equals(loginUser.getUserName())) {
        throw new PermissionDeniedRequestException("User {&HAS_NO_PERMISSION_TO_ACCESS}", 403);
      }
      RuleMetricDepartmentUser ruleMetricDepartmentUser = ruleMetricDepartmentUserDao.findByRuleMetric(ruleMetricInDb);
      if (ruleMetricDepartmentUser != null) {
        LOGGER.info("Rule metric[{}] will be delete.", ruleMetricInDb.toString());
        ruleMetricDepartmentUserDao.delete(ruleMetricDepartmentUser);
      } else {
        throw new PermissionDeniedRequestException("User {&HAS_NO_PERMISSION_TO_ACCESS}", 403);
      }
    }

    ruleMetricDao.delete(ruleMetricInDb);
    return new GeneralResponse<>("200", "{&DELETE_RULE_METRIC_SUCCESSFULLY}", null);
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
    LOGGER.info("Start to modify rule metric, modify request: [{}], user: [{}]",request.toString(), userName);
    if (! ruleMetricInDb.getName().equals(request.getName())) {
      checkDuplicateName(request.getName());
    }
    if (! ruleMetricInDb.getEnCode().equals(request.getEnCode())) {
      checkDuplicateCode(request.getEnCode());
    }
    User loginUser = userDao.findByUsername(userName);
    List<UserRole> userRoles =  userRoleDao.findByUser(loginUser);

    Integer roleType = roleService.getRoleType(userRoles);
    if (roleType.equals(RoleDefaultTypeEnum.ADMIN.getCode())) {
      LOGGER.info("First level(created by SYS_ADMIN) indicator will be modified soon.");
    } else if (roleType.equals(RoleDefaultTypeEnum.DEPARTMENT_ADMIN.getCode())){
      LOGGER.info("Second level(created by DEPARTMENT_ADMIN) indicator will be modified soon.");

      if (ruleMetricInDb.getLevel().equals(RuleMetricLevelEnum.DEFAULT_METRIC.getCode())) {
        throw new PermissionDeniedRequestException("User {&HAS_NO_PERMISSION_TO_ACCESS}", 403);
      }
      List<Department> managedDepartment = new ArrayList<>();
      for (UserRole userRole : userRoles) {
        Department department = userRole.getRole().getDepartment();
        if (department != null) {
          managedDepartment.add(department);
        }
      }

      RuleMetricDepartmentUser ruleMetricDepartmentUser = ruleMetricDepartmentUserDao.findByRuleMetric(ruleMetricInDb);
      if (ruleMetricDepartmentUser != null && managedDepartment.contains(ruleMetricDepartmentUser.getDepartment())) {
        LOGGER.info("Rule metric[{}]", ruleMetricInDb.toString());
      } else {
        throw new PermissionDeniedRequestException("User {&HAS_NO_PERMISSION_TO_ACCESS}", 403);
      }
    } else {
      LOGGER.info("Third level(created by PROJECTOR) indicator will be modified soon.");

      if (! ruleMetricInDb.getLevel().equals(RuleMetricLevelEnum.PERSONAL_METRIC.getCode())
          || ! ruleMetricInDb.getCreateUser().equals(loginUser.getUserName())) {
        throw new PermissionDeniedRequestException("User {&HAS_NO_PERMISSION_TO_ACCESS}", 403);
      }
    }
    Integer bussCode = request.getBussCode();
    ruleMetricInDb.setName(request.getName());
    ruleMetricInDb.setCnName(request.getCnName());
    ruleMetricInDb.setMetricDesc(request.getDesc());
    ruleMetricInDb.setBussCode(bussCode);
    if (RuleMetricBussCodeEnum.SUBSYSTEM.getCode().equals(bussCode)) {
      ruleMetricInDb.setSubSystemName(request.getSubSystemName());
      ruleMetricInDb.setFullCnName(request.getFullCnName());
      // Empty them
      ruleMetricInDb.setBussCustom("");
      ruleMetricInDb.setProductName("");
    } else if (RuleMetricBussCodeEnum.PRODUCT.getCode().equals(bussCode)) {
      ruleMetricInDb.setProductName(request.getProductName());

      ruleMetricInDb.setSubSystemName("");
      ruleMetricInDb.setFullCnName("");
      ruleMetricInDb.setBussCustom("");
    } else if (RuleMetricBussCodeEnum.CUSTOM.getCode().equals(bussCode)) {
      ruleMetricInDb.setBussCustom(request.getBussCustom());

      ruleMetricInDb.setProductName("");
      ruleMetricInDb.setSubSystemName("");
      ruleMetricInDb.setFullCnName("");
    }

    ruleMetricInDb.setModifyUser(userName);
    ruleMetricInDb.setModifyTime(ExecutionManagerImpl.PRINT_TIME_FORMAT.format(new Date()));
    ruleMetricInDb.setType(request.getType());
    ruleMetricInDb.setEnCode(request.getEnCode());
    ruleMetricInDb.setFrequency(request.getFrequency());
    ruleMetricInDb.setDepartmentName(request.getDepartmentName());
    ruleMetricInDb.setDevDepartmentName(request.getDevDepartmentName());
    ruleMetricInDb.setOpsDepartmentName(request.getOpsDepartmentName());

    ruleMetricInDb.setAvailable(request.getAvailable());

    RuleMetricResponse response = new RuleMetricResponse(ruleMetricDao.add(ruleMetricInDb));
    return new GeneralResponse<>("200", "{&MODIFY_RULE_METRIC_SUCCESSFULLY}", response);
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
    LOGGER.info("Start to get rule metric, rule metric ID: [{}], user: [{}]", id, userName);

    User loginUser = userDao.findByUsername(userName);
    List<UserRole> userRoles = userRoleDao.findByUser(loginUser);
    Integer roleType = roleService.getRoleType(userRoles);

    RuleMetricDepartmentUser ruleMetricDepartmentUser = ruleMetricDepartmentUserDao.findByRuleMetric(ruleMetricInDb);
    if (roleType.equals(RoleDefaultTypeEnum.ADMIN.getCode())) {
      LOGGER.info("SYS_ADMIN will get rule metric.");
    } else if (roleType.equals(RoleDefaultTypeEnum.DEPARTMENT_ADMIN.getCode())) {
      LOGGER.info("DEPARTMENT_ADMIN will get rule metric.");

      List<Department> managedDepartment = new ArrayList<>();
      for (UserRole userRole : userRoles) {
        Department department = userRole.getRole().getDepartment();
        if (department != null) {
          managedDepartment.add(department);
        }
      }

      if (ruleMetricDepartmentUser.getDepartment() != null) {
        List<Department> res = managedDepartment.stream().filter(department ->
            department.getId() == ruleMetricDepartmentUser.getDepartment().getId()).collect(Collectors.toList());
         if (res.size() > 0) {
           LOGGER.info("Rule metric[{}] comes from department: {}", ruleMetricInDb.toString(), ruleMetricDepartmentUser.getDepartment().getName());
         } else {
           throw new PermissionDeniedRequestException("User {&HAS_NO_PERMISSION_TO_ACCESS}", 403);
         }
      } else if (ruleMetricInDb.getLevel().equals(RuleMetricLevelEnum.DEFAULT_METRIC.getCode())) {
        LOGGER.info("DEPARTMENT_ADMIN will get first level rule metric.");
      } else {
        throw new PermissionDeniedRequestException("User {&HAS_NO_PERMISSION_TO_ACCESS}", 403);
      }
    } else {
      LOGGER.info("PROJECTOR will get rule metric.");
      Department department = loginUser.getDepartment();

      if (ruleMetricInDb.getLevel().equals(RuleMetricLevelEnum.DEPARTMENT_METRIC) && ! department.equals(ruleMetricDepartmentUser.getDepartment())) {
        throw new PermissionDeniedRequestException("User {&HAS_NO_PERMISSION_TO_ACCESS}", 403);
      }

      if (ruleMetricInDb.getLevel().equals(RuleMetricLevelEnum.PERSONAL_METRIC) && ! loginUser.equals(ruleMetricDepartmentUser.getUser())) {
        throw new PermissionDeniedRequestException("User {&HAS_NO_PERMISSION_TO_ACCESS}", 403);
      }
    }
    RuleMetricResponse ruleMetricResponse = new RuleMetricResponse(ruleMetricInDb);
    return new GeneralResponse<>("200", "{&GET_RULE_METRIC_SUCCESSFULLY}", ruleMetricResponse);
  }

  @Override
  public GeneralResponse<GetAllResponse<RuleMetricResponse>> getAllRuleMetric(PageRequest request) throws UnExpectedRequestException {
    PageRequest.checkRequest(request);
    String userName = HttpUtils.getUserName(httpServletRequest);
    LOGGER.info("Start to get all rule metric, page request: [{}], user: [{}]",request.toString(), userName);
    User loginUser = userDao.findByUsername(userName);
    List<UserRole> userRoles =  userRoleDao.findByUser(loginUser);
    Integer roleType = roleService.getRoleType(userRoles);

    List<RuleMetric> ruleMetrics = new ArrayList<>();
    long total = 0;
    if (roleType.equals(RoleDefaultTypeEnum.ADMIN.getCode())) {
      LOGGER.info("SYS_ADMIN will get all rule metrics.");
      ruleMetrics.addAll(ruleMetricDao.findAllRuleMetrics(request.getPage(), request.getSize()));
      total = ruleMetricDao.countAllRuleMetrics();
    } else if (roleType.equals(RoleDefaultTypeEnum.DEPARTMENT_ADMIN.getCode())){
      LOGGER.info("DEPARTMENT_ADMIN will get rule metrics of all management departments and all projectors.");
      List<Department> departments = new ArrayList<>();
      for (UserRole temp : userRoles) {
        Department department = temp.getRole().getDepartment();
        if (department != null) {
          departments.add(department);
        }
      }
      ruleMetrics.addAll(ruleMetricDao.findRuleMetrics(RuleMetricLevelEnum.DEFAULT_METRIC.getCode(),
          departments, null, request.getPage(), request.getSize()));
      total = ruleMetricDao.countRuleMetrics(RuleMetricLevelEnum.DEFAULT_METRIC.getCode(),
          departments, null);
    } else {
      LOGGER.info("PROJECTOR  will get rule metrics of department and own.");
      List<Department> departments = new ArrayList<>();
      departments.add(loginUser.getDepartment());
      ruleMetrics.addAll(ruleMetricDao.findRuleMetrics(RuleMetricLevelEnum.DEFAULT_METRIC.getCode(),
          departments, loginUser, request.getPage(), request.getSize()));
      total = ruleMetricDao.countRuleMetrics(RuleMetricLevelEnum.DEFAULT_METRIC.getCode(),
          departments, loginUser);
    }

    GetAllResponse<RuleMetricResponse> response = new GetAllResponse<>();
    List<RuleMetricResponse> ruleMetricResponses = new ArrayList<>();
    for (RuleMetric ruleMetric : ruleMetrics) {
      ruleMetricResponses.add(new RuleMetricResponse(ruleMetric));
    }
    response.setData(ruleMetricResponses);
    response.setTotal(total);

    return new GeneralResponse<>("200", "{&GET_RULE_METRIC_SUCCESSFULLY}", response);
  }

  @Override
  public RuleMetricConditionResponse conditions() {
    String userName = HttpUtils.getUserName(httpServletRequest);
    LOGGER.info("Start to get rule metric condition, user: [{}]", userName);
    User loginUser = userDao.findByUsername(userName);
    List<UserRole> userRoles =  userRoleDao.findByUser(loginUser);
    Integer roleType = roleService.getRoleType(userRoles);

    List<RuleMetric> ruleMetrics = new ArrayList<>();
    if (roleType.equals(RoleDefaultTypeEnum.ADMIN.getCode())) {
      LOGGER.info("SYS_ADMIN will get all rule metrics with conditions.");
      ruleMetrics.addAll(ruleMetricDao.findAllRuleMetrics(0, Integer.MAX_VALUE));
    } else if (roleType.equals(RoleDefaultTypeEnum.DEPARTMENT_ADMIN.getCode())){
      LOGGER.info("DEPARTMENT_ADMIN will get rule metrics of all management departments and all projectors.");
      List<Department> departments = new ArrayList<>();
      for (UserRole temp : userRoles) {
        Department department = temp.getRole().getDepartment();
        if (department != null) {
          departments.add(department);
        }
      }
      ruleMetrics.addAll(ruleMetricDao.findRuleMetrics(RuleMetricLevelEnum.DEFAULT_METRIC.getCode(),
          departments, null, 0, Integer.MAX_VALUE));
    } else {
      LOGGER.info("PROJECTOR  will get rule metrics of department and own.");
      List<Department> departments = new ArrayList<>();
      departments.add(loginUser.getDepartment());
      ruleMetrics.addAll(ruleMetricDao.findRuleMetrics(RuleMetricLevelEnum.DEFAULT_METRIC.getCode(),
          departments, loginUser, 0, Integer.MAX_VALUE));
    }

    Set<String> subSystemNameSet = ruleMetrics.stream().map(RuleMetric::getSubSystemName).filter(s -> StringUtils.isNotBlank(s)).collect(Collectors.toSet());
    Set<RuleMetricTypeConfig> ruleMetricTypeConfigss = ruleMetricTypeConfigDao.findAllRuleMetricTypeConfig().stream().collect(Collectors.toSet());
    Set<String> enCode = ruleMetrics.stream().map(RuleMetric::getEnCode).filter(s -> StringUtils.isNotBlank(s)).collect(Collectors.toSet());

    RuleMetricConditionResponse response = new RuleMetricConditionResponse();
    response.setSubSystemNameCondition(subSystemNameSet);
    response.setRuleMetricType(ruleMetricTypeConfigss);
    response.setEnCode(enCode);

    return response;
  }

  @Override
  public GeneralResponse<GetAllResponse<RuleMetricResponse>> queryRuleMetric(RuleMetricQueryRequest request) throws UnExpectedRequestException {
    CommonChecker.checkObject(request, "Rule Metric query request");
    if (StringUtils.isBlank(request.getSubSystemName())) {
      request.setSubSystemName("");
    }
    if (StringUtils.isBlank(request.getRuleMetricName())) {
      request.setRuleMetricName("%");
    } else {
      request.setRuleMetricName("%" + request.getRuleMetricName() + "%");
    }
    String userName = HttpUtils.getUserName(httpServletRequest);
    LOGGER.info("Start to get all rule metric, page request: [{}], user: [{}]",request.toString(), userName);
    User loginUser = userDao.findByUsername(userName);
    List<UserRole> userRoles =  userRoleDao.findByUser(loginUser);
    Integer roleType = roleService.getRoleType(userRoles);

    List<RuleMetric> ruleMetrics = new ArrayList<>();
    long total = 0;
    if (roleType.equals(RoleDefaultTypeEnum.ADMIN.getCode())) {
      LOGGER.info("SYS_ADMIN will get all rule metrics in query.");
      ruleMetrics.addAll(ruleMetricDao.queryAllRuleMetrics(request.getSubSystemName(), request.getRuleMetricName(), request.getEnCode(), request.getType()
          , request.getAvailable(), request.getPage(), request.getSize()));
      total = ruleMetricDao.countQueryAllRuleMetrics(request.getSubSystemName(), request.getRuleMetricName(), request.getEnCode(), request.getType()
          , request.getAvailable());
    } else if (roleType.equals(RoleDefaultTypeEnum.DEPARTMENT_ADMIN.getCode())){
      LOGGER.info("DEPARTMENT_ADMIN will get rule metrics of all management departments and all projectors.");
      List<Department> departments = new ArrayList<>();
      for (UserRole temp : userRoles) {
        Department department = temp.getRole().getDepartment();
        if (department != null) {
          departments.add(department);
        }
      }
      ruleMetrics.addAll(ruleMetricDao.queryRuleMetrics(RuleMetricLevelEnum.DEFAULT_METRIC.getCode(), departments, null,
          request.getSubSystemName(), request.getRuleMetricName(), request.getEnCode(), request.getType()
          , request.getAvailable(), request.getPage(), request.getSize()));
      total = ruleMetricDao.countQueryRuleMetrics(RuleMetricLevelEnum.DEFAULT_METRIC.getCode(), departments, null,
          request.getSubSystemName(), request.getRuleMetricName(), request.getEnCode(), request.getType()
          , request.getAvailable());
    } else {
      LOGGER.info("PROJECTOR  will get rule metrics of department and own.");
      List<Department> departments = new ArrayList<>();
      departments.add(loginUser.getDepartment());
      ruleMetrics.addAll(ruleMetricDao.queryRuleMetrics(RuleMetricLevelEnum.DEFAULT_METRIC.getCode(),
          departments, loginUser, request.getSubSystemName(), request.getRuleMetricName(), request.getEnCode(), request.getType()
          , request.getAvailable(), request.getPage(), request.getSize()));
      total = ruleMetricDao.countQueryRuleMetrics(RuleMetricLevelEnum.DEFAULT_METRIC.getCode(), departments, loginUser,
          request.getSubSystemName(), request.getRuleMetricName(), request.getEnCode(), request.getType()
          , request.getAvailable());
    }

    GetAllResponse<RuleMetricResponse> response = new GetAllResponse<>();
    List<RuleMetricResponse> ruleMetricResponses = new ArrayList<>();
    for (RuleMetric ruleMetric : ruleMetrics) {
      ruleMetricResponses.add(new RuleMetricResponse(ruleMetric));
    }
    response.setData(ruleMetricResponses);
    response.setTotal(total);

    return new GeneralResponse<>("200", "{&RULE_METRIC_QUERY_SUCCESS}", response);
  }

  @Override
  public DataInfo<HiveRuleDetail> getRulesByRuleMetric(long id, int page, int size) throws UnExpectedRequestException {
    DataInfo<HiveRuleDetail> dataInfo = new DataInfo<>();
    if (id <= 0) {
      throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
    }
    // Check rule metric existence.
    RuleMetric ruleMetricInDb = ruleMetricDao.findById(id);
    if (ruleMetricInDb == null) {
      throw new UnExpectedRequestException("Rule Metric ID [" + id + "] {&DOES_NOT_EXIST}");
    }
    List<Long> ruleIds = taskResultDao.findRuleByRuleMetric(ruleMetricInDb.getId(), page, size)
        .stream().distinct().collect(Collectors.toList());
    int total = taskResultDao.countRuleByRuleMetric(ruleMetricInDb.getId());
    List<HiveRuleDetail> result = new ArrayList<>(ruleIds.size());
    for (Long ruleId : ruleIds) {
      Rule ruleInDb = ruleDao.findById(ruleId);
      if (ruleInDb == null) {
        total --;
        continue;
      }
      List<Long> ruleMetricIds = ruleInDb.getAlarmConfigs().stream().map(AlarmConfig::getRuleMetric).filter(ruleMetric -> ruleMetric != null)
          .map(RuleMetric::getId).collect(Collectors.toList());
      if (ruleInDb != null && ruleMetricIds.contains(id)) {
        HiveRuleDetail hiveRuleDetail = new HiveRuleDetail(ruleInDb);
        result.add(hiveRuleDetail);
      }

    }
    dataInfo.setContent(result);
    dataInfo.setTotalCount(total);
    return dataInfo;
  }

    @Override
    public DataInfo<RuleMetricValueResponse> getResultsByRuleMetric(long ruleMetricId, int page, int size) throws UnExpectedRequestException {
      DataInfo<RuleMetricValueResponse> dataInfo = new DataInfo<>();
      int total = taskResultDao.countValuesByRuleMetric(ruleMetricId);
      List<TaskResult> values = taskResultDao.findValuesByRuleMetric(ruleMetricId, page, size);

      List<RuleMetricValueResponse> responses = new ArrayList<>(values.size());
      for (TaskResult taskResult : values) {
        if (taskResult == null) {
          continue;
        }

        RuleMetricValueResponse ruleMetricValueResponse = new RuleMetricValueResponse();
        ruleMetricValueResponse.setGenerateTime(taskResult.getCreateTime());
        Rule currentRule = ruleDao.findById(taskResult.getRuleId());
        ruleMetricValueResponse.setRelatedRuleName(currentRule == null ? "Deleted" : currentRule.getName());
        ruleMetricValueResponse.setRuleMetricValue(StringUtils.isBlank(taskResult.getValue()) ? "0" : taskResult.getValue());
        responses.add(ruleMetricValueResponse);
      }
      dataInfo.setContent(responses);
      dataInfo.setTotalCount(total);
      return dataInfo;
    }

    @Override
    public GeneralResponse<?> download(DownloadRuleMetricRequest request, HttpServletResponse response)
        throws UnExpectedRequestException, IOException, WriteExcelException, PermissionDeniedRequestException {
      // Check rule metric IDs permission.
      PageRequest pageRequest = new PageRequest(0, Integer.MAX_VALUE);
      List<RuleMetricResponse> ownRuleMetric = getAllRuleMetric(pageRequest).getData().getData().stream().collect(Collectors.toList());
      if (ownRuleMetric.size() <= 0 || ownRuleMetric.size() >= MAX_RULE_METRIC_COUNT) {
        throw new UnExpectedRequestException("The number of metrics is illegal");
      }
      List<Long> downloadIds = request.getRuleMetricIds();
      List<Long> ownIds = ownRuleMetric.stream().map(RuleMetricResponse::getId).collect(Collectors.toList());
      if (ownIds.containsAll(downloadIds)) {
        List<RuleMetricResponse> downloadRuleMetric = ownRuleMetric.stream().filter(ruleMetricResponse ->
            downloadIds.contains(ruleMetricResponse.getId())).collect(Collectors.toList());
        List<ExcelRuleMetric> excelRuleMetrics = new ArrayList<>(downloadRuleMetric.size());

        for (RuleMetricResponse ruleMetricResponse : downloadRuleMetric) {
          ExcelRuleMetric excelRuleMetric = new ExcelRuleMetric();
          excelRuleMetric.setName(ruleMetricResponse.getName());
          excelRuleMetric.setChName(ruleMetricResponse.getCnName());
          excelRuleMetric.setMetricDesc(ruleMetricResponse.getMetricDesc());

          int bussCode = ruleMetricResponse.getBussCode();
          excelRuleMetric.setDimension(bussCode + "");
          if (RuleMetricBussCodeEnum.SUBSYSTEM.getCode().equals(bussCode)) {
            excelRuleMetric.setFullCnName(ruleMetricResponse.getFullCnName());
            excelRuleMetric.setSubSystemName(ruleMetricResponse.getSubSystemName());
            excelRuleMetric.setSubSystemId(String.valueOf(ruleMetricResponse.getSubSystemId()));
          } else if (RuleMetricBussCodeEnum.PRODUCT.getCode().equals(bussCode)) {
            excelRuleMetric.setProductId(ruleMetricResponse.getProductId());
            excelRuleMetric.setProductName(ruleMetricResponse.getProductName());
          } else if (RuleMetricBussCodeEnum.CUSTOM.getCode().equals(bussCode)) {
            excelRuleMetric.setBussCustom(ruleMetricResponse.getBussCustom());
          }
          excelRuleMetric.setFrequency(String.valueOf(ruleMetricResponse.getFrequency()));
          excelRuleMetric.setDevDepartmentName(ruleMetricResponse.getDevDepartmentName());
          excelRuleMetric.setOpsDepartmentName(ruleMetricResponse.getOpsDepartmentName());
          excelRuleMetric.setDepartmentCode(ruleMetricResponse.getDepartmentCode());
          excelRuleMetric.setDepartmentName(ruleMetricResponse.getDepartmentName());
          excelRuleMetric.setAvailable(ruleMetricResponse.getAvailable());
          excelRuleMetric.setEnCode(ruleMetricResponse.getEnCode());
          excelRuleMetric.setType(ruleMetricResponse.getType());

          excelRuleMetrics.add(excelRuleMetric);
        }
        String fileName = "batch_metrics_export_" + FILE_DATE_FORMATTER.format(new Date());
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
      return null;
    }

  private void writeExcelToOutput(List<ExcelRuleMetric> excelRuleMetrics, OutputStream outputStream) throws WriteExcelException, IOException {
    try {
      LOGGER.info("Start to write metric excel");
      ExcelWriter writer = new ExcelWriter(outputStream, ExcelTypeEnum.XLSX, true);
      Sheet templateSheet = new Sheet(1, 0, ExcelRuleMetric.class);
      templateSheet.setSheetName(ExcelSheetName.RULE_METRIC_NAME);
      writer.write(excelRuleMetrics, templateSheet);
      writer.finish();

      LOGGER.info("Finish to write metric excel");
    } catch (Exception e) {
      throw new WriteExcelException(e.getMessage());
    } finally {
      outputStream.close();
    }
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
  public GeneralResponse<?> upload(InputStream fileInputStream, FormDataContentDisposition fileDisposition) throws UnExpectedRequestException, IOException {
    // Check Arguments
    if (fileInputStream == null || fileDisposition == null) {
      throw new UnExpectedRequestException("{&FILE_CAN_NOT_BE_NULL_OR_EMPTY}");
    }
    // Check suffix name of file
    String fileName = fileDisposition.getFileName();
    String suffixName = fileName.substring(fileName.lastIndexOf('.'));
    if (! suffixName.equals(SUPPORT_EXCEL_SUFFIX_NAME)) {
      throw new UnExpectedRequestException("{&DO_NOT_SUPPORT_SUFFIX_NAME}: [" + suffixName + "]. {&ONLY_SUPPORT} [" + SUPPORT_EXCEL_SUFFIX_NAME + "]");
    }
    String userName = HttpUtils.getUserName(httpServletRequest);
    LOGGER.info(userName + " start to upload rule metrics.");

    ExcelRuleMetricListener listener = new ExcelRuleMetricListener();
    List<ExcelRuleMetric> excelRuleMetrics = listener.getRuleMetricContent();
    ExcelReader excelReader = new ExcelReader(fileInputStream, null, listener);
    List<Sheet> sheets = excelReader.getSheets();
    for (Sheet sheet : sheets) {
      if (sheet.getSheetName().equals(ExcelSheetName.RULE_METRIC_NAME)) {
        sheet.setClazz(ExcelRuleMetric.class);
        sheet.setHeadLineMun(1);
        excelReader.read(sheet);
      }
    }
    if (CollectionUtils.isEmpty(excelRuleMetrics)) {
      throw new UnExpectedRequestException("{&FILE_CAN_NOT_BE_EMPTY_OR_FILE_CAN_NOT_BE_RECOGNIZED}");
    }
    try {
      for (ExcelRuleMetric excelRuleMetric : excelRuleMetrics) {
        AddRuleMetricRequest addRuleMetricRequest = new AddRuleMetricRequest();

        addRuleMetric(addRuleMetricRequest);
      }
      LOGGER.info("Succeed to add all rule metrics");
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    } finally {
      fileInputStream.close();
    }
    return new GeneralResponse<>("200", "{&FAILED_TO_UPLOAD_RULE_METRIC}", null);
  }

  @Override
  public GeneralResponse<?> types() {
    return new GeneralResponse<>("200", "", ruleMetricTypeConfigDao.findAllRuleMetricTypeConfig());
  }
}
