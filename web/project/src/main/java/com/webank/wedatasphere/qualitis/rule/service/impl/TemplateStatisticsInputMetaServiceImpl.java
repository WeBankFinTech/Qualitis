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

import com.webank.wedatasphere.qualitis.rule.constant.FunctionTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.StatisticsValueTypeEnum;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.service.RuleTemplateService;
import com.webank.wedatasphere.qualitis.rule.service.TemplateStatisticsInputMetaService;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.rule.dao.repository.TemplateStatisticsInputMetaRepository;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateStatisticsInputMeta;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.rule.constant.StatisticsValueTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.repository.TemplateStatisticsInputMetaRepository;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateStatisticsInputMeta;
import com.webank.wedatasphere.qualitis.rule.service.RuleTemplateService;
import com.webank.wedatasphere.qualitis.rule.service.TemplateStatisticsInputMetaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * @author howeye
 */
@Service
public class TemplateStatisticsInputMetaServiceImpl implements TemplateStatisticsInputMetaService {

    @Autowired
    private TemplateStatisticsInputMetaRepository repository;

    @Autowired
    private RuleTemplateService templateService;

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateStatisticsInputMetaServiceImpl.class);

    @Override
    public TemplateStatisticsInputMeta checkStatisticsArgId(Long statisticsArgId) throws UnExpectedRequestException {
        TemplateStatisticsInputMeta templateStatisticsInputMeta = repository.findById(statisticsArgId).orElse(null);
        if (templateStatisticsInputMeta == null) {
            throw new UnExpectedRequestException("statistics_id {&DOES_NOT_EXIST}");
        }

        return templateStatisticsInputMeta;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public Set<TemplateStatisticsInputMeta> getAndSaveTemplateStatisticsInputMeta(String outputName, Integer functionType, String functionContent,
                                                                                  Boolean saveMidTable, Template template) {
        Set<TemplateStatisticsInputMeta> templateStatisticsInputMetas = new HashSet<>();
        TemplateStatisticsInputMeta templateStatisticsInputMeta = new TemplateStatisticsInputMeta();
        templateStatisticsInputMeta.setFuncName(FunctionTypeEnum.getByCode(functionType).getFunction());
        if (saveMidTable) {
            templateStatisticsInputMeta.setValue(functionContent);
        } else {
            templateStatisticsInputMeta.setValue(templateService.getFunctionAlias(functionType));
        }
        templateStatisticsInputMeta.setName(outputName);
        templateStatisticsInputMeta.setResultType("Long");
        templateStatisticsInputMeta.setValueType(StatisticsValueTypeEnum.FIXED_VALUE.getCode());
        templateStatisticsInputMeta.setTemplate(template);

        TemplateStatisticsInputMeta savedTemplateStatisticsInputMeta = repository.save(templateStatisticsInputMeta);
        templateStatisticsInputMetas.add(savedTemplateStatisticsInputMeta);
        LOGGER.info("Succeed to save template statistics input_meta, input_meta_id: {}, name: {}", savedTemplateStatisticsInputMeta.getId(), savedTemplateStatisticsInputMeta.getName());
        return templateStatisticsInputMetas;
    }
}
