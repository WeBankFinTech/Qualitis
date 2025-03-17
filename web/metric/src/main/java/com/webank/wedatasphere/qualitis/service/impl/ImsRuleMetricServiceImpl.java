package com.webank.wedatasphere.qualitis.service.impl;

//import cn.hutool.core.date.DatePattern;
//import cn.hutool.core.date.DateUnit;
//import cn.hutool.core.date.DateUtil;
//import cn.webank.bdp.wedatasphere.biz.concurrent.exception.ThreadPoolNotFoundException;
//import cn.webank.bdp.wedatasphere.biz.concurrent.pool.GeneralThreadPool;
//import cn.webank.bdp.wedatasphere.biz.concurrent.pool.manager.AbstractThreadPoolManager;
//import com.google.common.collect.Lists;
//import com.webank.wedatasphere.qualitis.constant.*;
//import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
//import com.webank.wedatasphere.qualitis.constants.ThreadPoolConstant;
//import com.webank.wedatasphere.qualitis.dao.*;
//import com.webank.wedatasphere.qualitis.dto.ImsMetricCollectDto;
//import com.webank.wedatasphere.qualitis.entity.*;
//import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
//import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
//import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
//import com.webank.wedatasphere.qualitis.metadata.response.DataMapResultInfo;
//import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
//import com.webank.wedatasphere.qualitis.request.*;
//import com.webank.wedatasphere.qualitis.response.*;
//import com.webank.wedatasphere.qualitis.rule.constant.*;
//import com.webank.wedatasphere.qualitis.rule.dao.MetricExtInfoDao;
//import com.webank.wedatasphere.qualitis.rule.dao.RuleTemplateDao;
//import com.webank.wedatasphere.qualitis.rule.dao.TemplateDataSourceTypeDao;
//import com.webank.wedatasphere.qualitis.rule.entity.DataVisibility;
//import com.webank.wedatasphere.qualitis.rule.entity.Template;
//import com.webank.wedatasphere.qualitis.rule.entity.TemplateDataSourceType;
//import com.webank.wedatasphere.qualitis.rule.request.TemplatePageRequest;
//import com.webank.wedatasphere.qualitis.rule.response.RuleTemplateResponse;
//import com.webank.wedatasphere.qualitis.rule.timer.MetadataSyncUpdater;
//import com.webank.wedatasphere.qualitis.service.DataVisibilityService;
import com.webank.wedatasphere.qualitis.service.ImsRuleMetricService;
//import com.webank.wedatasphere.qualitis.service.RoleService;
//import com.webank.wedatasphere.qualitis.service.SubDepartmentPermissionService;
//import com.webank.wedatasphere.qualitis.util.HttpUtils;
//import com.webank.wedatasphere.qualitis.util.RequestParametersUtils;
//import com.webank.wedatasphere.qualitis.util.ScanRulesUtils;
//import com.webank.wedatasphere.qualitis.util.UuidGenerator;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.collections.MapUtils;
//import org.apache.commons.lang.StringUtils;
//import org.apache.tools.ant.util.DateUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.annotation.PostConstruct;
//import javax.persistence.Tuple;
//import javax.servlet.http.HttpServletRequest;
//import javax.ws.rs.core.Context;
//import java.math.BigDecimal;
//import java.math.BigInteger;
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.function.Function;
//import java.util.stream.Collectors;


/**
 * @author v_wenxuanzhang
 */
@Service
public class ImsRuleMetricServiceImpl implements ImsRuleMetricService {

//    @Autowired
//    private ImsmetricDataDao imsmetricDataDao;
//    @Autowired
//    private ImsmetricIdentifyDao imsmetricIdentifyDao;
//    @Autowired
//    private ImsmetricCheckResultDao imsmetricCheckResultDao;
//    @Autowired
//    private ImsMetricCollectDao imsMetricCollectDao;
//    @Autowired
//    private ImsMetricDao imsMetricDao;
//    @Autowired
//    private UserDao userDao;
//    @Autowired
//    private UserRoleDao userRoleDao;
//    @Autowired
//    private RoleService roleService;
//    @Autowired
//    private SubDepartmentPermissionService subDepartmentPermissionService;
//    @Autowired
//    private RuleTemplateDao ruleTemplateDao;
//    @Autowired
//    private TemplateDataSourceTypeDao templateDataSourceTypeDao;
//    @Autowired
//    private DataVisibilityService dataVisibilityService;
//    @Autowired
//    private CalcuUnitDao calcuUnitDao;
//    @Autowired
//    private MetricExtInfoDao metricExtInfoDao;
//
//    @Value("${dss.origin-urls}")
//    private String dssOriginUrls;
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(ImsRuleMetricServiceImpl.class);
//
//    List<String> CALCU_UNIT_NAME_LIST = Lists.newArrayList(ImsRuleCalcTypeEnum.ENUM_NUM.getDescribe(), ImsRuleCalcTypeEnum.ENUM_RATE.getDescribe());
//
//    private HttpServletRequest httpServletRequest;
//    private final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    private final DateFormat dfs = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
//    private final String REQUEST_SOURCE = "dqm";
//
//    public ImsRuleMetricServiceImpl(@Context HttpServletRequest httpServletRequest) {
//        this.httpServletRequest = httpServletRequest;
//    }
//
//    private Long getTimes(int date) throws UnExpectedRequestException {
//        try {
//            dfs.setLenient(false);
//            return dfs.parse(date + " 00:00:00").getTime();
//        } catch (ParseException e) {
//            throw new UnExpectedRequestException("date param error");
//        }
//    }
//
//    @Autowired
//    private AbstractThreadPoolManager threadPoolManager;
//    GeneralThreadPool sysMetadataThreadPool;
//
//    @PostConstruct
//    public void init() throws ThreadPoolNotFoundException {
//        sysMetadataThreadPool = threadPoolManager.getThreadPool(ThreadPoolConstant.SYNC_METADATA);
//    }

//    @Override
//    public GeneralResponse<GetAllMetricResponse<ImsRuleMetricQueryResponse>> getMetricDataFromOuter(ImsRuleMetricQueryRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
//        LOGGER.info("Request body: ", request.toString());
//        CommonChecker.checkString(request.getUsername(), "username must be not null.");
//        CommonChecker.checkObject(request.getStartDate(), "startDate");
//        CommonChecker.checkObject(request.getEndDate(), "endDate");
//        CommonChecker.checkString(request.getMetricId(), "metricId");
//        CommonChecker.checkString(request.getExecuteUser(), "executeUser");
//        User loginUser = userDao.findByUsername(request.getUsername());
//        if (loginUser == null) {
//            throw new UnExpectedRequestException("username is not exists.");
//        }
//
//        Date startDate = DateUtil.parse(String.valueOf(request.getStartDate()), "yyyyMMdd");
//        Date endDate = DateUtil.parse(String.valueOf(request.getEndDate()), "yyyyMMdd");
//        if (DateUtil.between(startDate, endDate, DateUnit.DAY) > 366) {
//            throw new UnExpectedRequestException("Out of time range.");
//        }
//
//        List<Long> metricIdList = new ArrayList<>();
//        String metricIdStr = request.getMetricId();
//        if (StringUtils.isNotBlank(metricIdStr)) {
//            String[] arr = metricIdStr.split(SpecCharEnum.COMMA.getValue());
//            for (String metricId : arr) {
//                metricIdList.add(Long.valueOf(metricId));
//            }
//        }
//
//        if (REQUEST_SOURCE.equals(request.getRequestSource())) {
//            List<ImsMetric> imsMetricList = imsMetricDao.findByIds(metricIdList);
//            List<Long> metricCollectIds = imsMetricList.stream().map(ImsMetric::getMetricCollectId).collect(Collectors.toList());
//            List<ImsMetricCollect> imsMetricCollectList = imsMetricCollectDao.findByIds(metricCollectIds);
//            if (CollectionUtils.isEmpty(imsMetricCollectList)) {
//                throw new PermissionDeniedRequestException("{&HAS_NO_MATCHING_PROXY_USERS_PERMISSIONS}");
//            }
//            List<String> proxyUserNames = loginUser.getUserProxyUsers().stream().map(userProxyUser -> userProxyUser.getProxyUser().getProxyUserName()).distinct().collect(Collectors.toList());
//            proxyUserNames.add(request.getExecuteUser());
//            List<String> needProxyUserNames = imsMetricCollectList.stream().map(imsMetricCollect -> imsMetricCollect.getProxyUser()).distinct().collect(Collectors.toList());
//            Collection<String> subtract = CollectionUtils.subtract(needProxyUserNames, proxyUserNames);
//            if (CollectionUtils.isNotEmpty(subtract)) {
//                throw new PermissionDeniedRequestException("{&HAS_NO_MATCHING_PROXY_USERS_PERMISSIONS}");
//            }
//        } else {
//            List<String> proxyUserNames = loginUser.getUserProxyUsers().stream().map(userProxyUser -> userProxyUser.getProxyUser().getProxyUserName()).distinct().collect(Collectors.toList());
//            proxyUserNames.add(request.getExecuteUser());
//            //check proxyUser
//            List<ImsmetricIdentify> identify = imsmetricIdentifyDao.queryIdentify(metricIdList);
//            Map<Long, ImsmetricIdentify> identifyMap = identify.stream().collect(Collectors.toMap(ImsmetricIdentify::getMetricId, Function.identity(), (oldVal, newVal) -> oldVal));
//            if (MapUtils.isEmpty(identifyMap)) {
//                throw new UnExpectedRequestException("{&CAN_NOT_FIND_METRIC_DATA}");
//            }
//            List<String> needProxyUserNames = identify.stream().map(imsmetricIdentify -> imsmetricIdentify.getDatasourceUser()).distinct().collect(Collectors.toList());
//            Collection<String> subtract = CollectionUtils.subtract(needProxyUserNames, proxyUserNames);
//            if (CollectionUtils.isNotEmpty(subtract)) {
//                throw new PermissionDeniedRequestException("{&HAS_NO_MATCHING_PROXY_USERS_PERMISSIONS}");
//            }
//        }
//
//        List<ImsmetricData> imsmetricDataList = imsmetricDataDao.queryImsmetricDatas(metricIdList,
//                request.getStartDate(), request.getEndDate());
//        List<ImsRuleMetricQueryResponse> imsRuleMetricQueryResponseArrayList = new ArrayList<>();
//        imsmetricDataList.stream().collect(Collectors.groupingBy(ImsmetricData::getMetricId)).values().forEach(item -> {
//            Map<Long, BigDecimal> valuesMap = new LinkedHashMap<>(16);
//            item.forEach(data -> {
//                valuesMap.put(data.getDs(), data.getY());
//            });
//            ImsmetricData imsmetricData0 = item.get(0);
//            Long metricId = imsmetricData0.getMetricId();
//            ImsRuleMetricQueryResponse imsRuleMetricQueryResponse = new ImsRuleMetricQueryResponse();
//            LineDataList lineDataList = new LineDataList();
//            lineDataList.setTimestampList(new ArrayList<>(valuesMap.keySet()));
//            lineDataList.setValueList(new ArrayList<>(valuesMap.values()));
//            imsRuleMetricQueryResponse.setMetricId(metricId);
//            imsRuleMetricQueryResponse.setLineDataList(lineDataList);
//
//            imsRuleMetricQueryResponseArrayList.add(imsRuleMetricQueryResponse);
//        });
//
//        GetAllMetricResponse<ImsRuleMetricQueryResponse> getAllResponse = new GetAllMetricResponse<>();
//        getAllResponse.setTitle("指标视图");
//        getAllResponse.setDssUrl(dssOriginUrls);
//        getAllResponse.setMetrics(imsRuleMetricQueryResponseArrayList);
//
//        return new GeneralResponse<>(ResponseStatusConstants.OK, "OK", getAllResponse);
//    }

//    @Override
//    public GeneralResponse<GetAllMetricResponse<ImsRuleMetricQueryResponse>> getMetricData(ImsRuleMetricQueryRequest request)
//            throws UnExpectedRequestException, PermissionDeniedRequestException {
//        GetAllMetricResponse<ImsRuleMetricQueryResponse> getAllResponse = new GetAllMetricResponse<>();
//
//        List<Long> metricIdList = new ArrayList<>();
//        String metricIdStr = request.getMetricId();
//        if (StringUtils.isNotBlank(metricIdStr)) {
//            String[] arr = metricIdStr.split(SpecCharEnum.COMMA.getValue());
//            for (String metricId : arr) {
//                metricIdList.add(Long.valueOf(metricId));
//            }
//        }
//
//        List<ImsRuleMetricQueryResponse> imsRuleMetricQueryResponseArrayList = new ArrayList<>();
//
//        if (REQUEST_SOURCE.equals(request.getRequestSource())) {
//            addMetricDataViewToResponse(request, metricIdList, imsRuleMetricQueryResponseArrayList);
//        } else {
//            addMetricDataViewToResponseOld(request, metricIdList, imsRuleMetricQueryResponseArrayList);
//        }
//
//        getAllResponse.setTitle("指标视图");
//        getAllResponse.setDssUrl(dssOriginUrls);
//        getAllResponse.setMetrics(imsRuleMetricQueryResponseArrayList);
//
//        return new GeneralResponse<>(ResponseStatusConstants.OK, "OK", getAllResponse);
//    }

//    private void addMetricDataViewToResponseOld(ImsRuleMetricQueryRequest request, List<Long> metricIdList, List<ImsRuleMetricQueryResponse> imsRuleMetricQueryResponseArrayList) throws UnExpectedRequestException, PermissionDeniedRequestException {
//        Map<Long, ImsmetricIdentify> identifyMap = checkProxyUserPermission(metricIdList);
//        if (MapUtils.isEmpty(identifyMap)) {
//            throw new UnExpectedRequestException("{&CAN_NOT_FIND_METRIC_DATA}");
//        }
//        Long startDateTimes = getTimes(request.getStartDate());
//        Long endDateTimes = getTimes(request.getEndDate());
//
//        //query metric date info
//        List<ImsmetricData> imsmetricDataList = imsmetricDataDao.queryImsmetricDatas(metricIdList,
//                request.getStartDate(), request.getEndDate());
//
//        BigDecimal zero = new BigDecimal("0.00000");
//
//        imsmetricDataList.stream().collect(Collectors.groupingBy(ImsmetricData::getMetricId)).values().forEach(item -> {
//            ImsmetricData imsmetricData0 = item.get(0);
//            Long metricId = imsmetricData0.getMetricId();
//            ImsmetricIdentify imsmetricIdentify = identifyMap.get(metricId);
//            if (null != imsmetricIdentify) {
//                Map<Long, BigDecimal> valuesMap = new LinkedHashMap<>(16);
//                item.forEach(data -> {
//                    for (long i = startDateTimes; i <= endDateTimes; i += 86400000) {
//                        if (i == data.getDs() * 1000) {
//                            valuesMap.put(i, data.getY());
//                        } else {
//                            if (!valuesMap.containsKey(i)) {
//                                valuesMap.put(i, zero);
//                            }
//                        }
//                    }
//                });
//
//                ImsRuleMetricQueryResponse imsRuleMetricQueryResponse = new ImsRuleMetricQueryResponse();
//                LineDataList lineDataList = new LineDataList();
//                lineDataList.setTimestampList(new ArrayList<>(valuesMap.keySet()));
//                lineDataList.setValueList(new ArrayList<>(valuesMap.values()));
//
//                imsRuleMetricQueryResponse.setMetricId(metricId);
//                String metricName = "";
//                if (ImsRuleMetricTypeEnum.TABLE_COLLECT.getCode() == imsmetricIdentify.getMetricType()) {
//                    metricName = String.format("[%s][%s][%s][%s]%s", imsmetricIdentify.getDatabaseName(),
//                            imsmetricIdentify.getTableName(), imsmetricIdentify.getAttrName(),
//                            ImsRuleCalcTypeEnum.match(imsmetricIdentify.getCalcType()).getDescribe(),
//                            ImsRuleMetricTypeEnum.match(imsmetricIdentify.getMetricType()).getDescribe());
//
//                } else if (ImsRuleMetricTypeEnum.ENUM_COLLECT.getCode() == imsmetricIdentify.getMetricType() ||
//                        ImsRuleMetricTypeEnum.ORIGIN_COLLECT.getCode() == imsmetricIdentify.getMetricType()) {
//                    metricName = String.format("[%s][%s][%s][%s][%s]%s", imsmetricIdentify.getDatabaseName(),
//                            imsmetricIdentify.getTableName(), imsmetricIdentify.getAttrName(),
//                            imsmetricIdentify.getRowvalueenumName(),
//                            ImsRuleCalcTypeEnum.match(imsmetricIdentify.getCalcType()).getDescribe(),
//                            ImsRuleMetricTypeEnum.match(imsmetricIdentify.getMetricType()).getDescribe());
//                }
//                imsRuleMetricQueryResponse.setMetricName(metricName);
//                imsRuleMetricQueryResponse.setLineDataList(lineDataList);
//                imsRuleMetricQueryResponse.setMetricType(imsmetricIdentify.getMetricType());
//
//                imsRuleMetricQueryResponseArrayList.add(imsRuleMetricQueryResponse);
//            }
//        });
//    }

//    private void addMetricDataViewToResponse(ImsRuleMetricQueryRequest request, List<Long> metricIdList, List<ImsRuleMetricQueryResponse> imsRuleMetricQueryResponseArrayList) throws UnExpectedRequestException, PermissionDeniedRequestException {
//        List<ImsMetric> imsMetricList = imsMetricDao.findByIds(metricIdList);
////        根据指标查询qualitis_metric_collect表，获取指标配置列表
//        List<Long> metricCollectIds = imsMetricList.stream().map(ImsMetric::getMetricCollectId).collect(Collectors.toList());
//        List<ImsMetricCollect> imsMetricCollectList = imsMetricCollectDao.findByIds(metricCollectIds);
//        checkMetricCollectUserPermissions(imsMetricCollectList);
//
//        List<ImsmetricData> imsmetricDataList = imsmetricDataDao.queryImsmetricDatas(metricIdList,
//                request.getStartDate(), request.getEndDate());
//        Map<Long, List<ImsmetricData>> imsmetricDataListMap = new HashMap<>();
//        for (ImsmetricData imsmetricData : imsmetricDataList) {
//            long metricId = imsmetricData.getMetricId();
//            List<ImsmetricData> imsmetricList = new ArrayList<>();
//            if (imsmetricDataListMap.containsKey(metricId)) {
//                imsmetricList = imsmetricDataListMap.get(metricId);
//            }
//            imsmetricList.add(imsmetricData);
//            imsmetricDataListMap.put(metricId, imsmetricList);
//        }
//
//        Map<Long, LineDataList> resultMap = fillDefaultValue(metricIdList, request.getStartDate(), request.getEndDate());
//
//        resultMap.entrySet().forEach(item -> {
//            ImsRuleMetricQueryResponse imsRuleMetricQueryResponse = new ImsRuleMetricQueryResponse();
//            Long metricId = item.getKey();
//            LineDataList lineDataList = item.getValue();
//            coverValue(lineDataList, imsmetricDataListMap.get(metricId), imsRuleMetricQueryResponse);
//            String metricName = "";
//            int metricType = 0;
//            try {
//                ImsMetricCollectQueryResponse metricCollectQueryResponse = getMetricCollectById(String.valueOf(metricId));
//                metricType = ImsRuleCalcTypeEnum.valueOf(metricCollectQueryResponse.getCalcuUnitName()).getCode();
//                metricName = String.format("库名%s|表名%s|字段名%s|算子名%s", metricCollectQueryResponse.getDbName(),
//                        metricCollectQueryResponse.getTableName(), metricCollectQueryResponse.getColumnName(), metricCollectQueryResponse.getCalcuUnitName());
//                if (StringUtils.isNotBlank(metricCollectQueryResponse.getCalcuUnitName()) && CALCU_UNIT_NAME_LIST.contains(metricCollectQueryResponse.getCalcuUnitName())) {
//                    metricName += "|枚举值" + metricCollectQueryResponse.getEnumValue();
//                }
//            } catch (UnExpectedRequestException e) {
//                LOGGER.error("query ims metric collect error.");
//            }
//            imsRuleMetricQueryResponse.setMetricId(metricId);
//            imsRuleMetricQueryResponse.setMetricName(metricName);
//            imsRuleMetricQueryResponse.setMetricType(metricType);
//
//            imsRuleMetricQueryResponseArrayList.add(imsRuleMetricQueryResponse);
//        });
//    }
//
//    /**
//     * 填充缺省值null
//     */
//    private Map<Long, LineDataList> fillDefaultValue(List<Long> metricIdList, int startDate, int endDate) throws UnExpectedRequestException {
//        Map<Long, LineDataList> metricMap = new HashMap<>();
//        long startTime = getTimes(startDate);
//        long endTime = getTimes(endDate);
//        final long offset = 24 * 60 * 60 * 1000L;
//        for (Long metricId : metricIdList) {
//            LineDataList lineData = new LineDataList();
//            metricMap.put(metricId, lineData);
//            lineData.setTimestampList(new ArrayList<>());
//            lineData.setValueList(new ArrayList<>());
//            long timestamp = startTime;
//            while (timestamp <= endTime) {
//                lineData.getTimestampList().add(timestamp);
//                lineData.getValueList().add(null);
//                timestamp += offset;
//            }
//        }
//        return metricMap;
//    }
//
//    /**
//     * 有效值覆盖缺省值
//     */
//    private void coverValue(LineDataList lineDataList, List<ImsmetricData> metricData, ImsRuleMetricQueryResponse imsRuleMetricQueryResponse) {
//        List<BigDecimal> valueList = lineDataList.getValueList();
//        List<Long> timestampList = lineDataList.getTimestampList();
//        if (CollectionUtils.isEmpty(metricData)) {
//            imsRuleMetricQueryResponse.setLineDataList(lineDataList);
//            return;
//        }
//        for (int i = 0; i < timestampList.size(); i++) {
//            long timestamp = timestampList.get(i);
//            for (ImsmetricData metricDatum : metricData) {
//                if (metricDatum.getDs() * 1000 == timestamp) {
//                    valueList.set(i, metricDatum.getY());
//                }
//            }
//        }
//        lineDataList.setValueList(valueList);
//        imsRuleMetricQueryResponse.setLineDataList(lineDataList);
//    }

//    @Override
//    public GeneralResponse<GetDataResponse<ImsAlarmDataQueryResponse>> getAlarmData(ImsAlarmDataQueryRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
//        GetDataResponse<ImsAlarmDataQueryResponse> getDataResponse = new GetDataResponse<>();
//
//        String metricIdStr = request.getMetricId();
//        if (StringUtils.isBlank(metricIdStr)) {
//            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
//        }
//        String[] arr = metricIdStr.split(",");
//        List<Long> metricIdList = new ArrayList<>();
//        for (String metricId : arr) {
//            metricIdList.add(Long.valueOf(metricId));
//        }
//
//        Map<Long, ImsmetricIdentify> identifyMap = checkProxyUserPermission(metricIdList);
//
//        List<ImsmetricCheckResult> imsmetricCheckResultList = imsmetricCheckResultDao.queryImsmetricCheckResult(metricIdList,
//                String.valueOf(request.getStartDate()), String.valueOf(request.getEndDate()));
//
//        List<AlarmData> alarmDataList = new ArrayList<>();
//
//        imsmetricCheckResultList.stream().forEach(imsmetricCheckResult -> {
//            Long metricId0 = imsmetricCheckResult.getMetricId();
//            ImsmetricIdentify imsmetricIdentify = identifyMap.get(metricId0);
//            if (null != imsmetricIdentify) {
//                AlarmData alarmData = new AlarmData();
//                alarmData.setAlarmId(String.valueOf(imsmetricCheckResult.getId()));
//                String alarmTitle = String.format("[%s][%s]智能检测波动告警", imsmetricIdentify.getDatabaseName(), imsmetricIdentify.getTableName());
//                alarmData.setAlarmTitle(alarmTitle);
//                String metricName = "";
//                if (ImsRuleMetricTypeEnum.TABLE_COLLECT.getCode() == imsmetricIdentify.getMetricType()) {
//                    metricName = String.format("[%s][%s][%s][%s]%s", imsmetricIdentify.getDatabaseName(),
//                            imsmetricIdentify.getTableName(), imsmetricIdentify.getAttrName(),
//                            ImsRuleCalcTypeEnum.match(imsmetricIdentify.getCalcType()).getDescribe(),
//                            ImsRuleMetricTypeEnum.match(imsmetricIdentify.getMetricType()).getDescribe());
//
//                } else if (ImsRuleMetricTypeEnum.ENUM_COLLECT.getCode() == imsmetricIdentify.getMetricType() ||
//                        ImsRuleMetricTypeEnum.ORIGIN_COLLECT.getCode() == imsmetricIdentify.getMetricType()) {
//                    metricName = String.format("[%s][%s][%s][%s][%s]%s", imsmetricIdentify.getDatabaseName(),
//                            imsmetricIdentify.getTableName(), imsmetricIdentify.getAttrName(),
//                            imsmetricIdentify.getRowvalueenumName(),
//                            ImsRuleCalcTypeEnum.match(imsmetricIdentify.getCalcType()).getDescribe(),
//                            ImsRuleMetricTypeEnum.match(imsmetricIdentify.getMetricType()).getDescribe());
//                }
//                alarmData.setMetricName(metricName);
//                alarmData.setMetricId(metricId0);
//                alarmData.setAlarmLevel(ImsAlarmLevelEnum.match(imsmetricCheckResult.getAlarmLevel()).getAlarmLevel());
//                alarmData.setAlarmStatus(ImsAlarmStatusEnum.match(imsmetricCheckResult.getIsAlarm()).getAlarmStatus());
//                alarmData.setAlarmTime(df.format(imsmetricCheckResult.getCreateTime()));
//                alarmData.setDomainName("工具域");
//                alarmData.setDataTime(getTimeInMillis(imsmetricCheckResult.getCreateTime()));
//                alarmDataList.add(alarmData);
//            }
//        });
//
//        ImsAlarmDataQueryResponse imsAlarmDataQueryResponse = new ImsAlarmDataQueryResponse();
//        imsAlarmDataQueryResponse.setAlarmData(alarmDataList);
//
//        getDataResponse.setData(imsAlarmDataQueryResponse);
//        return new GeneralResponse<>(ResponseStatusConstants.OK, "OK", getDataResponse);
//    }

//    @Override
//    public GetAllResponse<ImsmetricCollectViewResponse> getMetricCollectList(MetricCollectQueryRequest request) throws UnExpectedRequestException {
//        LOGGER.info("Query metric collect list. request: {}", request.toString());
//        CommonChecker.checkString(request.getStartDate(), "start_date");
//        CommonChecker.checkString(request.getEndDate(), "end_date");
//        Long dataStartDate = DateUtil.parse(request.getStartDate(), DatePattern.NORM_DATE_PATTERN).getTime() / 1000;
//        Long dataEndDate = DateUtil.parse(request.getEndDate(), DatePattern.NORM_DATE_PATTERN).getTime() / 1000;
//        RequestParametersUtils.emptyStringToNull(request);
//
//        String userName = HttpUtils.getUserName(httpServletRequest);
//        User loginUser = userDao.findByUsername(userName);
//        if (loginUser == null) {
//            throw new UnExpectedRequestException("username is not exists.");
//        }
//        List<String> proxyUserNames = loginUser.getUserProxyUsers().stream().map(userProxyUser -> userProxyUser.getProxyUser().getProxyUserName()).distinct().collect(Collectors.toList());
//        if (CollectionUtils.isEmpty(proxyUserNames)) {
//            return new GetAllResponse<>(0, Collections.emptyList());
//        }
//
//        Page<ImsMetricCollectDto> imsMetricDtoPage = imsMetricDao.findImsMetricInAdvance(request.getMetricId(), request.getMetricValue(), request.getDatasourceType(), request.getClusterName(), request.getDatabase(), request.getTable(), request.getColumn(), request.getTemplateId(), dataStartDate, dataEndDate, request.getDataUser(), proxyUserNames, request.getPage(), request.getSize());
//
//        GetAllResponse<ImsmetricCollectViewResponse> getAllResponse = new GetAllResponse<>();
//        if (CollectionUtils.isEmpty(imsMetricDtoPage.getContent())) {
//            LOGGER.warn("Metric list is empty.");
//            return getAllResponse;
//        }
//
//        Map<Long, MetricExtInfo> metricIdMap = Collections.emptyMap();
//        List<Long> metricIds = imsMetricDtoPage.getContent().stream().map(ImsMetricCollectDto::getMetricId).collect(Collectors.toList());
//        List<MetricExtInfo> metricExtInfoList = metricExtInfoDao.list(metricIds, MetricClassEnum.IMS_METRIC);
//        if (CollectionUtils.isNotEmpty(metricExtInfoList)) {
//            metricIdMap = metricExtInfoList.stream().collect(Collectors.toMap(MetricExtInfo::getMetricId, Function.identity(), (v1, v2) -> v1));
//        }
//
//        List<Template> templateList = ruleTemplateDao.findByIds(imsMetricDtoPage.stream().map(ImsMetricCollectDto::getTemplateId).collect(Collectors.toList()));
//        Map<Long, String> templateIdAndNameMap = templateList.stream().collect(Collectors.toMap(Template::getId, Template::getName, (oldVal, newVal) -> oldVal));
//
//        Map<Long, MetricExtInfo> finalMetricIdMapMap = metricIdMap;
//        List<ImsmetricCollectViewResponse> imsmetricCollectViewResponses = imsMetricDtoPage.stream()
//                .map(imsMetric -> {
//                    ImsmetricCollectViewResponse imsmetricCollectViewResponse = new ImsmetricCollectViewResponse(imsMetric, templateIdAndNameMap.get(imsMetric.getTemplateId()));
//                    imsmetricCollectViewResponse.setIdentifyValue(ScanRulesUtils.auditSenseInfoFromIdentifyValue(imsmetricCollectViewResponse.getIdentifyValue()));
//                    if (finalMetricIdMapMap.containsKey(imsMetric.getMetricId())) {
//                        MetricExtInfo metricExtInfo = finalMetricIdMapMap.get(imsMetric.getMetricId());
//                        imsmetricCollectViewResponse.setCalculationMode(metricExtInfo.getCalculationMode());
//                        if (StringUtils.isNotBlank(metricExtInfo.getMonitoringCapabilities())) {
//                            imsmetricCollectViewResponse.setMonitoringCapabilities(Arrays.asList(StringUtils.split(metricExtInfo.getMonitoringCapabilities(), SpecCharEnum.COMMA.getValue())));
//                        }
//                        imsmetricCollectViewResponse.setBusinessDomain(metricExtInfo.getBusinessDomain());
//                        imsmetricCollectViewResponse.setBusinessModel(metricExtInfo.getBusinessModel());
//                        imsmetricCollectViewResponse.setBusinessStrategy(metricExtInfo.getBusinessStrategy());
//                        imsmetricCollectViewResponse.setMetricDefinition(metricExtInfo.getMetricDefinition());
//                        imsmetricCollectViewResponse.setBusinessSystem(metricExtInfo.getBusinessSystem());
//                        imsmetricCollectViewResponse.setImsmetricDesc(metricExtInfo.getImsmetricDesc());
//                    }
//                    return imsmetricCollectViewResponse;
//                }).collect(Collectors.toList());
//
//        getAllResponse.setTotal(imsMetricDtoPage.getTotalElements());
//        getAllResponse.setData(imsmetricCollectViewResponses);
//        return getAllResponse;
//    }
//
//    @Override
//    public List<ImsmetricCollectViewOuterResponse> getMetricCollectListForOuter(MetricCollectOuterQueryRequest request) {
//        LOGGER.info("Query metric collect list from outer. request: {}", request.toString());
//        List<ImsMetricCollectDto> collectDtoList = imsMetricDao.findImsMetric(request.getClusterName(), request.getDatabase(), request.getTable(), request.getColumn(), request.getCalcuUnitName());
//        return collectDtoList.stream().map(ImsmetricCollectViewOuterResponse::new).collect(Collectors.toList());
//    }
//
//    private void checkMetricCollectUserPermissions(List<ImsMetricCollect> imsMetricCollectList) throws PermissionDeniedRequestException {
//        String userName = HttpUtils.getUserName(httpServletRequest);
//        User loginUser = userDao.findByUsername(userName);
//        List<String> proxyUserNames = loginUser.getUserProxyUsers().stream().map(userProxyUser -> userProxyUser.getProxyUser().getProxyUserName()).distinct().collect(Collectors.toList());
//        List<String> needProxyUserNames = imsMetricCollectList.stream().map(imsMetricCollect -> imsMetricCollect.getProxyUser()).distinct().collect(Collectors.toList());
//        Collection<String> subtract = CollectionUtils.subtract(needProxyUserNames, proxyUserNames);
//        if (CollectionUtils.isNotEmpty(subtract)) {
//            throw new PermissionDeniedRequestException("{&HAS_NO_MATCHING_PROXY_USERS_PERMISSIONS}");
//        }
//    }
//
//    @Override
//    public GetAllResponse<MetricTemplateQueryResponse> getTemplateList(TemplatePageRequest request) throws UnExpectedRequestException {
//        LOGGER.info("get metric template request detail: {}", request.toString());
//        request.checkRequest();
//        String dataSourceType = request.getDataSourceType();
//        Integer dataSourceTypeCode = TemplateDataSourceTypeEnum.getCode(dataSourceType);
//        if (StringUtils.isNotBlank(dataSourceType) && dataSourceTypeCode == null) {
//            throw new UnExpectedRequestException("Illegal parameter: data_source_type");
//        }
//
//        List<Template> templates;
//        long total = 0;
//        User userInDb;
//        if (StringUtils.isNotBlank(request.getUserName())) {
//            userInDb = userDao.findByUsername(request.getUserName());
//        } else {
//            userInDb = userDao.findById(HttpUtils.getUserId(httpServletRequest));
//        }
//        if (userInDb == null) {
//            throw new UnExpectedRequestException("User name {&REQUEST_CAN_NOT_BE_NULL}");
//        }
//
//        String createUserId = null;
//        if (StringUtils.isNotBlank(request.getCreateName())) {
//            User user = userDao.findByUsername(request.getCreateName());
//            createUserId = String.valueOf(user == null ? 0 : user.getId());
//        }
//        String modifyUserId = null;
//        if (StringUtils.isNotBlank(request.getModifyName())) {
//            User user = userDao.findByUsername(request.getModifyName());
//            modifyUserId = String.valueOf(user == null ? 0 : user.getId());
//        }
//
//        Set<String> actionRangeSet = CollectionUtils.isEmpty(request.getActionRange()) ? null : request.getActionRange();
//
//        List<UserRole> userRoles = userRoleDao.findByUser(userInDb);
//        Integer roleType = roleService.getRoleType(userRoles);
//        if (roleType.equals(RoleSystemTypeEnum.ADMIN.getCode())) {
//            Page<Template> resultPage = ruleTemplateDao.findTemplatesWithAdmin(RuleTemplateTypeEnum.METRIC_COLLECT.getCode(), dataSourceTypeCode, TableDataTypeEnum.RULE_TEMPLATE.getCode()
//                    , request.getCnName(), request.getEnName(), request.getVerificationLevel()
//                    , request.getVerificationType(), createUserId, modifyUserId, request.getDevDepartmentId(), request.getOpsDepartmentId()
//                    , actionRangeSet, request.getCreateStartTime(), request.getCreateEndTime(), request.getModifyStartTime(), request.getModifyEndTime()
//                    , request.getTemplateId(), request.getDescription()
//                    , request.getPage(), request.getSize());
//            templates = resultPage.getContent();
//            total = resultPage.getTotalElements();
//        } else if (roleType.equals(RoleSystemTypeEnum.DEPARTMENT_ADMIN.getCode())) {
//            List<Long> departmentIds = userRoles.stream().map(UserRole::getRole).filter(Objects::nonNull)
//                    .map(Role::getDepartment).filter(Objects::nonNull)
//                    .map(Department::getId).collect(Collectors.toList());
//            if (Objects.nonNull(userInDb.getDepartment())) {
//                departmentIds.add(userInDb.getDepartment().getId());
//            }
//            List<Long> devAndOpsInfoWithDeptList = subDepartmentPermissionService.getSubDepartmentIdList(departmentIds);
//            Page<Template> resultPage = ruleTemplateDao.findTemplates(RuleTemplateTypeEnum.METRIC_COLLECT.getCode(), dataSourceTypeCode
//                    , TableDataTypeEnum.RULE_TEMPLATE.getCode(), devAndOpsInfoWithDeptList.isEmpty() ? null : devAndOpsInfoWithDeptList
//                    , userInDb.getId(), request.getCnName(), request.getEnName(), request.getVerificationLevel()
//                    , request.getVerificationType(), createUserId, modifyUserId, request.getDevDepartmentId(), request.getOpsDepartmentId()
//                    , actionRangeSet, request.getCreateStartTime(), request.getCreateEndTime(), request.getModifyStartTime(), request.getModifyEndTime()
//                    , request.getTemplateId(), request.getDescription()
//                    , request.getPage(), request.getSize());
//            templates = resultPage.getContent();
//            total = resultPage.getTotalElements();
//        } else {
//            Page<Template> resultPage = ruleTemplateDao.findTemplates(RuleTemplateTypeEnum.METRIC_COLLECT.getCode(), dataSourceTypeCode
//                    , TableDataTypeEnum.RULE_TEMPLATE.getCode(), Arrays.asList(userInDb.getSubDepartmentCode()), userInDb.getId(), request.getCnName(), request.getEnName()
//                    , request.getVerificationLevel(), request.getVerificationType(), createUserId, modifyUserId, request.getDevDepartmentId()
//                    , request.getOpsDepartmentId(), actionRangeSet, request.getCreateStartTime(), request.getCreateEndTime(), request.getModifyStartTime(), request.getModifyEndTime()
//                    , request.getTemplateId(), request.getDescription()
//                    , request.getPage(), request.getSize());
//            templates = resultPage.getContent();
//            total = resultPage.getTotalElements();
//        }
//
//        List<MetricTemplateQueryResponse> responseList = templates.stream().map(MetricTemplateQueryResponse::new).collect(Collectors.toList());
//        setDatasourceTypeToResp(responseList);
//        setDataVisibilityToResp(responseList);
//        List<Long> calcuUnitIds = templates.stream().map(Template::getCalcuUnitId).collect(Collectors.toList());
//        List<CalcuUnit> calcuUnitList = calcuUnitDao.findByIds(calcuUnitIds);
//        Map<Long, String> calcuSqlMap = calcuUnitList.stream().collect(Collectors.toMap(CalcuUnit::getId, CalcuUnit::getSqlAction, (oldVal, newOld) -> oldVal));
//        responseList.forEach(metricTemplateQueryResponse -> metricTemplateQueryResponse.setSqlAction(calcuSqlMap.getOrDefault(metricTemplateQueryResponse.getCalcuUnitId(), "")));
//
//        GetAllResponse<MetricTemplateQueryResponse> response = new GetAllResponse<>(total, responseList);
//
//        return response;
//    }
//
//    @Override
//    public List<String> findAllDataUsers() {
//        return imsmetricDataDao.findAllDataUsers();
//    }
//
//    @Override
//    public DataSourcesConditionResponse getAllDataSources(String cluster, String db) throws UnExpectedRequestException {
//        DataSourcesConditionResponse dataSourcesConditionResponse = new DataSourcesConditionResponse();
//        List<ImsMetricCollect> metricCollectList = imsMetricCollectDao.findByDatasource(cluster, db, null, null, null);
//
//        String userName = HttpUtils.getUserName(httpServletRequest);
//        User loginUser = userDao.findByUsername(userName);
//        if (loginUser == null) {
//            throw new UnExpectedRequestException("username is not exists.");
//        }
//        List<String> proxyUserNames = loginUser.getUserProxyUsers().stream().map(userProxyUser -> userProxyUser.getProxyUser().getProxyUserName()).distinct().collect(Collectors.toList());
//        if (CollectionUtils.isEmpty(proxyUserNames)) {
//            dataSourcesConditionResponse.setClusterNames(Collections.emptyList());
//            dataSourcesConditionResponse.setDatabases(Collections.emptyList());
//            dataSourcesConditionResponse.setTables(Collections.emptyList());
//            dataSourcesConditionResponse.setColumns(Collections.emptyList());
//            return dataSourcesConditionResponse;
//        }
//        metricCollectList = metricCollectList.stream().filter(metricCollect -> proxyUserNames.contains(metricCollect.getProxyUser())).collect(Collectors.toList());
//
//        dataSourcesConditionResponse.setClusterNames(metricCollectList.stream().map(ImsMetricCollect::getClusterName).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList()));
//        dataSourcesConditionResponse.setDatabases(metricCollectList.stream().map(ImsMetricCollect::getDbName).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList()));
//        dataSourcesConditionResponse.setTables(metricCollectList.stream().map(ImsMetricCollect::getTableName).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList()));
//        dataSourcesConditionResponse.setColumns(metricCollectList.stream().map(ImsMetricCollect::getColumnName).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList()));
//        return dataSourcesConditionResponse;
//    }
//
//    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
//    @Override
//    public void createMetricTemplate(AddCalcuTemplateRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
//        User userInDb = userDao.findById(HttpUtils.getUserId(httpServletRequest));
//        List<UserRole> userRoles = userRoleDao.findByUser(userInDb);
//        Integer roleType = roleService.getRoleType(userRoles);
//
//        List<DepartmentSubInfoRequest> dataVisibilityList = request.getVisibilityDepartmentList();
//        boolean allVisibility = false;
//        if (CollectionUtils.isNotEmpty(dataVisibilityList)) {
//            allVisibility = dataVisibilityList.stream().map(DepartmentSubInfoRequest::getId).anyMatch(DataVisibilityService.ALL_DEPARTMENT_VISIBILITY::equals);
//        }
//        subDepartmentPermissionService.checkEditablePermission(roleType, userInDb, null, request.getDevDepartmentId(), request.getOpsDepartmentId(), allVisibility);
//
//        Template template = ruleTemplateDao.findTemplateByEnName(request.getEnName());
//        if (null != template) {
//            throw new UnExpectedRequestException("Template En name {&ALREADY_EXIST}");
//        }
//        Optional<Template> templateOptional = ruleTemplateDao.getDefaultByName(request.getCnName());
//        if (templateOptional.isPresent()) {
//            throw new UnExpectedRequestException("Template name {&ALREADY_EXIST}");
//        }
//        String nowDate = DateUtil.now();
//
//        CalcuUnit calcuUnit = new CalcuUnit();
//        calcuUnit.setName(request.getEnName());
//        calcuUnit.setUdfId(request.getUdfId());
//        calcuUnit.setSqlAction(request.getSqlAction());
//        calcuUnit.setCreateUser(userInDb.getUsername());
//        calcuUnit.setCreateTime(nowDate);
//        calcuUnit = calcuUnitDao.save(calcuUnit);
//
//        Template newTemplate = new Template();
//        newTemplate.setName(request.getCnName());
//        newTemplate.setEnName(request.getEnName());
//        newTemplate.setClusterNum(-1);
//        newTemplate.setDbNum(-1);
//        newTemplate.setTableNum(-1);
//        newTemplate.setFieldNum(-1);
//        newTemplate.setDescription(request.getDescription());
//        newTemplate.setTemplateType(RuleTemplateTypeEnum.METRIC_COLLECT.getCode());
//        newTemplate.setImportExportName(UuidGenerator.generate());
//        newTemplate.setDevDepartmentName(request.getDevDepartmentName());
//        newTemplate.setOpsDepartmentName(request.getOpsDepartmentName());
//        newTemplate.setDevDepartmentId(request.getDevDepartmentId());
//        newTemplate.setOpsDepartmentId(request.getOpsDepartmentId());
//        newTemplate.setCreateUser(userInDb);
//        newTemplate.setCreateTime(nowDate);
//        newTemplate.setCalcuUnitId(calcuUnit.getId());
//        newTemplate = ruleTemplateDao.saveTemplate(newTemplate);
//
//        TemplateDataSourceType templateDataSourceType = new TemplateDataSourceType(TemplateDataSourceTypeEnum.HIVE.getCode(), newTemplate);
//        templateDataSourceTypeDao.save(templateDataSourceType);
//
////        Save data visibility
//        dataVisibilityService.saveBatch(newTemplate.getId(), TableDataTypeEnum.RULE_TEMPLATE, request.getVisibilityDepartmentList());
//    }
//
//    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
//    @Override
//    public void modifyMetricTemplate(ModifyCalcuTemplateRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
//        User userInDb = userDao.findById(HttpUtils.getUserId(httpServletRequest));
//        List<UserRole> userRoles = userRoleDao.findByUser(userInDb);
//        Integer roleType = roleService.getRoleType(userRoles);
//        Template templateInDb = ruleTemplateDao.findById(request.getRuleTemplateId());
//        if (templateInDb == null) {
//            throw new UnExpectedRequestException("Template is not exists.");
//        }
//        if (!templateInDb.getName().equals(request.getCnName())) {
//            Template templateByName = ruleTemplateDao.findByName(request.getCnName());
//            if (templateByName != null) {
//                throw new UnExpectedRequestException("Template name {&ALREADY_EXIST}");
//            }
//        }
//
//        List<DepartmentSubInfoRequest> dataVisibilityList = request.getVisibilityDepartmentList();
//        boolean allVisibility = false;
//        if (CollectionUtils.isNotEmpty(dataVisibilityList)) {
//            allVisibility = dataVisibilityList.stream().map(DepartmentSubInfoRequest::getId).anyMatch(DataVisibilityService.ALL_DEPARTMENT_VISIBILITY::equals);
//        }
//        String createUser = Objects.nonNull(templateInDb.getCreateUser()) ? templateInDb.getCreateUser().getUsername() : null;
//        subDepartmentPermissionService.checkEditablePermission(roleType, userInDb, createUser, request.getDevDepartmentId(), request.getOpsDepartmentId(), allVisibility);
//
//        Optional<CalcuUnit> calcuUnitOptional = calcuUnitDao.findById(templateInDb.getCalcuUnitId());
//        if (!calcuUnitOptional.isPresent()) {
//            throw new UnExpectedRequestException("CalcuUnit is not exists.");
//        }
//        String nowDate = DateUtil.now();
//
//        CalcuUnit calcuUnit = calcuUnitOptional.get();
//        calcuUnit.setUdfId(request.getUdfId());
//        calcuUnit.setSqlAction(request.getSqlAction());
//        calcuUnit.setModifyUser(userInDb.getUsername());
//        calcuUnit.setModifyTime(nowDate);
//        calcuUnitDao.save(calcuUnit);
//
//        templateInDb.setName(request.getCnName());
//        templateInDb.setDescription(request.getDescription());
//        templateInDb.setDevDepartmentName(request.getDevDepartmentName());
//        templateInDb.setOpsDepartmentName(request.getOpsDepartmentName());
//        templateInDb.setDevDepartmentId(request.getDevDepartmentId());
//        templateInDb.setOpsDepartmentId(request.getOpsDepartmentId());
//        templateInDb.setModifyUser(userInDb);
//        templateInDb.setModifyTime(nowDate);
//        ruleTemplateDao.saveTemplate(templateInDb);
//
////        Update data visibility
//        dataVisibilityService.delete(templateInDb.getId(), TableDataTypeEnum.RULE_TEMPLATE);
//        dataVisibilityService.saveBatch(templateInDb.getId(), TableDataTypeEnum.RULE_TEMPLATE, request.getVisibilityDepartmentList());
//    }
//
//    @Override
//    public MetricTemplateDetailResponse getMetricTemplateDetail(Long templateId) throws UnExpectedRequestException {
//        Template template = ruleTemplateDao.findById(templateId);
//        if (template == null) {
//            throw new UnExpectedRequestException("template is not exists.");
//        }
//        Optional<CalcuUnit> calcuUnitOptional = calcuUnitDao.findById(template.getCalcuUnitId());
//        if (!calcuUnitOptional.isPresent()) {
//            throw new UnExpectedRequestException("calcu_unit is not exists.");
//        }
//        MetricTemplateDetailResponse metricTemplateDetailResponse = new MetricTemplateDetailResponse(template, calcuUnitOptional.get());
//        List<DataVisibility> dataVisibilityList = dataVisibilityService.filter(templateId, TableDataTypeEnum.RULE_TEMPLATE);
//        if (CollectionUtils.isNotEmpty(dataVisibilityList)) {
//            List<DepartmentSubInfoResponse> departmentInfoResponses = dataVisibilityList.stream().map(DepartmentSubInfoResponse::new).collect(Collectors.toList());
//            metricTemplateDetailResponse.setVisibilityDepartmentList(departmentInfoResponses);
//        }
//        return metricTemplateDetailResponse;
//    }
//
//    private void setDatasourceTypeToResp(List<MetricTemplateQueryResponse> responseList) {
//        List<Long> templateIds = responseList.stream().map(MetricTemplateQueryResponse::getRuleTemplateId).collect(Collectors.toList());
//        List<TemplateDataSourceType> templateDataSourceTypes = templateDataSourceTypeDao.findByTemplateIds(templateIds);
//        if (CollectionUtils.isEmpty(templateDataSourceTypes)) {
//            return;
//        }
//        Map<Long, List<TemplateDataSourceType>> dataVisibilityMap = templateDataSourceTypes.stream().collect(Collectors.groupingBy(templateDataSourceType -> templateDataSourceType.getTemplate().getId()));
//
//        for (MetricTemplateQueryResponse ruleTemplateResponse : responseList) {
//            List<TemplateDataSourceType> templateDataSourceTypeList = dataVisibilityMap.get(ruleTemplateResponse.getRuleTemplateId());
//            if (CollectionUtils.isNotEmpty(templateDataSourceTypeList)) {
//                List<Integer> types = templateDataSourceTypeList.stream().map(TemplateDataSourceType::getDataSourceTypeId).collect(Collectors.toList());
//                ruleTemplateResponse.setDatasourceType(types);
//            }
//        }
//    }
//
//    private void setDataVisibilityToResp(List<MetricTemplateQueryResponse> responseList) {
//        List<Long> templateIds = responseList.stream().map(MetricTemplateQueryResponse::getRuleTemplateId).collect(Collectors.toList());
//        List<DataVisibility> dataVisibilityList = dataVisibilityService.filterByIds(templateIds, TableDataTypeEnum.RULE_TEMPLATE);
//        if (CollectionUtils.isEmpty(dataVisibilityList)) {
//            return;
//        }
//        Map<Long, List<DataVisibility>> dataVisibilityMap = dataVisibilityList.stream().collect(Collectors.groupingBy(DataVisibility::getTableDataId));
//
//        for (RuleTemplateResponse ruleTemplateResponse : responseList) {
//            List<DataVisibility> templateDtaVisibilityList = dataVisibilityMap.get(ruleTemplateResponse.getRuleTemplateId());
//            if (CollectionUtils.isNotEmpty(templateDtaVisibilityList)) {
//                List<DepartmentSubInfoResponse> departmentInfoResponses = templateDtaVisibilityList.stream().map(DepartmentSubInfoResponse::new).collect(Collectors.toList());
//                ruleTemplateResponse.setVisibilityDepartmentNameList(departmentInfoResponses);
//            }
//        }
//    }
//
//    private Long getTimeInMillis(Date time) {
//        long times = 0;
//        try {
//            String timeStr = DateUtils.format(time, DateUtils.ISO8601_DATE_PATTERN);
//            Date date = df.parse(timeStr + " 00:00:00");
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(date);
//            times = cal.getTimeInMillis();
//        } catch (ParseException e) {
//            LOGGER.error("times format error, {}", time);
//        }
//        return times;
//    }

//    private Map<Long, ImsmetricIdentify> checkProxyUserPermission(List<Long> metricIdList) throws UnExpectedRequestException, PermissionDeniedRequestException {
//        String userName = HttpUtils.getUserName(httpServletRequest);
//        LOGGER.info("Start to get rule metric, rule metric ID: [{}], user: [{}]", metricIdList, userName);
//        User loginUser = userDao.findByUsername(userName);
//        List<String> proxyUserNames = loginUser.getUserProxyUsers().stream().map(userProxyUser -> userProxyUser.getProxyUser().getProxyUserName()).distinct().collect(Collectors.toList());
//        //check proxyUser
//        List<ImsmetricIdentify> identify = imsmetricIdentifyDao.queryIdentify(metricIdList);
//        Map<Long, ImsmetricIdentify> identifyMap = identify.stream().collect(Collectors.toMap(ImsmetricIdentify::getMetricId, Function.identity(), (oldVal, newOld) -> oldVal));
//        if (MapUtils.isEmpty(identifyMap)) {
//            throw new UnExpectedRequestException("{&CAN_NOT_FIND_METRIC_DATA}");
//        }
//        List<String> needProxyUserNames = identify.stream().map(imsmetricIdentify -> imsmetricIdentify.getDatasourceUser()).distinct().collect(Collectors.toList());
//        Collection<String> subtract = CollectionUtils.subtract(needProxyUserNames, proxyUserNames);
//        if (CollectionUtils.isNotEmpty(subtract)) {
//            throw new PermissionDeniedRequestException("{&HAS_NO_MATCHING_PROXY_USERS_PERMISSIONS}");
//        }
//        return identifyMap;
//    }

//    @Override
//    public ImsMetricCollectQueryResponse getMetricIdentifyById(String metricId) throws UnExpectedRequestException {
//        ImsMetricCollectQueryResponse imsMetricCollectQueryResponse = getMetricCollectById(metricId);
//        String calcuUnitName = imsMetricCollectQueryResponse.getCalcuUnitName();
//        imsMetricCollectQueryResponse.setEnumType(Boolean.FALSE);
//        if (StringUtils.isNotBlank(calcuUnitName) && CALCU_UNIT_NAME_LIST.contains(calcuUnitName)) {
//            imsMetricCollectQueryResponse.setEnumType(Boolean.TRUE);
//            return imsMetricCollectQueryResponse;
//        }
//        return imsMetricCollectQueryResponse;
//    }
//
//    private ImsMetricCollectQueryResponse getMetricCollectById(String metricId) throws UnExpectedRequestException {
//        if (StringUtils.isBlank(metricId)) {
//            throw new UnExpectedRequestException("metricId is null.");
//        }
//        Tuple identifyTuple = imsMetricDao.getMetricIdentifyById(metricId);
//        if (null == identifyTuple) {
//            throw new UnExpectedRequestException("{&CAN_NOT_FIND_METRIC_DATA}");
//        }
//        ImsMetricCollectQueryResponse imsMetricCollectQueryResponse = new ImsMetricCollectQueryResponse();
//        imsMetricCollectQueryResponse.setMetricId(identifyTuple.get("metric_id", BigInteger.class));
//        imsMetricCollectQueryResponse.setDbName(identifyTuple.get("db_name", String.class));
//        imsMetricCollectQueryResponse.setTableName(identifyTuple.get("table_name", String.class));
//        imsMetricCollectQueryResponse.setColumnName(identifyTuple.get("column_name", String.class));
//        String identifyValue = identifyTuple.get("identify_value", String.class);
//        // 脱敏
//        identifyValue = ScanRulesUtils.auditSenseInfoFromIdentifyValue(identifyValue);
//        imsMetricCollectQueryResponse.setEnumValue(identifyValue);
//        imsMetricCollectQueryResponse.setProxyUser(identifyTuple.get("proxy_user", String.class));
//        imsMetricCollectQueryResponse.setCalcuUnitName(identifyTuple.get("calcu_unit_name", String.class));
//        if (StringUtils.isEmpty(imsMetricCollectQueryResponse.getDbName()) || StringUtils.isEmpty(imsMetricCollectQueryResponse.getTableName())
//                || StringUtils.isEmpty(imsMetricCollectQueryResponse.getColumnName())) {
//            throw new UnExpectedRequestException("{&METRIC_COLLECT_DATA_ERROR}");
//        }
//        return imsMetricCollectQueryResponse;
//    }
//
//    @Override
//    public GeneralResponse<Map<String, Object>> getMetricRelation(String metricId) throws UnExpectedRequestException {
//        ImsMetricCollectQueryResponse imsMetricCollectQueryResponse = getMetricCollectById(metricId);
//        if (ImsRuleCalcTypeEnum.COUNT.getDescribe().equals(imsMetricCollectQueryResponse.getCalcuUnitName())) {
//            throw new UnExpectedRequestException("count of metric relation is null.");
//        }
//        List<String> calcuUnitNameList = Lists.newArrayList(ImsRuleCalcTypeEnum.COUNT.getDescribe());
//        List<Tuple> tupleList = imsMetricDao.getMetricIdentify(imsMetricCollectQueryResponse.getDbName(),
//                imsMetricCollectQueryResponse.getTableName(), calcuUnitNameList);
//        if (CollectionUtils.isEmpty(tupleList)) {
//            throw new UnExpectedRequestException("metric relation is null.");
//        }
//        Map<String, Object> relationMetricMap = new HashMap<>();
//        relationMetricMap.put("metricId", new BigInteger(metricId));
//        relationMetricMap.put("relationMetricId", tupleList.get(0).get("metric_id", BigInteger.class));
//        return new GeneralResponse(ResponseStatusConstants.OK, "OK", relationMetricMap);
//    }
//
//    @Value("${rule.datasource.max-size:1000}")
//    private Integer ruleDatasourceMaxSize;
//    @Value("${rule.datasource.per-size:500}")
//    private Integer ruleDatasourcePerSize;
//    @Value("${overseas_external_version.enable:false}")
//    private Boolean overseasVersionEnabled;
//    @Autowired
//    private MetaDataClient metaDataClient;
//    @Autowired
//    private ClusterInfoDao clusterInfoDao;
//    @Autowired
//    private MetadataSyncUpdater metadataSyncUpdater;
//    private AtomicBoolean isEndedStatusOnSyncMetadata = new AtomicBoolean(Boolean.TRUE);
//
//    @Override
//    public GeneralResponse<DataMapResultInfo<String>> syncMetadata(String userName) throws UnExpectedRequestException {
//        if (overseasVersionEnabled){
//            LOGGER.info("disable sync metadata");
//            return new GeneralResponse(ResponseStatusConstants.OK, "THIS_FEATURE_IS_TEMPORARILY_DISABLED", null);
//        }
//        LOGGER.info("Ready to sync metric metadata, loginUser: {}", userName);
//        if (isRepeatSubmitOnSyncMetadata()) {
//            throw new UnExpectedRequestException("已在同步，请勿重复提交");
//        }
//        Map<String, String> clusterNameAndTypeMap = getClusterNameAndTypeMap();
//
//        User user = userDao.findByUsername(userName);
//        List<String> proxyUserList = user.getUserProxyUsers().stream().map(UserProxyUser::getProxyUser).map(ProxyUser::getProxyUserName).collect(Collectors.toList());
//        proxyUserList.add(userName);
//
//        sysMetadataThreadPool.execute(() -> {
//            isEndedStatusOnSyncMetadata.set(Boolean.FALSE);
//            try {
//                Page<ImsMetricCollectDto> imsMetricDtoPage = imsMetricDao.findImsMetricInAdvance(null, null
//                        , null, null, null, null
//                        , null, null, null, null, null, proxyUserList, 0, ruleDatasourcePerSize);
//                int totalPage = imsMetricDtoPage.getTotalPages();
//                int currentPage = 0;
//                do {
//                    List<ImsMetricCollectDto> imsMetricCollectDtoList = imsMetricDtoPage.getContent();
//                    if (CollectionUtils.isEmpty(imsMetricCollectDtoList)) {
//                        LOGGER.info("List of ImsMetricCollectDto is empty");
//                        break;
//                    }
//                    List<ImsMetric> imsMetricList = imsMetricCollectDtoList.stream().map(imsMetricCollectDto -> {
//                        ImsMetric imsMetric = new ImsMetric();
//                        imsMetric.setMetricCollectId(imsMetricCollectDto.getCollectId());
//                        imsMetric.setId(imsMetricCollectDto.getMetricId());
//                        return imsMetric;
//                    }).collect(Collectors.toList());
//                    Map<Long, ImsMetricCollect> collectMap = imsMetricCollectDtoList.stream().map(imsMetricCollectDto -> {
//                        ImsMetricCollect imsMetricCollect = new ImsMetricCollect();
//                        imsMetricCollect.setId(imsMetricCollectDto.getCollectId());
//                        imsMetricCollect.setClusterName(imsMetricCollectDto.getClusterName());
//                        imsMetricCollect.setDbName(imsMetricCollectDto.getDbName());
//                        imsMetricCollect.setTableName(imsMetricCollectDto.getTableName());
//                        imsMetricCollect.setColumnName(imsMetricCollectDto.getColumnName());
//                        imsMetricCollect.setDatasourceType(imsMetricCollectDto.getDatasourceType());
//                        return imsMetricCollect;
//                    }).collect(Collectors.toMap(ImsMetricCollect::getId, Function.identity(), (v1, v2) -> v1));
//                    LOGGER.info("Query data from qualitis_imsmetric, page: {}, count: {}", currentPage, imsMetricList.size());
//                    metadataSyncUpdater.submitImsMetric(imsMetricList, collectMap, clusterNameAndTypeMap, userName);
//
//                    ++currentPage;
//
//                    try {
//                        Thread.sleep(60000);
//                    } catch (InterruptedException e) {
//                        LOGGER.warn("Thread was interrupted", e);
//                    }
//
//                    imsMetricDtoPage = imsMetricDao.findImsMetricInAdvance(null, null
//                            , null, null, null, null
//                            , null, null, null, null, null, proxyUserList, currentPage, ruleDatasourcePerSize);
//                } while (currentPage <= totalPage);
//            } finally {
//                isEndedStatusOnSyncMetadata.set(Boolean.TRUE);
//                LOGGER.info("Finished to sync metadata to qualitis_rule_datasource ");
//            }
//        });
//
//        return new GeneralResponse(ResponseStatusConstants.OK, "success", null);
//    }
//
//    private Map<String, String> getClusterNameAndTypeMap() {
//        List<ClusterInfo> clusterInfoList = clusterInfoDao.findAllClusterInfo(0, 500);
//        return clusterInfoList.stream().collect(Collectors.toMap(ClusterInfo::getClusterName, ClusterInfo::getClusterType, (oldVal, newVal) -> oldVal));
//    }
//
//    private boolean isRepeatSubmitOnSyncMetadata() {
//        return !isEndedStatusOnSyncMetadata.get();
//    }

}
