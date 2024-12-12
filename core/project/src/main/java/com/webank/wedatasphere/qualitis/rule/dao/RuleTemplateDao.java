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

package com.webank.wedatasphere.qualitis.rule.dao;

import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author howeye
 */
public interface RuleTemplateDao {
    /**
     * Find rule template by id
     *
     * @param ruleTemplateId
     * @return
     */
    Template findById(Long ruleTemplateId);

    /**
     * find by ids
     * @param templateIds
     * @return
     */
    List<Template> findByIds(List<Long> templateIds);

    /**
     * Find all rule template
     *
     * @param page
     * @param size
     * @param templateType
     * @param cnName
     * @param enName
     * @param dataSourceType
     * @param verificationLevel
     * @param verificationType
     * @param createId
     * @param modifyId
     * @param devDepartmentId
     * @param opsDepartmentId
     * @param actionRange
     * @param dataType
     * @return
     */
    List<Template> findAllDefaultTemplate(int page, int size, Integer templateType, String cnName, String enName, Integer dataSourceType, Long verificationLevel, Long verificationType, String createId, String modifyId, Long devDepartmentId, Long opsDepartmentId, Set<String> actionRange, String dataType);

    /**
     * Find template by rule template level
     *
     * @param level
     * @param page
     * @param size
     * @return
     */
    List<Template> findAllDefaultTemplateByLevel(Integer level, int page, int size);

    /**
     * Count all default template
     *
     * @param templateType
     * @param cnName
     * @param enName
     * @param dataSourceType
     * @param verificationLevel
     * @param verificationType
     * @param createId
     * @param modifyId
     * @param devDepartmentId
     * @param opsDepartmentId
     * @param actionRange
     * @param dataType
     * @return
     */
    Long countAllDefaultTemplate(Integer templateType, String cnName, String enName, Integer dataSourceType, Long verificationLevel, Long verificationType, String createId, String modifyId, Long devDepartmentId, Long opsDepartmentId, Set<String> actionRange, String dataType);


    /**
     * Save template
     *
     * @param template
     * @return
     */
    Template saveTemplate(Template template);

    /**
     * Delete template
     *
     * @param template
     */
    void deleteTemplate(Template template);

    /**
     * Find all multi-table verification template
     *
     * @param dataSourceTypeCode
     * @param page
     * @param size
     * @return
     */
    List<Template> findAllMultiTemplate(Integer dataSourceTypeCode, int page, int size);

    /**
     * Count all multi-table template
     *
     * @param dataSourceTypeCode
     * @return
     */
    Long countAllMultiTemplate(Integer dataSourceTypeCode);

    /**
     * Find all template
     *
     * @return
     */
    List<Template> getAllTemplate();

    /**
     * Find template by name
     *
     * @param name
     * @return
     */
    Template findByName(String name);

    /**
     * Find template by import export name
     *
     * @param importExportName
     * @return
     */
    Template findByImportExportName(String importExportName);

    /**
     * Find templates.
     *
     * @param type
     * @param dataSourceTypeCode
     * @param tableDataType
     * @param dataVisibilityDeptList
     * @param createUserId
     * @param cnName
     * @param enName
     * @param verificationLevel
     * @param verificationType
     * @param createId
     * @param modifyId
     * @param devDepartmentId
     * @param opsDepartmentId
     * @param actionRange
     * @param page
     * @param size
     * @return
     */
    Page<Template> findTemplates(Integer type,
                                 Integer dataSourceTypeCode, String tableDataType, List<Long> dataVisibilityDeptList, Long createUserId, String cnName, String enName
                                , Long verificationLevel, Long verificationType, String createId, String modifyId, Long devDepartmentId, Long opsDepartmentId, Set<String> actionRange
                                , String createStartTime, String createEndTime, String updateStartTime, String updateEndTime
                                , Long templateId, String description
                                , int page, int size);

    Page<Template> findTemplatesWithAdmin(Integer type,
                                 Integer dataSourceTypeCode, String tableDataType, String cnName, String enName
            , Long verificationLevel, Long verificationType, String createId, String modifyId, Long devDepartmentId, Long opsDepartmentId, Set<String> actionRange
            , String createStartTime, String createEndTime, String updateStartTime, String updateEndTime
            , Long templateId, String description
            , int page, int size);

    /**
     * Count templates.
     *
     * @param multiSourceTemplateCode
     * @param dataSourceTypeCode
     * @param tableDataType
     * @param dataVisibilityDeptList
     * @param createUser
     * @param cnName
     * @param enName
     * @param verificationLevel
     * @param verificationType
     * @param createId
     * @param modifyId
     * @param devDepartmentId
     * @param opsDepartmentId
     * @return
     */
    long countTemplates(Integer multiSourceTemplateCode,
                        Integer dataSourceTypeCode, String tableDataType, List<Long> dataVisibilityDeptList, User createUser, String cnName, String enName, Long verificationLevel, Long verificationType, Long createId, Long modifyId, Long devDepartmentId, Long opsDepartmentId);

    /**
     * find By Rule
     *
     * @return
     */
    List<Map<String, Object>> findTemplatesOptionListInRule();

    /**
     * findTemplatesOptionList
     *
     * @param tableDataType
     * @param dataVisibilityDeptList
     * @param createUser
     * @param templateType
     * @return
     */
    List<Map<String, Object>> findTemplatesOptionList(String tableDataType, List<Long> dataVisibilityDeptList, User createUser, Integer templateType);

    /**
     * findAllTemplatesOptionList
     *
     * @param templateType
     * @return
     */
    List<Map<String, Object>> findAllTemplatesOptionList(Integer templateType);

    /**
     * find All Templates By Project Id
     *
     * @param projectId
     * @return
     */
    List<Map<String, Object>> findAllTemplatesByProjectId(Long projectId);

    /**
     * find Hive Data Source Type All Templates
     *
     * @param dataSourceTypes
     * @param code
     * @return
     */
    List<Map<String, Object>> findHiveDataSourceTypeAllTemplates(List<Integer> dataSourceTypes, Integer code);

    /**
     * Get default or multi template by name
     *
     * @param templateName
     * @return
     */
    Optional<Template> getDefaultByName(String templateName);

    /**
     * getTemplateMidTableInputMeta
     *
     * @param enNames
     * @return
     */
    List<Map<String, Object>> getTemplateDefaultInputMeta(List<String> enNames);

    /**
     * find Template By EnName
     *
     * @param templateEnName
     * @return
     */
    Template findTemplateByEnName(String templateEnName);

    /**
     * query templates by calcu_unit_ids
     *
     * @param calcuUnitIds
     * @return
     */
    List<Template> findMetricCollectTemplates(List<Long> calcuUnitIds);
}
