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
import com.webank.wedatasphere.qualitis.rule.entity.TemplateOutputMeta;

import java.util.List;

/**
 * @author howeye
 */
public interface TemplateOutputMetaDao {

    /**
     * Find template output meta by template
     * @param template
     * @return
     */
    List<TemplateOutputMeta> findByRuleTemplate(Template template);

    /**
     * Find template output meta by id
     * @param templateOutputId
     * @return
     */
    TemplateOutputMeta findById(Long templateOutputId);

    /**
     * Save template output meta
     * @param templateOutputMeta
     * @return
     */
    TemplateOutputMeta saveTemplateOutputMeta(TemplateOutputMeta templateOutputMeta);

    /**
     * Find TemplateOutputMeta By Template
     * @param template
     * @return
     */
    List<TemplateOutputMeta> findByTemplate(Template template);

    /**
     * Delete output meta by template
     * @param templateInDb
     */
    void deleteByTemplate(Template templateInDb);
}
