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

import com.webank.wedatasphere.qualitis.entity.TaskResult;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author howeye
 */
public interface TaskResultRepository extends JpaRepository<TaskResult, Long> {
    /**
     * Find task result by application and rules
     * @param applicationId
     * @param ruleId
     * @return
     */
    List<TaskResult> findByApplicationIdAndRuleId(String applicationId, Long ruleId);

    /**
     * Find value avg from begin time and end time
     * @param begin
     * @param end
     * @param ruleId
     * @return
     */
    @Query("select avg(value) from TaskResult t where (t.createTime between ?1 and ?2) and t.ruleId = ?3 and t.saveResult = 1")
    Double findAvgByCreateTimeBetween(String begin, String end, Long ruleId);


    /**
     * Find avg value by rule ID and rule metric ID.
     * @param begin
     * @param end
     * @param ruleId
     * @param ruleMetricId
     * @return
     */
    @Query("select avg(value) from TaskResult t where (t.createTime between ?1 and ?2) and t.ruleId = ?3 and (t.ruleMetricId = ?4) and t.saveResult = 1")
    Double findAvgByCreateTimeBetween(String begin, String end, Long ruleId, Long ruleMetricId);

    /**
     * Find task result by application and rule id
     * @param applicationId
     * @param ruleIds
     * @return
     */
    List<TaskResult> findByApplicationIdAndRuleIdIn(String applicationId, List<Long> ruleIds);

    /**
     * Find rule IDs by rule metric ID.
     * @param id
     * @param pageable
     * @return
     */
    @Query(value = "SELECT tr.ruleId from TaskResult tr where tr.ruleMetricId = ?1")
    Page<Long> findRuleByRuleMetricId(Long id, Pageable pageable);

    /**
     * Find values by rule ID and rule metric ID.
     * @param ruleMetricId
     * @param pageable
     * @return
     */
    @Query(value = "SELECT tr from TaskResult tr where tr.ruleMetricId = ?1")
    Page<TaskResult> findValuesByRuleAndRuleMetric(long ruleMetricId, Pageable pageable);

    /**
     * Find value.
     * @param applicationId
     * @param ruleId
     * @param ruleMetricId
     * @return
     */
    @Query(value = "SELECT tr from TaskResult tr where tr.applicationId = ?1 and tr.ruleId = ?2 and tr.ruleMetricId = ?3")
    TaskResult findValue(String applicationId, Long ruleId, Long ruleMetricId);

    /**
     * Count values.
     * @param ruleMetricId
     * @return
     */
    @Query(value = "SELECT count(tr.id) from TaskResult tr where tr.ruleMetricId = ?1")
    int countValuesByRuleMetric(long ruleMetricId);

    /**
     * Count rules.
     * @param ruleMetricId
     * @return
     */
    @Query(value = "SELECT count(tr.ruleId) from TaskResult tr where tr.ruleMetricId = ?1")
    int countRulesByRuleMetric(Long ruleMetricId);
}
