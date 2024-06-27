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
     * Find task result by application and rule id
     * @param applicationId
     * @param ruleIds
     * @return
     */
    List<TaskResult> findByApplicationIdAndRuleIdIn(String applicationId, List<Long> ruleIds);

    /**
     * Find value avg from begin time and end time
     * @param begin
     * @param end
     * @param ruleId
     * @param ruleMetricId
     * @param applicationId
     * @return
     */
    @Query("select avg(value) from TaskResult t where (t.createTime between ?1 and ?2) and t.ruleId = ?3 and (?4 IS NULL OR t.ruleMetricId = ?4) and t.applicationId != ?5 and t.saveResult = 1")
    Double findAvgByCreateTimeBetweenAndRuleAndMetricAndApplication(String begin, String end, Long ruleId, Long ruleMetricId, String applicationId);


    /**
     * Count value from begin time and end time
     * @param begin
     * @param end
     * @param ruleId
     * @param ruleMetricId
     * @param applicationId
     * @return
     */
    @Query("select count(t.id) from TaskResult t where (t.createTime between ?1 and ?2) and t.ruleId = ?3 and (?4 IS NULL OR t.ruleMetricId = ?4) and t.applicationId != ?5 and t.saveResult = 1")
    long countByCreateTimeBetweenAndRuleAndMetricAndApplication(String begin, String end, Long ruleId, Long ruleMetricId, String applicationId);

    /**
     * Find avg value by rule ID and rule metric ID.
     * @param begin
     * @param end
     * @param ruleId
     * @param ruleMetricId
     * @return
     */
    @Query("select avg(value) from TaskResult t where (t.createTime between ?1 and ?2) and t.ruleId = ?3 and (?4 IS NULL OR t.ruleMetricId = ?4) and t.saveResult = 1")
    Double findAvgByCreateTimeBetweenAndRuleAndRuleMetric(String begin, String end, Long ruleId, Long ruleMetricId);

    /**
     * Count
     * @param start
     * @param end
     * @param ruleId
     * @param ruleMetricId
     * @param applicationId
     * @return
     */
    @Query("select count(value) from TaskResult t where (t.createTime between ?1 and ?2) and t.ruleId = ?3 and (?4 IS NULL OR t.ruleMetricId = ?4) and t.applicationId != ?5 and t.saveResult = 1")
    long countByCreateTimeBetweenAndRuleAndRuleMetric(String start, String end, Long ruleId, Long ruleMetricId, String applicationId);

    /**
     * Find rule IDs by rule metric ID.
     * @param id
     * @param pageable
     * @return
     */
    @Query(value = "SELECT tr.ruleId from TaskResult tr where tr.ruleMetricId = ?1")
    Page<Long> findRuleIdsByRuleMetric(Long id, Pageable pageable);

    /**
     * Count rules.
     * @param ruleMetricId
     * @return
     */
    @Query(value = "SELECT count(tr.ruleId) from TaskResult tr where tr.ruleMetricId = ?1")
    int countRuleIdsByRuleMetric(Long ruleMetricId);

    /**
     * Find values by rule ID and rule metric ID.
     * @param ruleMetricId
     * @param startTime
     * @param endTime
     * @param envName
     * @param pageable
     * @return
     */
    @Query(value = "SELECT tr from TaskResult tr where tr.ruleMetricId = ?1 and tr.saveResult = 1 and (tr.createTime between ?2 and ?3) and (?4 is null or LENGTH(?4) = 0 or tr.envName = ?4)")
    Page<TaskResult> findValuesByRuleMetric(long ruleMetricId, String startTime, String endTime, String envName, Pageable pageable);

    /**
     * Count values.
     * @param ruleMetricId
     * @param startTime
     * @param endTime
     * @param envName
     * @return
     */
    @Query(value = "SELECT count(tr.id) from TaskResult tr where tr.ruleMetricId = ?1 and tr.saveResult = 1 and (tr.createTime between ?2 and ?3) and (?4 is null or LENGTH(?4) = 0 or tr.envName = ?4)")
    int countValuesByRuleMetric(long ruleMetricId, String startTime, String endTime, String envName);

    /**
     * Find value for upload task info and file rule.
     * @param applicationId
     * @param ruleId
     * @param ruleMetricId
     * @return
     */
    @Query(value = "SELECT tr from TaskResult tr where tr.applicationId = ?1 and tr.ruleId = ?2 and (?3 IS NULL OR tr.ruleMetricId = ?3)")
    TaskResult findValue(String applicationId, Long ruleId, Long ruleMetricId);

    /**
     * Find values with time.
     * @param ruleMetricId
     * @param startTime
     * @param endTime
     * @return
     */
    @Query(value = "SELECT tr from TaskResult tr where tr.ruleMetricId = ?1 and tr.createTime between ?2 and ?3")
    List<TaskResult> findValuesByRuleMetricWithTime(Long ruleMetricId, String startTime, String endTime);

    /**
     * Find all by application ID
     * @param applicationId
     * @return
     */
    @Query(value = "SELECT tr from TaskResult tr where tr.applicationId = ?1")
    List<TaskResult> findByApplicationId(String applicationId);

    /**
     * Find all by application ID list
     * @param applicationIdList
     * @return
     */
    List<TaskResult> findByApplicationIdIn(List<String> applicationIdList);
}
