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

package com.webank.wedatasphere.qualitis.dao.repository;

import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.entity.TaskRuleAlarmConfig;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author allenzhou
 */
public interface TaskRuleAlarmConfigRepository extends JpaRepository<TaskRuleAlarmConfig, Long> {

    /**
     * Find taskRuleAlarmConfig.
     * @param taskRuleAlarmConfigId
     * @return
     */
    @Query("select j from TaskRuleAlarmConfig j where j.id = ?1")
    TaskRuleAlarmConfig findTaskRuleAlarmConfig(Long taskRuleAlarmConfigId);

    /**
     * Get by rule metric id.
     * @param ruleMetric
     * @return
     */
    List<TaskRuleAlarmConfig> findByRuleMetric(RuleMetric ruleMetric);
}
