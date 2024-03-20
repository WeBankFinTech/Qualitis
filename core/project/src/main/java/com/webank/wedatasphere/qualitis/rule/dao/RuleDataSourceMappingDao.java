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

import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSourceMapping;

import java.util.List;

/**
 * @author howeye
 */
public interface RuleDataSourceMappingDao {

    /**
     * Save rule datasource mapping
     * @param ruleDataSourceMapping
     * @return
     */
    RuleDataSourceMapping saveRuleDataSourceMapping(RuleDataSourceMapping ruleDataSourceMapping);

    /**
     * Save all
     * @param ruleDataSourceMappingList
     * @return
     */
    List<RuleDataSourceMapping> saveAll(List<RuleDataSourceMapping> ruleDataSourceMappingList);

    /**
     * Delete rule datasource mapping by rule
     * @param rule
     */
    void deleteByRule(Rule rule);

    /**
     * delete By Rule List
     * @param rules
     */
    void deleteByRuleList(List<Rule> rules);

    /**
     * find By Rule List
     * @param ruleList
     * @return
     */
    List<RuleDataSourceMapping> findByRuleList(List<Rule> ruleList);
}
