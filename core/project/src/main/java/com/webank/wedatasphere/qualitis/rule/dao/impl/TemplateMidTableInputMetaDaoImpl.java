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

package com.webank.wedatasphere.qualitis.rule.dao.impl;

import com.webank.wedatasphere.qualitis.rule.dao.TemplateMidTableInputMetaDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.TemplateInputMetaRepository;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateMidTableInputMeta;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author howeye
 */
@Repository
public class TemplateMidTableInputMetaDaoImpl implements TemplateMidTableInputMetaDao {

    @Autowired
    private TemplateInputMetaRepository templateInputMetaRepository;

    @Override
    public List<TemplateMidTableInputMeta> findByRuleTemplate(Template template) {
        return templateInputMetaRepository.findByTemplate(template);
    }

    @Override
    public TemplateMidTableInputMeta findById(Long templateInputMetaId) {
        return templateInputMetaRepository.findById(templateInputMetaId).orElse(null);
    }

    @Override
    public Set<TemplateMidTableInputMeta> saveAll(List<TemplateMidTableInputMeta> templateMidTableInputMetas) {
        Set<TemplateMidTableInputMeta> result = new HashSet<>();
        result.addAll(templateInputMetaRepository.saveAll(templateMidTableInputMetas));
        return result;
    }

    @Override
    public void deleteByTemplate(Template templateInDb) {
        templateInputMetaRepository.deleteByTemplate(templateInDb);
    }
}
