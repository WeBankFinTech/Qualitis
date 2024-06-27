package com.webank.wedatasphere.qualitis.timer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.webank.wedatasphere.qualitis.bean.EmailEntity;
import com.webank.wedatasphere.qualitis.bean.SendMailMakeRequest;
import com.webank.wedatasphere.qualitis.client.MailClient;
import com.webank.wedatasphere.qualitis.config.EsbSdkConfig;
import com.webank.wedatasphere.qualitis.config.ThreadPoolTaskConfig;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constant.TaskStatusEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.dao.ApplicationDao;
import com.webank.wedatasphere.qualitis.dao.MailLockRecordDao;
import com.webank.wedatasphere.qualitis.dao.TaskDao;
import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.entity.MailLockRecord;
import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.entity.TaskRuleSimple;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.ha.AbstractServiceCoordinator;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.report.constant.ExecutionFrequencyEnum;
import com.webank.wedatasphere.qualitis.report.dao.SubscribeOperateReportDao;
import com.webank.wedatasphere.qualitis.report.dao.SubscriptionRecordDao;
import com.webank.wedatasphere.qualitis.report.entity.SubscribeOperateReport;
import com.webank.wedatasphere.qualitis.report.entity.SubscriptionRecord;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.util.HtmlTableGeneratorUtils;
import com.webank.wedatasphere.qualitis.util.map.CustomObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @author v_gaojiedeng@webank.com
 */
@Configuration
public class PushReportScheduled {

    @Autowired
    private SubscribeOperateReportDao subscribeOperateReportDao;
    @Autowired
    private SubscriptionRecordDao subscriptionRecordDao;
    @Autowired
    private RuleDao ruleDao;
    @Autowired
    private ApplicationDao applicationDao;
    @Autowired
    private TaskDao taskDao;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private EsbSdkConfig esbSdkConfig;
    @Autowired
    private MailLockRecordDao mailLockRecordDao;
    @Autowired
    private ThreadPoolTaskConfig threadPoolTaskConfig;

    private static final Logger LOGGER = LoggerFactory.getLogger(PushReportScheduled.class);
    private static final String DAILY_DATE = "daily";
    private static final String WEEKLY_DATE = "weekly";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Autowired
    private AbstractServiceCoordinator abstractServiceCoordinator;

    /**
     * 每个批次数据条目
     */
    private static final int BATCH_SIZE = 10;

    private static final List<String> TABLE_HEADERS = Arrays.asList("项目ID", "项目名称", "已配置规则数量", "过去周期已配置规则增量",
            "已配置规则的表数量", "过去周期已配置规则的表增量", "已配置规则的KPI表数量", "过去周期已配置规则的KPI表增量", "KPI 表已配置规则数量",
            "过去周期KPI表已配置规则增量", "在调度监控的规则数（过去周期）", "通过校验规则数（过去周期）", "未通过校验规则数（过去周期）", "失败规则数（过去周期）");

    /**
     * 用户订阅项目的运营指标，多项目汇总，一行统计记录，接入邮件系统，发送报表：
     * 已配置规则的表数量；
     * 过去周期已配置规则的表增量；
     * 已配置规则数量；
     * 过去周期已配置规则增量；
     * 已配置规则的 KPI 表数量；
     * 过去周期已配置规则的 KPI 表增量；
     * KPI 表已配置规则数量；
     * 过去周期 KPI 表已配置规则增量；
     * 在调度监控的规则数（过去周期）；
     * 通过校验规则数（过去周期）；
     * 未通过校验规则数（过去周期）；
     * 失败规则数（过去周期）;
     * <p>
     * 0 0 2 * * 1 每周周一2点 (避免与其他的调度冲突)
     * 0 0 1 * * ? 每天1时
     */

    @Scheduled(cron = "${report.daily.cron}")
    @Async("threadPoolTaskDailyExecutor")
    public void dailyMonitor() {
        if (!threadPoolTaskConfig.getReportCronEnable()) {
            return;
        }
        try {
            LOGGER.info(Thread.currentThread().getName() + " >>>>>>>>>> Start operating report scheduling <<<<<<<<<<");
            abstractServiceCoordinator.coordinate();
            handleOperatingReportLogic(ExecutionFrequencyEnum.DAILY.getCode(), DAILY_DATE);
        } catch (Exception e) {
            LOGGER.error("Failed to operating report scheduling, caused by: {}", e.getMessage(), e);
        } finally {
            abstractServiceCoordinator.release();
        }
    }

    @Scheduled(cron = "${report.weekly.cron}")
    @Async("threadPoolTaskWeeklyExecutor")
    public void weeklyMonitor() {
        if (!threadPoolTaskConfig.getReportCronEnable()) {
            return;
        }
        try {
            LOGGER.info(Thread.currentThread().getName() + " >>>>>>>>>> Start operating report scheduling <<<<<<<<<<");
            abstractServiceCoordinator.coordinate();
            handleOperatingReportLogic(ExecutionFrequencyEnum.WEEKLY.getCode(), WEEKLY_DATE);
        } catch (Exception e) {
            LOGGER.error("Failed to operating report scheduling, caused by: {}", e.getMessage(), e);
        } finally {
            abstractServiceCoordinator.release();
        }
    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public void handleOperatingReportLogic(Integer code, String date) throws ParseException {
        LOGGER.info("System Current Time: {}", QualitisConstants.PRINT_TIME_FORMAT.format(new Date()));
        // Check another qualitis Services.
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            LOGGER.info("Start to scheduling machine ip:{}" + inetAddress.getHostAddress());
        } catch (UnknownHostException e) {
            LOGGER.error("Failed to get host info.");
        }

        Date nowDate = new Date();
        MailLockRecord mailLockRecord = mailLockRecordDao.findByUnique(simpleDateFormat.parse(simpleDateFormat.format(new Date())), true, code);
        if (mailLockRecord != null) {
            LOGGER.info("mail lock in the possession of.");
            return;
        } else {
            LOGGER.info("mail lock is not exist.");
        }

        List<SubscribeOperateReport> subscribeOperateReports = subscribeOperateReportDao.selectAllMateFrequency(code);
        if (CollectionUtils.isEmpty(subscribeOperateReports)) {
            LOGGER.info(">>>>>>>>>> There is no " + date + " execution frequency in the operation report <<<<<<<<<<");
            return;
        }
        List<SendMailMakeRequest> sendMailMakeRequests = Lists.newArrayList();

        for (SubscribeOperateReport subscribeOperateReport : subscribeOperateReports) {
            List<Project> projects = subscribeOperateReport.getSubscribeOperateReportProjectsSet().stream().map(item -> item.getProject()).collect(Collectors.toList());
            List<Map<String, Object>> mapLists = new ArrayList<Map<String, Object>>();
            List<SubscriptionRecord> subscriptionRecords = Lists.newArrayList();

            for (Project project : projects) {
                // 1.rulesCount 表数量  2.rulesTableCount 已配置规则的表数量  3.rulesKpiCount KPI表数量 4.rulesKpiTableCount KPI已配置规则数量
                // 5.schedulingRulesCount 在调度监控的规则数 6.passRulesCount 通过校验规则数 7.noPassRulesCount 未通过校验规则数 8.failRulesCount 失败规则数
                int rulesCount = 0, rulesTableCount = 0, rulesKpiCount = 0, rulesKpiTableCount = 0,
                        schedulingRulesCount = 0, passRulesCount = 0, noPassRulesCount = 0, failRulesCount = 0;

                int pastCyclesRulesCount = 0, pastCyclesRulesTableCount = 0, pastCyclesRulesKpiCount = 0, pastCyclesRulesKpiTableCount = 0,
                        pastCyclesSchedulingRulesCount = 0, pastCyclesPassRulesCount = 0, pastCyclesNoPassRulesCount = 0, pastCyclesFailRulesCount = 0;
                List<Rule> rules = ruleDao.findByProject(project);
                rulesCount = rules.size();
                for (Rule rule : rules) {
                    List<RuleDataSource> ruleDataSources = rule.getRuleDataSources().stream().filter(item -> StringUtils.isNotBlank(item.getDbName()) && StringUtils.isNotBlank(item.getTableName())).collect(Collectors.toList());
                    List<RuleDataSource> ruleDataSourceKpiTable = rule.getRuleDataSources().stream().filter((item -> StringUtils.isNotBlank(item.getDbName()) && StringUtils.isNotBlank(item.getTableName()))).
                            filter(os -> StringUtils.isNotBlank(os.getTagName()) && os.getTagName().contains("KPI")).collect(Collectors.toList());
                    List<RuleDataSource> ruleDataSourceKpiRules = rule.getRuleDataSources().stream().filter((item -> StringUtils.isNotBlank(item.getDbName()) && StringUtils.isNotBlank(item.getTableName()))).
                            filter(os -> StringUtils.isNotBlank(os.getTagName()) && os.getTagName().contains("KPI")).
                            collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(p -> p.getRule().getId()))), ArrayList::new));
                    rulesTableCount += ruleDataSources.size();
                    rulesKpiCount += ruleDataSourceKpiTable.size();
                    rulesKpiTableCount += ruleDataSourceKpiRules.size();
                }

                List<Application> applications = applicationDao.findByProject(project);
                for (Application application : applications) {
                    List<Task> tasks = taskDao.findByApplication(application);
                    List<TaskRuleSimple> usingTasks = tasks.stream().map(Task::getTaskRuleSimples).flatMap(
                            taskRuleSimpleSet -> taskRuleSimpleSet.stream()).distinct().collect(Collectors.toList());
                    List<TaskRuleSimple> passTasks = tasks.stream().filter(item -> item.getStatus().equals(TaskStatusEnum.PASS_CHECKOUT.getCode())).map(Task::getTaskRuleSimples).flatMap(
                            taskRuleSimpleSet -> taskRuleSimpleSet.stream()).distinct().collect(Collectors.toList());
                    List<TaskRuleSimple> failTasks = tasks.stream().filter(item -> item.getStatus().equals(TaskStatusEnum.FAIL_CHECKOUT.getCode())).map(Task::getTaskRuleSimples).flatMap(
                            taskRuleSimpleSet -> taskRuleSimpleSet.stream()).distinct().collect(Collectors.toList());
                    List<TaskRuleSimple> failedTasks = tasks.stream().filter(item -> item.getStatus().equals(TaskStatusEnum.FAILED.getCode())).map(Task::getTaskRuleSimples).flatMap(
                            taskRuleSimpleSet -> taskRuleSimpleSet.stream()).distinct().collect(Collectors.toList());
                    schedulingRulesCount += usingTasks.size();
                    passRulesCount += passTasks.size();
                    noPassRulesCount += failTasks.size();
                    failRulesCount += failedTasks.size();
                }

                SubscriptionRecord matchProjectAndFrequency = subscriptionRecordDao.findMatchProjectAndFrequency(project.getId(), code);
                if (null == matchProjectAndFrequency) {
                    subscriptionRecords.add(setBasicInfo(project, rulesCount, rulesTableCount, rulesKpiCount, rulesKpiTableCount, schedulingRulesCount,
                            passRulesCount, noPassRulesCount, failRulesCount, code, new SubscriptionRecord()));
                } else {
                    pastCyclesRulesCount = rulesCount - Integer.parseInt(String.valueOf(matchProjectAndFrequency.getConfiguredRulesNum())) < 0 ? 0 : rulesCount - Integer.parseInt(String.valueOf(matchProjectAndFrequency.getConfiguredRulesNum()));
                    pastCyclesRulesTableCount = rulesTableCount - Integer.parseInt(String.valueOf(matchProjectAndFrequency.getConfiguredRulesTableNum())) < 0 ? 0 : rulesTableCount - Integer.parseInt(String.valueOf(matchProjectAndFrequency.getConfiguredRulesTableNum()));
                    pastCyclesRulesKpiCount = rulesKpiCount - Integer.parseInt(String.valueOf(matchProjectAndFrequency.getConfiguredRulesKpiNum())) < 0 ? 0 : rulesKpiCount - Integer.parseInt(String.valueOf(matchProjectAndFrequency.getConfiguredRulesKpiNum()));
                    pastCyclesRulesKpiTableCount = rulesKpiTableCount - Integer.parseInt(String.valueOf(matchProjectAndFrequency.getConfiguredRulesKpiTableNum())) < 0 ? 0 : rulesKpiTableCount - Integer.parseInt(String.valueOf(matchProjectAndFrequency.getConfiguredRulesKpiTableNum()));
                    pastCyclesSchedulingRulesCount = schedulingRulesCount - Integer.parseInt(String.valueOf(matchProjectAndFrequency.getSchedulingRules())) < 0 ? 0 : schedulingRulesCount - Integer.parseInt(String.valueOf(matchProjectAndFrequency.getSchedulingRules()));
                    pastCyclesPassRulesCount = passRulesCount - Integer.parseInt(String.valueOf(matchProjectAndFrequency.getPassRules())) < 0 ? 0 : passRulesCount - Integer.parseInt(String.valueOf(matchProjectAndFrequency.getPassRules()));
                    pastCyclesNoPassRulesCount = noPassRulesCount - Integer.parseInt(String.valueOf(matchProjectAndFrequency.getNoPassRules())) < 0 ? 0 : noPassRulesCount - Integer.parseInt(String.valueOf(matchProjectAndFrequency.getNoPassRules()));
                    pastCyclesFailRulesCount = failRulesCount - Integer.parseInt(String.valueOf(matchProjectAndFrequency.getFailRules())) < 0 ? 0 : failRulesCount - Integer.parseInt(String.valueOf(matchProjectAndFrequency.getFailRules()));

                    subscriptionRecords.add(setBasicInfo(project, rulesCount, rulesTableCount, rulesKpiCount, rulesKpiTableCount, schedulingRulesCount,
                            passRulesCount, noPassRulesCount, failRulesCount, code, matchProjectAndFrequency));
                }

                //封装报表数据，调用邮件接口，发送信息
                Map<String, Object> map = Maps.newHashMap();
                map.put("project_id", project.getId());
                map.put("project_name", project.getName());
                map.put("rules_count", rulesCount);
                map.put("past_cycles_rules_count", pastCyclesRulesCount);
                map.put("rules_table_count", rulesTableCount);
                map.put("past_cycles_rules_table_count", pastCyclesRulesTableCount);
                map.put("rules_kpi_count", rulesKpiCount);
                map.put("past_cycles_rules_kpi_count", pastCyclesRulesKpiCount);
                map.put("rules_kpi_table_count", rulesKpiTableCount);
                map.put("past_cycles_rules_kpi_table_count", pastCyclesRulesKpiTableCount);
                map.put("past_cycles_scheduling_rules_count", pastCyclesSchedulingRulesCount);
                map.put("past_cycles_pass_rules_count", pastCyclesPassRulesCount);
                map.put("past_cycles_no_pass_rules_count", pastCyclesNoPassRulesCount);
                map.put("past_cycles_fail_rules_count", pastCyclesFailRulesCount);
                mapLists.add(map);
            }

            SendMailMakeRequest sendMailMakeRequest = new SendMailMakeRequest();
            sendMailMakeRequest.setReceiver(subscribeOperateReport.getReceiver());
            sendMailMakeRequest.setMapLists(mapLists);
            sendMailMakeRequest.setSubscriptionRecords(subscriptionRecords);
            sendMailMakeRequest.setCreateUser(subscribeOperateReport.getCreateUser());
            sendMailMakeRequests.add(sendMailMakeRequest);
        }

        if (CollectionUtils.isNotEmpty(sendMailMakeRequests)) {
            //send_date与execution_frequency 组合唯一索引，避免多实例定时调度重复执行导致重复数据出现
            MailLockRecord currentMailLockRecord = mailLockRecordDao.save(new MailLockRecord(sendMailMakeRequests.size(), true, new Date(nowDate.getTime())
                    , QualitisConstants.PRINT_TIME_FORMAT.format(nowDate), "", code));
            //只有数据对象入库成功，才能发起邮件
            if (currentMailLockRecord == null) {
                return;
            }

            LOGGER.info(">>>>>>>>>> SEND EMAIL PACKAGING RESULT SET : <<<<<<<<<<" + sendMailMakeRequests.toString());
            try {
                // 获取执行的轮次
                int round = (sendMailMakeRequests.size() - 1) / BATCH_SIZE;
                LOGGER.info(">>>>>>>>>> Sending emails in batches round : <<<<<<<<<< " + round);
                for (int i = 0; i <= round; i++) {
                    // 求每个批次起始位置
                    int fromIndex = i * BATCH_SIZE;
                    int toIndex = (i + 1) * BATCH_SIZE;
                    // 如果是最后一个批次，则不能越界
                    if (i == round) {
                        toIndex = sendMailMakeRequests.size();
                    }
                    // TODO: 对subList执行进一步要做的操作
                    List<SendMailMakeRequest> subList = sendMailMakeRequests.subList(fromIndex, toIndex);

                    for (SendMailMakeRequest sendMailMakeRequest : subList) {
                        //发送邮件，请求esb接口
                        sendMailMessage(sendMailMakeRequest.getReceiver(), sendMailMakeRequest.getMapLists(), sendMailMakeRequest.getCreateUser());
                        //运营报表记录入库
                        for (SubscriptionRecord subscriptionRecord : sendMailMakeRequest.getSubscriptionRecords()) {
                            subscriptionRecordDao.save(subscriptionRecord);
                            LOGGER.info(">>>>>>>>>> Subscription Record Object : <<<<<<<<<< " + subscriptionRecord.toString());
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Failed to send emails record . Exception: {}", e.getMessage(), e);
                currentMailLockRecord.setStatus(false);
                currentMailLockRecord.setErrMsg(e.getMessage());
            }

        }
        LOGGER.info(">>>>>>>>>> End of operation report scheduling <<<<<<<<<<");
    }

    private SubscriptionRecord setBasicInfo(Project project, Integer rulesCount, Integer rulesTableCount, Integer rulesKpiCount, Integer rulesKpiTableCount,
                                            Integer schedulingRulesCount, Integer passRulesCount, Integer noPassRulesCount, Integer failRulesCount, Integer executionFrequency, SubscriptionRecord subscriptionRecord) {
        subscriptionRecord.setProject(project);
        subscriptionRecord.setExecutionFrequency(executionFrequency);
        subscriptionRecord.setConfiguredRulesNum(Long.valueOf(rulesCount));
        subscriptionRecord.setConfiguredRulesTableNum(Long.valueOf(rulesTableCount));
        subscriptionRecord.setConfiguredRulesKpiNum(Long.valueOf(rulesKpiCount));
        subscriptionRecord.setConfiguredRulesKpiTableNum(Long.valueOf(rulesKpiTableCount));
        subscriptionRecord.setSchedulingRules(Long.valueOf(schedulingRulesCount));
        subscriptionRecord.setPassRules(Long.valueOf(passRulesCount));
        subscriptionRecord.setNoPassRules(Long.valueOf(noPassRulesCount));
        subscriptionRecord.setFailRules(Long.valueOf(failRulesCount));
        return subscriptionRecord;
    }

    private void sendMailMessage(String receiver, List<Map<String, Object>> mapLists, String createUser) throws Exception {
        String[] specReceiver = receiver.split(SpecCharEnum.COMMA.getValue());
        List<String> resultReceiver = Lists.newArrayList();
        for (String accepter : specReceiver) {
            resultReceiver.add(accepter + "@webank.com");
        }
        //邮件封装实体
        EmailEntity emailEntity = new EmailEntity();
        //发件人邮箱
        emailEntity.setFrom("wds@webank.com");
        //收件人邮箱，多人用分号隔开。（to，cc，bcc不能全部同时为空）
        emailEntity.setToList(resultReceiver);
        //邮件标题
        emailEntity.setTitle(esbSdkConfig.getTitle() + "(" + simpleDateFormat.format(new Date()) + ")");

        List<List<String>> data = new ArrayList<>();
        for (Map<String, Object> mapList : mapLists) {
            data.add(Arrays.asList(mapList.get("project_id").toString(), mapList.get("project_name").toString(), mapList.get("rules_count").toString(),
                    mapList.get("past_cycles_rules_count").toString(), mapList.get("rules_table_count").toString(), mapList.get("past_cycles_rules_table_count").toString(),
                    mapList.get("rules_kpi_count").toString(), mapList.get("past_cycles_rules_kpi_count").toString(), mapList.get("rules_kpi_table_count").toString(),
                    mapList.get("past_cycles_rules_kpi_table_count").toString(), mapList.get("past_cycles_scheduling_rules_count").toString(), mapList.get("past_cycles_pass_rules_count").toString(),
                    mapList.get("past_cycles_no_pass_rules_count").toString(), mapList.get("past_cycles_fail_rules_count").toString()
            ));
        }
        // 创建HtmlTableGenerator对象
        HtmlTableGeneratorUtils tableGenerator = new HtmlTableGeneratorUtils(mapLists.size(), 14, TABLE_HEADERS, data);
        // 生成HTML表格
        String htmlTable = tableGenerator.generateTable();
        // 打印生成的HTML表格
        LOGGER.info(">>>>>>>>>> OUT PUT HTML TABLE :<<<<<<<<<<  " + htmlTable);
        //邮件内容,如果带图片的话
        emailEntity.setContent(htmlTable);
        //邮件格式，0 文本、1 Html。默认值为0。
        emailEntity.setBodyFormat(1);

        convertEmail(emailEntity);
        mailClient.sendEsbMail(CustomObjectMapper.transObjectToJson(emailEntity), createUser);
    }


    /**
     * @param email 邮件对象
     * @return email
     * @Description:将EmailEntity里的TO/CC/BC List<String>分别转换为字符串字段To CC BCC，多个时，用分号隔开
     */
    private EmailEntity convertEmail(EmailEntity email) {
        if (email.getToList() != null && email.getToList().size() != 0) {
            email.setTo(List2String(email.getToList()));
        }
        return email;
    }

    public static String List2String(List<String> list) {
        if (list == null || list.size() == 0) {
            return "";
        }
        return org.apache.commons.lang3.StringUtils.join(list, ";");
    }

}
