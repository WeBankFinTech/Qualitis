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

package com.webank.wedatasphere.qualitis.translator;

import com.webank.wedatasphere.qualitis.exception.RuleVariableNotFoundException;
import com.webank.wedatasphere.qualitis.exception.RuleVariableNotSupportException;
import com.webank.wedatasphere.qualitis.rule.entity.RuleVariable;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateStatisticsInputMeta;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author howeye
 */
public abstract class AbstractTranslator {
    /**
     * Generate persistence statement.
     * @param ruleId
     * @param ruleMetricMaps
     * @param templateStatisticsInputMetas
     * @param applicationId
     * @param ruleVariables
     * @param createTime
     * @param count
     * @param runDate
     * @return
     * @throws RuleVariableNotSupportException
     * @throws RuleVariableNotFoundException
     */
    public abstract List<String> persistenceTranslate(Long ruleId, Map<String, Long> ruleMetricMaps,
        Set<TemplateStatisticsInputMeta> templateStatisticsInputMetas, String applicationId, List<RuleVariable> ruleVariables,
        String createTime, Integer count, String runDate) throws RuleVariableNotSupportException, RuleVariableNotFoundException;

    /**
     * Generate initial statement.
     * @return
     */
    public abstract List<String> getInitSentence();
}
