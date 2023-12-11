package com.webank.wedatasphere.qualitis.rule.service.impl;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.constant.ProjectUserPermissionEnum;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.query.request.ExecutionParametersRequest;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.rule.constant.AlarmEventEnum;
import com.webank.wedatasphere.qualitis.rule.constant.BuiltInNameEnum;
import com.webank.wedatasphere.qualitis.rule.constant.DataSelectMethodEnum;
import com.webank.wedatasphere.qualitis.rule.constant.DynamicEngineEnum;
import com.webank.wedatasphere.qualitis.rule.constant.FlinkParameterEnum;
import com.webank.wedatasphere.qualitis.rule.constant.NoiseStrategyEnum;
import com.webank.wedatasphere.qualitis.rule.constant.SparkParameterEnum;
import com.webank.wedatasphere.qualitis.rule.constant.StaticArgumentsTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.YarnParameterEnum;
import com.webank.wedatasphere.qualitis.rule.dao.AlarmArgumentsExecutionParametersDao;
import com.webank.wedatasphere.qualitis.rule.dao.ExecutionParametersDao;
import com.webank.wedatasphere.qualitis.rule.dao.ExecutionVariableDao;
import com.webank.wedatasphere.qualitis.rule.dao.NoiseEliminationManagementDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.dao.StaticExecutionParametersDao;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmArgumentsExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.entity.ExecutionVariable;
import com.webank.wedatasphere.qualitis.rule.entity.NoiseEliminationManagement;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.StaticExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.request.AddExecutionParametersRequest;
import com.webank.wedatasphere.qualitis.rule.request.AlarmArgumentsExecutionParametersRequest;
import com.webank.wedatasphere.qualitis.rule.request.DeleteExecutionParametersRequest;
import com.webank.wedatasphere.qualitis.rule.request.ExecutionExecutionParametersRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyExecutionParametersRequest;
import com.webank.wedatasphere.qualitis.rule.request.NoiseEliminationManagementRequest;
import com.webank.wedatasphere.qualitis.rule.request.StaticExecutionParametersRequest;
import com.webank.wedatasphere.qualitis.rule.response.ExecutionParametersResponse;
import com.webank.wedatasphere.qualitis.rule.service.ExecutionParametersService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author v_gaojiedeng
 */
@Service
public class ExecutionParametersServiceImpl implements ExecutionParametersService {

    @Autowired
    private ExecutionParametersDao executionParametersDao;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private RuleDao ruleDao;
    @Autowired
    private StaticExecutionParametersDao staticExecutionParametersDao;
    @Autowired
    private AlarmArgumentsExecutionParametersDao alarmArgumentsExecutionParametersDao;
    @Autowired
    private NoiseEliminationManagementDao noiseEliminationManagementDao;
    @Autowired
    private ExecutionVariableDao executionVariableDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionParametersServiceImpl.class);

    private HttpServletRequest httpServletRequest;

    public ExecutionParametersServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<ExecutionParametersResponse> addExecutionParametersForOuter(AddExecutionParametersRequest request, String createUser) throws UnExpectedRequestException, PermissionDeniedRequestException {
        return addExecutionParametersReal(request, createUser);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<ExecutionParametersResponse> addExecutionParameters(AddExecutionParametersRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }

        return addExecutionParametersReal(request, loginUser);
    }

    private GeneralResponse<ExecutionParametersResponse> addExecutionParametersReal(AddExecutionParametersRequest request, String loginUser)
            throws UnExpectedRequestException, PermissionDeniedRequestException {
        AddExecutionParametersRequest.checkRequest(request, false);

        if (request.getUnionAll() == null) {
            request.setUnionAll(false);
        }
        // Check existence of project
        Project projectInDb = projectService.checkProjectExistence(request.getProjectId(), loginUser);
        // Check permissions of project
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
        projectService.checkProjectPermission(projectInDb, loginUser, permissions);
        checkExecutionParametersName(request.getName(), request.getProjectId());
        ExecutionParameters executionParameters = new ExecutionParameters();
        setBasicInfo(executionParameters, loginUser, request);

        ExecutionParameters savedExecutionParameters = executionParametersDao.saveExecutionParameters(executionParameters);
        batchHandleStaticAndAlarmArguments(savedExecutionParameters, request.getStaticExecutionParametersRequests(), request.getAlarmArgumentsExecutionParametersRequests(), request.getNoiseEliminationManagementRequests(), request.getExecutionManagementRequests());
        LOGGER.info("Succeed to save ExecutionParameters, execution_parameters_id: {}", savedExecutionParameters.getId());
        savedExecutionParameters = executionParametersDao.saveExecutionParameters(savedExecutionParameters);

        ExecutionParametersResponse response = new ExecutionParametersResponse(savedExecutionParameters);
        LOGGER.info("Succeed to add ExecutionParameters, execution_parameters_id: {}", response.getExecutionParametersId());
        return new GeneralResponse<>("200", "{&ADD_EXECUTIONPARAMETERS_SUCCESSFULLY}", response);
    }

    private void setBasicInfo(ExecutionParameters executionParameters, String loginUser, AddExecutionParametersRequest request) {
        String nowDate = QualitisConstants.PRINT_TIME_FORMAT.format(new Date());
        executionParameters.setName(request.getName());
        executionParameters.setAlert(request.getAlert());
        executionParameters.setAlertLevel(request.getAlertLevel());
        executionParameters.setAlertReceiver(request.getAlertReceiver());
        executionParameters.setAbortOnFailure(request.getAbortOnFailure());
        executionParameters.setSpecifyStaticStartupParam(request.getSpecifyStaticStartupParam());

        executionParameters.setProjectId(request.getProjectId());
        executionParameters.setCreateUser(loginUser);
        executionParameters.setCreateTime(nowDate);
        executionParameters.setCluster(request.getCluster());
        executionParameters.setAbnormalDatabase(request.getAbnormalDatabase());
        executionParameters.setAbnormalProxyUser(request.getAbnormalProxyUser());
        executionParameters.setAbnormalDataStorage(request.getAbnormalDataStorage());
        executionParameters.setUnionAll(request.getUnionAll());

        executionParameters.setFilter(request.getFilter());
        executionParameters.setSpecifyFilter(request.getSpecifyFilter());
        executionParameters.setSourceTableFilter(request.getSourceTableFilter());
        executionParameters.setTargetTableFilter(request.getTargetTableFilter());
        executionParameters.setDeleteFailCheckResult(request.getDeleteFailCheckResult());
        executionParameters.setUploadRuleMetricValue(request.getUploadRuleMetricValue());
        executionParameters.setUploadAbnormalValue(request.getUploadAbnormalValue());
        executionParameters.setWhetherNoise(request.getWhetherNoise());

        executionParameters.setExecutionVariable(request.getExecutionVariable());
        executionParameters.setAdvancedExecution(request.getAdvancedExecution());
        executionParameters.setEngineReuse(request.getEngineReuse());
        executionParameters.setConcurrencyGranularity(request.getConcurrencyGranularity());
        executionParameters.setDynamicPartitioning(request.getDynamicPartitioning());
        executionParameters.setTopPartition(request.getTopPartition());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse<ExecutionParametersResponse> modifyExecutionParametersForOuter(ModifyExecutionParametersRequest modifyExecutionParametersRequest, String createUser)
            throws UnExpectedRequestException, PermissionDeniedRequestException {
        return modifyExecutionParametersReal(modifyExecutionParametersRequest, createUser);
    }

    @Override
    public List<Map<String, Object>> getAllStaticArgumentsTypeEnum() {
        return StaticArgumentsTypeEnum.getStaticArgumentsTypeList();
    }

    @Override
    public List<Map<String, Object>> getAllAlarmEventTypeEnum() {
        return AlarmEventEnum.getAlarmEventEnumList();
    }

    @Override
    public List<Map<String, Object>> getAllDataSelectMethodEnum() {
        return DataSelectMethodEnum.getDataSelectMethodList();
    }

    @Override
    public List<Map<String, Object>> getAllNoiseStrategyEnumEnum() {
        return NoiseStrategyEnum.getNoiseStrategyEnumList();
    }

    @Override
    public List<Map<String, Object>> getAllDynamicEngineEnum() {
        return DynamicEngineEnum.getDynamicEngineList();
    }

    @Override
    public List<Map<String, Object>> getAllEngineJudgeEnum(Integer code) throws UnExpectedRequestException {
        if (code == null) {
            throw new UnExpectedRequestException("code [" + code + "] {&DOES_NOT_EXIST}");
        }
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        switch (code) {
            case 1:
                list = YarnParameterEnum.getYarnParameterList();
                break;
            case 2:
                list = SparkParameterEnum.getSparkParameterList();
                break;
            case 3:
                list = FlinkParameterEnum.getFlinkParameterList();
                break;
            default:
                break;
        }

        return list;
    }

    @Override
    public List<String> getAllExecutionVariableEnum() {
        return BuiltInNameEnum.getBuiltInNameList();
    }

    @Override
    public GeneralResponse handleExecutionParametersDataMigration() {
        List<ExecutionParameters> executionParameters = executionParametersDao.selectAllExecutionParameters();
        List<ExecutionParameters> collect = executionParameters.stream().filter(item -> StringUtils.isNotBlank(item.getExecutionParam())).collect(Collectors.toList());
        LOGGER.info("Start synchronizing executionParameters executionParam data to ExecutionVariable object");
        List<ExecutionVariable> executionVariableList = Lists.newArrayList();
        for (ExecutionParameters executionParameter : collect) {
            String[] execParamStrs = executionParameter.getExecutionParam().split(SpecCharEnum.DIVIDER.getValue());
            for (String str : execParamStrs) {
                String[] strs = str.split(SpecCharEnum.COLON.getValue());
                String execParamKey = strs[0];
                String execParamValue = strs[1];
                ExecutionVariable executionVariable = new ExecutionVariable();
                executionVariable.setExecutionParameters(executionParameter);
                if (BuiltInNameEnum.FPS_ID.getValue().equals(execParamKey) || BuiltInNameEnum.FPS_HASH.getValue().equals(execParamKey)
                        || BuiltInNameEnum.RUN_DATE.getValue().equals(execParamKey) || BuiltInNameEnum.PARTITION.getValue().equals(execParamKey)) {
                    executionVariable.setVariableType(StaticArgumentsTypeEnum.BUILT_ARGUMENTS.getCode());
                } else {
                    executionVariable.setVariableType(StaticArgumentsTypeEnum.CUSTOM_ARGUMENTS.getCode());
                }

                executionVariable.setVariableName(execParamKey);
                executionVariable.setVariableValue(execParamValue);

                //qualitis_execution_variable存在匹配的数据   当多次调用该接口，ExecutionVariable存在匹配数据就不会往该表新增数据(兜底校验)
                List<ExecutionVariable> list = executionVariableDao.selectMateExecutionVariable(executionVariable.getVariableType(), execParamKey, execParamValue, executionParameter);
                if (CollectionUtils.isEmpty(list)) {
                    executionVariableList.add(executionVariable);
                    executionParameter.setExecutionVariable(true);
                }
            }

            executionParametersDao.saveExecutionParameters(executionParameter);
        }
        if (CollectionUtils.isNotEmpty(executionVariableList)) {
            executionVariableDao.saveAll(executionVariableList);
        }
        LOGGER.info("end to synchronizing  executionParameters executionParam data to executionVariable object");

        return new GeneralResponse<>("200", "{&SYNCHRONIZATION_EXECUTION_VARIABLE_SUCCESSFULLY}", null);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse<ExecutionParametersResponse> modifyExecutionParameters(ModifyExecutionParametersRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }

        return modifyExecutionParametersReal(request, loginUser);
    }

    private GeneralResponse<ExecutionParametersResponse> modifyExecutionParametersReal(ModifyExecutionParametersRequest request, String loginUser)
            throws UnExpectedRequestException, PermissionDeniedRequestException {
        // Check Arguments
        ModifyExecutionParametersRequest.checkRequest(request);
        if (request.getUnionAll() == null) {
            request.setUnionAll(false);
        }
        // Check existence of ExecutionParameters
        ExecutionParameters executionParameters = executionParametersDao.findById(request.getExecutionParametersId());
        if (executionParameters == null) {
            throw new UnExpectedRequestException("execution_parameters_id [" + request.getExecutionParametersId() + "] {&DOES_NOT_EXIST}");
        }
        // Check existence of project
        Project projectInDb = projectService.checkProjectExistence(request.getProjectId(), loginUser);
        // Check permissions of project
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
        projectService.checkProjectPermission(projectInDb, loginUser, permissions);

        if (!executionParameters.getName().equals(request.getName())) {
            checkExecutionParametersName(request.getName(), projectInDb.getId());
        }
        staticExecutionParametersDao.deleteByExecutionParameters(executionParameters);
        alarmArgumentsExecutionParametersDao.deleteByExecutionParameters(executionParameters);
        noiseEliminationManagementDao.deleteByExecutionParameters(executionParameters);
        executionVariableDao.deleteByExecutionParameters(executionParameters);
        //修改执行参数模板，同时更新已在规则中使用的执行参数名称
        List<Rule> ruleList = ruleDao.getDeployExecutionParameters(executionParameters.getProjectId(), executionParameters.getName());
        if (CollectionUtils.isNotEmpty(ruleList)) {
            String nowDate = QualitisConstants.PRINT_TIME_FORMAT.format(new Date());
            ruleList.stream().map(u -> {
                u.setExecutionParametersName(StringUtils.isNotBlank(request.getName()) ? request.getName() : u.getExecutionParametersName());
                u.setModifyUser(loginUser);
                u.setModifyTime(nowDate);
                return u;
            }).collect(Collectors.toList());
            //批量修改规则
            ruleDao.saveRules(ruleList);
        }
        setBasicInfo(executionParameters, loginUser, request);
        ExecutionParameters savedExecutionParameters = executionParametersDao.saveExecutionParameters(executionParameters);
        LOGGER.info("Succeed to modify ExecutionParameters, execution_parameters_id: {}", savedExecutionParameters.getId());

        batchHandleStaticAndAlarmArguments(savedExecutionParameters, request.getStaticExecutionParametersRequests(), request.getAlarmArgumentsExecutionParametersRequests(), request.getNoiseEliminationManagementRequests(), request.getExecutionManagementRequests());

        ExecutionParametersResponse response = new ExecutionParametersResponse(savedExecutionParameters);
        return new GeneralResponse<>("200", "{&MODIFY_EXECUTIONPARAMETERS_SUCCESSFULLY}", response);
    }

    private void batchHandleStaticAndAlarmArguments(ExecutionParameters savedExecutionParameters, List<StaticExecutionParametersRequest> staticExecutionParametersRequests, List<AlarmArgumentsExecutionParametersRequest> alarmArgumentsExecutionParametersRequests, List<NoiseEliminationManagementRequest> noiseEliminationManagementRequests, List<ExecutionExecutionParametersRequest> executionManagementRequests) throws UnExpectedRequestException {
        //执行参数 - 静态执行参数 ： 改成 执行参数 - 动态引擎配置   staticStartupParam内置参数=(等号)拼接  executionParam自定义参数:(冒号)拼接
        if (savedExecutionParameters.getSpecifyStaticStartupParam() != null && savedExecutionParameters.getSpecifyStaticStartupParam() && CollectionUtils.isNotEmpty(staticExecutionParametersRequests)) {
            List<StaticExecutionParameters> collect = staticExecutionParametersRequests.stream().map(temp -> {
                StaticExecutionParameters staticExecutionParameters = new StaticExecutionParameters();
                staticExecutionParameters.setParameterName(temp.getParameterName());
                staticExecutionParameters.setParameterType(temp.getParameterType());
                staticExecutionParameters.setParameterValue(temp.getParameterValue());
                staticExecutionParameters.setExecutionParameters(savedExecutionParameters);
                return staticExecutionParameters;
            }).collect(Collectors.toList());
            staticExecutionParametersDao.saveAll(collect);

            //DynamicEngineEnum中YARN、SPARK、FLINK、CUSTOM
            String staticStartupParam = staticExecutionParametersRequests.stream()
                    .filter(staticExecutionParametersRequest -> DynamicEngineEnum.YARN_ARGUMENTS.getCode().equals(staticExecutionParametersRequest.getParameterType()) ||
                            DynamicEngineEnum.SPARK_ARGUMENTS.getCode().equals(staticExecutionParametersRequest.getParameterType()) ||
                            DynamicEngineEnum.FLINK_ARGUMENTS.getCode().equals(staticExecutionParametersRequest.getParameterType()) ||
                            DynamicEngineEnum.CUSTOM_ARGUMENTS.getCode().equals(staticExecutionParametersRequest.getParameterType())
                    )
                    .map(staticExecutionParametersRequest -> staticExecutionParametersRequest.getParameterName() + SpecCharEnum.EQUAL.getValue() + staticExecutionParametersRequest.getParameterValue())
                    .collect(Collectors.joining(SpecCharEnum.DIVIDER.getValue()));

            savedExecutionParameters.setStaticStartupParam(staticStartupParam);
        }

        //告警参数
        if (savedExecutionParameters.getAlert() != null && savedExecutionParameters.getAlert() && CollectionUtils.isNotEmpty(alarmArgumentsExecutionParametersRequests)) {
            Set<Integer> setList = alarmArgumentsExecutionParametersRequests.stream().map(AlarmArgumentsExecutionParametersRequest::getAlarmEvent).collect(Collectors.toSet());
            if (alarmArgumentsExecutionParametersRequests.size() != setList.size()) {
                throw new UnExpectedRequestException("{&ALARM_EVENT_NOT_BE_REPEAT}");
            }

            List<AlarmArgumentsExecutionParameters> collect = alarmArgumentsExecutionParametersRequests.stream().map(temp -> {
                AlarmArgumentsExecutionParameters alarmArgumentsExecutionParameters = new AlarmArgumentsExecutionParameters();
                alarmArgumentsExecutionParameters.setAlarmEvent(temp.getAlarmEvent());
                alarmArgumentsExecutionParameters.setAlarmLevel(temp.getAlarmLevel());
                alarmArgumentsExecutionParameters.setAlarmReceiver(temp.getAlarmReceiver());
                alarmArgumentsExecutionParameters.setExecutionParameters(savedExecutionParameters);
                return alarmArgumentsExecutionParameters;
            }).collect(Collectors.toList());
            alarmArgumentsExecutionParametersDao.saveAll(collect);
        }

        //去噪参数
        if (savedExecutionParameters.getWhetherNoise() != null && savedExecutionParameters.getWhetherNoise() && CollectionUtils.isNotEmpty(noiseEliminationManagementRequests)) {
            List<NoiseEliminationManagement> collectList = noiseEliminationManagementRequests.stream().map(temp -> {
                NoiseEliminationManagement noiseEliminationManagement = new NoiseEliminationManagement();
                noiseEliminationManagement.setDateSelectionMethod(temp.getDateSelectionMethod());
                noiseEliminationManagement.setBusinessDate(StringEscapeUtils.unescapeJava(temp.getBusinessDate()));
                noiseEliminationManagement.setTemplateId(temp.getTemplateId());
                noiseEliminationManagement.setNoiseNormRatio(StringEscapeUtils.unescapeJava(temp.getNoiseNormRatio()));
                noiseEliminationManagement.setEliminateStrategy(temp.getEliminateStrategy());
                noiseEliminationManagement.setAvailable(temp.getAvailable());
                noiseEliminationManagement.setExecutionParameters(savedExecutionParameters);

                return noiseEliminationManagement;
            }).collect(Collectors.toList());

            noiseEliminationManagementDao.saveAll(collectList);
        }

        //执行变量  execution_variable
        if (savedExecutionParameters.getExecutionVariable() != null && savedExecutionParameters.getExecutionVariable() && CollectionUtils.isNotEmpty(executionManagementRequests)) {
            List<ExecutionVariable> executionVariableList = executionManagementRequests.stream().map(temp -> {
                ExecutionVariable executionVariable = new ExecutionVariable();
                executionVariable.setVariableType(temp.getVariableType());
                executionVariable.setVariableName(temp.getVariableName());
                executionVariable.setVariableValue(temp.getVariableValue());
                executionVariable.setExecutionParameters(savedExecutionParameters);

                return executionVariable;
            }).collect(Collectors.toList());

            executionVariableDao.saveAll(executionVariableList);

            String executionParam = executionManagementRequests.stream()
                    .map(executionManagementRequest -> executionManagementRequest.getVariableName() + SpecCharEnum.COLON.getValue() + executionManagementRequest.getVariableValue())
                    .collect(Collectors.joining(SpecCharEnum.DIVIDER.getValue()));
            //并发粒度：库粒度、表粒度，引擎复用参数，拼接到执行变量------>因规则执行时，表单的默认会传发粒度、引擎复用等且优先级最高，所以这里不需拼接；

            savedExecutionParameters.setExecutionParam(executionParam);
        }

    }

    private void setBasicInfo(ExecutionParameters executionParameters, String loginUser, ModifyExecutionParametersRequest request) {
        String nowDate = QualitisConstants.PRINT_TIME_FORMAT.format(new Date());
        executionParameters.setName(request.getName());
        executionParameters.setAbortOnFailure(request.getAbortOnFailure());
        executionParameters.setSpecifyStaticStartupParam(request.getSpecifyStaticStartupParam());
        executionParameters.setAlert(request.getAlert());
        executionParameters.setAlertLevel(request.getAlertLevel());
        executionParameters.setAlertReceiver(request.getAlertReceiver());

        executionParameters.setProjectId(request.getProjectId());
        executionParameters.setModifyUser(loginUser);
        executionParameters.setModifyTime(nowDate);
        executionParameters.setCluster(request.getCluster());
        executionParameters.setAbnormalDatabase(request.getAbnormalDatabase());
        executionParameters.setAbnormalProxyUser(request.getAbnormalProxyUser());
        executionParameters.setAbnormalDataStorage(request.getAbnormalDataStorage());
        executionParameters.setUnionAll(request.getUnionAll());

        executionParameters.setFilter(request.getFilter());
        executionParameters.setSpecifyFilter(request.getSpecifyFilter());
        executionParameters.setDeleteFailCheckResult(request.getDeleteFailCheckResult());
        executionParameters.setUploadRuleMetricValue(request.getUploadRuleMetricValue());
        executionParameters.setUploadAbnormalValue(request.getUploadAbnormalValue());
        executionParameters.setSourceTableFilter(request.getSourceTableFilter());
        executionParameters.setTargetTableFilter(request.getTargetTableFilter());
        executionParameters.setWhetherNoise(request.getWhetherNoise());

        executionParameters.setExecutionVariable(request.getExecutionVariable());
        executionParameters.setAdvancedExecution(request.getAdvancedExecution());
        executionParameters.setEngineReuse(request.getEngineReuse());
        executionParameters.setConcurrencyGranularity(request.getConcurrencyGranularity());
        executionParameters.setDynamicPartitioning(request.getDynamicPartitioning());
        executionParameters.setTopPartition(request.getTopPartition());
    }

    private void checkExecutionParametersName(String name, Long projectId) throws UnExpectedRequestException {
        ExecutionParameters executionParameters = executionParametersDao.findByNameAndProjectId(name, projectId);
        if (executionParameters != null) {
            throw new UnExpectedRequestException("ExecutionParameters name {&ALREADY_EXIST}");
        }
    }


    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse deleteExecutionParameters(DeleteExecutionParametersRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        // Check Arguments
        DeleteExecutionParametersRequest.checkRequest(request);
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        // Check existence of ExecutionParameters
        ExecutionParameters executionParameters = executionParametersDao.findById(request.getExecutionParametersId());
        if (executionParameters == null) {
            throw new UnExpectedRequestException("execution_parameters_id [" + request.getExecutionParametersId() + "] {&DOES_NOT_EXIST}");
        }
        // Check existence of project
        Project projectInDb = projectService.checkProjectExistence(request.getProjectId(),
                loginUser);
        // Check permissions of project
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
        projectService.checkProjectPermission(projectInDb, loginUser, permissions);

        //校验规则是否存在有配置了该模板name
        List<Rule> ruleList = ruleDao.getDeployExecutionParameters(executionParameters.getProjectId(), executionParameters.getName());
        if (CollectionUtils.isNotEmpty(ruleList)) {
            LOGGER.info("This is ruleList", Arrays.toString(ruleList.toArray()));
            throw new UnExpectedRequestException("{&EXECUTIONPARAMETERS_ALREADY_CONFIGURED_IN_THE_RULE}.");
        } else {
            // Delete ExecutionParameters
            executionParametersDao.deleteExecutionParameters(executionParameters);
            LOGGER.info("Succeed to delete ExecutionParameters. execution_parameters_id: {}", executionParameters.getId());
        }

        return new GeneralResponse<>("200", "{&DELETE_EXECUTIONPARAMETERS_SUCCESSFULLY}", null);
    }

    @Override
    public GeneralResponse<GetAllResponse<ExecutionParametersResponse>> getAllExecutionParameters(ExecutionParametersRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        GetAllResponse<ExecutionParametersResponse> response = getExecutionParameters(request);
        LOGGER.info("Succeed to get all ExecutionParameters, response: {}", response);
        return new GeneralResponse<>("200", "{&GET_ALL_EXECUTIONPARAMETERS_SUCCESSFULLY}", response);

    }

    @Override
    public GeneralResponse<ExecutionParametersResponse> getExecutionParametersDetail(Long executionParametersId) throws UnExpectedRequestException {
        // Check existence of ExecutionParameters
        ExecutionParameters executionParameters = executionParametersDao.findById(executionParametersId);
        if (executionParameters == null) {
            throw new UnExpectedRequestException("execution_parameters_id [" + executionParametersId + "] {&DOES_NOT_EXIST}");
        }

        Long projectInDbId = executionParameters.getProjectId();
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        projectService.checkProjectExistence(projectInDbId, loginUser);

        LOGGER.info("Succeed to find ExecutionParameters. execution_parameters_id: {}", executionParametersId);

        ExecutionParametersResponse response = new ExecutionParametersResponse(executionParameters);
        LOGGER.info("Succeed to get ExecutionParameters detail. execution_parameters_id: {}", executionParametersId);
        return new GeneralResponse<>("200", "{&GET_EXECUTIONPARAMETERS_DETAIL_SUCCESSFULLY}", response);
    }

    public GetAllResponse<ExecutionParametersResponse> getExecutionParameters(ExecutionParametersRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        ExecutionParametersRequest.checkRequest(request);
        String username = HttpUtils.getUserName(httpServletRequest);
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        Project projectInDb = projectService.checkProjectExistence(request.getProjectId(),
                loginUser);
        // Check if user has permission get project.
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.BUSSMAN.getCode());
        projectService.checkProjectPermission(projectInDb, username, permissions);

        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(request.getPage());
        pageRequest.setSize(request.getSize());
        // Check Arguments
        PageRequest.checkRequest(pageRequest);
        // Paging get ExecutionParameters
        int page = request.getPage();
        int size = request.getSize();
        List<ExecutionParameters> allExecutionParameters;
        GetAllResponse<ExecutionParametersResponse> response = new GetAllResponse<>();
        if (StringUtils.isNotBlank(request.getName())) {
            allExecutionParameters = executionParametersDao.findAllExecutionParameters(projectInDb.getId(), "%" + request.getName() + "%", page, size);
            response.setTotal(executionParametersDao.countTotalByName(projectInDb.getId(), "%" + request.getName() + "%"));
        } else {
            allExecutionParameters = executionParametersDao.findAll(projectInDb.getId(), page, size);
            response.setTotal(executionParametersDao.countTotal(projectInDb.getId()));
        }
        List<ExecutionParametersResponse> executionParametersResponse = new ArrayList<>();
        for (ExecutionParameters executionParameters : allExecutionParameters) {
            executionParametersResponse.add(new ExecutionParametersResponse(executionParameters));
        }
        response.setData(executionParametersResponse);

        return response;
    }

}
