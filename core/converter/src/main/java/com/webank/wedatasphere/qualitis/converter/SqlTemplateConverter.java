/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.qualitis.converter;

import com.webank.wedatasphere.qualitis.bean.DataQualityJob;
import com.webank.wedatasphere.qualitis.bean.DataQualityTask;
import com.webank.wedatasphere.qualitis.bean.RuleTaskDetail;
import com.webank.wedatasphere.qualitis.exception.ConvertException;
import com.webank.wedatasphere.qualitis.exception.DataQualityTaskException;
import com.webank.wedatasphere.qualitis.exception.RuleVariableNotFoundException;
import com.webank.wedatasphere.qualitis.exception.RuleVariableNotSupportException;
import com.webank.wedatasphere.qualitis.rule.constant.InputActionStepEnum;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTemplateTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateInputTypeEnum;
import com.webank.wedatasphere.qualitis.rule.entity.*;
import com.webank.wedatasphere.qualitis.translator.AbstractTranslator;
import com.webank.wedatasphere.qualitis.util.DateExprReplaceUtil;
import com.webank.wedatasphere.qualitis.bean.DataQualityJob;
import com.webank.wedatasphere.qualitis.bean.DataQualityTask;
import com.webank.wedatasphere.qualitis.bean.RuleTaskDetail;
import com.webank.wedatasphere.qualitis.exception.ConvertException;
import com.webank.wedatasphere.qualitis.exception.DataQualityTaskException;
import com.webank.wedatasphere.qualitis.exception.RuleVariableNotFoundException;
import com.webank.wedatasphere.qualitis.exception.RuleVariableNotSupportException;
import com.webank.wedatasphere.qualitis.rule.constant.InputActionStepEnum;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTemplateTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateInputTypeEnum;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.entity.RuleVariable;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateStatisticsInputMeta;
import com.webank.wedatasphere.qualitis.translator.AbstractTranslator;
import com.webank.wedatasphere.qualitis.util.DateExprReplaceUtil;
import java.util.regex.Matcher;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * SQL Template Converter, can convert task into sql code
 * example：
 * val tmp1 = spark.sql("select * from bdp_test_ods_mask.asd where (fdgdfg) and (new_clum_mask is null)");
 * tmp1.write.saveAsTable("qualitishduser05_tmp_safe.mid_application_26_20190117094607_649214");
 * @author howeye
 */
@Component
public class SqlTemplateConverter extends AbstractTemplateConverter {

    @Autowired
    private AbstractTranslator abstractTranslator;

    public static final String VARIABLE_NAME_PLACEHOLDER = "${VARIABLE}";
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile(".*\\$\\{(.*)}.*");
    private static final Pattern AGGREGATE_FUNC_PATTERN = Pattern.compile("[a-zA-Z]+\\([0-9a-zA-Z_]+\\)");
    private static final String SPARK_SQL_TEMPLATE_PLACEHOLDER = "${SQL}";
    private static final String SAVE_MID_TABLE_NAME_PLACEHOLDER = "${TABLE_NAME}";
    private static final String FILTER_PLACEHOLDER = "${filter}";
    private static final String FILTER_LEFT_PLACEHOLDER = "${filter_left}";
    private static final String FILTER_RIGHT_PLACEHOLDER = "${filter_right}";
    private static final Integer COMMON_RULE = 1;
    private static final Integer CUSTOM_RULE = 2;
    private static final Integer MUL_SOURCE_RULE = 3;

    private static final String SPARK_SQL_TEMPLATE = "val " + VARIABLE_NAME_PLACEHOLDER + " = spark.sql(\"" + SPARK_SQL_TEMPLATE_PLACEHOLDER + "\");";
    private static final String SAVE_MID_TABLE_SENTENCE_TEMPLATE = VARIABLE_NAME_PLACEHOLDER + ".write.saveAsTable(\"" + SAVE_MID_TABLE_NAME_PLACEHOLDER + "\");";

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlTemplateConverter.class);

    /**
     * Convert task into scala code
     * @param dataQualityTask
     * @return
     * @throws ConvertException
     * @throws DataQualityTaskException
     * @throws RuleVariableNotSupportException
     * @throws RuleVariableNotFoundException
     */
    @Override
    public DataQualityJob convert(DataQualityTask dataQualityTask) throws ConvertException, DataQualityTaskException, RuleVariableNotSupportException, RuleVariableNotFoundException {
        LOGGER.info("Start to convert template to actual code, task: " + dataQualityTask);
        if (null == dataQualityTask || dataQualityTask.getRuleTaskDetails().isEmpty()) {
            throw new DataQualityTaskException("Task can not be null or empty");
        }
        DataQualityJob job = new DataQualityJob();
        List<String> initSentence = abstractTranslator.getInitSentence();
        job.getJobCode().addAll(initSentence);
        LOGGER.info("Succeed to get init code. codes: " + initSentence);

        int count = 0;
        for (RuleTaskDetail ruleTaskDetail : dataQualityTask.getRuleTaskDetails()) {
            count++;
            if (ruleTaskDetail.getRule().getChildRule() != null) {
                String parentMidTableName = ruleTaskDetail.getMidTableName().split(",")[0];
                String childMidTableName = ruleTaskDetail.getMidTableName().split(",")[1];
                ruleTaskDetail.setMidTableName(parentMidTableName);

                List<String> codes = generateSparkSqlByTask(ruleTaskDetail.getRule().getChildRule(), dataQualityTask.getApplicationId(),
                        childMidTableName, dataQualityTask.getCreateTime(), dataQualityTask.getPartition(), count);
                job.getJobCode().addAll(codes);
                LOGGER.info("Succeed to convert rule into code. rule_id: {}, rul_name: {}, codes: {}", ruleTaskDetail.getRule().getId(), ruleTaskDetail.getRule().getName(), codes);
                count ++;
                // Fix variables' sequence when solving child rule of parent rule. Solving widget influence.
                count ++;
            }
            List<String> codes = generateSparkSqlByTask(ruleTaskDetail.getRule(), dataQualityTask.getApplicationId(),
                    ruleTaskDetail.getMidTableName(), dataQualityTask.getCreateTime(), dataQualityTask.getPartition(), count);
            job.getJobCode().addAll(codes);
            LOGGER.info("Succeed to convert rule into code. rule_id: {}, rul_name: {}, codes: {}", ruleTaskDetail.getRule().getId(), ruleTaskDetail.getRule().getName(), codes);
        }
        LOGGER.info("Succeed to convert all template into actual code");
        job.setTaskId(dataQualityTask.getTaskId());
        return job;
    }

    /**
     * Convert task into scala code
     * @param rule
     * @param applicationId
     * @param midTableName
     * @param createTime
     * @param partition
     * @param count
     * @return
     * @throws ConvertException
     * @throws RuleVariableNotSupportException
     * @throws RuleVariableNotFoundException
     */
    private List<String> generateSparkSqlByTask(Rule rule, String applicationId, String midTableName, String createTime, String partition, Integer count) throws ConvertException, RuleVariableNotSupportException, RuleVariableNotFoundException {
        List<String> sqlList = new ArrayList<>();
        // Get SQL from template
        String templateMidTableAction = rule.getTemplate().getMidTableAction();

        // Get input meta from template
        List<RuleVariable> inputMetaRuleVariables = rule.getRuleVariables().stream().filter(
                ruleVariable -> ruleVariable.getInputActionStep().equals(InputActionStepEnum.TEMPLATE_INPUT_META.getCode())).collect(Collectors.toList());

        // Change filter
        if (rule.getTemplate().getTemplateType().equals(RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode())) {
            Set<RuleDataSource> ruleDataSources = rule.getRuleDataSources();
            if (rule.getParentRule() != null) {
                ruleDataSources = new HashSet<>();
                Set<RuleDataSource> parentRuleDataSources = rule.getParentRule().getRuleDataSources();
                for (RuleDataSource ruleDataSource : parentRuleDataSources) {
                    RuleDataSource tmp = new RuleDataSource(ruleDataSource);
                    if (tmp.getDatasourceIndex() == 0) {
                        tmp.setDatasourceIndex(1);
                    } else {
                        tmp.setDatasourceIndex(0);
                    }
                    ruleDataSources.add(tmp);
                }
            }
            for (RuleDataSource ruleDataSource : ruleDataSources) {
                if (ruleDataSource.getDatasourceIndex().equals(0)) {
                    String leftFilter = ruleDataSource.getFilter();
                    leftFilter = DateExprReplaceUtil.replaceDateExpr(leftFilter);
                    templateMidTableAction = templateMidTableAction.replace(FILTER_LEFT_PLACEHOLDER, leftFilter);
                } else {
                    String rightFilter = ruleDataSource.getFilter();
                    rightFilter = DateExprReplaceUtil.replaceDateExpr(rightFilter);
                    templateMidTableAction = templateMidTableAction.replace(FILTER_RIGHT_PLACEHOLDER, rightFilter);
                }
            }
        }
        if (StringUtils.isBlank(partition)) {
            if (rule.getTemplate().getTemplateType().equals(RuleTemplateTypeEnum.SINGLE_SOURCE_TEMPLATE.getCode())) {
                partition = new ArrayList<>(rule.getRuleDataSources()).get(0).getFilter();
            } else if (rule.getTemplate().getTemplateType().equals(RuleTemplateTypeEnum.CUSTOM.getCode())) {
                partition = rule.getWhereContent();
            } else if (rule.getTemplate().getTemplateType().equals(RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode())) {
                // Replace placeholder
                partition = "";
                List<RuleVariable> filterVariable = inputMetaRuleVariables.stream().filter(
                        r -> r.getTemplateMidTableInputMeta().getInputType().equals(TemplateInputTypeEnum.CONDITION.getCode()))
                        .collect(Collectors.toList());
                if (!filterVariable.isEmpty()) {
                    partition = filterVariable.get(0).getValue();
                }
            }
        }
        // Get SQL From template and replace all replaceholders
        String midTableAction = replaceVariable(templateMidTableAction, inputMetaRuleVariables, partition);

        Set<TemplateStatisticsInputMeta> templateStatisticsAction = rule.getTemplate().getStatisticAction();

        // Generate select statement and save into hive database
        sqlList.addAll(generateSparkSqlAndSaveSentence(midTableAction, midTableName, rule.getTemplate().getSaveMidTable(), count));
        count ++;
        // Generate statistics statement, and save into mysql
        List<RuleVariable> statisticsRuleVariables = rule.getRuleVariables().stream().filter(
                ruleVariable -> ruleVariable.getInputActionStep().equals(InputActionStepEnum.STATISTICS_ARG.getCode())).collect(Collectors.toList());
        sqlList.addAll(saveStatisticAndSaveMySqlSentence(rule.getId(), templateStatisticsAction, applicationId, statisticsRuleVariables, createTime, count));

        return sqlList;
    }

    private List<String> saveStatisticAndSaveMySqlSentence(Long ruleId, Set<TemplateStatisticsInputMeta> templateStatisticsInputMetas, String applicationId, List<RuleVariable> ruleVariables,
                                                           String createTime, Integer count) throws RuleVariableNotSupportException, RuleVariableNotFoundException {
        return abstractTranslator.persistenceTranslate(ruleId, templateStatisticsInputMetas, applicationId, ruleVariables, createTime, count);
    }


    /**
     * Generate scala code of select statement and save into hive database
     * @param sql
     * @param saveTableName
     * @param saveMidTable
     * @param count
     * @return
     */
    private List<String> generateSparkSqlAndSaveSentence(String sql, String saveTableName, Boolean saveMidTable, Integer count) {
        List<String> sparkSqlList = new ArrayList<>();
        String sparkSqlSentence = getSparkSqlSentence(sql, count);
        sparkSqlList.add(sparkSqlSentence);
        LOGGER.info("Succeed to generate spark sql. sentence: {}", sparkSqlSentence);
        // Fix bug in the workflow between widget node and qualitis node.
        String variableFormer = getVariableName(count);
        String str1 = "val schemas = " + variableFormer + ".schema.fields.map(f => f.name).toList";
        String str2 = "val newSchemas = schemas.map(s => s.replaceAll(\"[()]\", \"\")).toList";
        // 后续变量序号加一
        count ++;
        String variableLatter = getVariableName(count);
        String str3 = "val " + variableLatter + " = " + variableFormer + ".toDF(newSchemas: _*)";
        sparkSqlList.add(str1);
        sparkSqlList.add(str2);
        sparkSqlList.add(str3);
        //
        if (saveMidTable) {
            String midTableSentence = getSaveMidTableSentence(saveTableName, count);
            sparkSqlList.add(midTableSentence);
            LOGGER.info("Succeed to generate spark sql. sentence: {}", midTableSentence);
        }
        return sparkSqlList;
    }

    private String getSaveMidTableSentence(String saveMidTableName, Integer count) {
        String tmp = SAVE_MID_TABLE_SENTENCE_TEMPLATE.replace(SAVE_MID_TABLE_NAME_PLACEHOLDER, saveMidTableName);
        return tmp.replace(VARIABLE_NAME_PLACEHOLDER, getVariableName(count));
    }

    private String getSparkSqlSentence(String sql, Integer count) {
        sql = sql.replace("\"", "\\\"");
        String str = SPARK_SQL_TEMPLATE.replace(SPARK_SQL_TEMPLATE_PLACEHOLDER, sql);
        return str.replace(VARIABLE_NAME_PLACEHOLDER, getVariableName(count));
    }

    /**
     * Replace all placeholder of template sql
     * @param template
     * @param variables
     * @param filter
     * @return
     * @throws ConvertException
     */
    private String replaceVariable(String template, List<RuleVariable> variables, String filter) throws ConvertException {
        String sqlAction = template;
        filter = DateExprReplaceUtil.replaceDateExpr(filter);
        sqlAction = sqlAction.replace(FILTER_PLACEHOLDER, filter);
        LOGGER.info("Succeed to replace {} into {}", FILTER_PLACEHOLDER, filter);
        for (RuleVariable ruleVariable : variables) {
            String placeHolder = "\\$\\{" + ruleVariable.getTemplateMidTableInputMeta().getPlaceholder() + "}";
            // Fix issue of wedget node in the front.
            if ("\\$\\{field}".equals(placeHolder)) {
                Matcher matcher = AGGREGATE_FUNC_PATTERN.matcher(ruleVariable.getValue());
                while(matcher.find()) {
                    String[] funcs = matcher.group().split("\n");
                    for (String func : funcs) {
                        ruleVariable.setValue(ruleVariable.getValue().replace(func, "`" + func + "`"));
                    }
                }
            }
            // Fix replacement issue that db is null when running workflow.
            if ("".equals(ruleVariable.getValue())) {
                sqlAction = sqlAction.replaceAll(placeHolder + ".", "");
            } else {
                sqlAction = sqlAction.replaceAll(placeHolder, ruleVariable.getValue());
            }

            LOGGER.info("Succeed to replace {} into {}", placeHolder, ruleVariable.getValue());
        }
        if (PLACEHOLDER_PATTERN.matcher(sqlAction).matches()) {
            throw new ConvertException("Unable to convert SQL, replacing placeholders failed");
        }

        return sqlAction;
    }

    /**
     * Get tmp variable name
     * @param count
     * @return
     */
    public String getVariableName(Integer count) {
        return "tmp" + count;
    }
}
