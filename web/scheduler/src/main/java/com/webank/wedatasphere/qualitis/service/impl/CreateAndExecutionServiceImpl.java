package com.webank.wedatasphere.qualitis.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.webank.wedatasphere.qualitis.concurrent.RuleContext;
import com.webank.wedatasphere.qualitis.concurrent.RuleContextManager;
import com.webank.wedatasphere.qualitis.config.LinkisConfig;
import com.webank.wedatasphere.qualitis.config.SpecialProjectRuleConfig;
import com.webank.wedatasphere.qualitis.constant.InvokeTypeEnum;
import com.webank.wedatasphere.qualitis.dao.RuleMetricDao;
import com.webank.wedatasphere.qualitis.dao.RuleMetricTypeConfigDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import com.webank.wedatasphere.qualitis.metadata.client.OperateCiService;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.request.*;
import com.webank.wedatasphere.qualitis.response.ApplicationProjectResponse;
import com.webank.wedatasphere.qualitis.response.CreateAndSubmitResponse;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.RuleMetricResponse;
import com.webank.wedatasphere.qualitis.rule.builder.AddRuleRequestBuilder;
import com.webank.wedatasphere.qualitis.rule.dao.*;
import com.webank.wedatasphere.qualitis.rule.entity.BdpClientHistory;
import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.rule.request.*;
import com.webank.wedatasphere.qualitis.rule.request.multi.AddMultiSourceRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.ModifyMultiSourceRequest;
import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;
import com.webank.wedatasphere.qualitis.rule.service.*;
import com.webank.wedatasphere.qualitis.scheduled.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.service.*;
import com.webank.wedatasphere.qualitis.util.JexlUtil;
import com.webank.wedatasphere.qualitis.util.SpringContextHolder;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import com.webank.wedatasphere.qualitis.util.map.CustomObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author allenzhou@webank.com
 * @date 2021/9/8 10:42
 */
@Service
public class CreateAndExecutionServiceImpl implements CreateAndExecutionService {
    @Autowired
    private RuleService ruleService;

    @Autowired
    private FileRuleService fileRuleService;

    @Autowired
    private CustomRuleService customRuleService;

    @Autowired
    private MultiSourceRuleService multiSourceRuleService;

    @Autowired
    private OperateCiService operateCiService;

    @Autowired
    private RuleMetricService ruleMetricService;

    @Autowired
    private ClusterInfoService clusterInfoService;

    @Autowired
    private OuterExecutionService outerExecutionService;

    @Autowired
    private ExecutionParametersService executionParametersService;

    @Autowired
    private RuleMetricCommonService ruleMetricCommonService;

    @Autowired
    private SubDepartmentPermissionService subDepartmentPermissionService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private MetaDataClient metaDataClient;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RuleGroupDao ruleGroupDao;

    @Autowired
    private RuleTemplateDao ruleTemplateDao;

    @Autowired
    private SpecialProjectRuleConfig specialProjectRuleConfig;

    @Autowired
    private LinkisConfig linkisConfig;

    @Autowired
    private RuleDao ruleDao;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private RuleMetricDao ruleMetricDao;

    @Autowired
    private ExecutionParametersDao executionParametersDao;

    @Autowired
    private RuleMetricTypeConfigDao ruleMetricTypeConfigDao;

    @Autowired
    private BdpClientHistoryDao bdpClientHistoryDao;

    @Autowired
    private CreateAndExecutionService createAndExecutionService;

    @Autowired
    private TaskService taskService;

    @Value("${task.create_and_submit.limit_size:1000}")
    private Long thresholdValue;

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateAndExecutionServiceImpl.class);

    private static final Map<String, Integer> FREQUENCY_EN = new HashMap<>(6);

    private static final Map<Integer, String> FREQUENCY_CN = new HashMap<>(6);

    private static final Map<String, String> PARAMS_TYPE = new HashMap<>(4 * 4);

    private static final String JUST_SAVE = "save()";
    private static final String ASYNC = "async()";
    private static final String STRING_TYPE = "string";
    private static final String BOOLEAN_TYPE = "boolean";
    private static final String QUALITIS_CMD_PROPS = "qualitis.cmd.props.";
    private static final int PROJECT_NAME_LENGTH = 128;
    private static final int RULE_NAME_LENGTH = 128;
    private static final Pattern PROJECT_RULE_NAME_PATTERN = Pattern.compile("^\\w+$");

    @Autowired
    @Qualifier("ruleExecutionThreadPool")
    private ThreadPoolExecutor ruleExecutionThreadPool;

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

        PARAMS_TYPE.put("cluster", "string");
        PARAMS_TYPE.put("datasource", "string");
        PARAMS_TYPE.put("mapping", "string");
        PARAMS_TYPE.put("filter", "string");
        PARAMS_TYPE.put("regx", "string");
        PARAMS_TYPE.put("range", "string");
        PARAMS_TYPE.put("dateRegx", "string");
        PARAMS_TYPE.put("seriesOfValues", "string");
        PARAMS_TYPE.put("condition1", "string");
        PARAMS_TYPE.put("condition2", "string");
        PARAMS_TYPE.put("alertInfo", "string");
        PARAMS_TYPE.put("execParams", "string");
        PARAMS_TYPE.put("ruleMetricName", "string");
        PARAMS_TYPE.put("value", "string");
        PARAMS_TYPE.put("deleteFailCheckResult", "boolean");
        PARAMS_TYPE.put("uploadRuleMetricValue", "boolean");
        PARAMS_TYPE.put("uploadAbnormalValue", "boolean");
        PARAMS_TYPE.put("abortOnFailure", "boolean");
    }

    @Override
    public RuleResponse addOrModifyRule(AddDirector addDirector, String loginUser, AbstractCommonRequest abstrackAddRequest, String bashContent,
                                        String workFlowName, String workFlowVersion, String workFlowSpace, String nodeName, RuleGroup ruleGroupInDb, CreateAndSubmitResponse response)
            throws UnExpectedRequestException, IOException, PermissionDeniedRequestException {
        RuleResponse ruleResponse = null;
        String mapDataset = checkRuleMetricAndSave(abstrackAddRequest, loginUser, bashContent);
        Map<String, Object> maps = Maps.newLinkedHashMap();
        if (StringUtils.isNotBlank(mapDataset)) {
            maps = new ObjectMapper().readValue(mapDataset, Map.class);
        }
        checkExecutionParameterAndSave(abstrackAddRequest, loginUser);
        try {
            if (abstrackAddRequest instanceof AddRuleRequest) {
                updateBashContentAndWorkflowInfo(abstrackAddRequest, !maps.isEmpty() && maps.get("template_function") != null ? maps.get("template_function").toString() : bashContent, workFlowName, workFlowVersion, workFlowSpace, nodeName);
                if (ruleGroupInDb != null) {
                    ((AddRuleRequest) abstrackAddRequest).setRuleGroupId(ruleGroupInDb.getId());
                }
                if (addDirector.getRule() == null) {
                    ruleResponse = ruleService.addRuleForOuter(abstrackAddRequest, loginUser).getData();
                } else {
                    ModifyRuleRequest modifyRuleRequest = new ModifyRuleRequest();
                    BeanUtils.copyProperties(abstrackAddRequest, modifyRuleRequest);
                    modifyRuleRequest.setRuleId(addDirector.getRule().getId());
                    ruleResponse = ruleService.modifyRuleDetailForOuter(modifyRuleRequest, loginUser).getData();
                }
            } else if (abstrackAddRequest instanceof AddMultiSourceRuleRequest) {
                updateBashContentAndWorkflowInfo(abstrackAddRequest, !maps.isEmpty() && maps.get("template_function") != null ? maps.get("template_function").toString() : bashContent, workFlowName, workFlowVersion, workFlowSpace, nodeName);
                if (ruleGroupInDb != null) {
                    ((AddMultiSourceRuleRequest) abstrackAddRequest).setRuleGroupId(ruleGroupInDb.getId());
                }
                if (addDirector.getRule() == null) {
                    ruleResponse = multiSourceRuleService.addRuleForOuter(abstrackAddRequest, true).getData();
                } else {
                    ModifyMultiSourceRequest modifyRuleRequest = new ModifyMultiSourceRequest();
                    BeanUtils.copyProperties(abstrackAddRequest, modifyRuleRequest);
                    modifyRuleRequest.setRuleId(addDirector.getRule().getId());
                    ruleResponse = multiSourceRuleService.modifyRuleDetailForOuter(modifyRuleRequest, loginUser).getData();
                }
            } else if (abstrackAddRequest instanceof AddCustomRuleRequest) {
                updateBashContentAndWorkflowInfo(abstrackAddRequest, !maps.isEmpty() && maps.get("template_function") != null ? maps.get("template_function").toString() : bashContent, workFlowName, workFlowVersion, workFlowSpace, nodeName);
                if (ruleGroupInDb != null) {
                    ((AddCustomRuleRequest) abstrackAddRequest).setRuleGroupId(ruleGroupInDb.getId());
                }
                if (addDirector.getRule() == null) {
                    ruleResponse = customRuleService.addRuleForOuter(abstrackAddRequest, loginUser).getData();
                } else {
                    ModifyCustomRuleRequest modifyRuleRequest = new ModifyCustomRuleRequest();
                    BeanUtils.copyProperties(abstrackAddRequest, modifyRuleRequest);
                    modifyRuleRequest.setRuleId(addDirector.getRule().getId());
                    ruleResponse = customRuleService.modifyRuleDetailForOuter(modifyRuleRequest, loginUser).getData();
                }
            } else if (abstrackAddRequest instanceof AddFileRuleRequest) {
                updateBashContentAndWorkflowInfo(abstrackAddRequest, !maps.isEmpty() && maps.get("template_function") != null ? maps.get("template_function").toString() : bashContent, workFlowName, workFlowVersion, workFlowSpace, nodeName);
                if (ruleGroupInDb != null) {
                    ((AddFileRuleRequest) abstrackAddRequest).setRuleGroupId(ruleGroupInDb.getId());
                }
                if (addDirector.getRule() == null) {
                    ruleResponse = fileRuleService.addRuleForOuter(abstrackAddRequest, loginUser).getData();
                } else {
                    ModifyFileRuleRequest modifyRuleRequest = new ModifyFileRuleRequest();
                    BeanUtils.copyProperties(abstrackAddRequest, modifyRuleRequest);
                    modifyRuleRequest.setRuleId(addDirector.getRule().getId());
                    ruleResponse = fileRuleService.modifyRuleDetailForOuter(modifyRuleRequest, loginUser).getData();
                }
            }
            if (ruleResponse == null) {
                throw new UnExpectedRequestException("Failed to create rule.");
            }
            response.setRuleResponse(ruleResponse);
            LOGGER.info("One rule response:" + ruleResponse.toString());
            if (StringUtils.isNotEmpty(mapDataset)) {
                ruleResponse.setAddRuleMetricNames(maps.get("rule_metric_name").toString());
            }
            return ruleResponse;
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new UnExpectedRequestException("Failed to operation metric or rule with request made by JEXL.", 400);
        } finally {
            addDirector.resetRule();
        }
    }

    private void updateBashContentAndWorkflowInfo(AbstractCommonRequest abstrackAddRequest, String bashContent, String workFlowName, String workFlowVersion, String workFlowSpace, String nodeName) {
        if (abstrackAddRequest instanceof AddRuleRequest) {
            ((AddRuleRequest) abstrackAddRequest).setBashContent(bashContent);
            ((AddRuleRequest) abstrackAddRequest).setWorkFlowName(workFlowName);
            ((AddRuleRequest) abstrackAddRequest).setWorkFlowVersion(workFlowVersion);
            ((AddRuleRequest) abstrackAddRequest).setWorkFlowSpace(workFlowSpace);
            ((AddRuleRequest) abstrackAddRequest).setNodeName(nodeName);
        } else if (abstrackAddRequest instanceof AddMultiSourceRuleRequest) {
            ((AddMultiSourceRuleRequest) abstrackAddRequest).setBashContent(bashContent);
            ((AddMultiSourceRuleRequest) abstrackAddRequest).setWorkFlowName(workFlowName);
            ((AddMultiSourceRuleRequest) abstrackAddRequest).setWorkFlowVersion(workFlowVersion);
            ((AddMultiSourceRuleRequest) abstrackAddRequest).setWorkFlowSpace(workFlowSpace);
            ((AddMultiSourceRuleRequest) abstrackAddRequest).setNodeName(nodeName);
        } else if (abstrackAddRequest instanceof AddCustomRuleRequest) {
            ((AddCustomRuleRequest) abstrackAddRequest).setBashContent(bashContent);
            ((AddCustomRuleRequest) abstrackAddRequest).setWorkFlowName(workFlowName);
            ((AddCustomRuleRequest) abstrackAddRequest).setWorkFlowVersion(workFlowVersion);
            ((AddCustomRuleRequest) abstrackAddRequest).setWorkFlowSpace(workFlowSpace);
            ((AddCustomRuleRequest) abstrackAddRequest).setNodeName(nodeName);
        } else if (abstrackAddRequest instanceof AddFileRuleRequest) {
            ((AddFileRuleRequest) abstrackAddRequest).setBashContent(bashContent);
            ((AddFileRuleRequest) abstrackAddRequest).setWorkFlowName(workFlowName);
            ((AddFileRuleRequest) abstrackAddRequest).setWorkFlowVersion(workFlowVersion);
            ((AddFileRuleRequest) abstrackAddRequest).setWorkFlowSpace(workFlowSpace);
            ((AddFileRuleRequest) abstrackAddRequest).setNodeName(nodeName);
        }

    }

    @Override
    public String checkRuleMetricAndSave(AbstractCommonRequest abstrackAddRequest, String createUser, String templateFunction)
            throws UnExpectedRequestException, IOException, PermissionDeniedRequestException {
        if (abstrackAddRequest instanceof AddRuleRequest) {
            AddRuleRequest request = (AddRuleRequest) abstrackAddRequest;
            List<String> ruleMetricNames = request.getRuleMetricNamesForBdpClient();
            boolean multiEnv = CollectionUtils.isNotEmpty(request.getDatasource().iterator().next().getDataSourceEnvRequests());
            Map<String, Object> maps = solveAndConstruct(null, ruleMetricNames, createUser, multiEnv, templateFunction);
            return maps.isEmpty() ? "" : CustomObjectMapper.transObjectToJson(solveAndConstruct(null, ruleMetricNames, createUser, multiEnv, templateFunction));
        } else if (abstrackAddRequest instanceof AddMultiSourceRuleRequest) {
            AddMultiSourceRuleRequest request = (AddMultiSourceRuleRequest) abstrackAddRequest;
            List<String> ruleMetricNames = request.getRuleMetricNamesForBdpClient();
            Map<String, Object> maps = solveAndConstruct(null, ruleMetricNames, createUser, false, templateFunction);
            return maps.isEmpty() ? "" : CustomObjectMapper.transObjectToJson(solveAndConstruct(null, ruleMetricNames, createUser, false, templateFunction));
        } else if (abstrackAddRequest instanceof AddCustomRuleRequest) {
            AddCustomRuleRequest request = (AddCustomRuleRequest) abstrackAddRequest;
            List<String> ruleMetricNames = request.getRuleMetricNamesForBdpClient();
            boolean multiEnv = CollectionUtils.isNotEmpty(request.getDataSourceEnvRequests());
            Map<String, Object> maps = solveAndConstruct(request.getSqlCheckArea(), ruleMetricNames, createUser, multiEnv, templateFunction);
            return maps.isEmpty() ? "" : CustomObjectMapper.transObjectToJson(solveAndConstruct(request.getSqlCheckArea(), ruleMetricNames, createUser, multiEnv, templateFunction));
        } else if (abstrackAddRequest instanceof AddFileRuleRequest) {
            AddFileRuleRequest request = (AddFileRuleRequest) abstrackAddRequest;
            List<String> ruleMetricNames = request.getRuleMetricNamesForBdpClient();
            Map<String, Object> maps = solveAndConstruct(null, ruleMetricNames, createUser, false, templateFunction);
            return maps.isEmpty() ? "" : CustomObjectMapper.transObjectToJson(solveAndConstruct(null, ruleMetricNames, createUser, false, templateFunction));
        }
        return "";
    }

    private Map<String, Object> solveAndConstruct(String sqlCheckArea, List<String> ruleMetricNames, String createUser, boolean multiEnv, String templateFunction)
            throws UnExpectedRequestException, IOException, PermissionDeniedRequestException {
        if (CollectionUtils.isNotEmpty(ruleMetricNames)) {
            List<String> reFreshRuleMetricName = Lists.newArrayList();
            Map<String, Object> dealMaps = Maps.newLinkedHashMap();

            for (String ruleMetricName : ruleMetricNames) {
                Map<String, Object> maps = ruleMetricCommonService.checkRuleMetricNameAndAddOrModify(ruleMetricName, null, multiEnv, createUser);
                RuleMetric ruleMetricInDb = (RuleMetric) maps.get("rule_metric");
                AddRuleMetricRequest ruleMetricRequest = (AddRuleMetricRequest) maps.get("rule_metric_request");
                if (ruleMetricInDb == null) {
                    RuleMetricResponse addResponse = ruleMetricService.addRuleMetricForOuter(ruleMetricRequest, createUser).getData();
                    reFreshRuleMetricName.add(addResponse.getName());
                    if (templateFunction.contains(ruleMetricName)) {
                        templateFunction = templateFunction.replaceAll(ruleMetricName, addResponse.getName());
                    }
                    if (StringUtils.isNotBlank(sqlCheckArea)) {
                        sqlCheckArea = sqlCheckArea.replaceAll(ruleMetricName, addResponse.getName());
                    }
                } else {
                    ModifyRuleMetricRequest modifyRuleMetricRequest = new ModifyRuleMetricRequest();
                    modifyRuleMetricRequest.setId(ruleMetricInDb.getId());
                    BeanUtils.copyProperties(ruleMetricRequest, modifyRuleMetricRequest);
                    RuleMetricResponse modifyResponse = ruleMetricService.modifyRuleMetricForOuter(modifyRuleMetricRequest, createUser).getData();
                    reFreshRuleMetricName.add(modifyResponse.getName());
                    if (templateFunction.contains(ruleMetricName)) {
                        templateFunction = templateFunction.replaceAll(ruleMetricName, modifyResponse.getName());
                    }
                    if (StringUtils.isNotBlank(sqlCheckArea)) {
                        sqlCheckArea = sqlCheckArea.replaceAll(ruleMetricName, modifyResponse.getName());
                    }
                }
                LOGGER.info("Finish to create metric with name: {}", ruleMetricName);
            }
            dealMaps.put("rule_metric_name", StringUtils.join(reFreshRuleMetricName, ","));
            dealMaps.put("sql_check_area", sqlCheckArea);
            dealMaps.put("template_function", templateFunction);

            return dealMaps;
        }
        return Maps.newLinkedHashMap();
    }

    @Override
    public void checkExecutionParameterAndSave(AbstractCommonRequest abstrackAddRequest, String createUser) throws UnExpectedRequestException, PermissionDeniedRequestException {
        LOGGER.info("Abstract Common Request parameter.", abstrackAddRequest != null ? abstrackAddRequest.toString() : null);
        String executionParameterName = abstrackAddRequest.getExecutionParametersName();
        if (StringUtils.isNotEmpty(executionParameterName)) {
            ExecutionParameters executionParametersInDb = executionParametersDao.findByNameAndProjectId(executionParameterName, abstrackAddRequest.getProjectId());
            if (executionParametersInDb == null) {
                AddExecutionParametersRequest addExecutionParametersRequest = new AddExecutionParametersRequest(abstrackAddRequest);
                addExecutionParametersRequest.setName(executionParameterName);
                executionParametersService.addExecutionParametersForOuter(addExecutionParametersRequest, createUser);
            } else {
                ModifyExecutionParametersRequest modifyExecutionParametersRequest = new ModifyExecutionParametersRequest();
                modifyExecutionParametersRequest.setExecutionParametersId(executionParametersInDb.getId());
                AddExecutionParametersRequest addExecutionParametersRequest = new AddExecutionParametersRequest(abstrackAddRequest);
                BeanUtils.copyProperties(addExecutionParametersRequest, modifyExecutionParametersRequest);
                modifyExecutionParametersRequest.setName(executionParameterName);
               executionParametersService.modifyExecutionParametersForOuter(modifyExecutionParametersRequest, createUser);
            }
        }

    }

    @Override
    public GeneralResponse createOrModifyAndSubmitRule(CreateAndSubmitRequest request) {
        LOGGER.info("bdp client request parameter.", request != null ? request.toString() : null);
        Boolean exceedTaskSize = taskService.getExecutingTaskNumber(-24) >= thresholdValue;
        if (exceedTaskSize) {
            return new GeneralResponse<>("5001", "Number of task exceeded limit", null);
        }

        RuleContext ruleContext = null;
        boolean isAsyncRequest = Objects.nonNull(request.getAsync()) && Boolean.TRUE.equals(request.getAsync());
        try {
            LOGGER.info("Begin to create new rule and execute in first time.");
            AddDirector addDirector = SpringContextHolder.getBean(AddDirector.class);
            addDirector.setUserName(request.getCreateUser());
            Map<String, Object> map = Maps.newHashMapWithExpectedSize(1);
            map.put("addDirector", addDirector);

            boolean discard = false;
            if (specialProjectRuleConfig.getProjectNames().contains(request.getProjectName()) && specialProjectRuleConfig.getRuleNames().contains(request.getRuleName())) {
                request.setRuleName(UuidGenerator.generate());
                discard = true;
            }

            String templateFunction = judgeTemplateFunction(request);
            CreateAndSubmitResponse response = new CreateAndSubmitResponse();
            RuleResponse ruleResponse = setProjectAndRuleInfo(addDirector, request, templateFunction);
            try {
                if (null == ruleResponse) {
                    AbstractCommonRequest abstractAddRequest = (AbstractCommonRequest) JexlUtil.executeExpression("addDirector." + templateFunction + ".returnRequest()", map);
                    LOGGER.info("Auto create rule or metric for bdp-client request: " + abstractAddRequest.toString());
                    ruleResponse = addOrModifyRule(abstractAddRequest, request, templateFunction, addDirector);
                    response.setRuleResponse(ruleResponse);

//                    如果是修改规则流程
                    if (addDirector.getRule() != null) {
                        String ruleContextId = generateRuleContextId(ruleResponse.getRuleId());
                        ruleContext = new RuleContext(ruleResponse.getRuleId(), ruleContextId);
                        RuleContextManager.add(ruleContext);
                    }
                }
//                Trigger to query a list of RuleVariable from DataBase
                ruleResponse.getRule().getRuleVariables();
            } catch (Exception e) {
                LOGGER.error("Failed to execute template function.");
                LOGGER.error(e.getMessage(), e);

                StringBuilder errorMsg = new StringBuilder(e.toString());
                if (e.getCause() != null) {
                    errorMsg.append(e.getCause().getMessage()).append(".\n");
                }
                if (StringUtils.isNotEmpty(e.getMessage())) {
                    errorMsg.append(e.getMessage()).append(".\n");
                }
                throw new UnExpectedRequestException(String.format("There is a problem with the input format. Please check template function and parameters name. Templatefunction: %s, exception: %s", templateFunction, errorMsg.toString()));
            }

            if (templateFunction.endsWith(JUST_SAVE)) {
                return new GeneralResponse<>("200", "Success to create rule", response);
            }

            if (templateFunction.endsWith(ASYNC)) {
                isAsyncRequest = Boolean.TRUE;
            }

            ApplicationProjectResponse applicationProjectResponse = ruleListExecution(request, ruleResponse, ruleContext, isAsyncRequest);
            response.setApplicationProjectResponse(applicationProjectResponse);

            if (discard) {
                ruleDao.deleteById(ruleResponse.getRuleId());
            }
            return new GeneralResponse<>("200", "Success to create and submit", response);
        } catch (UnExpectedRequestException e) {
            LOGGER.error("Failed to create and submit, caused by: \n");
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("400", "Failed to create and submit, caused by: " + e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error("Failed to create and submit, caused by: \n");
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", "Failed to create and submit, caused by: " + e.getMessage(), null);
        }
    }

    private ApplicationProjectResponse ruleListExecution(CreateAndSubmitRequest request, RuleResponse ruleResponse, RuleContext ruleContext, boolean async) throws UnExpectedRequestException, PermissionDeniedRequestException {
        LOGGER.info("Ready to submit rule, ruleId: {}", ruleResponse.getRuleId());
        // Submit rule.
        RuleListExecutionRequest ruleListExecutionRequest = new RuleListExecutionRequest(ruleResponse, request.getCreateUser(), request.getExecutionUser());
        ruleListExecutionRequest.setJobId(request.getJobId());
        ruleListExecutionRequest.setSetFlag(request.getSetFlag());
        ruleListExecutionRequest.setExecutionParam(request.getExecutionParam());
        ruleListExecutionRequest.setDyNamicPartition(request.getDynamicPartition());
        ruleListExecutionRequest.setDyNamicPartitionPrefix(request.getDynamicPartitionPrefix());
        ruleListExecutionRequest.setRuleContext(ruleContext);
        if (async) {
            ruleExecutionThreadPool.submit(() -> {
                try {
                    outerExecutionService.ruleListExecution(ruleListExecutionRequest, InvokeTypeEnum.BDP_CLIENT_API_INVOKE.getCode(), null).getData();
                } catch (UnExpectedRequestException e) {
                    LOGGER.error("Failed to create and submit, caused by: \n");
                    LOGGER.error(e.getMessage(), e);
                } catch (PermissionDeniedRequestException e) {
                    LOGGER.error("Failed to create and submit, caused by: \n");
                    LOGGER.error(e.getMessage(), e);
                } finally {
                    if (Objects.nonNull(ruleContext)) {
                        LOGGER.info("Clear expired data, ruleContext: {}", ruleContext);
                        RuleContextManager.remove(ruleContext);
                    }
                }
            });
        } else {
            try {
                return (ApplicationProjectResponse) outerExecutionService.ruleListExecution(ruleListExecutionRequest,
                        InvokeTypeEnum.BDP_CLIENT_API_INVOKE.getCode(), null).getData();
            } finally {
                if (Objects.nonNull(ruleContext)) {
                    LOGGER.info("Clear expired data, ruleContext: {}", ruleContext);
                    RuleContextManager.remove(ruleContext);
                }
            }
        }
        return null;
    }

    private String generateRuleContextId(Long ruleId) {
        return "" + ruleId + Thread.currentThread().getId() + (System.currentTimeMillis() / 1000) + UuidGenerator.generateRandom(6);
    }

    private RuleResponse addOrModifyRule(AbstractCommonRequest abstractCommonRequest, CreateAndSubmitRequest request,
                                         String templateFunction, AddDirector addDirector)
            throws UnExpectedRequestException, IOException, PermissionDeniedRequestException {
        String mapDataset = createAndExecutionService.checkRuleMetricAndSave(abstractCommonRequest, request.getCreateUser(), templateFunction);
        Map<String, Object> maps = Maps.newLinkedHashMap();
        if (StringUtils.isNotBlank(mapDataset)) {
            maps = new ObjectMapper().readValue(mapDataset, Map.class);
        }
        createAndExecutionService.checkExecutionParameterAndSave(abstractCommonRequest, request.getCreateUser());
        try {
            RuleResponse ruleResponse;
            if (addDirector.getRule() == null) {
                ruleResponse = addRuleAndGetRuleResponse(abstractCommonRequest, templateFunction, request, addDirector, maps);
            } else {
                ruleResponse = modifyRuleAndGetRuleResponse(abstractCommonRequest, templateFunction, request, addDirector, maps);
            }

            if (ruleResponse == null) {
                throw new UnExpectedRequestException("Failed to create rule for bdp-client.");
            }
            LOGGER.info("One rule response for bdp-client:" + ruleResponse.toString());
            if (StringUtils.isNotEmpty(mapDataset)) {
                ruleResponse.setAddRuleMetricNames(maps.get("rule_metric_name").toString());
            }
            return ruleResponse;
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new UnExpectedRequestException("Failed to operation metric or rule with request made by JEXL.", 400);
        } finally {
            addDirector.reset();
        }
    }

    private RuleResponse addRuleAndGetRuleResponse(AbstractCommonRequest abstractCommonRequest
            , String templateFunction, CreateAndSubmitRequest request, AddDirector addDirector, Map<String, Object> maps) throws UnExpectedRequestException, PermissionDeniedRequestException, IOException {
        RuleTypeEnum ruleTypeEnum = getRuleType(abstractCommonRequest);
        abstractCommonRequest.setBashContent(!maps.isEmpty() && maps.get("template_function") != null ? maps.get("template_function").toString() : templateFunction);
        RuleResponse ruleResponse = null;
        BdpClientHistory bdpClientHistory = null;
        switch (ruleTypeEnum) {
            case SINGLE_TEMPLATE_RULE:
                ruleResponse = ruleService.addRuleForOuter(abstractCommonRequest, request.getCreateUser()).getData();
                bdpClientHistory = addDirector.getBdpClientHistory();
                if (bdpClientHistory != null) {
                    bdpClientHistory.setRuleId(ruleResponse.getRuleId());
                    bdpClientHistoryDao.save(bdpClientHistory);
                }
                break;
            case MULTI_TEMPLATE_RULE:
                ruleResponse = multiSourceRuleService.addRuleForOuter(abstractCommonRequest, true).getData();
                bdpClientHistory = addDirector.getBdpClientHistory();
                if (bdpClientHistory != null) {
                    bdpClientHistory.setRuleId(ruleResponse.getRuleId());
                    bdpClientHistoryDao.save(bdpClientHistory);
                }
                break;
            case CUSTOM_RULE:
                String sqlCheckArea = !maps.isEmpty() && maps.get("sql_check_area") != null ? maps.get("sql_check_area").toString() : "";
                if (StringUtils.isNotBlank(sqlCheckArea)) {
                    AddCustomRuleRequest addCustomRuleRequest = (AddCustomRuleRequest) abstractCommonRequest;
                    addCustomRuleRequest.setSqlCheckArea(sqlCheckArea);
                    ruleResponse = customRuleService.addRuleForOuter((AbstractCommonRequest) addCustomRuleRequest, request.getCreateUser()).getData();
                } else {
                    ruleResponse = customRuleService.addRuleForOuter(abstractCommonRequest, request.getCreateUser()).getData();
                }
                bdpClientHistory = addDirector.getBdpClientHistory();
                if (bdpClientHistory != null) {
                    bdpClientHistory.setRuleId(ruleResponse.getRuleId());
                    bdpClientHistoryDao.save(bdpClientHistory);
                }
                break;
            case FILE_TEMPLATE_RULE:
                ruleResponse = fileRuleService.addRuleForOuter(abstractCommonRequest, request.getCreateUser()).getData();
                bdpClientHistory = addDirector.getBdpClientHistory();
                if (bdpClientHistory != null) {
                    bdpClientHistory.setRuleId(ruleResponse.getRuleId());
                    bdpClientHistoryDao.save(bdpClientHistory);
                }
                break;
            default:
                break;
        }
        return ruleResponse;
    }

    private RuleResponse modifyRuleAndGetRuleResponse(AbstractCommonRequest abstractCommonRequest
            , String templateFunction, CreateAndSubmitRequest request, AddDirector addDirector, Map<String, Object> maps) throws UnExpectedRequestException, PermissionDeniedRequestException, IOException {
        RuleTypeEnum ruleTypeEnum = getRuleType(abstractCommonRequest);
        abstractCommonRequest.setBashContent(!maps.isEmpty() && maps.get("template_function") != null ? maps.get("template_function").toString() : templateFunction);
        RuleResponse ruleResponse = null;
        switch (ruleTypeEnum) {
            case SINGLE_TEMPLATE_RULE:
                ModifyRuleRequest modifyRuleRequest = new ModifyRuleRequest();
                BeanUtils.copyProperties(abstractCommonRequest, modifyRuleRequest);
                modifyRuleRequest.setRuleId(addDirector.getRule().getId());
                ruleResponse = ruleService.modifyRuleDetailForOuter(modifyRuleRequest, request.getCreateUser()).getData();
                break;
            case MULTI_TEMPLATE_RULE:
                ModifyMultiSourceRequest modifyMultiSourceRequest = new ModifyMultiSourceRequest();
                BeanUtils.copyProperties(abstractCommonRequest, modifyMultiSourceRequest);
                modifyMultiSourceRequest.setRuleId(addDirector.getRule().getId());
                ruleResponse = multiSourceRuleService.modifyRuleDetailForOuter(modifyMultiSourceRequest, request.getCreateUser()).getData();
                break;
            case CUSTOM_RULE:
                ModifyCustomRuleRequest customRuleRequest = new ModifyCustomRuleRequest();
                String sqlCheckArea = !maps.isEmpty() && maps.get("sql_check_area") != null ? maps.get("sql_check_area").toString() : "";
                BeanUtils.copyProperties(abstractCommonRequest, customRuleRequest);
                if (StringUtils.isNotBlank(sqlCheckArea)) {
                    customRuleRequest.setSqlCheckArea(sqlCheckArea);
                }
                customRuleRequest.setRuleId(addDirector.getRule().getId());
                customRuleRequest.setFromOuter(true);
                ruleResponse = customRuleService.modifyRuleDetailForOuter(customRuleRequest, request.getCreateUser()).getData();
                break;
            case FILE_TEMPLATE_RULE:
                ModifyFileRuleRequest fileRuleRequest = new ModifyFileRuleRequest();
                BeanUtils.copyProperties(abstractCommonRequest, fileRuleRequest);
                fileRuleRequest.setRuleId(addDirector.getRule().getId());
                ruleResponse = fileRuleService.modifyRuleDetailForOuter(fileRuleRequest, request.getCreateUser()).getData();
                break;
            default:
                break;
        }
        return ruleResponse;
    }

    public RuleTypeEnum getRuleType(AbstractCommonRequest abstractCommonRequest) {
        if (abstractCommonRequest instanceof AddRuleRequest) {
            return RuleTypeEnum.SINGLE_TEMPLATE_RULE;
        } else if (abstractCommonRequest instanceof AddMultiSourceRuleRequest) {
            return RuleTypeEnum.MULTI_TEMPLATE_RULE;
        } else if (abstractCommonRequest instanceof AddCustomRuleRequest) {
            return RuleTypeEnum.CUSTOM_RULE;
        } else if (abstractCommonRequest instanceof AddFileRuleRequest) {
            return RuleTypeEnum.FILE_TEMPLATE_RULE;
        }
        return null;
    }

    private RuleResponse getRuleResponse(AbstractCommonRequest abstrackAddRequest, CreateAndSubmitRequest request, String templateFunction,
                                         RuleResponse ruleResponse, AddDirector addDirector) throws UnExpectedRequestException, PermissionDeniedRequestException, IOException {
        if (abstrackAddRequest instanceof AddRuleRequest) {
            abstrackAddRequest.setBashContent(templateFunction);
            if (addDirector.getRule() == null) {
                ruleResponse = ruleService.addRuleForOuter(abstrackAddRequest, request.getCreateUser()).getData();
                BdpClientHistory bdpClientHistory = addDirector.getBdpClientHistory();
                if (bdpClientHistory != null) {
                    bdpClientHistory.setRuleId(ruleResponse.getRuleId());
                    bdpClientHistoryDao.save(bdpClientHistory);
                }
            } else {
                ModifyRuleRequest modifyRuleRequest = new ModifyRuleRequest();
                BeanUtils.copyProperties(abstrackAddRequest, modifyRuleRequest);
                modifyRuleRequest.setRuleId(addDirector.getRule().getId());
                ruleResponse = ruleService.modifyRuleDetailForOuter(modifyRuleRequest, request.getCreateUser()).getData();
            }
        } else if (abstrackAddRequest instanceof AddMultiSourceRuleRequest) {
            ((AddMultiSourceRuleRequest) abstrackAddRequest).setBashContent(templateFunction);
            if (addDirector.getRule() == null) {
                ruleResponse = multiSourceRuleService.addRuleForOuter(abstrackAddRequest, true).getData();
                BdpClientHistory bdpClientHistory = addDirector.getBdpClientHistory();
                if (bdpClientHistory != null) {
                    bdpClientHistory.setRuleId(ruleResponse.getRuleId());
                    bdpClientHistoryDao.save(bdpClientHistory);
                }
            } else {
                ModifyMultiSourceRequest modifyRuleRequest = new ModifyMultiSourceRequest();
                BeanUtils.copyProperties(abstrackAddRequest, modifyRuleRequest);
                modifyRuleRequest.setRuleId(addDirector.getRule().getId());
                ruleResponse = multiSourceRuleService.modifyRuleDetailForOuter(modifyRuleRequest, request.getCreateUser()).getData();
            }
        } else if (abstrackAddRequest instanceof AddCustomRuleRequest) {
            ((AddCustomRuleRequest) abstrackAddRequest).setBashContent(templateFunction);
            if (addDirector.getRule() == null) {
                ruleResponse = customRuleService.addRuleForOuter(abstrackAddRequest, request.getCreateUser()).getData();
                BdpClientHistory bdpClientHistory = addDirector.getBdpClientHistory();
                if (bdpClientHistory != null) {
                    bdpClientHistory.setRuleId(ruleResponse.getRuleId());
                    bdpClientHistoryDao.save(bdpClientHistory);
                }
            } else {
                ModifyCustomRuleRequest modifyRuleRequest = new ModifyCustomRuleRequest();
                BeanUtils.copyProperties(abstrackAddRequest, modifyRuleRequest);
                modifyRuleRequest.setRuleId(addDirector.getRule().getId());
                modifyRuleRequest.setFromOuter(true);
                ruleResponse = customRuleService.modifyRuleDetailForOuter(modifyRuleRequest, request.getCreateUser()).getData();
            }
        } else if (abstrackAddRequest instanceof AddFileRuleRequest) {
            ((AddFileRuleRequest) abstrackAddRequest).setBashContent(templateFunction);
            if (addDirector.getRule() == null) {
                ruleResponse = fileRuleService.addRuleForOuter(abstrackAddRequest, request.getCreateUser()).getData();
                BdpClientHistory bdpClientHistory = addDirector.getBdpClientHistory();
                if (bdpClientHistory != null) {
                    bdpClientHistory.setRuleId(ruleResponse.getRuleId());
                    bdpClientHistoryDao.save(bdpClientHistory);
                }
            } else {
                ModifyFileRuleRequest modifyRuleRequest = new ModifyFileRuleRequest();
                BeanUtils.copyProperties(abstrackAddRequest, modifyRuleRequest);
                modifyRuleRequest.setRuleId(addDirector.getRule().getId());
                ruleResponse = fileRuleService.modifyRuleDetailForOuter(modifyRuleRequest, request.getCreateUser()).getData();
            }
        }
        if (ruleResponse == null) {
            throw new UnExpectedRequestException("Failed to create rule for bdp-client.");
        }
        return ruleResponse;
    }

    private RuleResponse setProjectAndRuleInfo(AddDirector addDirector, CreateAndSubmitRequest request, String templateFunction) {
        String projectName = request.getProjectName();
        String ruleName = request.getRuleName();

        addDirector.setRuleDetail(request.getRuleDetail());
        addDirector.setRuleCnName(request.getRuleCnName());

        if (StringUtils.isNotBlank(projectName) && StringUtils.isNotBlank(ruleName)) {
            addDirector.setProjectName(projectName);
            addDirector.setRuleName(ruleName);
            Project project = projectDao.findByNameAndCreateUser(projectName, request.getCreateUser());
            if (project != null) {
                addDirector.setProject(project);
                Rule rule = ruleDao.findByProjectAndRuleName(project, ruleName);
                if (rule != null) {
                    addDirector.setRule(rule);
                    if (StringUtils.isNotEmpty(rule.getBashContent()) && rule.getBashContent().equals(templateFunction) && Boolean.TRUE.equals(rule.getEnable())) {
                        return new RuleResponse(rule);
                    }
                }
            }
        } else if (StringUtils.isNotBlank(projectName)) {
            LOGGER.info("Only pick up project name: " + projectName);
            addDirector.setProjectName(projectName);
            Project project = projectDao.findByNameAndCreateUser(projectName, request.getCreateUser());
            if (project != null) {
                addDirector.setProject(project);
            }
            addDirector.setBdpClientHistory(new BdpClientHistory());
        } else if (StringUtils.isNotBlank(ruleName)) {
            LOGGER.info("Only pick up rule name: " + ruleName);
            addDirector.setRuleName(ruleName);
            addDirector.setBdpClientHistory(new BdpClientHistory());
        } else {
            LOGGER.info("No project name, no rule name.");
            addDirector.setBdpClientHistory(new BdpClientHistory());
        }

        return null;
    }

    private String judgeTemplateFunction(CreateAndSubmitRequest request) throws UnExpectedRequestException {
        CreateAndSubmitRequest.checkRequest(request);
        // Check char, number, underscore
        Matcher matcher = PROJECT_RULE_NAME_PATTERN.matcher(request.getProjectName());
        boolean find = matcher.find();
        if (!find || request.getProjectName().length() > PROJECT_NAME_LENGTH) {
            throw new UnExpectedRequestException("Project name is illegal");
        } else {
            matcher = PROJECT_RULE_NAME_PATTERN.matcher(request.getRuleName());
            find = matcher.find();
            if (!find || request.getRuleName().length() > RULE_NAME_LENGTH) {
                throw new UnExpectedRequestException("Rule name is illegal.");
            }
        }
        // Check same rule in history.
        String templateFunction = request.getTemplateFunction();
        boolean emptyParams = templateFunction.contains("expectColumnNotNull()")
                || templateFunction.contains("expectColumnsPrimaryNotRepeat()")
                || templateFunction.contains("expectTableRows()")
                || templateFunction.contains("expectColumnAvg()")
                || templateFunction.contains("expectColumnSum()")
                || templateFunction.contains("expectColumnMax()")
                || templateFunction.contains("expectColumnMin()")
                || templateFunction.contains("expectColumnMatchRegx()")
                || templateFunction.contains("expectColumnMatchDate()")
                || templateFunction.contains("expectColumnMatchNum()")
                || templateFunction.contains("expectColumnInList()")
                || templateFunction.contains("expectColumnInListNewValueCheck()")
                || templateFunction.contains("expectColumnInStandardValueListNewValueCheck()")
                || templateFunction.contains("expectColumnInRange()")
                || templateFunction.contains("expectColumnInRangeNewValueCheck()")
                || templateFunction.contains("expectColumnMatchIDCard()")
                || templateFunction.contains("expectColumnLogicCheck()")
                || templateFunction.contains("expectColumnNotEmpty()")
                || templateFunction.contains("expectColumnNotNullNotEmpty()")
                || templateFunction.contains("expectTableConsistent()")
                || templateFunction.contains("expectSpecifiedColumnConsistent()")
                || templateFunction.contains("expectJoinTableSqlPass()")
                || templateFunction.contains("expectFileAmountCount()")
                || templateFunction.contains("expectFileSizePass()")
                || templateFunction.contains("expectSqlPass()")
                || templateFunction.contains("expectLinesNotRepeat()")
                || templateFunction.contains("addRuleMetric()")
                || templateFunction.contains("addRuleMetricWithCheck()");

        if (emptyParams) {
            List<PropsRequest> propsRequestList = request.getPropsRequests();
            templateFunction = fillWithExistsProps(templateFunction, propsRequestList);
        }
        return templateFunction;
    }

    private String fillWithExistsProps(String templateFunction, List<PropsRequest> propsRequestList) throws UnExpectedRequestException {
        if (CollectionUtils.isEmpty(propsRequestList)) {
            throw new UnExpectedRequestException("Props of parameters can not be empty when template function found no parameters");
        }
        LOGGER.info("Start to fill template function with exists props.");
        Map<String, String> propsMap = new HashMap<>(propsRequestList.size());
        for (PropsRequest propsRequest : propsRequestList) {
            propsMap.put(propsRequest.getKey(), propsRequest.getValue());
        }

        Method[] declaredMethods = AddDirector.class.getDeclaredMethods();
        Method[] builderDeclaredMethods = AddRuleRequestBuilder.class.getDeclaredMethods();
        List<Method> declaredMethodsList = Arrays.asList(declaredMethods);
        List<Method> builderDeclaredMethodsList = Arrays.asList(builderDeclaredMethods);

        ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

        for (Method declaredMethod : declaredMethodsList) {
            String replaceFunc = declaredMethod.getName() + "()";
            if (templateFunction.contains(replaceFunc)) {
                String[] paramNames = parameterNameDiscoverer.getParameterNames(declaredMethod);
                List<String> realValues = new ArrayList<>(paramNames.length);
                for (String paramName : paramNames) {
                    if (PARAMS_TYPE.get(paramName) == null) {
                        throw new UnExpectedRequestException("Not support param type. Param name: " + paramName);
                    } else if (PARAMS_TYPE.get(paramName).equals(BOOLEAN_TYPE)) {
                        String realValue = propsMap.get(QUALITIS_CMD_PROPS + paramName);
                        if (realValue != null) {
                            realValues.add(realValue);
                        } else {
                            realValues.add("false");
                        }
                    } else if (PARAMS_TYPE.get(paramName).equals(STRING_TYPE)) {
                        String realValue = propsMap.get(QUALITIS_CMD_PROPS + paramName);
                        if (realValue != null) {
                            realValues.add("\"" + realValue + "\"");
                        } else {
                            realValues.add("null");
                        }
                    }
                }
                templateFunction = templateFunction.replace(replaceFunc, declaredMethod.getName() + "(" + StringUtils.join(realValues, ", ") + ")");
                break;
            }
        }

        for (Method declaredMethod : builderDeclaredMethodsList) {
            String replaceFunc = declaredMethod.getName() + "()";
            if (templateFunction.contains(replaceFunc)) {
                String[] paramNames = parameterNameDiscoverer.getParameterNames(declaredMethod);
                if (paramNames == null) {
                    break;
                }
                List<String> realValues = new ArrayList<>(paramNames.length);
                for (String paramName : paramNames) {
                    if (PARAMS_TYPE.get(paramName) == null) {
                        throw new UnExpectedRequestException("Not support param type. Param name: " + paramName);
                    } else if (PARAMS_TYPE.get(paramName).equals(BOOLEAN_TYPE)) {
                        String realValue = propsMap.get(QUALITIS_CMD_PROPS + paramName);
                        if (realValue != null) {
                            realValues.add(realValue);
                        } else {
                            realValues.add("false");
                        }
                    } else if (PARAMS_TYPE.get(paramName).equals(STRING_TYPE)) {
                        String realValue = propsMap.get(QUALITIS_CMD_PROPS + paramName);
                        if (realValue != null) {
                            realValues.add("\"" + realValue + "\"");
                        } else {
                            realValues.add("null");
                        }
                    }
                }
                templateFunction = templateFunction.replace(replaceFunc, declaredMethod.getName() + "(" + StringUtils.join(realValues, ", ") + ")");
            }
        }
        return templateFunction;
    }

}
