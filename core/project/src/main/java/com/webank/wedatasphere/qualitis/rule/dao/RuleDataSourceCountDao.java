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

import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSourceCount;

/**
 * @author allenzhou
 */
public interface RuleDataSourceCountDao {

    /**
     * Find datasource count
     * @param datasourceName
     * @param userId
     * @return
     */
    Integer findCount(String datasourceName, Long userId);

    /**
     * Save.
     * @param ruleDataSourceCount
     * @return
     */
    RuleDataSourceCount save(RuleDataSourceCount ruleDataSourceCount);

}
