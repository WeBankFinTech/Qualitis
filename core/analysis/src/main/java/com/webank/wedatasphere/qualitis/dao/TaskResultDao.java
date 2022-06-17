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

package com.webank.wedatasphere.qualitis.dao;

import com.webank.wedatasphere.qualitis.entity.TaskResult;

import java.util.List;

/**
 * @author howeye
 */
public interface TaskResultDao {
    /**
     * Find task result by application id and rule id
     * @param applicationId
     * @param ruleId
     * @return
     */
    List<TaskResult> findByApplicationAndRule(String applicationId, Long ruleId);

    /**
     * Find avg value between create time and rule id
     * @param begin
     * @param end
     * @param ruleId
     * @return
     */
    Double findAvgByCreateTimeBetweenAndRule(String begin, String end, Long ruleId);

    /**
     * Find task result by application and rule id in
     * @param applicationId
     * @param ruleIds
     * @return
     */
    List<TaskResult> findByApplicationIdAndRuleIn(String applicationId, List<Long> ruleIds);

    /**
     * Save file task result.
     * @param taskResult
     * @return
     */
    TaskResult saveTaskResult(TaskResult taskResult);

    /**
     * Find rule IDs by rule metric ID.
     * @param id
     * @param page
     * @param size
     * @return
     */
    List<Long> findRuleByRuleMetric(Long id, int page, int size);

    /**
     * Find values by rule ID and rule metric ID.
     * @param ruleMetricId
     * @param page
     * @param size
     * @return
     */
    List<TaskResult> findValuesByRuleMetric(long ruleMetricId, int page, int size);

    /**
     * Find avg value by rule ID and rule metric ID.
     * @param start
     * @param end
     * @param ruleId
     * @param ruleMetricId
     * @return
     */
    Double findAvgByCreateTimeBetweenAndRuleAndRuleMetric(String start, String end, Long ruleId, Long ruleMetricId);

    /**
     * Find value.
     * @param applicationId
     * @param ruleId
     * @param ruleMetricId
     * @return
     */
    TaskResult find(String applicationId, Long ruleId, Long ruleMetricId);

    /**
     * Count values.
     * @param ruleMetricId
     * @return
     */
    int countValuesByRuleMetric(long ruleMetricId);

    /**
     * Count rules.
     * @param ruleMetricId
     * @return
     */
    int countRuleByRuleMetric(Long ruleMetricId);
}
