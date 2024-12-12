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

import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmConfig;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.List;

/**
 * @author howeye
 */
public interface AlarmConfigRepository extends JpaRepository<AlarmConfig, Long> {
    /**
     * Delete alarmConfig by rule
     * @param rule
     */
    void deleteByRule(Rule rule);

    /**
     * delete By RuleId
     * @param ruleId
     */
    @Modifying
    @Query(value = "delete from qualitis_rule_alarm_config where rule_id = ?1", nativeQuery = true)
    Integer deleteByRuleId(Long ruleId);

    /**
     * Get lock
     * @param ruleId
     * @return
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<AlarmConfig> findByRuleId(Long ruleId);

    /**
     * Get by rule metric
     * @param ruleMetric
     * @return
     */
    @Query(value = "SELECT ac from AlarmConfig ac WHERE ac.ruleMetric = ?1")
    List<AlarmConfig> getByRuleMetric(RuleMetric ruleMetric);
}
