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

import com.webank.wedatasphere.qualitis.rule.request.AlarmConfigRequest;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.rule.constant.CheckTemplateEnum;
import com.webank.wedatasphere.qualitis.rule.dao.AlarmConfigDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.AlarmConfigRepository;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmConfig;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateOutputMeta;
import com.webank.wedatasphere.qualitis.rule.request.CustomAlarmConfigRequest;
import com.webank.wedatasphere.qualitis.rule.service.TemplateOutputMetaService;
import com.webank.wedatasphere.qualitis.rule.service.AlarmConfigService;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.rule.constant.CheckTemplateEnum;
import com.webank.wedatasphere.qualitis.rule.dao.AlarmConfigDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.AlarmConfigRepository;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmConfig;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateOutputMeta;
import com.webank.wedatasphere.qualitis.rule.request.AlarmConfigRequest;
import com.webank.wedatasphere.qualitis.rule.request.CustomAlarmConfigRequest;
import com.webank.wedatasphere.qualitis.rule.service.AlarmConfigService;
import com.webank.wedatasphere.qualitis.rule.service.TemplateOutputMetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author howeye
 */
@Service
public class AlarmConfigServiceImpl implements AlarmConfigService {

    @Autowired
    private AlarmConfigDao alarmConfigDao;

    @Autowired
    private TemplateOutputMetaService templateOutputMetaService;

    @Autowired
    private AlarmConfigRepository alarmConfigRepository;

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public List<AlarmConfig> checkAndSaveAlarmVariable(List<AlarmConfigRequest> requests, Rule rule) throws UnExpectedRequestException {
        List<AlarmConfig> alarmConfigs = new ArrayList<>();
        for (AlarmConfigRequest request : requests) {
            // Check Arguments
            AlarmConfigRequest.checkRequest(request);

            // Check existence of templateOutputMeta
            TemplateOutputMeta templateOutputMetaInDb = templateOutputMetaService.checkTemplateOutputMetaId(request.getOutputMetaId());
            // return if is multi-table template
            if (isChildOrParentOutput(rule.getTemplate(), templateOutputMetaInDb)) {
                continue;
            }

            // Check template output meta
            checkTemplateContain(rule.getTemplate(), templateOutputMetaInDb);

            // Generate alarmConfig and save
            AlarmConfig newAlarmConfig = new AlarmConfig();
            newAlarmConfig.setRule(rule);
            newAlarmConfig.setTemplateOutputMeta(templateOutputMetaInDb);
            newAlarmConfig.setCheckTemplate(request.getCheckTemplate());
            newAlarmConfig.setThreshold(request.getThreshold());
            if (request.getCheckTemplate().equals(CheckTemplateEnum.FIXED_VALUE.getCode())) {
                newAlarmConfig.setCompareType(request.getCompareType());
            }

            alarmConfigs.add(newAlarmConfig);
        }

        return alarmConfigDao.saveAllAlarmConfig(alarmConfigs);
    }

    @Override
    public void deleteByRule(Rule rule) {
        alarmConfigRepository.deleteByRule(rule);
    }

    @Override
    public List<AlarmConfig> checkAndSaveCustomAlarmVariable(List<CustomAlarmConfigRequest> requests, Rule rule) {
        List<AlarmConfig> alarmConfigs = new ArrayList<>();
        for (CustomAlarmConfigRequest request : requests) {

            // Check existence of templateOutputMeta
            TemplateOutputMeta templateOutputMetaInDb = rule.getTemplate().getTemplateOutputMetas().iterator().next();

            // Generate alarmConfig and save
            AlarmConfig newAlarmConfig = new AlarmConfig();
            newAlarmConfig.setRule(rule);
            newAlarmConfig.setTemplateOutputMeta(templateOutputMetaInDb);
            newAlarmConfig.setCheckTemplate(request.getCheckTemplate());
            newAlarmConfig.setThreshold(request.getThreshold());
            if (request.getCheckTemplate().equals(CheckTemplateEnum.FIXED_VALUE.getCode())) {
                newAlarmConfig.setCompareType(request.getCompareType());
            }

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

    private Boolean isChildOrParentOutput(Template template, TemplateOutputMeta templateOutputMeta) {
        Template childTemplate = template.getChildTemplate();
        Template parentTemplate = template.getParentTemplate();

        if (childTemplate != null) {
            if (childTemplate.getTemplateOutputMetas().contains(templateOutputMeta)) {
                return true;
            }
        }
        if (parentTemplate != null) {
            if (parentTemplate.getTemplateOutputMetas().contains(templateOutputMeta)) {
                return true;
            }
        }
        return false;
    }
}
