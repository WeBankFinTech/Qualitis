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

import com.webank.wedatasphere.qualitis.rule.entity.BdpClientHistory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author allenzhou
 */
public interface BdpClientHistoryRepository extends JpaRepository<BdpClientHistory, Long> {

    /**
     * Find create rule history
     * @param templateFunction
     * @param datasource
     * @param projectName
     * @return
     */
    BdpClientHistory findByTemplateFunctionAndDatasourceAndProjectName(String templateFunction, String datasource, String projectName);

    /**
     * Find histroy by rule id
     * @param ruleId
     * @return
     */
    BdpClientHistory findByRuleId(Long ruleId);
}
