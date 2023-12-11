package com.webank.wedatasphere.qualitis.checkalert.service.impl;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.qualitis.checkalert.dao.CheckAlertDao;
import com.webank.wedatasphere.qualitis.checkalert.entity.CheckAlert;
import com.webank.wedatasphere.qualitis.checkalert.request.CheckAlertRequest;
import com.webank.wedatasphere.qualitis.checkalert.request.QueryCheckAlertRequest;
import com.webank.wedatasphere.qualitis.checkalert.request.QueryWorkFlowRequest;
import com.webank.wedatasphere.qualitis.checkalert.response.CheckAlertResponse;
import com.webank.wedatasphere.qualitis.checkalert.service.CheckAlertService;
import com.webank.wedatasphere.qualitis.config.LinkisConfig;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.response.column.ColumnInfoDetail;
import com.webank.wedatasphere.qualitis.project.constant.ProjectUserPermissionEnum;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.rule.constant.GroupTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.service.RuleGroupService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author allenzhou@webank.com
 * @date 2023/3/1 11:43
 */
@Service
public class CheckAlertServiceImpl implements CheckAlertService {
    private HttpServletRequest httpServletRequest;

    public CheckAlertServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Autowired
    private LinkisConfig linkisConfig;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private CheckAlertDao checkAlertDao;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private MetaDataClient metaDataClient;

    @Autowired
    private RuleGroupService ruleGroupService;

    @Autowired
    private RuleDao ruleDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckAlertServiceImpl.class);

    public static final int WORKFLOW_LIST_TYPE = 1;

    public static final int DATA_ALARM_LIST_TYPE = 2;

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<CheckAlertResponse> add(CheckAlertRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        CheckAlertRequest.checkRequest(request, false);
        // Check if it is duplicated with topic, workflow name version Space and Project.
        CheckAlert checkAlertExists = checkAlertDao.findByTopicAndWorkflowInfo(request.getTopic(), request.getWorkFlowName(), request.getWorkFlowVersion(), request.getWorkFlowSpace(), request.getProjectId());

        if (checkAlertExists != null) {
            throw new UnExpectedRequestException("Check alert topic {&ALREADY_EXIST}");
        }

        Project projectInDb = projectDao.findById(request.getProjectId());
        if (projectInDb == null) {
            throw new UnExpectedRequestException("Project {&DOES_NOT_EXIST}, maybe not synchronized with project appconn!");
        }

        String loginUser = HttpUtils.getUserName(httpServletRequest);
        LOGGER.info(loginUser + " start to add check alert. Request: " + request.toString());

        // Check if user has permission.
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.BUSSMAN.getCode());
        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
        projectService.checkProjectPermission(projectInDb, loginUser, permissions);

        CheckAlert checkAlert = new CheckAlert();

        checkAlert.setProject(projectInDb);
        basicInfo(loginUser, request, checkAlert, false);
        checkAlert.setRuleGroup(ruleGroupService.generate(request.getRuleGroupId(), "", projectInDb, GroupTypeEnum.CHECK_ALERT_GROUP.getCode()));

        CheckAlert savedCheckalert = checkAlertDao.save(checkAlert);

        LOGGER.info("Created check alert successfully. Check alert info: [{}]", savedCheckalert.toString());
        return new GeneralResponse<>("200", "{&ADD_RULE_SUCCESSFULLY}", new CheckAlertResponse(savedCheckalert));
    }

    private void basicInfo(String loginUser, CheckAlertRequest request, CheckAlert checkAlert, boolean modify) {
        checkAlert.setTopic(request.getTopic());
        checkAlert.setInfoReceiver(request.getInfoReceiver());
        checkAlert.setMajorReceiver(request.getMajorReceiver());
        checkAlert.setAlertTable(request.getAlertTable());
        checkAlert.setFilter(request.getFilter());

        checkAlert.setAlertCol(request.getAlertCol());
        checkAlert.setMajorAlertCol(request.getMajorAlertCol());
        checkAlert.setContentCols(request.getContentCols());

        if (modify) {
            checkAlert.setModifyUser(loginUser);
            checkAlert.setModifyTime(QualitisConstants.PRINT_TIME_FORMAT.format(new Date()));
        } else {
            checkAlert.setCreateUser(loginUser);
            checkAlert.setCreateTime(QualitisConstants.PRINT_TIME_FORMAT.format(new Date()));
        }

        checkAlert.setNodeName(request.getNodeName());
        checkAlert.setWorkFlowName(request.getWorkFlowName());
        checkAlert.setWorkFlowVersion(request.getWorkFlowVersion());
        checkAlert.setWorkFlowSpace(request.getWorkFlowSpace());
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<CheckAlertResponse> delete(Long checkAlertId) throws UnExpectedRequestException, PermissionDeniedRequestException {
        CheckAlert checkAlertInDb = checkAlertDao.findById(checkAlertId);

        if (checkAlertInDb == null) {
            throw new UnExpectedRequestException("Check alert {&DOES_NOT_EXIST}");
        }

        String loginUser = HttpUtils.getUserName(httpServletRequest);
        LOGGER.info(loginUser + " start to get check alert. Check alert ID: " + checkAlertId);

        // Check if user has permission.
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.BUSSMAN.getCode());
        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
        projectService.checkProjectPermission(checkAlertInDb.getProject(), loginUser, permissions);

        checkAlertDao.delete(checkAlertInDb);
        return new GeneralResponse<>("200", "{&DELETE_RULE_SUCCESSFULLY}", null);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<CheckAlertResponse> modify(CheckAlertRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        CheckAlertRequest.checkRequest(request, true);

        // Check if it is duplicated with topic, workflow name version Space and Project.
        CheckAlert checkAlertExists = checkAlertDao.findByTopicAndWorkflowInfo(request.getTopic(), request.getWorkFlowName(), request.getWorkFlowVersion(), request.getWorkFlowSpace(), request.getProjectId());

        CheckAlert checkAlertInDb = checkAlertDao.findById(request.getId());

        if (checkAlertInDb == null) {
            throw new UnExpectedRequestException("Check alert {&DOES_NOT_EXIST}");
        }
        if (checkAlertExists != null && !checkAlertExists.getId().equals(checkAlertInDb.getId())) {
            throw new UnExpectedRequestException("Check alert topic {&ALREADY_EXIST}");
        }

        Project projectInDb = projectDao.findById(request.getProjectId());
        if (projectInDb == null) {
            throw new UnExpectedRequestException("Project {&DOES_NOT_EXIST}, maybe not synchronized with project appconn!");
        }

        String loginUser = HttpUtils.getUserName(httpServletRequest);
        LOGGER.info(loginUser + " start to modify check alert. Request: " + request.toString());

        // Check if user has permission.
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.BUSSMAN.getCode());
        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
        projectService.checkProjectPermission(projectInDb, loginUser, permissions);


        checkAlertInDb.setProject(projectInDb);
        basicInfo(loginUser, request, checkAlertInDb, true);
        checkAlertInDb.setRuleGroup(ruleGroupService.generate(request.getRuleGroupId(), "", projectInDb, GroupTypeEnum.CHECK_ALERT_GROUP.getCode()));

        CheckAlert savedCheckalert = checkAlertDao.save(checkAlertInDb);

        LOGGER.info("Modified check alert successfully. Check alert info: [{}]", savedCheckalert.toString());
        return new GeneralResponse<>("200", "{&MODIFY_RULE_SUCCESSFULLY}", new CheckAlertResponse(savedCheckalert));
    }

    @Override
    public GeneralResponse<CheckAlertResponse> get(Long checkAlertId) throws UnExpectedRequestException, PermissionDeniedRequestException {
        CheckAlert checkAlertInDb = checkAlertDao.findById(checkAlertId);

        if (checkAlertInDb == null) {
            throw new UnExpectedRequestException("Check alert {&DOES_NOT_EXIST}");
        }

        String loginUser = HttpUtils.getUserName(httpServletRequest);
        LOGGER.info(loginUser + " start to get check alert. Check alert ID: " + checkAlertId);

        // Check if user has permission.
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.BUSSMAN.getCode());
        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
        projectService.checkProjectPermission(checkAlertInDb.getProject(), loginUser, permissions);

        return new GeneralResponse<>("200", "{&GET_RULE_DETAIL_SUCCESSFULLY}", new CheckAlertResponse(checkAlertInDb));
    }

    @Override
    public GeneralResponse<Object> checkDatasource(String alertTable, String alertCol, String majorAlertCol, String contentCols) throws Exception {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        String[] dbAndTable = alertTable.split(SpecCharEnum.PERIOD.getValue());

        if (dbAndTable.length != QualitisConstants.LENGTH_TWO) {
            throw new UnExpectedRequestException("Alert table is illegal.");
        }
        List<ColumnInfoDetail> columnInfoDetails = metaDataClient.getColumnInfo(linkisConfig.getBdapCheckAlertCluster(), dbAndTable[QualitisConstants.COMMON_ARRAY_INDEX_O], dbAndTable[QualitisConstants.COMMON_ARRAY_INDEX_1], loginUser);

        if (CollectionUtils.isEmpty(columnInfoDetails)) {
            throw new MetaDataAcquireFailedException("Alert table has no columns.");
        }
        Set<String> columnNames = columnInfoDetails.stream().map(columnInfoDetail -> columnInfoDetail.getFieldName()).collect(Collectors.toSet());

        if (StringUtils.isNotEmpty(alertCol) && !columnNames.contains(alertCol)) {
            throw new MetaDataAcquireFailedException("Alert column does not exist.");
        }

        if (StringUtils.isNotEmpty(majorAlertCol) && !columnNames.contains(majorAlertCol)) {
            throw new MetaDataAcquireFailedException("Major alert column does not exist.");
        }

        if (StringUtils.isNotEmpty(contentCols)) {
            String[] pairs = contentCols.split(SpecCharEnum.DIVIDER.getValue());

            for (String pair : pairs) {
                String[] temp = pair.split(SpecCharEnum.COLON.getValue());
                String realColName = temp[QualitisConstants.COMMON_ARRAY_INDEX_O];
                if (!columnNames.contains(realColName)) {
                    throw new MetaDataAcquireFailedException("Content column does not exist.");
                }
            }
        }

        return new GeneralResponse<>("200", "{&CHECK_ALERT_TABLE_IS_CORRECT}", null);
    }

    @Override
    public GeneralResponse getCheckAlertQuery(QueryCheckAlertRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        Project projectInDb = projectService.checkProjectExistence(request.getProjectId(),
                loginUser);
        // Check if user has permission get project.
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.BUSSMAN.getCode());
        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
        projectService.checkProjectPermission(projectInDb, loginUser, permissions);

        Page<CheckAlert> checkAlertPage = checkAlertDao.checkAlertQuery(request.getTopic(), request.getAlertTable(), request.getWorkFlowSpace(), request.getWorkFlowProject(),
                request.getWorkFlowName(), request.getNodeName(), request.getCreateUser(), request.getModifyUser(), request.getStartCreateTime(),
                request.getEndCreateTime(), request.getStartModifyTime(), request.getEndModifyTime(), request.getProjectId(), request.getPage(), request.getSize());

        if (checkAlertPage.getSize() <= 0) {
            return new GeneralResponse<>("200", "{&GET_CHECK_ALERT_QUERY_SUCCESSFULLY}", new CheckAlertResponse());
        }

        GetAllResponse<CheckAlertResponse> response = new GetAllResponse<>();
        List<CheckAlertResponse> checkAlertResponse = new ArrayList<>();
        for (CheckAlert checkAlert : checkAlertPage) {
            checkAlertResponse.add(new CheckAlertResponse(checkAlert));
        }

        response.setData(checkAlertResponse);
        response.setTotal(Long.valueOf(checkAlertPage.getTotalElements()).intValue());
        LOGGER.info("Succeed to get CheckAlert. response: {}", response);
        return new GeneralResponse<>("200", "{&GET_CHECK_ALERT_QUERY_SUCCESSFULLY}", response);
    }

    @Override
    public Map<String, Object> getDeduplicationField(QueryWorkFlowRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request.getProjectId(), "project Id");
        CommonChecker.checkObject(request.getType(), "type");
        List<Map<String, Object>> deduplicationField = Lists.newArrayList();
        if (request.getType().equals(WORKFLOW_LIST_TYPE)) {
            deduplicationField = ruleDao.findWorkFlowFiled(request.getProjectId());
        } else if (request.getType().equals(DATA_ALARM_LIST_TYPE)) {
            deduplicationField = checkAlertDao.getDeduplicationField(request.getProjectId());
        }

        List<String> workFlowSpace = Lists.newArrayList();
        List<String> workFlowProject = Lists.newArrayList();
        List<String> workFlowName = Lists.newArrayList();
        List<String> nodeName = Lists.newArrayList();
        List<String> alertTable = Lists.newArrayList();
        for (Map<String, Object> map : deduplicationField) {
            for (Map.Entry<String, Object> m : map.entrySet()) {
                if (Objects.nonNull(m.getKey()) && "workFlowSpace".equals(m.getKey().toString()) && StringUtils.isNotBlank(m.getValue().toString())) {
                    workFlowSpace.add(m.getValue().toString());
                } else if (Objects.nonNull(m.getKey()) && "workFlowProject".equals(m.getKey().toString()) && StringUtils.isNotBlank(m.getValue().toString())) {
                    workFlowProject.add(m.getValue().toString());
                } else if (Objects.nonNull(m.getKey()) && "workFlowName".equals(m.getKey().toString()) && StringUtils.isNotBlank(m.getValue().toString())) {
                    workFlowName.add(m.getValue().toString());
                } else if (Objects.nonNull(m.getKey()) && "nodeName".equals(m.getKey().toString()) && StringUtils.isNotBlank(m.getValue().toString())) {
                    nodeName.add(m.getValue().toString());
                } else if (Objects.nonNull(m.getKey()) && "alertTable".equals(m.getKey().toString()) && StringUtils.isNotBlank(m.getValue().toString())) {
                    alertTable.add(m.getValue().toString());
                }

            }
        }
        Map<String, Object> results = new HashMap<>(5);

        results.put("work_flow_space", CollectionUtils.isNotEmpty(workFlowSpace) ? workFlowSpace : Lists.newArrayList());
        results.put("work_flow_project", CollectionUtils.isNotEmpty(workFlowProject) ? workFlowProject : Lists.newArrayList());
        results.put("work_flow_name", CollectionUtils.isNotEmpty(workFlowName) ? workFlowName : Lists.newArrayList());
        results.put("node_name", CollectionUtils.isNotEmpty(nodeName) ? nodeName : Lists.newArrayList());
        results.put("alert_table", CollectionUtils.isNotEmpty(alertTable) ? alertTable : Lists.newArrayList());
        return results;
    }
}
