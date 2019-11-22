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
     * @param page
     * @param size
     * @return
     */
    List<Template> findAllMultiTemplate(int page, int size);

    /**
     * Count all multi-table template
     * @return
     */
    Long countAllMultiTemplate();

    /**
     * Find all template
     * @return
     */
    List<Template> getAllTemplate();
}
