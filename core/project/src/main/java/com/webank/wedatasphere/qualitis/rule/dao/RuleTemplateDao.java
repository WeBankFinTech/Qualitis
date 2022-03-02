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

import com.webank.wedatasphere.qualitis.entity.Department;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.rule.entity.Template;

import java.util.List;

/**
 * @author howeye
 */
public interface RuleTemplateDao {
    /**
     * Find rule template by id
     * @param ruleTemplateId
     * @return
     */
    Template findById(Long ruleTemplateId);

    /**
     * Find all rule template
     * @param page
     * @param size
     * @return
     */
    List<Template> findAllDefaultTemplate(int page, int size);

    /**
     * Find template by rule template level
     * @param level
     * @param page
     * @param size
     * @return
     */
    List<Template> findAllDefaultTemplateByLevel(Integer level, int page, int size);

    /**
     * Count all default template
     * @return
     */
    Long countAllDefaultTemplate();

    /**
     * Save template
     * @param template
     * @return
     */
    Template saveTemplate(Template template);

    /**
     * Delete template
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
     * @return
     * @param dataSourceTypeCode
     */
    Long countAllMultiTemplate(Integer dataSourceTypeCode);

    /**
     * Find all template
     * @return
     */
    List<Template> getAllTemplate();

    /**
     * Find template by name
     * @param name
     * @return
     */
    Template findByName(String name);

    /**
     * Find template by import export name
     * @param importExportName
     * @return
     */
    Template findByImportExportName(String importExportName);

    /**
     * Find templates.
     * @param level
     * @param type
     * @param departmentList
     * @param userList
     * @param dataSourceTypeCode
     * @param page
     * @param size
     * @return
     */
    List<Template> findTemplates(Integer level, Integer type, List<Department> departmentList, List<User> userList,
        Integer dataSourceTypeCode, int page, int size);

    /**
     * Count templates.
     * @param code
     * @param multiSourceTemplateCode
     * @param departments
     * @param users
     * @param dataSourceTypeCode
     * @return
     */
    long countTemplates(Integer code, Integer multiSourceTemplateCode, List<Department> departments, List<User> users,
        Integer dataSourceTypeCode);
}
