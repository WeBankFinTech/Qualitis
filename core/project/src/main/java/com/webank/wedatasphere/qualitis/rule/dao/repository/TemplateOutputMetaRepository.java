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

import com.webank.wedatasphere.qualitis.rule.entity.TemplateOutputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateOutputMeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author howeye
 */
public interface TemplateOutputMetaRepository extends JpaRepository<TemplateOutputMeta, Long> {

    /**
     * Find template output meta by template
     * @param template
     * @return
     */
    List<TemplateOutputMeta> findByTemplate(Template template);

    /**
     * Find template output meta by template and output name
     * @param template
     * @param outputName
     * @return
     */
    TemplateOutputMeta findByTemplateAndOutputName(Template template, String outputName);

    /**
     * Delete output meta by template
     * @param templateInDb
     */
    void deleteByTemplate(Template templateInDb);
}
