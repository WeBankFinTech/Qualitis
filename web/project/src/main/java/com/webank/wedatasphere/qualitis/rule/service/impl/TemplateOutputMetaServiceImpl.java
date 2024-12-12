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

import com.webank.wedatasphere.qualitis.rule.constant.FieldTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.FunctionTypeEnum;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.service.TemplateOutputMetaService;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.rule.dao.TemplateOutputMetaDao;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateOutputMeta;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.rule.dao.TemplateOutputMetaDao;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateOutputMeta;
import com.webank.wedatasphere.qualitis.rule.service.TemplateOutputMetaService;
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
public class TemplateOutputMetaServiceImpl implements TemplateOutputMetaService {

    @Autowired
    private TemplateOutputMetaDao templateOutputMetaDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateOutputMetaServiceImpl.class);

    @Override
    public TemplateOutputMeta checkTemplateOutputMetaId(Long templateOutputMetaId) throws UnExpectedRequestException {
        TemplateOutputMeta templateOutputMeta =  templateOutputMetaDao.findById(templateOutputMetaId);
        if (templateOutputMeta == null) {
            throw new UnExpectedRequestException("template_output_id {&DOES_NOT_EXIST}");
        }

        return templateOutputMeta;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public Set<TemplateOutputMeta> getAndSaveTemplateOutputMeta(String outputName, Integer functionType, Boolean saveMidTable, Template template) {
        Set<TemplateOutputMeta> templateOutputMetas = new HashSet<>();
        TemplateOutputMeta templateOutputMeta = new TemplateOutputMeta();
        templateOutputMeta.setTemplate(template);
        templateOutputMeta.setOutputName(outputName);
        if (saveMidTable) {
            templateOutputMeta.setFieldName(FunctionTypeEnum.getByCode(functionType).getFunction());
        } else {
            templateOutputMeta.setFieldName(FunctionTypeEnum.MAX_FUNCTION.getFunction());
        }
        templateOutputMeta.setFieldType(FieldTypeEnum.NUMBER.getCode());
        TemplateOutputMeta savedTemplateOutputMeta = templateOutputMetaDao.saveTemplateOutputMeta(templateOutputMeta);
        LOGGER.info("Succeed to save template output_meta: {}", templateOutputMeta);

        templateOutputMetas.add(savedTemplateOutputMeta);
        return templateOutputMetas;
    }

    @Override
    public void deleteByTemplate(Template templateInDb) {
        templateOutputMetaDao.deleteByTemplate(templateInDb);
    }
}
