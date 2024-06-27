package com.webank.wedatasphere.qualitis.report.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Sets;
import com.webank.wedatasphere.qualitis.client.MailClient;
import com.webank.wedatasphere.qualitis.config.EsbSdkConfig;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.constant.OperateTypeEnum;
import com.webank.wedatasphere.qualitis.project.constant.ProjectUserPermissionEnum;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.dao.ProjectUserDao;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.entity.ProjectUser;
import com.webank.wedatasphere.qualitis.project.service.ProjectEventService;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.report.constant.ExecutionFrequencyEnum;
import com.webank.wedatasphere.qualitis.report.dao.SubscribeOperateReportDao;
import com.webank.wedatasphere.qualitis.report.dao.SubscribeOperateReportProjectsDao;
import com.webank.wedatasphere.qualitis.report.dao.SubscriptionRecordDao;
import com.webank.wedatasphere.qualitis.report.entity.SubscribeOperateReport;
import com.webank.wedatasphere.qualitis.report.entity.SubscribeOperateReportProjects;
import com.webank.wedatasphere.qualitis.report.entity.SubscriptionRecord;
import com.webank.wedatasphere.qualitis.report.request.OperateReportQueryRequest;
import com.webank.wedatasphere.qualitis.report.request.SubscribeOperateReportRequest;
import com.webank.wedatasphere.qualitis.report.response.SubscribeOperateReportResponse;
import com.webank.wedatasphere.qualitis.report.service.SubscribeOperateReportService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author v_gaojiedeng@webank.com
 */
@Service
public class SubscribeOperateReportServiceImpl implements SubscribeOperateReportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscribeOperateReportServiceImpl.class);
    private HttpServletRequest httpServletRequest;

    public SubscribeOperateReportServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private SubscribeOperateReportDao subscribeOperateReportDao;
    @Autowired
    private SubscribeOperateReportProjectsDao subscribeOperateReportProjectsDao;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ProjectUserDao projectUserDao;
    @Autowired
    private ProjectEventService projectEventService;
    @Autowired
    private EsbSdkConfig esbSdkConfig;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private SubscriptionRecordDao subscriptionRecordDao;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String OPERATION_PREFIX = "hduser";
    private static final String RESPONSE_CODE = "Code";

    private Cache<String, Object> hrInfoCache = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.DAYS)
            .expireAfterWrite(1, TimeUnit.DAYS)
            .build();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse<SubscribeOperateReportResponse> add(SubscribeOperateReportRequest request) throws Exception {
        SubscribeOperateReportRequest.checkRequest(request, false);
        SubscribeOperateReport subscribeOperateReport = setBasicInfoAndCheckParameter(request, null);

        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&ADD_SUBSCRIBE_OPERATE_REPORT_SUCCESSFULLY}", new SubscribeOperateReportResponse(subscribeOperateReport));
    }

    public static Boolean isObjectNotEmpty(Object obj) {
        String str = ObjectUtils.toString(obj, "");
        return StringUtils.isNotBlank(str);
    }

    private List<Map<String, Object>> checkHrCacheExists() throws Exception {
        List<Map<String, Object>> data = (List<Map<String, Object>>) hrInfoCache.getIfPresent("Data");

        if (isObjectNotEmpty(data)) {
            LOGGER.debug("Getting hr message from local cache, key: {}", data);
        } else {
            String hrMessage = mailClient.getHrMessage();
            Map<String, Object> msgMap = objectMapper.readValue(hrMessage, Map.class);
            if (!msgMap.isEmpty() && Integer.parseInt(msgMap.get(RESPONSE_CODE).toString()) != 0) {
                throw new UnExpectedRequestException("Getting hr message Failure,Return Code: " + Integer.parseInt(msgMap.get("Code").toString()));
            }
            List<Map<String, Object>> content = (List<Map<String, Object>>) (msgMap.get("Data"));
            hrInfoCache.put("Data", content);
            return content;
        }
        return data;
    }


    private SubscribeOperateReport setBasicInfoAndCheckParameter(SubscribeOperateReportRequest request, SubscribeOperateReport report) throws Exception {
        SubscribeOperateReport subscribeOperateReport;
        String loginUser = HttpUtils.getUserName(httpServletRequest);

        checkRealUser(loginUser, false);
        if (null == report) {
            subscribeOperateReport = new SubscribeOperateReport();
            subscribeOperateReport.setCreateUser(loginUser);
            subscribeOperateReport.setCreateTime(QualitisConstants.PRINT_TIME_FORMAT.format(new Date()));
        } else {
            subscribeOperateReport = report;
            subscribeOperateReport.setModifyUser(loginUser);
            subscribeOperateReport.setModifyTime(QualitisConstants.PRINT_TIME_FORMAT.format(new Date()));
        }

        String[] segmentation = request.getReceiver().split(SpecCharEnum.COMMA.getValue());
        //接收人去重，因前端是输入框
        Set<String> list = Sets.newHashSet();
        if (segmentation.length > 0) {
            list = Arrays.asList(segmentation).stream().collect(Collectors.toSet());
        }
        //从本地缓存里拿人员信息
        List<Map<String, Object>> listMap = checkHrCacheExists();

        for (String segment : list) {
            checkRealUser(segment, true);
            Map<String, Object> englishName = listMap.stream().filter(item -> segment.equals(item.get("ENGLISH_NAME").toString())).findAny().orElse(null);
            if (englishName.isEmpty()) {
                throw new UnExpectedRequestException("The HR system cannot find the recipient information,recipient is: " + segment);
            }
            String ifManager = null;
            if (!englishName.isEmpty() && englishName.get("IF_MANAGER") != null) {
                ifManager = (englishName.get("IF_MANAGER")).toString();
            }
            if (StringUtils.isBlank(ifManager)) {
                throw new UnExpectedRequestException(segment + " is the recipient missing a leader attribute");
            }

            if ("Y".equals(ifManager)) {
                throw new UnExpectedRequestException(segment + " is a leader and cannot send emails");
            }

        }

        subscribeOperateReport.setReceiver(CollectionUtils.isNotEmpty(list) ? list.stream().map(String::valueOf).collect(Collectors.joining(SpecCharEnum.COMMA.getValue())) : "");

        Set<SubscribeOperateReportProjects> subscribeOperateReportProjectsSet = Sets.newHashSet();

        for (Long projectId : request.getProjectIds()) {
            Project project = projectDao.findById(projectId);
            if (project == null) {
                throw new UnExpectedRequestException("Project {&DOES_NOT_EXIST}, project id is +" + projectId);
            }

            List<ProjectUser> projectUsers = projectUserDao.findByProject(project);
            List<String> projectUserNames = projectUsers.stream().map(ProjectUser::getUserName).collect(Collectors.toList());
            checkPermissionByUser(loginUser, projectId, projectUsers, projectUserNames);

            for (String receiver : segmentation) {
                checkPermissionByUser(receiver, projectId, projectUsers, projectUserNames);
            }

            SubscribeOperateReport operateReport = subscribeOperateReportDao.findMatchOperateReport(projectId, request.getExecutionFrequency());
            if ((request.getId() == null && operateReport != null) || (request.getId() != null && operateReport != null && !operateReport.getId().equals(request.getId()))) {
                throw new UnExpectedRequestException(String.format("[%s-%s-%s] 订阅已存在，不可重复订阅", project.getName(), operateReport.getCreateUser(),
                        ExecutionFrequencyEnum.getExecutionFrequencyName(request.getExecutionFrequency())));
            }

            SubscribeOperateReportProjects subscribeOperateReportProjects = new SubscribeOperateReportProjects();
            subscribeOperateReportProjects.setProject(project);
            subscribeOperateReportProjectsSet.add(subscribeOperateReportProjects);

            //项目的操作日志
            if (null == report) {
                projectEventService.record(project, loginUser, "订阅项目接收人：" + request.getReceiver(), OperateTypeEnum.SUBSCRIBE_PROJECT);
            } else {
                projectEventService.record(project, loginUser, "订阅项目接收人：" + request.getReceiver(), OperateTypeEnum.MODIFY_SUBSCRIBE_PROJECT);
            }

        }

        //编辑时，先删除关联表后新增
        if (report != null) {
            subscribeOperateReportProjectsDao.deleteBySubscribeOperateReport(subscribeOperateReport);
        }
        subscribeOperateReport.setExecutionFrequency(request.getExecutionFrequency());

        SubscribeOperateReport saveSubscribeOperateReport = subscribeOperateReportDao.save(subscribeOperateReport);
        for (SubscribeOperateReportProjects subscribeOperateReportProjects : subscribeOperateReportProjectsSet) {
            subscribeOperateReportProjects.setSubscribeOperateReport(saveSubscribeOperateReport);
        }
        subscribeOperateReportProjectsDao.saveAll(subscribeOperateReportProjectsSet.stream().collect(Collectors.toList()));

        subscribeOperateReport.setSubscribeOperateReportProjectsSet(subscribeOperateReportProjectsSet);
        return subscribeOperateReport;
    }

    /**
     * 验证登录人或接收人是否有项目权限  permission  1Admin 2编辑 3执行 4查看
     *
     * @param user
     * @param projectId
     * @param projectUsers
     * @param projectUserNames
     * @throws PermissionDeniedRequestException
     */
    private void checkPermissionByUser(String user, Long projectId, List<ProjectUser> projectUsers, List<String> projectUserNames) throws PermissionDeniedRequestException {
        if (!projectUserNames.contains(user)) {
            throw new PermissionDeniedRequestException(user + " {&ACCESS_USER_WITHOUT_PROJECT_PERMISSION}", 403);
        }
        boolean modifyPermission = projectUsers.stream().filter(item -> item.getUserName().equals(user)).anyMatch(os -> os.getPermission().equals(ProjectUserPermissionEnum.DEVELOPER.getCode()));
        boolean executePermission = projectUsers.stream().filter(item -> item.getUserName().equals(user)).anyMatch(os -> os.getPermission().equals(ProjectUserPermissionEnum.OPERATOR.getCode()));
        boolean adminPermission = projectUsers.stream().filter(item -> item.getUserName().equals(user)).anyMatch(os -> os.getPermission().equals(ProjectUserPermissionEnum.CREATOR.getCode()));
        if (!modifyPermission && !executePermission && !adminPermission) {
            throw new PermissionDeniedRequestException("{&ACCESS_USER_WITHOUT_PROJECT_PERMISSION}, project id and user is:" + projectId + "-----" + user);
        }
    }

    /**
     * 验证登录人、接收人是否为实名用户,是否在白、黑名单里
     *
     * @param accessUser
     * @param flag
     * @throws UnExpectedRequestException
     */
    private void checkRealUser(String accessUser, Boolean flag) throws UnExpectedRequestException {
        User user = userDao.findByUsername(accessUser);

        if (user == null) {
            throw new UnExpectedRequestException(String.format("{&FAILED_TO_FIND_USER} %s", accessUser));
        } else if (user.getUsername().startsWith(OPERATION_PREFIX)) {
            throw new UnExpectedRequestException(String.format("%s {&NOT_A_REAL_NAME_USER}", accessUser));
        }
        if (flag && StringUtils.isNotBlank(esbSdkConfig.getEmailWhiteList())) {
            List<String> whiteList = Arrays.asList(esbSdkConfig.getEmailWhiteList().split(SpecCharEnum.COMMA.getValue()));
            boolean exist = whiteList.stream().anyMatch(item -> item.equals(accessUser));
            if (!exist) {
                throw new UnExpectedRequestException(String.format("%s {&NOT_ON_THE_CONFIGURED_LIST}", accessUser));
            }
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse<SubscribeOperateReportResponse> delete(Long subscribeOperateReportId) throws UnExpectedRequestException, PermissionDeniedRequestException {
        SubscribeOperateReport subscribeOperateReport = subscribeOperateReportDao.findById(subscribeOperateReportId);
        if (subscribeOperateReport == null) {
            throw new UnExpectedRequestException("subscribe operate report {&DOES_NOT_EXIST}");
        }

        String loginUser = HttpUtils.getUserName(httpServletRequest);
        LOGGER.info(loginUser + " start to get subscribe operate report. subscribe operate report id: " + subscribeOperateReportId);

        // Check if user has permission.
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.BUSSMAN.getCode());
        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
        for (SubscribeOperateReportProjects subscribeOperateReportProjects : subscribeOperateReport.getSubscribeOperateReportProjectsSet()) {
            projectService.checkProjectPermission(subscribeOperateReportProjects.getProject(), loginUser, permissions);
            projectEventService.record(subscribeOperateReportProjects.getProject(), loginUser, "订阅项目接收人：" + subscribeOperateReport.getReceiver(), OperateTypeEnum.DELETE_SUBSCRIBE_PROJECT);
        }

        for (SubscribeOperateReportProjects subscribeOperateReportProjects : subscribeOperateReport.getSubscribeOperateReportProjectsSet()) {
            SubscriptionRecord subscriptionRecord = subscriptionRecordDao.findMatchProjectAndFrequency(subscribeOperateReportProjects.getProject().getId(), subscribeOperateReport.getExecutionFrequency());
            if (null != subscriptionRecord) {
                subscriptionRecordDao.delete(subscriptionRecord);
            }
        }
        subscribeOperateReportDao.delete(subscribeOperateReport);
        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&DELETE_SUBSCRIBE_OPERATE_REPORT_SUCCESSFULLY}", null);
    }

    @Override
    public List<Map<String, Object>> getAllExecutionFrequencyEnum() {
        return ExecutionFrequencyEnum.getExecutionFrequencyEnumList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse<SubscribeOperateReportResponse> modify(SubscribeOperateReportRequest request) throws Exception {
        SubscribeOperateReportRequest.checkRequest(request, true);
        SubscribeOperateReport subscribeOperateReport = subscribeOperateReportDao.findById(request.getId());
        if (subscribeOperateReport == null) {
            throw new UnExpectedRequestException("subscribe operate report {&DOES_NOT_EXIST}");
        }

        SubscribeOperateReport saveSubscribeOperateReport = setBasicInfoAndCheckParameter(request, subscribeOperateReport);

        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&MODIFY_SUBSCRIBE_OPERATE_REPORT_SUCCESSFULLY}", new SubscribeOperateReportResponse(saveSubscribeOperateReport));
    }

    @Override
    public GeneralResponse<SubscribeOperateReportResponse> get(Long subscribeOperateReportId) throws UnExpectedRequestException, PermissionDeniedRequestException {
        SubscribeOperateReport subscribeOperateReport = subscribeOperateReportDao.findById(subscribeOperateReportId);
        if (subscribeOperateReport == null) {
            throw new UnExpectedRequestException("subscribe operate report {&DOES_NOT_EXIST}");
        }

        String loginUser = HttpUtils.getUserName(httpServletRequest);
        LOGGER.info(loginUser + " start to get subscribe operate report. subscribe operate report id: " + subscribeOperateReportId);

        // Check if user has permission.
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.BUSSMAN.getCode());
        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
        for (SubscribeOperateReportProjects subscribeOperateReportProjects : subscribeOperateReport.getSubscribeOperateReportProjectsSet()) {
            projectService.checkProjectPermission(subscribeOperateReportProjects.getProject(), loginUser, permissions);
        }

        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_SUBSCRIBE_OPERATE_REPORT_DETAIL_SUCCESSFULLY}", new SubscribeOperateReportResponse(subscribeOperateReport));
    }

    @Override
    public GeneralResponse getSubscribeOperateReportQuery(OperateReportQueryRequest request) throws UnExpectedRequestException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);

        Page<SubscribeOperateReport> subscribeOperateReportPage = subscribeOperateReportDao.subscribeOperateReportQuery(request.getProjectName(), request.getReceiver(), request.getProjectType(),loginUser, request.getPage(), request.getSize());

        if (subscribeOperateReportPage.getSize() <= 0) {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_SUBSCRIBE_OPERATE_REPORT_QUERY_SUCCESSFULLY}", new SubscribeOperateReportResponse());
        }

        GetAllResponse<SubscribeOperateReportResponse> response = new GetAllResponse<>();
        List<SubscribeOperateReportResponse> subscribeOperateReportResponses = new ArrayList<>();
        for (SubscribeOperateReport subscribeOperateReport : subscribeOperateReportPage) {
            subscribeOperateReportResponses.add(new SubscribeOperateReportResponse(subscribeOperateReport));
        }

        response.setData(subscribeOperateReportResponses);
        response.setTotal(Long.valueOf(subscribeOperateReportPage.getTotalElements()).intValue());
        LOGGER.info("Succeed to get Subscribe Operate Report. response: {}", response);
        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_SUBSCRIBE_OPERATE_REPORT_QUERY_SUCCESSFULLY}", response);
    }

}
