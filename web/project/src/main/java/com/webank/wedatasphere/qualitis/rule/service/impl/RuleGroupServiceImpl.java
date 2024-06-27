package com.webank.wedatasphere.qualitis.rule.service.impl;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constant.UnionWayEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.dao.RuleMetricDao;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.constant.ProjectUserPermissionEnum;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.rule.config.RuleConfig;
import com.webank.wedatasphere.qualitis.rule.constant.RuleLockRangeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.ExecutionParametersDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleGroupDao;
import com.webank.wedatasphere.qualitis.rule.entity.*;
import com.webank.wedatasphere.qualitis.rule.exception.RuleLockException;
import com.webank.wedatasphere.qualitis.rule.request.*;
import com.webank.wedatasphere.qualitis.rule.response.RuleDetailResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleGroupResponse;
import com.webank.wedatasphere.qualitis.rule.service.*;
import com.webank.wedatasphere.qualitis.rule.timer.RuleUpdaterCallable;
import com.webank.wedatasphere.qualitis.rule.timer.RuleUpdaterThreadFactory;
import com.webank.wedatasphere.qualitis.scheduled.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.service.RuleMetricCommonService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author allenzhou@webank.com
 * @date 2022/6/1 15:20
 */
@Service
public class RuleGroupServiceImpl implements RuleGroupService {
    @Autowired
    private RuleConfig ruleConfig;

    @Autowired
    private RuleService ruleService;

    @Autowired
    private FileRuleService fileRuleService;

    @Autowired
    private CustomRuleService customRuleService;

    @Autowired
    private MultiSourceRuleService multiSourceRuleService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private RuleDataSourceService ruleDataSourceService;

    @Autowired
    private RuleVariableService ruleVariableService;

    @Autowired
    private RuleDao ruleDao;

    @Autowired
    private RuleGroupDao ruleGroupDao;

    @Autowired
    private RuleLockService ruleLockService;

    @Autowired
    private ExecutionParametersDao executionParametersDao;

    @Autowired
    private RuleMetricCommonService ruleMetricCommonService;

    @Autowired
    private RuleMetricDao ruleMetricDao;

    private HttpServletRequest httpServletRequest;

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleGroupServiceImpl.class);

    private static final ThreadPoolExecutor POOL = new ThreadPoolExecutor(50,
        Integer.MAX_VALUE,
        60,
        TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(1000),
        new RuleUpdaterThreadFactory(),
        new ThreadPoolExecutor.DiscardPolicy());

    public RuleGroupServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public RuleGroupResponse addRuleGroup(AddRuleGroupRequest request)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        List<DataSourceRequest> dataSourceRequests = request.getDatasource();
        LOGGER.info(loginUser + " start to add rule group with datasource.");

        // Check existence of project
        Project projectInDb = projectService.checkProjectExistence(request.getProjectId(),
            loginUser);
        // Check permissions of project
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
        projectService.checkProjectPermission(projectInDb, loginUser, permissions);


        String ruleGroupName = request.getRuleGroupName();
        if (StringUtils.isEmpty(request.getRuleGroupName())) {
            // Construct rule group name
            String datasourcePrefix = dataSourceRequests.stream().map(dataSourceRequest ->
                StringUtils.isNotBlank(dataSourceRequest.getDbName()) ? dataSourceRequest.getDbName() + SpecCharEnum.BOTTOM_BAR.getValue() + dataSourceRequest.getTableName() : "" + SpecCharEnum.BOTTOM_BAR.getValue() + dataSourceRequest.getTableName())
                .collect(Collectors.joining(SpecCharEnum.BOTTOM_BAR.getValue()));
            ruleGroupName = datasourcePrefix + SpecCharEnum.BOTTOM_BAR.getValue() + UUID.randomUUID().toString().replace(SpecCharEnum.MINUS.getValue(), "");
        }

        LOGGER.info("New rule group name is " + ruleGroupName);
        RuleGroup ruleGroup = new RuleGroup(ruleGroupName, request.getProjectId());

        // For workflow node context.
        boolean cs = false;
        if (StringUtils.isNotBlank(request.getCsId())) {
            LOGGER.info("Workflow context service ID: " + request.getCsId());
            cs = true;
        }
        RuleGroup savedRuleGroup = ruleGroupDao.saveRuleGroup(ruleGroup);

        List<RuleDataSource> savedRuleDataSource = ruleDataSourceService.checkAndSaveRuleDataSource(dataSourceRequests, null, savedRuleGroup, cs, loginUser);
        LOGGER.info("Success to save rule group datasource.");
        savedRuleGroup.setRuleDataSources(new HashSet<>(savedRuleDataSource));

        return new RuleGroupResponse(savedRuleGroup);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public RuleGroupResponse modifyRuleGroup(ModifyRuleGroupRequest modifyRuleGroupRequest)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        return modifyRuleGroupReal(modifyRuleGroupRequest);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    @Override
    public RuleGroupResponse modifyRuleGroupWithLock(ModifyRuleGroupRequest modifyRuleGroupRequest) throws UnExpectedRequestException, PermissionDeniedRequestException, RuleLockException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        if (!ruleLockService.tryAcquire(modifyRuleGroupRequest.getRuleGroupId(), RuleLockRangeEnum.TABLE_GROUP_DATASOURCE, loginUser)) {
            LOGGER.warn("Failed to acquire lock");
            throw new RuleLockException("{&RULE_LOCK_ACQUIRE_FAILED}");
        }
        try {
            return modifyRuleGroupReal(modifyRuleGroupRequest);
        } finally {
            ruleLockService.release(modifyRuleGroupRequest.getRuleGroupId(), RuleLockRangeEnum.TABLE_GROUP_DATASOURCE, loginUser);
        }
    }

    private RuleGroupResponse modifyRuleGroupReal(ModifyRuleGroupRequest modifyRuleGroupRequest) throws UnExpectedRequestException, PermissionDeniedRequestException {
        // Check ruleGroup existence
        RuleGroup ruleGroupInDb = ruleGroupDao.findById(modifyRuleGroupRequest.getRuleGroupId());
        if (ruleGroupInDb == null) {
            throw new UnExpectedRequestException(String.format("Rule Group: %s {&DOES_NOT_EXIST}", modifyRuleGroupRequest.getRuleGroupId()));
        }
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        // Check existence of project
        Project projectInDb = projectService.checkProjectExistence(modifyRuleGroupRequest.getProjectId(),
                loginUser);
        // Check permissions of project
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
        projectService.checkProjectPermission(projectInDb, loginUser, permissions);

        // Check group name and workflow version unique in project.
        if (StringUtils.isEmpty(ruleGroupInDb.getVersion()) && ! ruleGroupInDb.getRuleGroupName().equals(modifyRuleGroupRequest.getRuleGroupName())) {
            RuleGroup ruleGroupExist = ruleGroupDao.findByRuleGroupNameAndProjectId(modifyRuleGroupRequest.getRuleGroupName(), projectInDb.getId());
            if (ruleGroupExist != null) {
                throw new UnExpectedRequestException(String.format("Rule group [name=%s]with no versio in this project {&ALREADY_EXIST}", modifyRuleGroupRequest.getRuleGroupName()));
            }
        }
        ruleGroupInDb.setRuleGroupName(modifyRuleGroupRequest.getRuleGroupName());
        // For workflow node context.
        boolean cs = false;
        if (StringUtils.isNotBlank(modifyRuleGroupRequest.getCsId())) {
            LOGGER.info("In workflow, save context service ID: " + modifyRuleGroupRequest.getCsId());
            modifyRuleGroupRequest.setCsId(modifyRuleGroupRequest.getCsId());
            cs = true;
        }
        if (CollectionUtils.isNotEmpty(ruleGroupInDb.getRuleDataSources())) {
            // Delete rule datasource
            ruleDataSourceService.deleteByRuleGroup(ruleGroupInDb);
            if (CollectionUtils.isNotEmpty(modifyRuleGroupRequest.getDatasource())) {
                List<RuleDataSource> savedRuleDataSource = ruleDataSourceService
                        .checkAndSaveRuleDataSource(modifyRuleGroupRequest.getDatasource(), null, ruleGroupInDb, cs, loginUser);
                LOGGER.info("Success to save rule group datasource.");
                ruleGroupInDb.setRuleDataSources(new HashSet<>(savedRuleDataSource));
            }
        }
        ruleGroupDao.saveRuleGroup(ruleGroupInDb);
        return new RuleGroupResponse(ruleGroupInDb);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public RuleGroupResponse addRules(AddGroupRulesRequest request, RuleGroup ruleGroupInDb)
        throws ExecutionException, InterruptedException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        List<AddGroupRuleRequest> addGroupRuleRequestList = request.getAddGroupRuleRequestList();
        // Transform datasource into datasource request
        List<DataSourceRequest> dataSourceRequests = getDataSourceRequst(ruleGroupInDb.getRuleDataSources());
        List<AddGroupRuleRequest> updateAddGroupRuleRequestList = new ArrayList<>(addGroupRuleRequestList.size());
        Set<String> executionParamNames = addGroupRuleRequestList.stream().map(addGroupRuleRequest -> addGroupRuleRequest.getExecutionParametersName()).collect(
            Collectors.toSet());
        Map<String, ExecutionParameters> map = new HashMap<>(executionParamNames.size());

        for (String currentExecutionParamName : executionParamNames) {
            ExecutionParameters executionParameters = executionParametersDao.findByNameAndProjectId(currentExecutionParamName, request.getProjectId());
            map.put(currentExecutionParamName, executionParameters);
        }

        for (AddGroupRuleRequest addGroupRuleRequest : addGroupRuleRequestList) {
            if (addGroupRuleRequest.getRuleEnable() == null) {
                addGroupRuleRequest.setRuleEnable(true);
            }
            if (addGroupRuleRequest.getUnionWay() == null) {
                addGroupRuleRequest.setUnionWay(UnionWayEnum.NO_COLLECT_CALCULATE.getCode());
            }
            if (StringUtils.isNotBlank(addGroupRuleRequest.getFilter())) {
                dataSourceRequests = dataSourceRequests.stream().map((dataSourceRequest) -> {
                    dataSourceRequest.setFilter(addGroupRuleRequest.getFilter());
                    return dataSourceRequest;
                }).collect(Collectors.toList());
            }
            if (CollectionUtils.isNotEmpty(addGroupRuleRequest.getDatasource())) {
                List<DataSourceColumnRequest> colNames = addGroupRuleRequest.getDatasource().iterator().next().getColNames();
                if (CollectionUtils.isNotEmpty(colNames)) {
                    dataSourceRequests = dataSourceRequests.stream().map((dataSourceRequest) -> {
                        dataSourceRequest.setColNames(colNames);
                        return dataSourceRequest;
                    }).collect(Collectors.toList());
                }
            }
            addGroupRuleRequest.setDatasource(dataSourceRequests);
            addGroupRuleRequest.setProjectId(request.getProjectId());
            addGroupRuleRequest.setRuleGroupId(request.getRuleGroupId());
            // Set attrbutes from execution parameter.
            setByExecutionParam(map, addGroupRuleRequest);
            updateAddGroupRuleRequestList.add(addGroupRuleRequest);
        }
        List<Future<List<Exception>>> exceptionList = new ArrayList<>();
        request.setAddGroupRuleRequestList(updateAddGroupRuleRequestList);
        addRuleConcurrent(updateAddGroupRuleRequestList, loginUser, exceptionList);

        // Check
        StringBuilder exceptionMessage = new StringBuilder();
        if (judgeExistsException(exceptionList, exceptionMessage)) {
            return new RuleGroupResponse(ResponseStatusConstants.OK, ruleGroupInDb, "All rules successfully finished");
        }
        return new RuleGroupResponse(ResponseStatusConstants.BAD_REQUEST, ruleGroupInDb, "Some rules failed to add, detail message: " + exceptionMessage.toString());
    }

    private boolean judgeExistsException(List<Future<List<Exception>>> exceptionList, StringBuilder exceptionMessage) throws InterruptedException, ExecutionException {
        List<Exception> exceptions = new ArrayList<>();
        for (Future<List<Exception>> future : exceptionList) {
            if (CollectionUtils.isNotEmpty(future.get())) {
                exceptions.addAll(future.get());
            }
        }
        if (CollectionUtils.isEmpty(exceptions)) {
            return true;
        } else {
            for (Exception e : exceptions) {
                exceptionMessage.append(e.getMessage()).append("\n");
            }
        }
        return false;
    }

    private void addRuleConcurrent(List<AddGroupRuleRequest> updateAddGroupRuleRequestList, String loginUser, List<Future<List<Exception>>> exceptionList) throws InterruptedException {
        int total = updateAddGroupRuleRequestList.size();
        int updateThreadSize = total / ruleConfig.getRuleUpdateSize() + 1;
        if (total % ruleConfig.getRuleUpdateSize() == 0) {
            updateThreadSize = total / ruleConfig.getRuleUpdateSize();
        }
        CountDownLatch latch = new CountDownLatch(updateThreadSize);
        for (int indexThread = 0; total > 0 && indexThread < total; indexThread += ruleConfig.getRuleUpdateSize()) {
            if (indexThread + ruleConfig.getRuleUpdateSize() < total) {
                Future<List<Exception>> exceptionFuture = POOL.submit(new RuleUpdaterCallable(ruleService, fileRuleService
                    , updateAddGroupRuleRequestList.subList(indexThread, indexThread + ruleConfig.getRuleUpdateSize()), loginUser, latch));
                exceptionList.add(exceptionFuture);
            } else {
                Future<List<Exception>> exceptionFuture = POOL.submit(new RuleUpdaterCallable(ruleService, fileRuleService
                    , updateAddGroupRuleRequestList.subList(indexThread, total), loginUser, latch));
                exceptionList.add(exceptionFuture);
            }
            updateThreadSize --;
        }

        if (total > 0 && updateThreadSize == 0) {
            try {
                latch.await();
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted!", e);
                Thread.currentThread().interrupt();
                throw new InterruptedException(e.getMessage());
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public RuleGroupResponse modifyRules(ModifyGroupRulesRequest request, RuleGroup ruleGroupInDb, int ruleTotal)
            throws ExecutionException, InterruptedException, UnExpectedRequestException {
        return modifyRulesReal(request,ruleGroupInDb, ruleTotal);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    @Override
    public RuleGroupResponse modifyRulesWithLock(ModifyGroupRulesRequest request, RuleGroup ruleGroupInDb, int ruleTotal) throws ExecutionException, InterruptedException, UnExpectedRequestException, RuleLockException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        if (!ruleLockService.tryAcquire(request.getRuleGroupId(), RuleLockRangeEnum.TABLE_GROUP_RULES, loginUser)) {
            LOGGER.warn("Failed to acquire lock");
            throw new RuleLockException("{&RULE_LOCK_ACQUIRE_FAILED}");
        }
        try {
            return modifyRulesReal(request,ruleGroupInDb, ruleTotal);
        } finally {
            ruleLockService.release(request.getRuleGroupId(), RuleLockRangeEnum.TABLE_GROUP_RULES, loginUser);
        }
    }

    private RuleGroupResponse modifyRulesReal(ModifyGroupRulesRequest request, RuleGroup ruleGroupInDb, int ruleTotal)
            throws ExecutionException, InterruptedException, UnExpectedRequestException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        List<ModifyGroupRuleRequest> modifyGroupRuleRequestList = request.getModifyGroupRuleRequestList();
        List<ModifyGroupRuleRequest> updateModifyGroupRuleRequestList = new ArrayList<>(modifyGroupRuleRequestList.size());
        Set<String> executionParamNames = modifyGroupRuleRequestList.stream().map(modifyGroupRuleRequest -> modifyGroupRuleRequest.getExecutionParametersName()).collect(
                Collectors.toSet());
        Map<String, ExecutionParameters> map = new HashMap<>(executionParamNames.size());

        for (String currentExecutionParamName : executionParamNames) {
            ExecutionParameters executionParameters = executionParametersDao.findByNameAndProjectId(currentExecutionParamName, request.getProjectId());
            map.put(currentExecutionParamName, executionParameters);
        }

        for (ModifyGroupRuleRequest modifyGroupRuleRequest : modifyGroupRuleRequestList) {
            CommonChecker.checkCollections(modifyGroupRuleRequest.getDatasource(), "datasource");
            if (modifyGroupRuleRequest.getRuleEnable() == null) {
                modifyGroupRuleRequest.setRuleEnable(true);
            }
            if (modifyGroupRuleRequest.getUnionWay() == null) {
                modifyGroupRuleRequest.setUnionWay(UnionWayEnum.NO_COLLECT_CALCULATE.getCode());
            }
            String filter = map.get(modifyGroupRuleRequest.getExecutionParametersName()).getFilter();
            if (StringUtils.isNotBlank(filter)) {
                String ruleGroupFilter = ruleGroupInDb.getRuleDataSources().iterator().next().getFilter();
                if (QualitisConstants.RULE_GROUP_FILTER_PLACEHOLDER.equals(ruleGroupFilter)) {
                    ruleGroupFilter = filter;
                } else {
                    ruleGroupFilter = "true";
                }
                String finalRuleGroupFilter = ruleGroupFilter;
                modifyGroupRuleRequest.setDatasource(modifyGroupRuleRequest.getDatasource().stream().map((dataSourceRequest) -> {
                    dataSourceRequest.setFilter(finalRuleGroupFilter);
                    return dataSourceRequest;
                }).collect(Collectors.toList()));
            } else {
                String ruleGroupFilter = ruleGroupInDb.getRuleDataSources().iterator().next().getFilter();
                if (QualitisConstants.RULE_GROUP_FILTER_PLACEHOLDER.equals(ruleGroupFilter)) {
                    ruleGroupFilter = "true";
                }
                String finalRuleGroupFilter = ruleGroupFilter;
                modifyGroupRuleRequest.setDatasource(modifyGroupRuleRequest.getDatasource().stream().map((dataSourceRequest) -> {
                    dataSourceRequest.setFilter(finalRuleGroupFilter);
                    return dataSourceRequest;
                }).collect(Collectors.toList()));
            }

            modifyGroupRuleRequest.setRuleGroupId(request.getRuleGroupId());
            modifyGroupRuleRequest.setProjectId(request.getProjectId());
            if (null == modifyGroupRuleRequest.getRuleId()) {
                modifyGroupRuleRequest.setRuleNo(++ ruleTotal);
            }
            // Set attrbutes from execution parameter.
            setByExecutionParam(map, modifyGroupRuleRequest);
            updateModifyGroupRuleRequestList.add(modifyGroupRuleRequest);
        }

        List<Future<List<Exception>>> exceptionList = new ArrayList<>();
        request.setModifyGroupRuleRequestList(updateModifyGroupRuleRequestList);
        modifyRulesConcurrent(updateModifyGroupRuleRequestList, loginUser, exceptionList);

        StringBuilder exceptionMessage = new StringBuilder();
        if (judgeExistsException(exceptionList, exceptionMessage)) {
            return new RuleGroupResponse(ResponseStatusConstants.OK, ruleGroupInDb, "All rules successfully finished");
        }
        return new RuleGroupResponse(ResponseStatusConstants.BAD_REQUEST, ruleGroupInDb, "Some rules failed to modify, detail message:" + exceptionMessage.toString());
    }

    private void modifyRulesConcurrent(List<ModifyGroupRuleRequest> updateModifyGroupRuleRequestList, String loginUser, List<Future<List<Exception>>> exceptionList) throws InterruptedException {
        int total = updateModifyGroupRuleRequestList.size();
        int updateThreadSize = total / ruleConfig.getRuleUpdateSize() + 1;
        if (total % ruleConfig.getRuleUpdateSize() == 0) {
            updateThreadSize = total / ruleConfig.getRuleUpdateSize();
        }
        CountDownLatch latch = new CountDownLatch(updateThreadSize);
        for (int indexThread = 0; total > 0 && indexThread < total; indexThread += ruleConfig.getRuleUpdateSize()) {
            if (indexThread + ruleConfig.getRuleUpdateSize() < total) {
                Future<List<Exception>> exceptionFuture = POOL.submit(new RuleUpdaterCallable(ruleService, fileRuleService
                    , updateModifyGroupRuleRequestList.subList(indexThread, indexThread + ruleConfig.getRuleUpdateSize()), loginUser, latch, "modify"));
                exceptionList.add(exceptionFuture);
            } else {
                Future<List<Exception>> exceptionFuture = POOL.submit(new RuleUpdaterCallable(ruleService, fileRuleService
                    , updateModifyGroupRuleRequestList.subList(indexThread, total), loginUser, latch, "modify"));
                exceptionList.add(exceptionFuture);
            }
            updateThreadSize --;
        }

        if (total > 0 && updateThreadSize == 0) {
            try {
                latch.await();
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted!", e);
                Thread.currentThread().interrupt();
                throw new InterruptedException(e.getMessage());
            }
        }
    }

    @Override
    public GetAllResponse<RuleDetailResponse> queryRules(QueryGroupRulesRequest request, RuleGroup ruleGroupInDb)
            throws UnExpectedRequestException, PermissionDeniedRequestException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        // Check existence of project
        Project projectInDb = projectService.checkProjectExistence(request.getProjectId(), loginUser);
        // Check permissions of project
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
        projectService.checkProjectPermission(projectInDb, loginUser, permissions);

        List<Rule> rules;
        long total = 0;
        //int total = ruleDao.countByRuleGroup(ruleGroupInDb);
        if (CollectionUtils.isEmpty(request.getColumns())) {
            request.setColumns(null);
        }
        if (StringUtils.isEmpty(request.getName())) {
            request.setName("");
        } else {
            request.setName("%" + request.getName() + "%");
        }
        if (StringUtils.isEmpty(request.getCnName())) {
            request.setCnName("");
        } else {
            request.setCnName("%" + request.getCnName() + "%");
        }
        GetAllResponse<RuleDetailResponse> response = new GetAllResponse<>();
        response.setTotal(total);

        boolean isFileType = RuleTypeEnum.FILE_TEMPLATE_RULE.getCode().equals(request.getRuleType());
        if (isFileType) {
            rules = ruleDao.findByRuleGroupAndFileOutNameWithPage(request.getPage(), request.getSize(), ruleGroupInDb, request.getTemplateId(), request.getName()
                    , request.getCnName(), request.getColumns(), request.getRuleType());
            total = ruleDao.countByRuleGroupAndFileOutName(ruleGroupInDb, request.getTemplateId(), request.getName()
                    , request.getCnName(), request.getColumns(), request.getRuleType());
        } else {
            Page<Rule> rulePage = ruleDao.findByRuleGroupWithPage(request.getPage(), request.getSize(), ruleGroupInDb, request.getTemplateId(), request.getName()
                    , request.getCnName(), request.getColumns(), request.getRuleType());
            rules = rulePage.getContent();
            total = rulePage.getTotalElements();
        }
        List<RuleVariable> ruleVariableList = ruleVariableService.queryByRules(rules);
        Map<Long, List<RuleVariable>> ruleVariableMap = ruleVariableList.stream().collect(Collectors.groupingBy(ruleVariable -> ruleVariable.getRule().getId()));
        List<RuleDetailResponse> ruleResponses = new ArrayList<>(rules.size());
        for (Rule rule : rules) {
            RuleDetailResponse ruleResponse = new RuleDetailResponse(rule, ruleVariableMap.getOrDefault(rule.getId(), Collections.emptyList()));
            ruleResponses.add(ruleResponse);
        }
        response.setData(ruleResponses);
        response.setTotal(total);
        return response;

    }

    @Override
    public List<Map<String, Object>> getOptionList(RuleGroupPageRequest request) {
        List<Map<String, Object>> resultList = ruleGroupDao.findByProject(request.getProjectId());
//        append version to ruleGroupName
        if (CollectionUtils.isNotEmpty(resultList)) {
             resultList.stream().forEach(entry -> {
                String ruleGroupName = String.valueOf(entry.get("rule_group_name"));
                Object version = entry.get("version");
                if (null != version) {
                    ruleGroupName += "(" + version +")";
                }
                entry.put("rule_group_name", ruleGroupName);
                entry.remove("version");
            });
        }
        return resultList;
    }

    @Override
    public GeneralResponse addBatchRule(AddBatchRuleRequest request) throws UnExpectedRequestException, InterruptedException, ExecutionException, PermissionDeniedRequestException, IOException {
        checkAddBatchRuleRequest(request);
        Project project = projectDao.findById(request.getProjectId());
        if (project == null) {
            throw new UnExpectedRequestException("Project {&DOES_NOT_EXIST}");
        }

        String loginUser = HttpUtils.getUserName(httpServletRequest);
        String ruleGroupName = "Template_Group_" + UuidGenerator.generate();
        RuleGroup ruleGroup = new RuleGroup(ruleGroupName, request.getProjectId());
        ruleGroupDao.saveRuleGroup(ruleGroup);

        setRuleMetricIfNotExists(request.getCheckObjectList(), loginUser);
        List<ModifyGroupRuleRequest> groupRuleRequestList = convert2ModifyGroupRuleRequest(project, ruleGroup, request);

        List<Future<List<Exception>>> exceptionList = new ArrayList<>();
        modifyRulesConcurrent(groupRuleRequestList, loginUser, exceptionList);

        StringBuilder exceptionMessage = new StringBuilder();
        if (judgeExistsException(exceptionList, exceptionMessage)) {
            return new GeneralResponse(ResponseStatusConstants.OK, "All rules successfully finished", new RuleGroupResponse(ruleGroup));
        }
        return new GeneralResponse(ResponseStatusConstants.BAD_REQUEST, "Some rules failed to modify, detail message:" + exceptionMessage.toString(), new RuleGroupResponse(ruleGroup));
    }

    @Override
    public RuleGroup generate(Long ruleGroupId, String ruleGroupName, Project projectInDb, int groupType) throws UnExpectedRequestException {
        RuleGroup ruleGroup;
        if (ruleGroupId != null) {
            ruleGroup = ruleGroupDao.findById(ruleGroupId);
            if (ruleGroup == null) {
                throw new UnExpectedRequestException(String.format("Rule Group: %s {&DOES_NOT_EXIST}", ruleGroupId));
            }
            if (StringUtils.isNotEmpty(ruleGroupName)) {
                ruleGroup.setRuleGroupName(ruleGroupName);
            }
            ruleGroup = ruleGroupDao.saveRuleGroup(ruleGroup);
        } else {
            if (StringUtils.isEmpty(ruleGroupName)) {
                ruleGroupName = "Group_" + UuidGenerator.generate();
            }
            ruleGroup = ruleGroupDao.saveRuleGroup(new RuleGroup(ruleGroupName, projectInDb.getId(), groupType));
        }
        return ruleGroup;
    }

    private void checkAddBatchRuleRequest(AddBatchRuleRequest request) throws UnExpectedRequestException {
        List<AddBatchRuleRequest.AddBatchCheckObjectRequest> checkObjectList = request.getCheckObjectList();

        List<String> ruleMetricNameList = checkObjectList.stream().map(AddBatchRuleRequest.AddBatchCheckObjectRequest::getRuleMetricName).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
        long ruleMetricCount = ruleMetricNameList.stream().distinct().count();
        if (ruleMetricNameList.size() > ruleMetricCount) {
            throw new UnExpectedRequestException("校验指标不能重复，一个校验对象对应一个校验指标!");
        }

        long datasourceCount = checkObjectList.stream().map(checkObject -> {
            StringBuilder dataSourceInfo = new StringBuilder();
            dataSourceInfo.append(checkObject.getClusterName());
            dataSourceInfo.append(checkObject.getDbName());
            dataSourceInfo.append(checkObject.getTableName());
            if (CollectionUtils.isNotEmpty(checkObject.getColNames())) {
                checkObject.getColNames().forEach(column -> dataSourceInfo.append(column.getColumnName()));
            }
            return dataSourceInfo.toString();
        }).distinct().count();
        if (datasourceCount < checkObjectList.size()) {
            throw new UnExpectedRequestException("校验对象的数据源信息不能相同!");
        }
    }

    private void setRuleMetricIfNotExists(List<AddBatchRuleRequest.AddBatchCheckObjectRequest> checkObjectList, String loginUser) throws UnExpectedRequestException, PermissionDeniedRequestException, IOException {
        for (AddBatchRuleRequest.AddBatchCheckObjectRequest AddBatchCheckObjectRequest: checkObjectList) {
            if (Objects.isNull(AddBatchCheckObjectRequest.getRuleMetricId()) && StringUtils.isNotEmpty(AddBatchCheckObjectRequest.getRuleMetricName())) {
                String ruleMetricName = AddBatchCheckObjectRequest.getRuleMetricName();
                RuleMetric ruleMetric = ruleMetricDao.findByName(ruleMetricName);
                if (ruleMetric == null) {
                    ruleMetric = ruleMetricCommonService.accordingRuleMetricNameAdd(ruleMetricName, loginUser, CollectionUtils.isNotEmpty(AddBatchCheckObjectRequest.getDataSourceEnvRequests()));
                }
                AddBatchCheckObjectRequest.setRuleMetricId(ruleMetric.getId());
                AddBatchCheckObjectRequest.setRuleMetricEnCode(ruleMetric.getEnCode());
            }
        }
    }

    private List<ModifyGroupRuleRequest> convert2ModifyGroupRuleRequest(Project project, RuleGroup ruleGroup, AddBatchRuleRequest request) {
        List<AddBatchRuleRequest.AddBatchCheckObjectRequest> checkObjectList = request.getCheckObjectList();
        List<ModifyGroupRuleRequest> groupRuleRequestList = Lists.newArrayListWithExpectedSize(checkObjectList.size());
        ExecutionParameters executionParameters = executionParametersDao.findByNameAndProjectId(request.getExecutionParametersName(), request.getProjectId());
        Map<String, ExecutionParameters> executionParametersMap = new HashMap<>();
        executionParametersMap.put(request.getExecutionParametersName(), executionParameters);

        int ruleSeq = 1;
        for (AddBatchRuleRequest.AddBatchCheckObjectRequest checkObject: checkObjectList) {
            ModifyGroupRuleRequest groupRuleRequest = new ModifyGroupRuleRequest();
            String ruleName = request.getRuleName() + SpecCharEnum.BOTTOM_BAR.getValue() + ruleSeq;
            ruleName = ruleName.replace(" ", "");
            String ruleCnName = request.getRuleCnName() + SpecCharEnum.BOTTOM_BAR.getValue() + ruleSeq;
            ruleCnName = ruleCnName.replace(" ", "");

            groupRuleRequest.setProjectId(project.getId());
            groupRuleRequest.setRuleGroupId(ruleGroup.getId());
            groupRuleRequest.setRuleGroupName(ruleGroup.getRuleGroupName());
            groupRuleRequest.setRuleName(ruleName);
            groupRuleRequest.setRuleCnName(ruleCnName);
            groupRuleRequest.setRuleType(request.getRuleType());
            groupRuleRequest.setRuleTemplateId(request.getRuleTemplateId());
            groupRuleRequest.setRuleDetail(request.getRuleDetail());
            groupRuleRequest.setExecutionParametersName(request.getExecutionParametersName());

            Rule rule = ruleDao.findByProjectAndRuleName(project, ruleName);
            if (rule != null) {
                groupRuleRequest.setRuleId(rule.getId());
                groupRuleRequest.setRuleGroupId(rule.getRuleGroup().getId());
                groupRuleRequest.setRuleGroupName(rule.getRuleGroup().getRuleGroupName());
            }

            setRuleMetricForAlarmConfig(checkObject, request.getCheckVariableList());
            setAlarmVariable(groupRuleRequest, request);

            DataSourceRequest dataSourceRequest = new DataSourceRequest();
            BeanUtils.copyProperties(checkObject, dataSourceRequest);
            groupRuleRequest.setDatasource(Arrays.asList(dataSourceRequest));

            // Set attrbutes from execution parameter.
            setByExecutionParam(executionParametersMap, groupRuleRequest);

            groupRuleRequestList.add(groupRuleRequest);

            ++ruleSeq;
        }

        return groupRuleRequestList;
    }

    private void setRuleMetricForAlarmConfig(AddBatchRuleRequest.AddBatchCheckObjectRequest checkObject, List<FileAlarmConfigRequest> checkVariableList) {
        if (CollectionUtils.isNotEmpty(checkVariableList)) {
            FileAlarmConfigRequest fileAlarmConfigRequest = checkVariableList.get(0);
            fileAlarmConfigRequest.setRuleMetricEnCode(checkObject.getRuleMetricEnCode());
            fileAlarmConfigRequest.setRuleMetricName(checkObject.getRuleMetricName());
        }
    }

    private void setAlarmVariable(ModifyGroupRuleRequest groupRuleRequest, AddBatchRuleRequest request) {
        if (RuleTypeEnum.FILE_TEMPLATE_RULE.getCode().equals(request.getRuleType())) {
            groupRuleRequest.setFileAlarmVariable(request.getCheckVariableList());
        } else {
            List<AlarmConfigRequest> alarmConfigRequestList = request.getCheckVariableList().stream().map(checkVariable -> {
                AlarmConfigRequest alarmConfigRequest = new AlarmConfigRequest();
                BeanUtils.copyProperties(checkVariable, alarmConfigRequest);
                return alarmConfigRequest;
            }).collect(Collectors.toList());
            groupRuleRequest.setAlarmVariable(alarmConfigRequestList);
        }
        groupRuleRequest.setTemplateArgumentRequests(request.getTemplateArgumentRequests());
        groupRuleRequest.setAlarm(true);
    }

    private void setByExecutionParam(Map<String, ExecutionParameters> map, AddGroupRuleRequest request) {
        ExecutionParameters executionParameters = map.get(request.getExecutionParametersName());
        request.setStaticStartupParam(executionParameters.getStaticStartupParam());
        request.setAbortOnFailure(executionParameters.getAbortOnFailure());
        request.setAlert(executionParameters.getAlert());
        request.setAlertLevel(executionParameters.getAlertLevel());
        request.setAlertReceiver(executionParameters.getAlertReceiver());
        request.setAbortOnFailure(executionParameters.getAbortOnFailure());
        request.setAbnormalProxyUser(executionParameters.getAbnormalProxyUser());
        request.setAbnormalDatabase(executionParameters.getAbnormalDatabase());
        request.setAbnormalCluster(executionParameters.getCluster());
        request.setDeleteFailCheckResult(executionParameters.getDeleteFailCheckResult());

        if (CollectionUtils.isNotEmpty(request.getAlarmVariable())) {
            request.setAlarmVariable(request.getAlarmVariable().stream().map((alarmConfigRequest) -> {
                alarmConfigRequest.setUploadRuleMetricValue(executionParameters.getUploadRuleMetricValue());
                alarmConfigRequest.setUploadAbnormalValue(executionParameters.getUploadAbnormalValue());
                return alarmConfigRequest;
            }).collect(Collectors.toList()));
        }
        if (CollectionUtils.isNotEmpty(request.getFileAlarmVariable())) {
            request.setFileAlarmVariable(request.getFileAlarmVariable().stream().map((alarmConfigRequest) -> {
                alarmConfigRequest.setUploadRuleMetricValue(executionParameters.getUploadRuleMetricValue());
                alarmConfigRequest.setUploadAbnormalValue(executionParameters.getUploadAbnormalValue());
                return alarmConfigRequest;
            }).collect(Collectors.toList()));
        }
    }

    private void setByExecutionParam(Map<String, ExecutionParameters> map, ModifyGroupRuleRequest request) {
        ExecutionParameters executionParameters = map.get(request.getExecutionParametersName());
        request.setStaticStartupParam(executionParameters.getStaticStartupParam());
        request.setAbortOnFailure(executionParameters.getAbortOnFailure());
        request.setAlert(executionParameters.getAlert());
        request.setAlertLevel(executionParameters.getAlertLevel());
        request.setAlertReceiver(executionParameters.getAlertReceiver());
        request.setAbortOnFailure(executionParameters.getAbortOnFailure());
        request.setAbnormalProxyUser(executionParameters.getAbnormalProxyUser());
        request.setAbnormalDatabase(executionParameters.getAbnormalDatabase());
        request.setAbnormalCluster(executionParameters.getCluster());
        request.setDeleteFailCheckResult(executionParameters.getDeleteFailCheckResult());

        if (CollectionUtils.isNotEmpty(request.getAlarmVariable())) {
            request.setAlarmVariable(request.getAlarmVariable().stream().map((alarmConfigRequest) -> {
                alarmConfigRequest.setUploadRuleMetricValue(executionParameters.getUploadRuleMetricValue());
                alarmConfigRequest.setUploadAbnormalValue(executionParameters.getUploadAbnormalValue());
                return alarmConfigRequest;
            }).collect(Collectors.toList()));
        }
        if (CollectionUtils.isNotEmpty(request.getFileAlarmVariable())) {
            request.setFileAlarmVariable(request.getFileAlarmVariable().stream().map((alarmConfigRequest) -> {
                alarmConfigRequest.setUploadRuleMetricValue(executionParameters.getUploadRuleMetricValue());
                alarmConfigRequest.setUploadAbnormalValue(executionParameters.getUploadAbnormalValue());
                return alarmConfigRequest;
            }).collect(Collectors.toList()));
        }
    }

    private List<DataSourceRequest> getDataSourceRequst(Set<RuleDataSource> ruleDataSources) {
        List<DataSourceRequest> dataSourceRequests = new ArrayList<>(ruleDataSources.size());

        for (RuleDataSource ruleDataSource : ruleDataSources) {
            DataSourceRequest dataSourceRequest = new DataSourceRequest(ruleDataSource);
            dataSourceRequests.add(dataSourceRequest);
        }
        return dataSourceRequests;
    }
}
