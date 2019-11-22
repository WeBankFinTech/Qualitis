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

package com.webank.wedatasphere.qualitis.rule.dao.impl;

import com.webank.wedatasphere.qualitis.rule.dao.RuleTemplateDao;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTemplateTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.repository.TemplateRepository;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.dao.RuleTemplateDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.TemplateRepository;
import org.aspectj.lang.annotation.AfterReturning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author howeye
 */
@Repository
public class RuleTemplateDaoImpl implements RuleTemplateDao {

    @Autowired
    private TemplateRepository templateRepository;

    @Override
    public Template findById(Long ruleTemplateId) {
        return templateRepository.findById(ruleTemplateId).orElse(null);
    }

    @Override
    public List<Template> findAllDefaultTemplate(int page, int size) {
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return templateRepository.findByTemplateType(RuleTemplateTypeEnum.SINGLE_SOURCE_TEMPLATE.getCode(), pageable).getContent();
    }

    @Override
    public Long countAllDefaultTemplate() {
        return templateRepository.countByTemplateType(RuleTemplateTypeEnum.SINGLE_SOURCE_TEMPLATE.getCode());
    }

    @Override
    public Template saveTemplate(Template template) {
        return templateRepository.save(template);
    }

    @Override
    public void deleteTemplate(Template template) {
        templateRepository.delete(template);
    }

    @Override
    public List<Template> findAllMultiTemplate(int page, int size) {
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return templateRepository.findByTemplateTypeAndParentTemplateIsNull(RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode(), pageable).getContent();
    }

    @Override
    public Long countAllMultiTemplate() {
        return templateRepository.countByTemplateTypeAndParentTemplateIsNull(RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode());
    }

    @Override
    public List<Template> getAllTemplate() {
        return templateRepository.findAll();
    }
}
