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

package com.webank.wedatasphere.qualitis.rule.service;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateMidTableInputMeta;
import java.util.List;
import java.util.Set;

/**
 * @author howeye
 */
public interface TemplateMidTableInputMetaService {

    /**
     * Find template input meta by id
     * @param templateInputMetaId
     * @return
     * @throws UnExpectedRequestException
     */
    TemplateMidTableInputMeta checkTemplateInputMetaId(Long templateInputMetaId) throws UnExpectedRequestException;

    /**
     * Save all template input meta.
     * @param templateMidTableInputMetas
     * @return
     */
    Set<TemplateMidTableInputMeta> saveAll(List<TemplateMidTableInputMeta> templateMidTableInputMetas);

    /**
     * Delete mid_table input meta by template
     * @param templateInDb
     */
    void deleteByTemplate(Template templateInDb);
}
