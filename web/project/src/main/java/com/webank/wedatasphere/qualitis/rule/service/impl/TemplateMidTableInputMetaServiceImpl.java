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

package com.webank.wedatasphere.qualitis.rule.service.impl;

import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.service.TemplateMidTableInputMetaService;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.rule.dao.TemplateMidTableInputMetaDao;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateMidTableInputMeta;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author howeye
 */
@Service
public class TemplateMidTableInputMetaServiceImpl implements TemplateMidTableInputMetaService {

    @Autowired
    private TemplateMidTableInputMetaDao templateMidTableInputMetaDao;

    @Override
    public TemplateMidTableInputMeta checkTemplateInputMetaId(Long templateInputMetaId) throws UnExpectedRequestException {
        TemplateMidTableInputMeta templateMidTableInputMeta = templateMidTableInputMetaDao.findById(templateInputMetaId);
        if (templateMidTableInputMeta == null) {
            throw new UnExpectedRequestException("template_input_meta_id {&DOES_NOT_EXIST}");
        }
        return templateMidTableInputMeta;
    }

    @Override
    public Set<TemplateMidTableInputMeta> saveAll(List<TemplateMidTableInputMeta> templateMidTableInputMetas) {
        return templateMidTableInputMetaDao.saveAll(templateMidTableInputMetas);
    }

    @Override
    public void deleteByTemplate(Template templateInDb) {
        templateMidTableInputMetaDao.deleteByTemplate(templateInDb);
    }
}
