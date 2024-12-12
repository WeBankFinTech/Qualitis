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

package com.webank.wedatasphere.qualitis.rule.dao.repository;

import com.webank.wedatasphere.qualitis.entity.Department;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author howeye
 */
public interface TemplateRepository extends JpaRepository<Template, Long> {

    /**
     * Find template by user permission.
     * @param level
     * @param departmentList
     * @param userList
     * @param type
     * @param dataSourceTypeId
     * @param pageable
     * @return
     */
    @Query(value = "SELECT qt FROM Template qt where (qt.level = ?1 and qt.templateType = ?2 or (qt IN (select qtd.template FROM TemplateDepartment qtd where qtd.department IN (?3)) and qt.templateType = ?2) or (qt IN (select qtu.template FROM TemplateUser qtu where qtu.user IN (?4)) and qt.templateType = ?2)) AND qt IN (SELECT tdst.template FROM TemplateDataSourceType tdst where tdst.dataSourceTypeId = ?5)")
    Page<Template> findTemplates(Integer level, Integer type, List<Department> departmentList, List<User> userList, Integer dataSourceTypeId, Pageable pageable);

    /**
     * Count templates;
     * @param level
     * @param type
     * @param departments
     * @param users
     * @param dataSourceTypeId
     * @return
     */
    @Query(value = "SELECT count(qt.id) FROM Template qt where (qt.level = ?1 and qt.templateType = ?2 or (qt IN (select qtd.template FROM TemplateDepartment qtd where qtd.department IN (?3)) and qt.templateType = ?2) or (qt IN (select qtu.template FROM TemplateUser qtu where qtu.user IN (?4)) and qt.templateType = ?2)) AND qt IN (SELECT tdst.template FROM TemplateDataSourceType tdst where tdst.dataSourceTypeId = ?5)")
    long countTemplates(Integer level, Integer type, List<Department> departments, List<User> users, Integer dataSourceTypeId);

    /**
     * Find template by rule template type
     * @param templateType
     * @param pageable
     * @return
     */
    Page<Template> findByTemplateType(Integer templateType, Pageable pageable);

    /**
     * Count by template type
     * @param templateType
     * @return
     */
    Long countByTemplateType(Integer templateType);

    /**
     * Find template by rule template level
     * @param level
     * @param pageable
     * @return
     */
    Page<Template> findByLevel(Integer level, Pageable pageable);

    /**
     * Find template by name
     * @param templateName
     * @return
     */
    Template findByName(String templateName);

    /**
     * Find template by import export name
     * @param importExportName
     * @return
     */
    Template findByImportExportName(String importExportName);

    /**
     * Find template which is not child template by template type
     * @param templateType
     * @param dataSourceTypeCode
     * @param pageable
     * @return
     */
    @Query(value = "SELECT qt FROM Template qt where (qt.templateType = ?1) AND qt IN (SELECT tdst.template FROM TemplateDataSourceType tdst where tdst.dataSourceTypeId = ?2)")
    Page<Template> findByTemplateTypeAndParentTemplateIsNull(Integer templateType, Integer dataSourceTypeCode, Pageable pageable);

    /**
     * Count template which is not child template by template type
     * @param templateType
     * @param dataSourceTypeCode
     * @return
     */
    @Query(value = "SELECT count(qt.id) FROM Template qt where (qt.templateType = ?1) AND qt IN (SELECT tdst.template FROM TemplateDataSourceType tdst where tdst.dataSourceTypeId = ?2)")
    Long countByTemplateTypeAndParentTemplateIsNull(Integer templateType, Integer dataSourceTypeCode);

    /**
     * Find template when import rules.
     * @return
     */
    @Query(value = "select t from Template t where t.templateType != 2")
    List<Template> findDefaultAndMultiTemplate();
}
