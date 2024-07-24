package com.webank.wedatasphere.qualitis.service.impl;

//import com.google.common.collect.Maps;
//import com.webank.wedatasphere.qualitis.constant.ImsRuleCalcTypeEnum;
//import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
//import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
//import com.webank.wedatasphere.qualitis.dao.CalcuUnitDao;
//import com.webank.wedatasphere.qualitis.dao.ImsMetricCollectDao;
//import com.webank.wedatasphere.qualitis.dao.ImsMetricSchedulerDao;
//import com.webank.wedatasphere.qualitis.dao.UserDao;
//import com.webank.wedatasphere.qualitis.dao.repository.AuthListRepository;
//import com.webank.wedatasphere.qualitis.dto.ImsMetricCollectDto;
//import com.webank.wedatasphere.qualitis.entity.*;
//import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
//import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
//import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
//import com.webank.wedatasphere.qualitis.metadata.response.column.ColumnInfoDetail;
//import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
//import com.webank.wedatasphere.qualitis.project.entity.Project;
//import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
//import com.webank.wedatasphere.qualitis.project.request.ParameterChecker;
//import com.webank.wedatasphere.qualitis.project.response.ProjectResponse;
//import com.webank.wedatasphere.qualitis.query.request.ExecutionParametersRequest;
//import com.webank.wedatasphere.qualitis.request.*;
//import com.webank.wedatasphere.qualitis.response.GeneralResponse;
//import com.webank.wedatasphere.qualitis.response.GetAllResponse;
//import com.webank.wedatasphere.qualitis.response.MetricCollectQueryResponse;
//import com.webank.wedatasphere.qualitis.response.MetricSchedulerDetailResponse;
//import com.webank.wedatasphere.qualitis.rule.constant.TemplateDataSourceTypeEnum;
//import com.webank.wedatasphere.qualitis.rule.dao.RuleTemplateDao;
//import com.webank.wedatasphere.qualitis.rule.entity.Template;
//import com.webank.wedatasphere.qualitis.rule.response.ExecutionParametersResponse;
//import com.webank.wedatasphere.qualitis.rule.service.ExecutionParametersService;
//import com.webank.wedatasphere.qualitis.scheduled.constant.ExecuteIntervalEnum;
//import com.webank.wedatasphere.qualitis.scheduled.util.CronUtil;
import com.webank.wedatasphere.qualitis.service.ImsRuleMetricCollectService;
//import com.webank.wedatasphere.qualitis.util.DateUtils;
//import com.webank.wedatasphere.qualitis.util.HttpUtils;
//import com.webank.wedatasphere.qualitis.util.SignUtil;
//import org.apache.commons.collections4.CollectionUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.client.RestTemplate;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.ws.rs.core.Context;
//import java.text.ParseException;
//import java.util.*;
//import java.util.function.Function;
//import java.util.stream.Collectors;

/**
 * @author v_minminghe@webank.com
 * @date 2024-05-14 10:28
 * @description
 */
@Service
public class ImsRuleMetricCollectServiceImpl implements ImsRuleMetricCollectService {

//    private static final Logger LOGGER = LoggerFactory.getLogger(ImsRuleMetricCollectServiceImpl.class);
//
//    @Autowired
//    private ImsMetricCollectDao imsMetricCollectDao;
//    @Autowired
//    private ExecutionParametersService executionParametersService;
//    @Autowired
//    private ProjectDao projectDao;
//    @Autowired
//    private ImsMetricSchedulerDao imsMetricSchedulerDao;
//    @Autowired
//    private RuleTemplateDao ruleTemplateDao;
//    @Autowired
//    private CalcuUnitDao calcuUnitDao;
//    @Autowired
//    private MetaDataClient metaDataClient;
//    @Autowired
//    private UserDao userDao;
//    @Autowired
//    private AuthListRepository authListRepository;
//    @Autowired
//    private RestTemplate restTemplate;
//
//    @Value("${metric.collector.host}")
//    private String collectorServerHost;
//
//    private final Integer DATA_TYPE_NUM = 1;
//
//    private HttpServletRequest httpServletRequest;
//
//    public ImsRuleMetricCollectServiceImpl(@Context HttpServletRequest httpServletRequest) {
//        this.httpServletRequest = httpServletRequest;
//    }
//
//    private List<String> NUMBER_TYPE_LIST = Arrays.asList("int", "bigint", "tinyint", "smallint", "decimal", "double", "float", "number", "date", "timestamp");
//
//
//    @Override
//    public List<String> getPartitionList(String clusterName, String db, String table) {
//        return imsMetricCollectDao.findPartitionList(clusterName, db, table);
//    }
//
//    @Override
//    public MetricSchedulerDetailResponse getSchedulerDetail(String db, String table, String partition) {
//        ImsMetricScheduler imsMetricScheduler = imsMetricSchedulerDao.findByPartition(db, table, partition);
//        if (imsMetricScheduler == null) {
//            return new MetricSchedulerDetailResponse();
//        }
//        return new MetricSchedulerDetailResponse(imsMetricScheduler);
//    }
//
//    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
//    @Override
//    public void createOrModifySchedulers(List<AddMetricSchedulerRequest> requests) throws UnExpectedRequestException, ParseException, IllegalAccessException {
//        List<ImsMetricScheduler> newImsMetricSchedulers = new ArrayList<>();
//        List<ImsMetricScheduler> imsMetricSchedulersInDb = new ArrayList<>();
//        for (AddMetricSchedulerRequest addMetricSchedulerRequest : requests) {
//            ParameterChecker.checkEmpty(addMetricSchedulerRequest);
//            String database = addMetricSchedulerRequest.getDatabase();
//            String table = addMetricSchedulerRequest.getTable();
//            List<AddMetricSchedulerRequest.SchedulerConfig> schedulerConfigs = addMetricSchedulerRequest.getSchedulerConfigs();
//            for (AddMetricSchedulerRequest.SchedulerConfig schedulerConfig : schedulerConfigs) {
//                String cron = CronUtil.createIntervalCron(ExecuteIntervalEnum.fromCode(schedulerConfig.getExecuteInterval()), schedulerConfig.getExecuteDateInInterval(), schedulerConfig.getExecuteTimeInDate());
//                ImsMetricScheduler metricSchedulerInDb = imsMetricSchedulerDao.findByPartition(database, table, schedulerConfig.getPartition());
//                if (metricSchedulerInDb != null) {
//                    metricSchedulerInDb.setExecFreq(cron);
//                    imsMetricSchedulersInDb.add(metricSchedulerInDb);
//                } else {
//                    ImsMetricScheduler imsMetricScheduler = new ImsMetricScheduler();
//                    imsMetricScheduler.setExecFreq(cron);
//                    imsMetricScheduler.setDbName(database);
//                    imsMetricScheduler.setTableName(table);
//                    imsMetricScheduler.setPartition(schedulerConfig.getPartition());
//                    newImsMetricSchedulers.add(imsMetricScheduler);
//                }
//            }
//        }
//
//        String collectorServerPath = "/qualitis/outer/api/v1/imsmetric/collect/scheduler/update";
//        String appId = QualitisConstants.DEFAULT_AUTH_APP_ID;
//        String nonce = "16895";
//        String timestamp = String.valueOf(System.currentTimeMillis());
//        AuthList authList = authListRepository.findByAppId(appId);
//        String signature = SignUtil.generateSignature(appId, authList.getAppToken(), nonce, timestamp);
//        collectorServerPath += "?app_id=" + appId + "&timestamp=" + timestamp + "&nonce=" + nonce + "&signature=" + signature;
//
//        if (CollectionUtils.isNotEmpty(imsMetricSchedulersInDb)) {
//            pushToCollectorServer(imsMetricSchedulersInDb, collectorServerHost + collectorServerPath);
//        }
//        if (CollectionUtils.isNotEmpty(newImsMetricSchedulers)) {
//            pushToCollectorServer(newImsMetricSchedulers, collectorServerHost + collectorServerPath);
//        }
//    }
//
//    private void pushToCollectorServer(List<ImsMetricScheduler> imsMetricSchedulers, String collectorServerUrl) throws UnExpectedRequestException {
//        for (ImsMetricScheduler imsMetricScheduler : imsMetricSchedulers) {
//            Map<String, String> paramMap = new HashMap<>();
//            paramMap.put("db_name", imsMetricScheduler.getDbName());
//            paramMap.put("table_name", imsMetricScheduler.getTableName());
//            paramMap.put("partition", imsMetricScheduler.getPartition());
//            paramMap.put("cron_expression", imsMetricScheduler.getExecFreq());
//            try {
//                GeneralResponse generalResponse = restTemplate.postForObject(collectorServerUrl, paramMap, GeneralResponse.class);
//                if (!ResponseStatusConstants.OK.equals(generalResponse.getCode())) {
//                    LOGGER.error("Failed to create scheduler from collector server.");
//                    throw new UnExpectedRequestException("Failed to create scheduler from collector server.");
//                }
//            } catch (Exception e) {
//                String errMsg = String.format("Failed to create scheduler. db: [%s], table: [%s], partition: [%s]", imsMetricScheduler.getDbName(), imsMetricScheduler.getTableName(), imsMetricScheduler.getPartition());
//                LOGGER.error("Failed to create scheduler. exception: {}", e.getMessage());
//                throw new UnExpectedRequestException(errMsg);
//            }
//        }
//    }
//
//    @Override
//    public List<ExecutionParametersResponse> getAllExecutionParameters() throws UnExpectedRequestException {
//        Project project = projectDao.findByName(QualitisConstants.IMSMETRIC_PROJECT);
//        if (project == null) {
//            throw new UnExpectedRequestException("Project doesn't exists.");
//        }
//        ExecutionParametersRequest request = new ExecutionParametersRequest();
//        request.setSize(Integer.MAX_VALUE);
//        request.setProjectId(project.getId());
//        try {
//            GeneralResponse<GetAllResponse<ExecutionParametersResponse>> generalResponse = executionParametersService.getAllExecutionParameters(request);
//            if (ResponseStatusConstants.OK.equals(generalResponse.getCode())) {
//                GetAllResponse<ExecutionParametersResponse> getAllResponse = generalResponse.getData();
//                if (CollectionUtils.isNotEmpty(getAllResponse.getData())) {
//                    return getAllResponse.getData();
//                }
//            }
//        } catch (PermissionDeniedRequestException e) {
//            throw new UnExpectedRequestException("Has no permissions.");
//        }
//        return Collections.emptyList();
//    }
//
//    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
//    @Override
//    public void deleteMetricCollect(List<Long> metricCollectIds) throws UnExpectedRequestException {
////        获取要删除的采集配置
//        List<ImsMetricCollect> deletedMetricCollectList = imsMetricCollectDao.findByIds(metricCollectIds);
//        if (CollectionUtils.isEmpty(deletedMetricCollectList)) {
//            throw new UnExpectedRequestException("No any data.");
//        }
//
//        Map<String, List<ImsMetricCollect>> filterAndMetricCollectListMapInDb = new HashMap<>();
//        Map<String, List<ImsMetricCollect>> filterAndMetricCollectListMapInReq = new HashMap<>();
//
////        按分区分组全部存量配置和待删除配置
//        for (ImsMetricCollect imsMetricCollect : deletedMetricCollectList) {
//            String key = imsMetricCollect.getClusterName() + imsMetricCollect.getDbName() + imsMetricCollect.getTableName() + imsMetricCollect.getFilter();
//            if (!filterAndMetricCollectListMapInDb.containsKey(key)) {
//                List<ImsMetricCollect> imsMetricCollectListInFilter = imsMetricCollectDao.findByDatasource(imsMetricCollect.getClusterName(), imsMetricCollect.getDbName(), imsMetricCollect.getTableName(), null, imsMetricCollect.getFilter());
//                filterAndMetricCollectListMapInDb.put(key, imsMetricCollectListInFilter);
//            }
//            if (!filterAndMetricCollectListMapInReq.containsKey(key)) {
//                List<ImsMetricCollect> metricCollectList = new ArrayList<>();
//                metricCollectList.add(imsMetricCollect);
//                filterAndMetricCollectListMapInReq.put(key, metricCollectList);
//            } else {
//                filterAndMetricCollectListMapInReq.get(key).add(imsMetricCollect);
//            }
//        }
//
//        List<ImsMetricScheduler> pausedMetricSchedulers = new ArrayList<>();
////        比较相同分区下，待删除配置的数据量是否与全部存量配置一致，并提取一致的分区，同时删除该分区对应的采集频率
//        for (Map.Entry<String, List<ImsMetricCollect>> filterMapInReq : filterAndMetricCollectListMapInReq.entrySet()) {
//            Integer countInDb = filterAndMetricCollectListMapInDb.get(filterMapInReq.getKey()).size();
//            Integer countInReq = filterMapInReq.getValue().size();
////        通过比较，本次删除的采集配置数，将是当前分区下的全量数据，那么可同时删除该分区对应的采集频率
//            if (countInReq.compareTo(countInDb) == 0) {
//                ImsMetricCollect imsMetricCollect = filterMapInReq.getValue().get(0);
//                ImsMetricScheduler imsMetricScheduler = imsMetricSchedulerDao.findByPartition(imsMetricCollect.getDbName(), imsMetricCollect.getTableName(), imsMetricCollect.getFilter());
//                if (imsMetricScheduler != null) {
//                    imsMetricScheduler.setExecFreq(imsMetricScheduler.getExecFreq() + " 2099");
//                    pausedMetricSchedulers.add(imsMetricScheduler);
//                }
//            }
//        }
//
//        if (CollectionUtils.isNotEmpty(pausedMetricSchedulers)) {
//            String collectorServerPath = "/qualitis/outer/api/v1/imsmetric/collect/scheduler/update";
//            String appId = QualitisConstants.DEFAULT_AUTH_APP_ID;
//            String nonce = "16895";
//            String timestamp = String.valueOf(System.currentTimeMillis());
//            AuthList authList = authListRepository.findByAppId(appId);
//            String signature = SignUtil.generateSignature(appId, authList.getAppToken(), nonce, timestamp);
//            collectorServerPath += "?app_id=" + appId + "&timestamp=" + timestamp + "&nonce=" + nonce + "&signature=" + signature;
//            pushToCollectorServer(pausedMetricSchedulers, collectorServerHost + collectorServerPath);
//        }
//
//        imsMetricCollectDao.deleteByIds(metricCollectIds);
//    }
//
//    @Override
//    public MetricCollectQueryResponse getDetailInfo(Long metricCollectId) throws UnExpectedRequestException {
//        MetricCollectQueryResponse metricCollectQueryResponse = new MetricCollectQueryResponse();
//        Optional<ImsMetricCollect> metricCollectOptional = imsMetricCollectDao.findById(metricCollectId);
//        if (!metricCollectOptional.isPresent()) {
//            return metricCollectQueryResponse;
//        }
//        ImsMetricCollect metricCollect = metricCollectOptional.get();
//        Template template = ruleTemplateDao.findById(metricCollect.getTemplateId());
//        if (template == null) {
//            throw new UnExpectedRequestException("Template doesn't exists.");
//        }
//        Optional<CalcuUnit> calcuUnitOptional = calcuUnitDao.findById(template.getCalcuUnitId());
//        if (!calcuUnitOptional.isPresent()) {
//            throw new UnExpectedRequestException("CalcuUnit doesn't exists.");
//        }
//        metricCollectQueryResponse.setId(metricCollectId);
//        metricCollectQueryResponse.setTemplateId(template.getId());
//        metricCollectQueryResponse.setTemplateEnName(template.getEnName());
//        metricCollectQueryResponse.setTemplateCnName(template.getName());
//        metricCollectQueryResponse.setClusterName(metricCollect.getClusterName());
//        metricCollectQueryResponse.setDatabase(metricCollect.getDbName());
//        metricCollectQueryResponse.setTable(metricCollect.getTableName());
//        metricCollectQueryResponse.setColumn(metricCollect.getColumnName());
//        metricCollectQueryResponse.setSqlAction(calcuUnitOptional.get().getSqlAction());
//        metricCollectQueryResponse.setExecutionParametersName(metricCollect.getExecutionParametersName());
//        metricCollectQueryResponse.setPartition(metricCollect.getFilter());
//        metricCollectQueryResponse.setProxyUser(metricCollect.getProxyUser());
//        return metricCollectQueryResponse;
//    }
//
//    @Override
//    public void modify(ModifyMetricCollectRequest request) throws UnExpectedRequestException {
//        Optional<ImsMetricCollect> metricCollectOptional = imsMetricCollectDao.findById(request.getId());
//        if (!metricCollectOptional.isPresent()) {
//            throw new UnExpectedRequestException("The data doesn't exists.");
//        }
//        String loginUser = HttpUtils.getUserName(httpServletRequest);
//        ImsMetricCollect metricCollect = metricCollectOptional.get();
////       如果算子或字段被修改，则需判断改后的算子-字段，是否已存在
//        if (!metricCollect.getColumnName().equals(request.getColumn()) || !metricCollect.getTemplateId().equals(request.getTemplateId())) {
//            List<ImsMetricCollect> metricCollectList = imsMetricCollectDao.findByDatasource(metricCollect.getClusterName(), metricCollect.getDbName(), metricCollect.getTableName(), request.getColumn(), metricCollect.getFilter());
//            boolean hasExisted = metricCollectList.stream().anyMatch(item -> item.getTemplateId().equals(request.getTemplateId()));
//            if (hasExisted) {
//                throw new UnExpectedRequestException("Same calcu_unit and column has existed in db.");
//            }
//        }
//
//        metricCollect.setColumnName(request.getColumn());
//        metricCollect.setExecutionParametersName(request.getExecutionParametersName());
//        metricCollect.setTemplateId(request.getTemplateId());
//        metricCollect.setModifyTime(DateUtils.now());
//        metricCollect.setModifyUser(loginUser);
//        imsMetricCollectDao.saveAll(Arrays.asList(metricCollect));
//    }
//
//    @Override
//    public GetAllResponse<MetricCollectQueryResponse> list(MetricCollectQueryRequest request) throws UnExpectedRequestException {
//        String userName = HttpUtils.getUserName(httpServletRequest);
//        User loginUser = userDao.findByUsername(userName);
//        if (loginUser == null) {
//            throw new UnExpectedRequestException("username is not exists.");
//        }
//        List<String> proxyUserNames = loginUser.getUserProxyUsers().stream().map(userProxyUser -> userProxyUser.getProxyUser().getProxyUserName()).distinct().collect(Collectors.toList());
//        if (CollectionUtils.isEmpty(proxyUserNames)) {
//            return new GetAllResponse<>(0, Collections.emptyList());
//        }
//        Page<ImsMetricCollectDto> responsePage = imsMetricCollectDao.queryListWithPage(request.getTemplateEnName(), request.getTemplateCnName(), request.getClusterName(), request.getDatabase(), request.getTable(), request.getPartition(), request.getCreateName(), request.getUpdateName(), request.getCreateStartTime(), request.getCreateEndTime(), request.getUpdateStartTime(), request.getUpdateEndTime(), proxyUserNames, request.getPage(), request.getSize());
//        List<MetricCollectQueryResponse> responses = responsePage.getContent().stream().map(MetricCollectQueryResponse::new).collect(Collectors.toList());
//        return new GetAllResponse<>(responsePage.getTotalElements(), responses);
//    }
//
//    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
//    @Override
//    public void createBatch(List<AddMetricCollectRequest> requests) throws UnExpectedRequestException {
////        检测不同采集组之间(相同库表)的分区是否有重复
//        long actualCount = requests.stream().map(metricCollectRequest -> metricCollectRequest.getDatabase() + metricCollectRequest.getTable() + metricCollectRequest.getPartition()).distinct().count();
//        if (requests.size() != actualCount) {
//            throw new UnExpectedRequestException("Found duplicated partition, please checking.");
//        }
//        Map<String, Long> calcuUnitNameAndTemplateMap = getCalcuUnitNameAndTemplateIdMap();
//        Long enumNumTemplate = calcuUnitNameAndTemplateMap.get(ImsRuleCalcTypeEnum.ENUM_NUM.getDescribe());
//        Long enumRateTemplate = calcuUnitNameAndTemplateMap.get(ImsRuleCalcTypeEnum.ENUM_RATE.getDescribe());
////        判断数据库中是否已存在集群、库、表、分区, 如果已存在，则删除已有的集群+库+表+分区下的所有采集配置
//        for (AddMetricCollectRequest addMetricCollectRequest : requests) {
//            List<ImsMetricCollect> metricCollectList = imsMetricCollectDao.findByDatasource(addMetricCollectRequest.getClusterName(), addMetricCollectRequest.getDatabase(), addMetricCollectRequest.getTable(), null, StringUtils.isNotBlank(addMetricCollectRequest.getPartition()) ? addMetricCollectRequest.getPartition() : null);
//            if (CollectionUtils.isNotEmpty(metricCollectList)) {
//                List<Long> ids = metricCollectList.stream().filter(imsMetricCollect -> ! (enumNumTemplate.equals(imsMetricCollect.getTemplateId()) || enumRateTemplate.equals(imsMetricCollect.getTemplateId()))).map(ImsMetricCollect::getId).collect(Collectors.toList());
//                if (CollectionUtils.isNotEmpty(ids)) {
//                    imsMetricCollectDao.deleteByIds(ids);
//                }
//            }
//        }
//
////        设置默认参数
//        requests.forEach(metricCollectRequest -> metricCollectRequest.setDatasourceType(TemplateDataSourceTypeEnum.HIVE.getCode()));
//
//        addMetricCollectConfigs(requests);
//
//    }
//
//    @Override
//    public void addMetricCollectConfigs(List<AddMetricCollectRequest> addMetricCollectRequests) throws UnExpectedRequestException {
//        Map<String, Long> calcuUnitNameAndTemplateMap = getCalcuUnitNameAndTemplateIdMap();
//        List<ImsMetricCollect> allImsMetricCollectList = new ArrayList<>();
//        String loginUser = HttpUtils.getUserName(httpServletRequest);
//        try {
////            采集组（最小粒度为分区）
//            for (AddMetricCollectRequest collectRequest : addMetricCollectRequests) {
//                List<ColumnInfoDetail> columnInfoDetailList = metaDataClient.getColumnInfo(collectRequest.getClusterName(), collectRequest.getDatabase(), collectRequest.getTable(), collectRequest.getProxyUser());
//                if (CollectionUtils.isEmpty(columnInfoDetailList)) {
//                    LOGGER.warn("No any fields. cluster: {}, database: {}, table: {}, user: {}", collectRequest.getClusterName(), collectRequest.getDatabase(), collectRequest.getTable(), collectRequest.getProxyUser());
//                    continue;
//                }
//                Map<String, ColumnInfoDetail> columnInfoDetailMap = columnInfoDetailList.stream().collect(Collectors.toMap(ColumnInfoDetail::getFieldName, Function.identity(), (oldVal, newVal) -> oldVal));
//
////                录入用户手动指定的采集配置（算子-字段）
//                List<ImsMetricCollect> manualMetricCollects = getManualMetricCollects(collectRequest, columnInfoDetailMap, loginUser);
//
////                自动录入采集配置(如果某个采集配置已经在手动录入阶段处理，则跳过)
//                List<ImsMetricCollect> automationMetricCollects = getAutomationMetricCollects(collectRequest, calcuUnitNameAndTemplateMap, columnInfoDetailMap.values(), loginUser);
//
//                removeManualFromAutomationMetricCollect(manualMetricCollects, automationMetricCollects);
//
////                获得COUNT采集配置
//                ColumnInfoDetail columnForRowCount = columnInfoDetailList.get(0);
//                ImsMetricCollect metricCollectForRowCount = createImsMetricCollect(calcuUnitNameAndTemplateMap.get(ImsRuleCalcTypeEnum.COUNT.getDescribe()), columnForRowCount.getFieldName(), columnForRowCount.getDataType(), null, loginUser, collectRequest);
//
//                allImsMetricCollectList.addAll(manualMetricCollects);
//                allImsMetricCollectList.addAll(automationMetricCollects);
//                allImsMetricCollectList.add(metricCollectForRowCount);
//            }
//        } catch (Exception e) {
//            throw new UnExpectedRequestException("Failed to get columns from cmdb. error: " + e.getMessage());
//        }
//
//        imsMetricCollectDao.saveAll(allImsMetricCollectList);
//
//        LOGGER.info("Success to save metric config.");
//    }
//
//    private void removeManualFromAutomationMetricCollect(List<ImsMetricCollect> manualMetricCollects, List<ImsMetricCollect> automationMetricCollects) {
//        if (CollectionUtils.isNotEmpty(manualMetricCollects)) {
//            Map<String, ImsMetricCollect> calUnitAndMetricCollectMap = manualMetricCollects.stream().collect(Collectors.toMap(imsMetricCollect -> imsMetricCollect.getTemplateId() + imsMetricCollect.getColumnName(), Function.identity(), (oldVal, newVal) -> oldVal));
//            ListIterator<ImsMetricCollect> listIterator = automationMetricCollects.listIterator();
//            while (listIterator.hasNext()) {
//                ImsMetricCollect imsMetricCollect = listIterator.next();
//                if (calUnitAndMetricCollectMap.containsKey(imsMetricCollect.getTemplateId() + imsMetricCollect.getColumnName())) {
//                    listIterator.remove();
//                }
//            }
//        }
//    }
//
//    @Override
//    public ProjectResponse getImsMetricProject() {
//        Project project = projectDao.findByName(QualitisConstants.IMSMETRIC_PROJECT);
//        return new ProjectResponse(project);
//    }
//
//    @Override
//    public void checkCreateRequest(AddMetricCollectRequest request) throws UnExpectedRequestException {
//        CommonChecker.checkString(request.getClusterName(), "cluster_name");
//        CommonChecker.checkString(request.getDatabase(), "database");
//        CommonChecker.checkString(request.getTable(), "table");
//        CommonChecker.checkString(request.getPartition(), "partition");
//        CommonChecker.checkString(request.getProxyUser(), "proxy_user");
//
//        List<AddMetricCollectConfigRequest> collectConfigRequests = request.getCollectConfigRequests();
//        CommonChecker.checkListMinSize(collectConfigRequests, 1, "collect_configs");
//        for (AddMetricCollectConfigRequest collectConfigRequest : collectConfigRequests) {
//            CommonChecker.checkString(collectConfigRequest.getExecutionParametersName(), "execution_parameters_name");
//
//            if (collectConfigRequest.isManualCollect()) {
//                List<AddMetricCalcuUnitConfigRequest> metricCalcuUnitConfigRequestList = collectConfigRequest.getMetricCalcuUnitConfigRequestList();
//                CommonChecker.checkListMinSize(metricCalcuUnitConfigRequestList, 1, "calcu_unit_configs");
//                for (AddMetricCalcuUnitConfigRequest metricCalcuUnitConfigRequest : metricCalcuUnitConfigRequestList) {
//                    CommonChecker.checkObject(metricCalcuUnitConfigRequest.getTemplateId(), "template_id");
//                    CommonChecker.checkListMinSize(metricCalcuUnitConfigRequest.getColumns(), 1, "columns");
//                }
//            }
//        }
//    }
//
//    private List<ImsMetricCollect> getManualMetricCollects(AddMetricCollectRequest addMetricCollectRequest, Map<String, ColumnInfoDetail> columnInfoDetailMap, String loginUser) throws UnExpectedRequestException {
//        List<AddMetricCollectConfigRequest> collectConfigRequests = addMetricCollectRequest.getCollectConfigRequests();
//        if (CollectionUtils.isEmpty(collectConfigRequests)) {
//            return Collections.emptyList();
//        }
//        List<ImsMetricCollect> manualImsMetricCollectList = new ArrayList<>();
////                    一个采集组，多个采集配置
//        for (AddMetricCollectConfigRequest metricCollectConfigRequest : collectConfigRequests) {
//            List<AddMetricCalcuUnitConfigRequest> metricCalcuUnitConfigRequestList = metricCollectConfigRequest.getMetricCalcuUnitConfigRequestList();
////                        一个算子多个字段
//            for (AddMetricCalcuUnitConfigRequest metricCalcuUnitConfigRequest : metricCalcuUnitConfigRequestList) {
//                Optional<String> invalidatedColumn = metricCalcuUnitConfigRequest.getColumns().stream().filter(column -> !columnInfoDetailMap.containsKey(column)).findFirst();
//                if (invalidatedColumn.isPresent()) {
//                    throw new UnExpectedRequestException("The field is not exists: " + invalidatedColumn.get());
//                }
//                List<ImsMetricCollect> imsMetricCollectList = metricCalcuUnitConfigRequest.getColumns().stream().map(column -> {
//                    ColumnInfoDetail columnInfoDetail = columnInfoDetailMap.get(column);
//                    ImsMetricCollect imsMetricCollect = createImsMetricCollect(metricCalcuUnitConfigRequest.getTemplateId(), column, columnInfoDetail.getDataType(), metricCollectConfigRequest.getExecutionParametersName(), loginUser, addMetricCollectRequest);
//                    return imsMetricCollect;
//                }).collect(Collectors.toList());
//                manualImsMetricCollectList.addAll(imsMetricCollectList);
//            }
//        }
//        return manualImsMetricCollectList;
//    }
//
//    private List<ImsMetricCollect> getAutomationMetricCollects(AddMetricCollectRequest collectRequest, Map<String, Long> calcuUnitNameAndTemplateMap, Collection<ColumnInfoDetail> columnInfoDetailList, String loginUser) {
//        if (CollectionUtils.isEmpty(columnInfoDetailList)) {
//            return Collections.emptyList();
//        }
//        List<ImsMetricCollect> automationMetricCollects = new ArrayList<>();
//        List<ColumnInfoDetail> numberMetricCollectColumnList = new ArrayList<>();
//        List<ColumnInfoDetail> stringMetricCollectColumnList = new ArrayList<>();
//        for (ColumnInfoDetail columnInfoDetail : columnInfoDetailList) {
//            if (columnInfoDetail.getPartitionField() != null && columnInfoDetail.getPartitionField()) {
//                continue;
//            }
//            String columnType = columnInfoDetail.getDataType().toLowerCase();
//            if (StringUtils.isBlank(columnType)) {
//                continue;
//            }
//            boolean isNumberType = NUMBER_TYPE_LIST.stream().anyMatch(type -> columnType.startsWith(type));
//            if (isNumberType) {
////                        数值处理
//                numberMetricCollectColumnList.add(columnInfoDetail);
//            } else {
////                        只按字符串处理，即只添加空值算子
//                stringMetricCollectColumnList.add(columnInfoDetail);
//            }
//        }
//
//        List<ImsMetricCollect> numberMetricCollectList = createMetricCollectByDataType(collectRequest, calcuUnitNameAndTemplateMap, numberMetricCollectColumnList, DATA_TYPE_NUM, loginUser);
//        List<ImsMetricCollect> stringMetricCollectList = createMetricCollectByDataType(collectRequest, calcuUnitNameAndTemplateMap, stringMetricCollectColumnList, null, loginUser);
//        automationMetricCollects.addAll(numberMetricCollectList);
//        automationMetricCollects.addAll(stringMetricCollectList);
//
//        return automationMetricCollects;
//    }
//
//    private List<ImsMetricCollect> createMetricCollectByDataType(AddMetricCollectRequest addMetricCollectRequest, Map<String, Long> calcuUnitNameAndTemplateMap, List<ColumnInfoDetail> columnList, Integer dataType, String loginUser) {
//        List<ImsMetricCollect> imsMetricCollectListByTable = new ArrayList<>();
//        List<Long> templateIdsInDataType = getTemplateIdsByDataType(calcuUnitNameAndTemplateMap, dataType);
//        for (ColumnInfoDetail column : columnList) {
////            按照模板（算子）维度新增指标
//            List<ImsMetricCollect> imsMetricCollectList = templateIdsInDataType.stream().map(templateId -> createImsMetricCollect(templateId, column.getFieldName(), column.getDataType(), null, loginUser, addMetricCollectRequest)).collect(Collectors.toList());
//            imsMetricCollectListByTable.addAll(imsMetricCollectList);
//        }
//
//        return imsMetricCollectListByTable;
//    }
//
//    private List<Long> getTemplateIdsByDataType(Map<String, Long> calcuUnitNameAndTemplateMap, Integer dataType) {
//        List<Long> templateIds = new ArrayList<>();
//        templateIds.add(calcuUnitNameAndTemplateMap.get(ImsRuleCalcTypeEnum.NULL_NUM.getDescribe()));
//        templateIds.add(calcuUnitNameAndTemplateMap.get(ImsRuleCalcTypeEnum.NULL_RATE.getDescribe()));
//        if (DATA_TYPE_NUM.equals(dataType)) {
//            templateIds.add(calcuUnitNameAndTemplateMap.get(ImsRuleCalcTypeEnum.SUM.getDescribe()));
//            templateIds.add(calcuUnitNameAndTemplateMap.get(ImsRuleCalcTypeEnum.MAX.getDescribe()));
//            templateIds.add(calcuUnitNameAndTemplateMap.get(ImsRuleCalcTypeEnum.MIN.getDescribe()));
//            templateIds.add(calcuUnitNameAndTemplateMap.get(ImsRuleCalcTypeEnum.AVG.getDescribe()));
//            templateIds.add(calcuUnitNameAndTemplateMap.get(ImsRuleCalcTypeEnum.STDDEV.getDescribe()));
//        }
//        return templateIds;
//    }
//
//    private Map<String, Long> getCalcuUnitNameAndTemplateIdMap() {
//        List<CalcuUnit> calcuUnitList = calcuUnitDao.findAll();
//        List<Template> templateList = ruleTemplateDao.findMetricCollectTemplates(calcuUnitList.stream().map(CalcuUnit::getId).collect(Collectors.toList()));
//        Map<Long, Template> calcuUnitIdAndTemplateMap = templateList.stream().collect(Collectors.toMap(Template::getCalcuUnitId, Function.identity(), (oldVal, newVal) -> oldVal));
//        Map<String, Long> calcuUnitNameAndTemplateMap = Maps.newHashMapWithExpectedSize(calcuUnitList.size());
//        for (CalcuUnit calcuUnit : calcuUnitList) {
//            if (calcuUnitIdAndTemplateMap.containsKey(calcuUnit.getId())) {
//                calcuUnitNameAndTemplateMap.put(calcuUnit.getName(), calcuUnitIdAndTemplateMap.get(calcuUnit.getId()).getId());
//            }
//        }
//        return calcuUnitNameAndTemplateMap;
//    }
//
//
//    private ImsMetricCollect createImsMetricCollect(Long templateId, String columnName, String columnType, String executionParametersName, String loginUser, AddMetricCollectRequest request) {
//        ImsMetricCollect imsMetricCollect = new ImsMetricCollect();
//        imsMetricCollect.setExecutionParametersName(StringUtils.isBlank(executionParametersName) ? "default" : executionParametersName);
//        imsMetricCollect.setTemplateId(templateId);
//        imsMetricCollect.setClusterName(request.getClusterName());
//        imsMetricCollect.setDbName(request.getDatabase());
//        imsMetricCollect.setTableName(request.getTable());
//        imsMetricCollect.setColumnName(columnName);
//        imsMetricCollect.setDatasourceType(request.getDatasourceType());
//        imsMetricCollect.setFilter(request.getPartition());
//        imsMetricCollect.setColumnType(columnType);
//        imsMetricCollect.setCreateUser(loginUser);
//        imsMetricCollect.setCreateTime(DateUtils.now());
//        if (StringUtils.isBlank(imsMetricCollect.getFilter())) {
//            imsMetricCollect.setFilter("ds='${run_today}'");
//        }
//        imsMetricCollect.setCollectAge(0);
//        imsMetricCollect.setProxyUser(request.getProxyUser());
//
//        return imsMetricCollect;
//    }

}
