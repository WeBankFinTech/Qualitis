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

import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateDataSourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author allenzhou
 */
public interface TemplateDataSourceTypeRepository extends JpaRepository<TemplateDataSourceType, Long> {
    /**
     * Find by data source type.
     * @param templateDataSourceId
     * @return
     */
    List<Template> findByDataSourceTypeId(Long templateDataSourceId);

    /**
     * Find by template.
     * @param template
     * @return
     */
    List<TemplateDataSourceType> findByTemplate(Template template);

    /**
     * Delete by template.
     * @param template
     * @return
     */
    void deleteByTemplate(Template template);

    /**
     * find By Template Ids
     * @param templateIds
     * @return
     */
    @Query(value = "select td.* from qualitis_template_datasource_type td where td.template_id in(?1)",nativeQuery = true)
    List<TemplateDataSourceType> findByTemplateIds(List<Long> templateIds);

}
