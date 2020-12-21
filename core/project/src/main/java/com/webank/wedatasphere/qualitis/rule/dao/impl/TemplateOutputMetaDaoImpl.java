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

import com.webank.wedatasphere.qualitis.rule.dao.TemplateOutputMetaDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.TemplateOutputMetaRepository;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateOutputMeta;
import com.webank.wedatasphere.qualitis.rule.dao.TemplateOutputMetaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author howeye
 */
@Repository
public class TemplateOutputMetaDaoImpl implements TemplateOutputMetaDao {

    @Autowired
    private TemplateOutputMetaRepository templateOutputMetaRepository;

    @Override
    public List<TemplateOutputMeta> findByRuleTemplate(Template template) {
        return templateOutputMetaRepository.findByTemplate(template);
    }

    @Override
    public TemplateOutputMeta findById(Long templateOutputId) {
        return templateOutputMetaRepository.findById(templateOutputId).orElse(null);
    }

    @Override
    public TemplateOutputMeta saveTemplateOutputMeta(TemplateOutputMeta templateOutputMeta) {
        return templateOutputMetaRepository.save(templateOutputMeta);
    }

    @Override
    public List<TemplateOutputMeta> findByTemplate(Template template) {
        return templateOutputMetaRepository.findByTemplate(template);
    }

    @Override
    public void deleteByTemplate(Template templateInDb) {
        templateOutputMetaRepository.deleteByTemplate(templateInDb);
    }


}
