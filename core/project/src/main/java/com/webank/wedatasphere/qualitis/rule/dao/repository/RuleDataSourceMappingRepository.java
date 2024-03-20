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

import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSourceMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author howeye
 */
public interface RuleDataSourceMappingRepository extends JpaRepository<RuleDataSourceMapping, Long> {

    /**
     * Delete rule datasource by rule
     * @param rule
     */
    void deleteByRule(Rule rule);

    /**
     * findByRule
     * @param rule
     * @return
     */
    List<RuleDataSourceMapping> findByRule(Rule rule);

    /**
     * deleteByIdIn
     * @param ids
     */
    @Modifying
    @Query(value = "delete from qualitis_rule_datasource_mapping where id in (?1)", nativeQuery = true)
    void deleteByIdIn(List<Long> ids);

    /**
     * find By Rule In
     * @param ruleList
     * @return
     */
    List<RuleDataSourceMapping> findByRuleIn(List<Rule> ruleList);

    /**
     * delete By Rule In
     * @param ruleList
     * @return
     */
    void deleteByRuleIn(List<Rule> ruleList);
}
