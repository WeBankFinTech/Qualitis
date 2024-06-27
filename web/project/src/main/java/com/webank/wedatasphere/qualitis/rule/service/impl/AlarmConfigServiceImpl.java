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

package com.webank.wedatasphere.qualitis.rule.service.impl;

import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.dao.RuleMetricDao;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.rule.constant.CheckTemplateEnum;
import com.webank.wedatasphere.qualitis.rule.dao.AlarmConfigDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.AlarmConfigRepository;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmConfig;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateOutputMeta;
import com.webank.wedatasphere.qualitis.rule.request.AlarmConfigRequest;
import com.webank.wedatasphere.qualitis.rule.request.CustomAlarmConfigRequest;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceRequest;
import com.webank.wedatasphere.qualitis.rule.request.FileAlarmConfigRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceConfigRequest;
import com.webank.wedatasphere.qualitis.rule.service.AlarmConfigService;
import com.webank.wedatasphere.qualitis.rule.service.TemplateOutputMetaService;
import com.webank.wedatasphere.qualitis.service.RuleMetricCommonService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
@Service
public class AlarmConfigServiceImpl implements AlarmConfigService {
    @Autowired
    private RuleMetricDao ruleMetricDao;

    @Autowired
    private AlarmConfigDao alarmConfigDao;

    @Autowired
    private TemplateOutputMetaService templateOutputMetaService;

    @Autowired
    private AlarmConfigRepository alarmConfigRepository;
    @Autowired
    private RuleMetricCommonService ruleMetricCommonService;

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public List<AlarmConfig> checkAndSaveAlarmVariable(List<AlarmConfigRequest> requests, Rule rule, String loginUser, List<DataSourceRequest> dataSourceRequests, MultiDataSourceConfigRequest source, MultiDataSourceConfigRequest target) throws UnExpectedRequestException, IOException, PermissionDeniedRequestException {
        List<AlarmConfig> alarmConfigs = new ArrayList<>();
        for (AlarmConfigRequest request : requests) {
            // Check Arguments
            AlarmConfigRequest.checkRequest(request);

            // Check existence of templateOutputMeta
            TemplateOutputMeta templateOutputMetaInDb = templateOutputMetaService.checkTemplateOutputMetaId(request.getOutputMetaId());

            // Check template output meta
            checkTemplateContain(rule.getTemplate(), templateOutputMetaInDb);

            // Generate alarmConfig and save
            AlarmConfig newAlarmConfig = new AlarmConfig();
            setAlarmConfigInfo(rule, templateOutputMetaInDb, newAlarmConfig, request.getCheckTemplate(), request.getThreshold(), request.getCompareType(), request.getUploadAbnormalValue(), request.getUploadRuleMetricValue(), request.getDeleteFailCheckResult());
            if (StringUtils.isNotBlank(request.getRuleMetricEnCode())) {
                RuleMetric ruleMetric = ruleMetricDao.findByEnCode(request.getRuleMetricEnCode());
                checkRuleMetricInOtherRules(ruleMetric, rule);
                newAlarmConfig.setRuleMetric(ruleMetric);
                newAlarmConfig.setUploadAbnormalValue(request.getUploadAbnormalValue());
                newAlarmConfig.setUploadRuleMetricValue(request.getUploadRuleMetricValue());
                newAlarmConfig.setDeleteFailCheckResult(request.getDeleteFailCheckResult());
            } else if (StringUtils.isNotBlank(request.getRuleMetricName())) {
                Boolean present = false;
                //单表
                if (CollectionUtils.isNotEmpty(dataSourceRequests)) {
                    present = dataSourceRequests.stream().filter(item -> CollectionUtils.isNotEmpty(item.getDataSourceEnvRequests())).findFirst().isPresent();
                } else if (source != null && target != null && (source.getDataSourceEnvRequests().size() > 1 || target.getDataSourceEnvRequests().size() > 1)) {
                    present = true;
                }
                //支持用户手动输入指标名
                RuleMetric ruleMetric = ruleMetricCommonService.accordingRuleMetricNameAdd(request.getRuleMetricName(), loginUser, present);
                if (ruleMetric == null) {
                    throw new UnExpectedRequestException("{&FAILED_TO_AUTOMATE_CREATE_METRICS}");
                }
                newAlarmConfig.setRuleMetric(ruleMetric);
                newAlarmConfig.setUploadAbnormalValue(request.getUploadAbnormalValue());
                newAlarmConfig.setUploadRuleMetricValue(request.getUploadRuleMetricValue());
                newAlarmConfig.setDeleteFailCheckResult(request.getDeleteFailCheckResult());
            }
            alarmConfigs.add(newAlarmConfig);
        }

        return alarmConfigDao.saveAllAlarmConfig(alarmConfigs);
    }

    private void checkRuleMetricInOtherRules(RuleMetric ruleMetric, Rule rule) throws UnExpectedRequestException {
        List<AlarmConfig> alarmConfigs = alarmConfigDao.getByRuleMetric(ruleMetric).stream().filter(alarmConfig -> !(alarmConfig.getRule().getName().equals(rule.getName()))).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(alarmConfigs)) {
            throw new UnExpectedRequestException("Rule metric has been occupied with other rule: " + alarmConfigs.iterator().next().getRule().getName());
        }
    }

    private void setAlarmConfigInfo(Rule rule, TemplateOutputMeta templateOutputMetaInDb, AlarmConfig newAlarmConfig, Integer checkTemplate, Double threshold, Integer compareType, Boolean uploadAbnormalValue, Boolean uploadRuleMetricValue, Boolean deleteFailCheckResult) throws UnExpectedRequestException {
        newAlarmConfig.setRule(rule);
        newAlarmConfig.setTemplateOutputMeta(templateOutputMetaInDb);
        newAlarmConfig.setCheckTemplate(checkTemplate);
        // check table structure rule
        if (QualitisConstants.isTableStructureConsistent(rule.getTemplate().getEnName()) && !checkTemplate.equals(CheckTemplateEnum.FIXED_VALUE.getCode())) {
            throw new UnExpectedRequestException("The verification method should be a fixed value");
        }

        newAlarmConfig.setThreshold(threshold);
        Integer checkTemplateCode = checkTemplate;
        if (checkTemplateCode.equals(CheckTemplateEnum.FIXED_VALUE.getCode())
                || checkTemplateCode.equals(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode())
                || checkTemplateCode.equals(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode())
                || checkTemplateCode.equals(CheckTemplateEnum.SEASON_RING_GROWTH.getCode())
                || checkTemplateCode.equals(CheckTemplateEnum.MONTH_RING_GROWTH.getCode())
                || checkTemplateCode.equals(CheckTemplateEnum.WEEK_RING_GROWTH.getCode())
                || checkTemplateCode.equals(CheckTemplateEnum.DAY_RING_GROWTH.getCode())
                || checkTemplateCode.equals(CheckTemplateEnum.HOUR_RING_GROWTH.getCode())
                || checkTemplateCode.equals(CheckTemplateEnum.YEAR_ON_YEAR.getCode())) {
            newAlarmConfig.setCompareType(compareType);
        }
        newAlarmConfig.setUploadAbnormalValue(uploadAbnormalValue);
        newAlarmConfig.setUploadRuleMetricValue(uploadRuleMetricValue);
        newAlarmConfig.setDeleteFailCheckResult(deleteFailCheckResult);
    }

    @Override
    public Integer deleteByRule(Rule rule) {
        return alarmConfigRepository.deleteByRuleId(rule.getId());
    }

    @Override
    public List<AlarmConfig> checkAndSaveCustomAlarmVariable(List<CustomAlarmConfigRequest> requests, Rule rule) throws UnExpectedRequestException {
        List<RuleMetric> ruleMetrics = new ArrayList<>();
        List<AlarmConfig> alarmConfigs = new ArrayList<>();

        for (CustomAlarmConfigRequest request : requests) {
            AlarmConfig newAlarmConfig = new AlarmConfig();
            RuleMetric ruleMetric = null;
            if (StringUtils.isNotBlank(request.getRuleMetricEnCode())) {
                ruleMetric = ruleMetricDao.findByEnCode(request.getRuleMetricEnCode());
                checkRuleMetricInOtherRules(ruleMetric, rule);
            }
            newAlarmConfig.setRuleMetric(ruleMetric);
            ruleMetrics.add(ruleMetric);

            // Check existence of templateOutputMeta
            RuleMetric finalRuleMetric = ruleMetric;
            TemplateOutputMeta templateOutputMetaInDb = rule.getTemplate().getTemplateOutputMetas().stream()
                    .filter(templateOutputMeta -> templateOutputMeta.getOutputName().equals(finalRuleMetric.getName())).iterator().next();

            // Generate alarmConfig and save
            setAlarmConfigInfo(rule, templateOutputMetaInDb, newAlarmConfig, request.getCheckTemplate(), request.getThreshold(), request.getCompareType(), request.getUploadAbnormalValue(), request.getUploadRuleMetricValue(), request.getDeleteFailCheckResult());
            alarmConfigs.add(newAlarmConfig);
        }
        Set<String> subSystemIds = ruleMetrics.stream().map(RuleMetric::getSubSystemId).collect(Collectors.toSet());
        Set<String> deptNames = ruleMetrics.stream().map(RuleMetric::getDevDepartmentName).collect(Collectors.toSet());
        if (subSystemIds.size() > 1 || deptNames.size() > 1) {
            throw new UnExpectedRequestException("{&NOT_SAME_DEPT_SYS_METRIC}");
        }
        return alarmConfigDao.saveAllAlarmConfig(alarmConfigs);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public List<AlarmConfig> checkAndSaveFileAlarmVariable(List<FileAlarmConfigRequest> requests, Rule rule, String loginUser, DataSourceRequest dataSourceRequest) throws UnExpectedRequestException, IOException, PermissionDeniedRequestException {
        List<AlarmConfig> alarmConfigs = new ArrayList<>();

        for (FileAlarmConfigRequest request : requests) {
            // Generate alarmConfig and save
            AlarmConfig newAlarmConfig = new AlarmConfig();
//            newAlarmConfig.setFileOutputName(request.getFileOutputName());
            // Check existence
            TemplateOutputMeta templateOutputMetaInDb = templateOutputMetaService.checkTemplateOutputMetaId(request.getOutputMetaId());
            // Check template output meta
            checkTemplateContain(rule.getTemplate(), templateOutputMetaInDb);
            newAlarmConfig.setFileOutputUnit(request.getFileOutputUnit());
            newAlarmConfig.setTemplateOutputMeta(templateOutputMetaInDb);
            newAlarmConfig.setCheckTemplate(request.getCheckTemplate());
            newAlarmConfig.setRule(rule);
            newAlarmConfig.setThreshold(request.getThreshold());
            Integer checkTemplateCode = request.getCheckTemplate();
            if (checkTemplateCode.equals(CheckTemplateEnum.FIXED_VALUE.getCode())
                    || checkTemplateCode.equals(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode())
                    || checkTemplateCode.equals(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode())
                    || checkTemplateCode.equals(CheckTemplateEnum.SEASON_RING_GROWTH.getCode())
                    || checkTemplateCode.equals(CheckTemplateEnum.MONTH_RING_GROWTH.getCode())
                    || checkTemplateCode.equals(CheckTemplateEnum.WEEK_RING_GROWTH.getCode())
                    || checkTemplateCode.equals(CheckTemplateEnum.DAY_RING_GROWTH.getCode())
                    || checkTemplateCode.equals(CheckTemplateEnum.HOUR_RING_GROWTH.getCode())
                    || checkTemplateCode.equals(CheckTemplateEnum.YEAR_ON_YEAR.getCode())) {
                newAlarmConfig.setCompareType(request.getCompareType());
            }
            if (StringUtils.isNotBlank(request.getRuleMetricEnCode())) {
                RuleMetric ruleMetric = ruleMetricDao.findByEnCode(request.getRuleMetricEnCode());
                checkRuleMetricInOtherRules(ruleMetric, rule);
                newAlarmConfig.setRuleMetric(ruleMetric);
            } else if (StringUtils.isNotBlank(request.getRuleMetricName()) && dataSourceRequest != null) {
                RuleMetric ruleMetric = ruleMetricCommonService.accordingRuleMetricNameAdd(request.getRuleMetricName(), loginUser, CollectionUtils.isNotEmpty(dataSourceRequest.getDataSourceEnvRequests()) ? true : false);
                if (ruleMetric == null) {
                    throw new UnExpectedRequestException("{&FAILED_TO_AUTOMATE_CREATE_METRICS}");
                }
                newAlarmConfig.setRuleMetric(ruleMetric);
            }
            newAlarmConfig.setUploadAbnormalValue(request.getUploadAbnormalValue());
            newAlarmConfig.setUploadRuleMetricValue(request.getUploadRuleMetricValue());
            newAlarmConfig.setDeleteFailCheckResult(request.getDeleteFailCheckResult());
            alarmConfigs.add(newAlarmConfig);
        }

        return alarmConfigDao.saveAllAlarmConfig(alarmConfigs);
    }

    private void checkTemplateContain(Template template, TemplateOutputMeta templateOutputMeta) throws UnExpectedRequestException {
        if (!template.getTemplateOutputMetas().contains(templateOutputMeta)) {
            throw new UnExpectedRequestException("Template: [" + template.getName() + "] {&DOES_NOT_CONTAIN_OUTPUT_META_ID}: ["
                    + templateOutputMeta.getId() + "]");
        }
    }
}
