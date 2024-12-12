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

import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTemplateTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.RuleTemplateDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.TemplateRepository;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
    public List<Template> findAllDefaultTemplate(int page, int size, Integer templateType, String cnName, String enName, Integer dataSourceType, Long verificationLevel, Long verificationType, String createId, String modifyId, Long devDepartmentId, Long opsDepartmentId, Set<String> actionRange, String dataType) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return templateRepository.findByTemplateType(templateType, cnName, enName, dataSourceType, verificationLevel, verificationType, createId, modifyId, devDepartmentId, opsDepartmentId, actionRange, dataType, pageable).getContent();
    }

    @Override
    public List<Template> findAllDefaultTemplateByLevel(Integer level, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return templateRepository.findByLevel(level, pageable).getContent();
    }

    @Override
    public Long countAllDefaultTemplate(Integer templateType, String cnName, String enName, Integer dataSourceType, Long verificationLevel, Long verificationType, String createId, String modifyId, Long devDepartmentId, Long opsDepartmentId, Set<String> actionRange, String dataType) {
        return templateRepository.countByTemplateType(templateType, cnName, enName, dataSourceType, verificationLevel, verificationType, createId, modifyId, devDepartmentId, opsDepartmentId, actionRange, dataType);
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
    public List<Template> findAllMultiTemplate(Integer dataSourceTypeCode, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return templateRepository.findByTemplateTypeAndParentTemplateIsNull(RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode(), dataSourceTypeCode, pageable).getContent();
    }

    @Override
    public Long countAllMultiTemplate(Integer dataSourceTypeCode) {
        return templateRepository.countByTemplateTypeAndParentTemplateIsNull(RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode(), dataSourceTypeCode);
    }

    @Override
    public List<Template> getAllTemplate() {
        return templateRepository.findDefaultAndMultiTemplate();
    }

    @Override
    public Template findByName(String name) {
        return templateRepository.findByName(name);
    }

    @Override
    public Template findByImportExportName(String importExportName) {
        return templateRepository.findByImportExportName(importExportName);
    }

    @Override
    public Page<Template> findTemplates(Integer type, Integer dataSourceTypeId, String tableDataType, List<Long> dataVisibilityDeptList, Long createUserId, String cnName, String enName, Long verificationLevel, Long verificationType, String createId, String modifyId, Long devDepartmentId, Long opsDepartmentId, Set<String> actionRange, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Template> resultPage = templateRepository.findTemplates(type, dataSourceTypeId, tableDataType, dataVisibilityDeptList, createUserId, cnName, enName, verificationLevel, verificationType, createId, modifyId, devDepartmentId, opsDepartmentId, actionRange, pageable);
        return resultPage;
    }

    @Override
    public long countTemplates(Integer multiSourceTemplateCode, Integer dataSourceTypeId, String tableDataType, List<Long> dataVisibilityDeptList, User createUser,String cnName,String enName,Long verificationLevel,Long verificationType,Long createId,Long modifyId,Long devDepartmentId,Long opsDepartmentId) {
        return templateRepository.countTemplates(multiSourceTemplateCode, dataSourceTypeId, tableDataType, dataVisibilityDeptList, createUser,cnName,enName,verificationLevel,verificationType,createId,modifyId,devDepartmentId,opsDepartmentId);
    }

    @Override
    public List<Map<String, Object>> findTemplatesOptionListInRule() {
        return templateRepository.findTemplatesOptionListInRule();
    }

    @Override
    public List<Map<String, Object>> findTemplatesOptionList(String tableDataType, List<Long> dataVisibilityDeptList, User createUser, Integer templateType) {
        return templateRepository.findTemplatesOptionList(tableDataType, dataVisibilityDeptList, createUser, templateType);
    }

    @Override
    public List<Map<String, Object>> findAllTemplatesOptionList(Integer templateType) {
        return templateRepository.findAllTemplatesOptionList(templateType);
    }

    @Override
    public Optional<Template> getDefaultByName(String templateName) {
        Specification<Template> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("name"), templateName));
            predicates.add(criteriaBuilder.notEqual(root.get("templateType"), 2));

            Predicate[] p = new Predicate[predicates.size()];
            query.where(criteriaBuilder.and(predicates.toArray(p)));

            return query.getRestriction();
        };
        return templateRepository.findOne(specification);
    }

    @Override
    public List<Map<String ,Object>> getTemplateDefaultInputMeta(List<Integer> ids) {
        return templateRepository.getTemplateDefaultInputMeta(ids);
    }

    @Override
    public List<Template> findTemplateByEnName(String templateEnName) {
        return templateRepository.findTemplateByEnName(templateEnName);
    }

}
