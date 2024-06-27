package com.webank.wedatasphere.qualitis.scheduler;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.webank.wedatasphere.qualitis.bean.DataQualityJob;
import com.webank.wedatasphere.qualitis.checkalert.entity.CheckAlert;
import com.webank.wedatasphere.qualitis.config.ImsConfig;
import com.webank.wedatasphere.qualitis.config.LinkisConfig;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.dto.ItsmUserDto;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.request.AddUserProxyUserRequest;
import com.webank.wedatasphere.qualitis.service.UserProxyUserService;
import com.webank.wedatasphere.qualitis.submitter.ExecutionManager;
import com.webank.wedatasphere.qualitis.util.DateExprReplaceUtil;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitorAdapter;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectVisitorAdapter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author v_minminghe@webank.com
 * @date 2024-03-12 10:58
 * @description
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest
public class ExecutionManagerTest {

    @Autowired
    private ExecutionManager executionManager;
    @Autowired
    private ImsConfig imsConfig;
    @Autowired
    private LinkisConfig linkisConfig;
    @Autowired
    private UserProxyUserService userProxyUserService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionManagerTest.class);


    public static String addHivePrefixToTables(String sql) {
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            statement.accept(new StatementVisitorAdapter() {
                @Override
                public void visit(Select select) {
                    select.getSelectBody().accept(new SelectVisitorAdapter() {
                        @Override
                        public void visit(PlainSelect plainSelect) {
                            if (plainSelect.getFromItem() instanceof Table) {
                                Table table = (Table) plainSelect.getFromItem();
                                modifyTable(table);
                            }
                            if (plainSelect.getJoins() != null) {
                                plainSelect.getJoins().forEach(join -> {
                                    if (join.getRightItem() instanceof Table) {
                                        Table table = (Table) join.getRightItem();
                                        modifyTable(table);
                                    }
                                });
                            }
                        }
                    });
                }
            });
            return statement.toString();
        } catch (JSQLParserException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void modifyTable(Table table) {
        String originalTableName = table.getFullyQualifiedName();
        if (originalTableName.contains(".")) {
            String[] parts = originalTableName.split("\\.", 2);
            String dbName = parts[0];
            String tableName = parts[1];
            table.setSchemaName("hive." + dbName);
            table.setName(tableName);
        } else {
            // 如果只有表名没有库名，也可以在这里添加默认库前缀
            // table.setSchemaName("hive"); // 可选
        }
    }

    @Test
    public void addMultiProxyUser() {
        String proxyUsers = "hduser02,hduser0201,hduser0203,hduser0204,hduser0205,hduser0206,hduser0207,hduser0208,hduser0209,hduser0210,hduser0211,hduser0212,hduser0213,hduser0214,hduser0215,hduser0216,hduser0217,hduser0218,hduser0219,hduser0220,hduser0221,hduser0222,hduser0223,hduser0224,hduser0225,hduser0226,hduser0227,hduser0232,hduser0233,hduser0234,hduser0295,hduser1001,hduser1002,hduser1003,hduser1004,hduser1005,hduser1006,hduser1007,hduser1008,hduser1009,hduser1010,hduser1011,hduser1015,hduser1016,hduser1017,hduser1021,hduser1030,hduser1038,hduser1040,hduser1050,hduser1051,hduser1052,hduser1053,hduser1059,hduser1061,hduser1080,hduser1088,hduser1089,hduser1091";

        ItsmUserDto itsmUserDto = new ItsmUserDto();
        itsmUserDto.setUsername("v_minminghe");
        itsmUserDto.setProxyUser(proxyUsers);
        if (org.apache.commons.lang3.StringUtils.isNotBlank(itsmUserDto.getProxyUser())) {
            Iterable<String> proxyUserIterable = Splitter.on(SpecCharEnum.COMMA.getValue()).omitEmptyStrings().trimResults().split(itsmUserDto.getProxyUser());
            proxyUserIterable.forEach(proxyUser -> {
                AddUserProxyUserRequest addUserProxyUserRequest = new AddUserProxyUserRequest();
                addUserProxyUserRequest.setUsername(itsmUserDto.getUsername());
                addUserProxyUserRequest.setProxyUserName(proxyUser);
                try {
                    userProxyUserService.addUserProxyUser(addUserProxyUserRequest);
                } catch (UnExpectedRequestException e) {
                    LOGGER.warn("Failed to add proxyUser: {}, username: {}, reason: {}", proxyUser, itsmUserDto.getUsername(), e.getMessage());
                }
            });
        }

    }


    private Map<String, Object> extractAlertReceivers(List<String> alertReceivers) {
        AtomicReference<String> erpGroupId = new AtomicReference<>();
        List<String> defaultReceivers = new ArrayList<>();
        alertReceivers.forEach(item -> {
            if (item.startsWith(SpecCharEnum.LEFT_BRACKET.getValue()) && item.endsWith(SpecCharEnum.RIGHT_BRACKET.getValue())) {
                erpGroupId.set(item.substring(1, item.length() - 1));
            } else {
                defaultReceivers.add(item);
            }
        });
        Map<String, Object> resultMap = Maps.newHashMapWithExpectedSize(2);
        if (erpGroupId.get() != null) {
            resultMap.put("erp_group_id", erpGroupId.get());
        }
        if (CollectionUtils.isNotEmpty(defaultReceivers)) {
            resultMap.put("alert_reciver", Joiner.on(SpecCharEnum.COMMA.getValue()).join(defaultReceivers));
        }
        return resultMap;
    }

    private CheckAlert getCheckAlert() {
        CheckAlert checkAlert = new CheckAlert();
        checkAlert.setTopic("test_topic");
        checkAlert.setDefaultReceiver("v_minminghe");
        checkAlert.setDefaultAlertLevel(2);
        checkAlert.setDefaultAlertWays("2,3");
        checkAlert.setAdvancedReceiver("allenzhou");
        checkAlert.setAdvancedAlertLevel(3);
        checkAlert.setAdvancedAlertWays("1,4");
        checkAlert.setAdvancedAlertCol("major_col");
        checkAlert.setWorkFlowName("wf_rule");
        checkAlert.setNodeName("node_alert");
        Project project = new Project();
        project.setName("ces_pro");
        checkAlert.setProject(project);
        return checkAlert;
    }

    @Test
    public void composeJobCodesTest() throws UnExpectedRequestException {
        DataQualityJob job = new DataQualityJob();
        List<String> jobCodes = job.getJobCode();
        List<String> selectPart = new ArrayList<>();
        List<String> contentTitle = new ArrayList<>();

        jobCodes.add("import sys.process._");
        jobCodes.add("import scala.util.parsing.json._");

        CheckAlert currentCheckAlert = getCheckAlert();

        String realContent = "\"" + linkisConfig.getCheckAlertTemplate().replace("qualitis_check_alert_topic", currentCheckAlert.getTopic()).replace("qualitis_check_alert_time", "2024/03/12").replace("qualitis_check_alert_project_info", currentCheckAlert.getProject().getName() + SpecCharEnum.COLON.getValue() + currentCheckAlert.getWorkFlowName() + SpecCharEnum.COLON.getValue() + currentCheckAlert.getNodeName()) + "\"";

        jobCodes.add("var alertContent = " + realContent);
        String filter = StringUtils.isNotEmpty(currentCheckAlert.getFilter()) ? currentCheckAlert.getFilter() : "true";

        filter = DateExprReplaceUtil.replaceFilter(new Date(), filter);
        jobCodes.add("val alertTableArr = spark.sql(\"SELECT " + StringUtils.join(selectPart, SpecCharEnum.COMMA.getValue())
                + " FROM " + currentCheckAlert.getAlertTable() + " WHERE " + currentCheckAlert.getAlertCol() + " = 1 AND " + filter + "\").collect()");
        jobCodes.add("var alertTableContent = \"\"");
        jobCodes.add("for(ele <- alertTableArr) {alertTableContent = alertTableContent.concat(ele.mkString(\" | \")).concat(\"\\n\")}");
        jobCodes.add("if (alertTableContent.length > 0) {");
        jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_default_content\", alertTableContent)");
        jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_default_receiver\", \"" + currentCheckAlert.getDefaultReceiver() + "\")");
        jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_default_title\", \"" + StringUtils.join(contentTitle, "|") + "\")");
        jobCodes.add("} else {");
        jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_default_content\", \"\")");
        jobCodes.add("alertContent = alertContent.replaceAll(\"\\\\[告警人\\\\] qualitis_check_alert_default_receiver\", \"\")");

        jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_default_title\", \"\")");
        jobCodes.add("}");

        if (StringUtils.isNotEmpty(currentCheckAlert.getAdvancedAlertCol())) {
            jobCodes.add("val majorAlertTableArr = spark.sql(\"SELECT " + StringUtils.join(selectPart, SpecCharEnum.COMMA.getValue())
                    + " FROM " + currentCheckAlert.getAlertTable() + " WHERE " + currentCheckAlert.getAdvancedAlertCol() + " = 1 AND " + filter + "\").collect()");
            jobCodes.add("var majorAlertTableContent = \"\"");
            jobCodes.add("for(ele <- majorAlertTableArr) {majorAlertTableContent = majorAlertTableContent.concat(ele.mkString(\" | \")).concat(\"\\n\")}");
            jobCodes.add("if (majorAlertTableContent.length > 0) {");
            jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_advanced_content\", majorAlertTableContent)");
            jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_advanced_receiver\", \"" + (StringUtils.isNotEmpty(currentCheckAlert.getAdvancedReceiver()) ? currentCheckAlert.getAdvancedReceiver() : "") + "\")");
            jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_advanced_title\", \"" + StringUtils.join(contentTitle, "|") + "\")");
            // CURL IMS
            if (StringUtils.isNotEmpty(currentCheckAlert.getAdvancedReceiver())) {
                jobCodes.add("val jsonAdvancedValue = " + "\"{\\\"userAuthKey\\\":\\\"" + imsConfig.getUserAuthKey() + "\\\",\\\"alertList\\\":[{\\\"alert_way\\\":\\\"" + currentCheckAlert.getAdvancedAlertWays() + "\\\",\\\"alert_title\\\":\\\"【业务运维关注】Qualitis Check Alert\\\",\\\"ci_type_id\\\":55,\\\"sub_system_id\\\":" + imsConfig.getSystemId() + ",\\\"alert_reciver\\\":\\\"" + currentCheckAlert.getAdvancedReceiver() + "\\\",\\\"alert_level\\\":\\\"" + currentCheckAlert.getAdvancedAlertLevel() + "\\\",\\\"alert_obj\\\":\\\"" + "dbAndTables[0]" + "[" + "dbAndTables[1]" + "]" + "\\\",\\\"alert_info\\\":\\\"qualitis_check_alert_Advanced\\\"}]}\"");
                jobCodes.add("val realJsonAdvancedValue = jsonAdvancedValue.replaceAll(\"qualitis_check_alert_Advanced\", alertContent)");
                jobCodes.add("val jsonAdvancedCmd = Seq(\"curl\", \"-H\", \"'Content-Type: application/json'\",\"-d\", s\"$realJsonAdvancedValue\",\"" + imsConfig.getUrl() + imsConfig.getSendAlarmPath() + "\")");
                jobCodes.add("val advancedResponse = jsonAdvancedCmd.!!");
                jobCodes.add("val code = JSON.parseFull(advancedResponse).get.asInstanceOf[Map[String, Object]].get(\"resultCode\").get.toString");
                jobCodes.add("if (! \"0.0\".equals(code)) throw new RuntimeException(\"Failed to send ims alarm. Return non-zero code from IMS.\")");
            }
            jobCodes.add("} else {");
            jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_advanced_content\", \"\")");
            jobCodes.add("alertContent = alertContent.replaceAll(\"\\\\[高等级告警人\\\\] qualitis_check_alert_advanced_receiver\", \"\")");

            jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_advanced_title\", \"\")");
            jobCodes.add("}");
        } else {
            jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_advanced_content\", \"\")");
            jobCodes.add("alertContent = alertContent.replaceAll(\"\\\\[高等级告警人\\\\] qualitis_check_alert_advanced_receiver\", \"\")");

            jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_advanced_title\", \"\")");
        }
        // CURL IMS
        jobCodes.add("if (alertTableContent.length > 0) {");
        jobCodes.add("val jsonDefaultValue = " + "\"{\\\"userAuthKey\\\":\\\"" + imsConfig.getUserAuthKey() + "\\\",\\\"alertList\\\":[{\\\"alert_way\\\":\\\"" + currentCheckAlert.getDefaultAlertWays() + "\\\",\\\"alert_title\\\":\\\"【业务运维关注】Qualitis Check Alert\\\",\\\"ci_type_id\\\":55,\\\"sub_system_id\\\":" + imsConfig.getSystemId() + ",\\\"alert_reciver\\\":\\\"" + currentCheckAlert.getDefaultReceiver() + "\\\",\\\"alert_level\\\":\\\"" + currentCheckAlert.getDefaultAlertLevel() + "\\\",\\\"alert_obj\\\":\\\"" + "dbAndTables[0]" + "[" + "dbAndTables[1]" + "]" + "\\\",\\\"alert_info\\\":\\\"qualitis_check_alert_info\\\"}]}\"");
        jobCodes.add("val realJsonDefaultValue = jsonDefaultValue.replaceAll(\"qualitis_check_alert_info\", alertContent)");
        jobCodes.add("val jsonDefaultCmd = Seq(\"curl\", \"-H\", \"'Content-Type: application/json'\",\"-d\", s\"$realJsonDefaultValue\",\"" + imsConfig.getUrl() + imsConfig.getSendAlarmPath() + "\")");
        jobCodes.add("val defaultResponse = jsonDefaultCmd.!!");
        jobCodes.add("val code = JSON.parseFull(defaultResponse).get.asInstanceOf[Map[String, Object]].get(\"resultCode\").get.toString");
        jobCodes.add("if (! \"0.0\".equals(code)) throw new RuntimeException(\"Failed to send ims alarm. Return non-zero code from IMS.\")");
        jobCodes.add("}");

        String code = String.join("\n", job.getJobCode());
        Assert.hasText(code, "empty string");
//        System.out.println("result: " + code);
    }

}
