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

import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author howeye
 */
public interface TemplateRepository extends JpaRepository<Template, Long>, JpaSpecificationExecutor<Template> {

    /**
     * Find template by user permission.
     *
     * @param type
     * @param dataSourceTypeId
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
     * @param pageable
     * @return
     */
    @Query(value = "SELECT qt.* FROM qualitis_template qt " +
            " where (?1 is null or qt.template_type = ?1) " +
            " AND qt.id in(SELECT tdst.template_id FROM qualitis_template_datasource_type tdst where tdst.template_id=qt.id AND (?2 is null or tdst.data_source_type_id = ?2) ) " +
            " AND if(nullif(?6,'')!='',qt.name like ?6,1=1) " +
            " AND if(nullif(?7,'')!='',qt.en_name like ?7,1=1) " +
            " AND (?8 is null or qt.verification_level = ?8) " +
            " AND (?9 is null or qt.verification_type = ?9) " +
            " AND (?10 is null or qt.create_user_id = ?10) " +
            " AND (?11 is null or qt.modify_user_id = ?11) " +
            " AND (?12 is null or qt.dev_department_id = ?12) " +
            " AND (?13 is null or qt.ops_department_id = ?13) " +
            " AND (coalesce(?14,null) is null or exists (select q.table_data_id from qualitis_auth_data_visibility as q where q.table_data_id=qt.id and q.table_data_type=?3 and (q.department_sub_id in (?14) or q.department_sub_id=0) )) " +
            " AND (EXISTS (select dv.table_data_id from qualitis_auth_data_visibility as dv where dv.table_data_type = ?3 and (dv.department_sub_id in (?4) or dv.department_sub_id=0) and dv.table_data_id=qt.id) OR qt.dev_department_id in (?4) OR qt.ops_department_id in (?4) OR qt.create_user_id = ?5 ) "
            , countQuery = "SELECT count(*) FROM qualitis_template qt " +
            " where (?1 is null or qt.template_type = ?1) " +
            " AND qt.id in(SELECT tdst.template_id FROM qualitis_template_datasource_type tdst where tdst.template_id=qt.id AND (?2 is null or tdst.data_source_type_id = ?2) ) " +
            " AND if(nullif(?6,'')!='',qt.name like ?6,1=1) " +
            " AND if(nullif(?7,'')!='',qt.en_name like ?7,1=1) " +
            " AND (?8 is null or qt.verification_level = ?8) " +
            " AND (?9 is null or qt.verification_type = ?9) " +
            " AND (?10 is null or qt.create_user_id = ?10) " +
            " AND (?11 is null or qt.modify_user_id = ?11) " +
            " AND (?12 is null or qt.dev_department_id = ?12) " +
            " AND (?13 is null or qt.ops_department_id = ?13) " +
            " AND (coalesce(?14,null) is null or exists (select q.table_data_id from qualitis_auth_data_visibility as q where q.table_data_id=qt.id and q.table_data_type=?3 and (q.department_sub_id in (?14) or q.department_sub_id=0) )) " +
            " AND (EXISTS (select dv.table_data_id from qualitis_auth_data_visibility as dv where dv.table_data_type = ?3 and (dv.department_sub_id in (?4) or dv.department_sub_id=0) and dv.table_data_id=qt.id) OR qt.dev_department_id in (?4) OR qt.ops_department_id in (?4) OR qt.create_user_id = ?5 ) ", nativeQuery = true)
    Page<Template> findTemplates(Integer type, Integer dataSourceTypeId
            , String tableDataType, List<Long> dataVisibilityDeptList, Long createUserId, String cnName, String enName, Long verificationLevel, Long verificationType, String createId, String modifyId, Long devDepartmentId, Long opsDepartmentId, Set<String> actionRange, Pageable pageable);

    /**
     * Count templates;
     *
     * @param type
     * @param dataSourceTypeId
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
    @Query(value = "SELECT count(qt.id) FROM Template qt where (qt.templateType = ?1 AND EXISTS (SELECT tdst.id FROM TemplateDataSourceType tdst where tdst.dataSourceTypeId = ?2 AND qt = tdst.template) " +
            "AND (" +
            "EXISTS (SELECT dv.tableDataId FROM DataVisibility dv WHERE dv.tableDataType = ?3 AND (dv.departmentSubId in (?4) OR dv.departmentSubId=0) AND qt = dv.tableDataId) " +
            "OR qt.devDepartmentId in (?4) OR qt.opsDepartmentId in (?4) OR qt.createUser = ?5 " +
            ")" +
            "AND ((LENGTH(?6) = 0 OR qt.name like ?6) AND (LENGTH(?7) = 0 OR qt.enName like ?7) AND (?8 is null OR qt.verificationLevel = ?8)  AND (?9 is null OR qt.verificationType = ?9) AND (?10 is null OR qt.createUser.id = ?10) " +
            "AND (?11 is null OR qt.modifyUser.id = ?11) AND (?12 is null OR qt.devDepartmentId = ?12) AND (?13 is null OR qt.opsDepartmentId = ?13))) "
    )
    long countTemplates(Integer type, Integer dataSourceTypeId
            , String tableDataType, List<Long> dataVisibilityDeptList, User createUser, String cnName, String enName, Long verificationLevel, Long verificationType, Long createId, Long modifyId, Long devDepartmentId, Long opsDepartmentId);

    /**
     * Find template by rule template type
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
     * @param pageable
     * @return
     */
    @Query(value = "SELECT qt.* FROM qualitis_template qt  " +
            " where (?1 is null or qt.template_type = ?1)  " +
            " AND qt.id in(SELECT tdst.template_id FROM qualitis_template_datasource_type tdst where tdst.template_id=qt.id AND (?4 is null or tdst.data_source_type_id = ?4) ) " +
            " AND if(nullif(?2,'')!='',qt.name like ?2,1=1) " +
            " AND if(nullif(?3,'')!='',qt.en_name like ?3,1=1) " +
            " AND (?5 is null or qt.verification_level = ?5) " +
            " AND (?6 is null or qt.verification_type = ?6) " +
            " AND (?7 is null or qt.create_user_id = ?7)  " +
            " AND (?8 is null or qt.modify_user_id = ?8)  " +
            " AND (?9 is null or qt.dev_department_id = ?9)  " +
            " AND (?10 is null or qt.ops_department_id = ?10)  " +
            " AND (coalesce(?11,null) is null or exists (select q.table_data_id from qualitis_auth_data_visibility as q where q.table_data_id=qt.id and q.table_data_type=?12 and (q.department_sub_id in (?11) or q.department_sub_id=0) )) ",
            countQuery = "SELECT count(*) FROM qualitis_template qt " +
                    " where (?1 is null or qt.template_type = ?1)  " +
                    " AND qt.id in(SELECT tdst.template_id FROM qualitis_template_datasource_type tdst where tdst.template_id=qt.id AND (?4 is null or tdst.data_source_type_id = ?4) ) " +
                    " AND if(nullif(?2,'')!='',qt.name like ?2,1=1) " +
                    " AND if(nullif(?3,'')!='',qt.en_name like ?3,1=1) " +
                    " AND (?5 is null or qt.verification_level = ?5) " +
                    " AND (?6 is null or qt.verification_type = ?6) " +
                    " AND (?7 is null or qt.create_user_id = ?7)  " +
                    " AND (?8 is null or qt.modify_user_id = ?8)  " +
                    " AND (?9 is null or qt.dev_department_id = ?9)  " +
                    " AND (?10 is null or qt.ops_department_id = ?10)  " +
                    " AND (coalesce(?11,null) is null or exists (select q.table_data_id from qualitis_auth_data_visibility as q where q.table_data_id=qt.id and q.table_data_type=?12 and (q.department_sub_id in (?11) or q.department_sub_id=0) )) "
            , nativeQuery = true)
    Page<Template> findByTemplateType(Integer templateType, String cnName, String enName, Integer dataSourceType, Long verificationLevel, Long verificationType, String createId, String modifyId, Long devDepartmentId, Long opsDepartmentId, Set<String> actionRange, String dataType, Pageable pageable);

    /**
     * Count by template type
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
    @Query(value = "SELECT count(*) FROM qualitis_template qt " +
            " where (?1 is null or qt.template_type = ?1)  " +
            " AND qt.id in(SELECT tdst.template_id FROM qualitis_template_datasource_type tdst where tdst.template_id=qt.id AND (?4 is null or tdst.data_source_type_id = ?4) ) " +
            " AND if(nullif(?2,'')!='',qt.name like ?2,1=1) " +
            " AND if(nullif(?3,'')!='',qt.en_name like ?3,1=1) " +
            " AND (?5 is null or qt.verification_level = ?5) " +
            " AND (?6 is null or qt.verification_type = ?6) " +
            " AND (?7 is null or qt.create_user_id = ?7)  " +
            " AND (?8 is null or qt.modify_user_id = ?8)  " +
            " AND (?9 is null or qt.dev_department_id = ?9)  " +
            " AND (?10 is null or qt.ops_department_id = ?10)  " +
            " AND (coalesce(?11,null) is null or exists (select q.table_data_id from qualitis_auth_data_visibility as q where q.table_data_id=qt.id and q.table_data_type=?12 and (q.department_sub_id in (?11) or q.department_sub_id=0) )) "
            , nativeQuery = true)
    Long countByTemplateType(Integer templateType, String cnName, String enName, Integer dataSourceType, Long verificationLevel, Long verificationType, String createId, String modifyId, Long devDepartmentId, Long opsDepartmentId, Set<String> actionRange, String dataType);

    /**
     * Find template by rule template level
     *
     * @param level
     * @param pageable
     * @return
     */
    Page<Template> findByLevel(Integer level, Pageable pageable);

    /**
     * Find template by name
     *
     * @param templateName
     * @return
     */
    Template findByName(String templateName);

    /**
     * Find template by import export name
     *
     * @param importExportName
     * @return
     */
    Template findByImportExportName(String importExportName);

    /**
     * Find template which is not child template by template type
     *
     * @param templateType
     * @param dataSourceTypeCode
     * @param pageable
     * @return
     */
    @Query(value = "SELECT qt FROM Template qt where (qt.templateType = ?1) AND qt IN (SELECT tdst.template FROM TemplateDataSourceType tdst where tdst.dataSourceTypeId = ?2)")
    Page<Template> findByTemplateTypeAndParentTemplateIsNull(Integer templateType, Integer dataSourceTypeCode, Pageable pageable);

    /**
     * Count template which is not child template by template type
     *
     * @param templateType
     * @param dataSourceTypeCode
     * @return
     */
    @Query(value = "SELECT count(qt.id) FROM Template qt where (qt.templateType = ?1) AND qt IN (SELECT tdst.template FROM TemplateDataSourceType tdst where tdst.dataSourceTypeId = ?2)")
    Long countByTemplateTypeAndParentTemplateIsNull(Integer templateType, Integer dataSourceTypeCode);

    /**
     * Find template when import rules.
     *
     * @return
     */
    @Query(value = "select t from Template t where t.templateType != 2")
    List<Template> findDefaultAndMultiTemplate();

    /**
     * Find template from Rule
     *
     * @return
     */
    @Query(value = "select r.template_id as template_id, r.rule_template_name as template_name " +
            "from qualitis_rule r where r.template_id is not null or r.template_id != '' group by template_id"
            , nativeQuery = true)
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
    @Query(value = "SELECT new map(qt.id as template_id, qt.name as template_name) FROM Template qt " +
            " WHERE " +
            " (EXISTS (SELECT dv.tableDataId FROM DataVisibility dv WHERE dv.tableDataType = ?1 AND (dv.departmentSubId in (?2) OR dv.departmentSubId=0) AND qt = dv.tableDataId) " +
            " OR qt.devDepartmentId in (?2) OR qt.opsDepartmentId in (?2) OR qt.createUser = ?3) " +
            " AND (LENGTH(?4) = 0 OR qt.templateType = ?4)"
    )
    List<Map<String, Object>> findTemplatesOptionList(String tableDataType, List<Long> dataVisibilityDeptList, User createUser, Integer templateType);

    /**
     * findAllTemplatesOptionList
     *
     * @param templateType
     * @return
     */
    @Query(value = "SELECT new map(qt.id as template_id, qt.name as template_name) FROM Template qt WHERE (LENGTH(?1) = 0 OR qt.templateType = ?1)")
    List<Map<String, Object>> findAllTemplatesOptionList(Integer templateType);


    /**
     * getTemplateDefaultInputMeta
     *
     * @param ids
     * @return
     */
    @Query(value = "select  * from qualitis_template_default_input_meta where id in (?1) "
            , nativeQuery = true)
    List<Map<String, Object>> getTemplateDefaultInputMeta(List<Integer> ids);

    /**
     * find Template By EnName
     *
     * @param templateEnName
     * @return
     */
    @Query(value = "select t from Template t where t.enName = ?1")
    List<Template> findTemplateByEnName(String templateEnName);
}
