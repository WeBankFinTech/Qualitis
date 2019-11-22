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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author howeye
 */
public interface TemplateRepository extends JpaRepository<Template, Long> {

    /**
     * Find template by rule template
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
     * Find template by name
     * @param templateName
     * @return
     */
    Template findByName(String templateName);

    /**
     * Find template which is not child template by template type
     * @param templateType
     * @param pageable
     * @return
     */
    Page<Template> findByTemplateTypeAndParentTemplateIsNull(Integer templateType, Pageable pageable);

    /**
     * Count template which is not child template by template type
     * @param templateType
     * @return
     */
    Long countByTemplateTypeAndParentTemplateIsNull(Integer templateType);
}
