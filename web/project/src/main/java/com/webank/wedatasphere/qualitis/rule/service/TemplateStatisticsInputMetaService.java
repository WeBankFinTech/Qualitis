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
import com.webank.wedatasphere.qualitis.rule.entity.TemplateStatisticsInputMeta;

import java.util.List;
import java.util.Set;

/**
 * @author howeye
 */
public interface TemplateStatisticsInputMetaService {

    /**
     * Check existence of argumentId
     * @param statisticsArgId
     * @return
     * @throws UnExpectedRequestException 参数异常时抛出
     */
    TemplateStatisticsInputMeta checkStatisticsArgId(Long statisticsArgId) throws UnExpectedRequestException;

    /**
     * Get and save statistics input meta
     * 获取并保存模版统计输入
     * @param outputName
     * @param functionType
     * @param functionContent
     * @param saveMidTable
     * @param template
     * @return
     */
    Set<TemplateStatisticsInputMeta> getAndSaveTemplateStatisticsInputMeta(String outputName, Integer functionType, String functionContent,
                                                                           Boolean saveMidTable, Template template);

    /**
     * Save all statistics input meta
     * @param templateStatisticsInputMetas
     * @return
     */
    Set<TemplateStatisticsInputMeta> saveAll(List<TemplateStatisticsInputMeta> templateStatisticsInputMetas);

    /**
     * Delete statistics input meta by template
     * @param templateInDb
     */
    void deleteByTemplate(Template templateInDb);
}
