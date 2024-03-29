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
import com.webank.wedatasphere.qualitis.rule.entity.TemplateMidTableInputMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author howeye
 */
public interface TemplateInputMetaRepository extends JpaRepository<TemplateMidTableInputMeta, Long> {

    /**
     * Find template input meta by template
     * @param template
     * @return
     */
    List<TemplateMidTableInputMeta> findByTemplate(Template template);

    /**
     * Find template input meta by template and name
     * @param template
     * @param name
     * @return
     */
    TemplateMidTableInputMeta findByTemplateAndName(Template template, String name);

    /**
     * Delete mid_table input meta by template
     * @param templateInDb
     */
    void deleteByTemplate(Template templateInDb);

    /**
     * findByInputType
     * @param type
     * @return
     */
    @Query(value = "select td.id from qualitis_template_mid_table_input_meta td where td.input_type = ?1",nativeQuery = true)
    List<Long> findByInputType(Integer type);
}
