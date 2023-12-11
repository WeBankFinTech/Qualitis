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


import com.webank.wedatasphere.qualitis.rule.entity.BdpClientHistory;

/**
 * @author allenzhou
 */
public interface BdpClientHistoryDao {

    /**
     * Find histroy by rule id
     * @param ruleId
     * @return
     */
    BdpClientHistory findByRuleId(Long ruleId);

    /**
     * Save rule history
     * @param bdpClientHistory
     * @return
     */
    BdpClientHistory save(BdpClientHistory bdpClientHistory);

    /**
     * Delete
     * @param bdpClientHistory
     */
    void delete(BdpClientHistory bdpClientHistory);

    /**
     * Find create rule history by templateFunction and datasource and project name
     * @param templateFuncName
     * @param s
     * @param s1
     * @return
     */
    BdpClientHistory findByTemplateFunctionAndDatasourceAndProjectName(String templateFuncName, String s, String s1);
}
