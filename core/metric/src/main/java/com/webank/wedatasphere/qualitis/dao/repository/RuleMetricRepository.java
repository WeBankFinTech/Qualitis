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

import com.webank.wedatasphere.qualitis.entity.Department;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.entity.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author allenzhou
 */
public interface RuleMetricRepository extends JpaRepository<RuleMetric, Long> {
    /**
     * Query pageable rule metrics with SYS_ADMIN.
     * @param subSystemName
     * @param ruleMetricName
     * @param enCode
     * @param type
     * @param available
     * @param pageable
     * @return
     */
    @Query(value = "SELECT qrm FROM RuleMetric qrm where (LENGTH(?1) = 0 OR qrm.subSystemName = ?1) AND (LENGTH(?2) = 0 OR qrm.name LIKE ?2) AND (LENGTH(?3) = 0 or qrm.enCode = ?3) AND (?4 is null or qrm.type = ?4) AND (?5 is null or qrm.available = ?5)")
    Page<RuleMetric> queryAll(String subSystemName, String ruleMetricName, String enCode, Integer type, Boolean available, Pageable pageable);

    /**
     * Count query rule metrics with SYS_ADMIN.
     * @param subSystemName
     * @param ruleMetricName
     * @param enCode
     * @param type
     * @param available
     * @return
     */
    @Query(value = "SELECT count(qrm.id) FROM RuleMetric qrm where (LENGTH(?1) = 0 OR qrm.subSystemName = ?1) AND (LENGTH(?2) = 0 OR qrm.name LIKE ?2) AND (LENGTH(?3) = 0 or qrm.enCode = ?3) AND (?4 is null or qrm.type = ?4) AND (?5 is null or qrm.available = ?5)")
    long countQueryAll(String subSystemName, String ruleMetricName, String enCode, Integer type, Boolean available);

    /**
     * Query pageable rule metrics with different characters(DEPARTMENT_ADMIN, PROJECTOR).
     * @param level
     * @param departmentList
     * @param user
     * @param subSystemName
     * @param ruleMetricName
     * @param enCode
     * @param type
     * @param available
     * @param pageable
     * @return
     */
    @Query(value = "SELECT qrm FROM RuleMetric qrm where (qrm.level = ?1 OR (qrm IN (SELECT qrmdu.ruleMetric FROM RuleMetricDepartmentUser qrmdu where qrmdu.department in (?2) OR ?3 is null OR qrmdu.user = ?3))) AND (?4 = '' OR qrm.subSystemName = ?4) AND (?5 = '' OR qrm.name LIKE ?5) AND (?6 = '' OR qrm.enCode = ?6) AND (?7 is null OR qrm.type = ?7) AND (?8 is null or qrm.available = ?8)")
    Page<RuleMetric> queryRuleMetrics(Integer level, List<Department> departmentList, User user, String subSystemName, String ruleMetricName, String enCode, Integer type, Boolean available, Pageable pageable);

    /**
     * Count query rule metrics with different characters(DEPARTMENT_ADMIN, PROJECTOR).
     * @param level
     * @param departmentList
     * @param user
     * @param subSystemName
     * @param ruleMetricName
     * @param enCode
     * @param type
     * @param available
     * @return
     */
    @Query(value = "SELECT count(qrm.id) FROM RuleMetric qrm where (qrm.level = ?1 OR (qrm IN (SELECT qrmdu.ruleMetric FROM RuleMetricDepartmentUser qrmdu where qrmdu.department in (?2) OR ?3 is null OR qrmdu.user = ?3))) AND (?4 = '' OR qrm.subSystemName = ?4) AND (?5 = '' OR qrm.name LIKE ?5) AND (?6 = '' OR qrm.enCode = ?6) AND (?7 is null OR qrm.type = ?7) AND (?8 is null or qrm.available = ?8)")
    long countQueryRuleMetrics(Integer level, List<Department> departmentList, User user, String subSystemName, String ruleMetricName, String enCode, Integer type, Boolean available);

    /**
     * Find pageable rule metrics with different characters(DEPARTMENT_ADMIN, PROJECTOR).
     * @param level
     * @param departmentList
     * @param user
     * @param pageable
     * @return
     */
    @Query(value = "SELECT qrm FROM RuleMetric qrm where qrm.level = ?1 OR (qrm IN (SELECT qrmdu.ruleMetric FROM RuleMetricDepartmentUser qrmdu where qrmdu.department in (?2) OR qrmdu.user = ?3))")
    Page<RuleMetric> findRuleMetrics(Integer level, List<Department> departmentList, User user, Pageable pageable);

    /**
     * Count all rule metrics with different characters(DEPARTMENT_ADMIN, PROJECTOR).
     * @param level
     * @param departmentList
     * @param user
     * @return
     */
    @Query(value = "SELECT count(qrm.id) FROM RuleMetric qrm where qrm.level = ?1 OR (qrm IN (SELECT qrmdu.ruleMetric FROM RuleMetricDepartmentUser qrmdu where qrmdu.department in (?2) OR qrmdu.user = ?3))")
    long countRuleMetrics(Integer level, List<Department> departmentList, User user);

    /**
     * Query rule metrics with name.
     * @param level
     * @param departmentList
     * @param user
     * @param name
     * @param pageable
     * @return
     */
    @Query(value = "SELECT qrm FROM RuleMetric qrm where qrm.level = ?1 AND qrm.name LIKE ?4 OR (qrm IN (SELECT qrmdu.ruleMetric FROM RuleMetricDepartmentUser qrmdu where qrmdu.department in (?2) OR qrmdu.user IN (?3)))")
    Page<RuleMetric> findWithRuleMetricName(Integer level, List<Department> departmentList, User user, String name, Pageable pageable);

    /**
     * Count of querying rule metrics with name.
     * @param level
     * @param departmentList
     * @param user
     * @param name
     * @return
     */
    @Query(value = "SELECT count(qrm.id) FROM RuleMetric qrm where qrm.level = ?1 AND qrm.name LIKE ?4 OR (qrm IN (SELECT qrmdu.ruleMetric FROM RuleMetricDepartmentUser qrmdu where qrmdu.department in (?2) OR qrmdu.user IN (?3)))")
    long countWithRuleMetricName(Integer level, List<Department> departmentList, User user, String name);

    /**
     * Find by name.
     * @param name
     * @return
     */
    RuleMetric findByName(String name);

    /**
     * Find by en code.
     * @param enCode
     * @return
     */
    @Query(value = "SELECT qrm FROM RuleMetric qrm where qrm.enCode = ?1")
    RuleMetric findByEnCode(String enCode);
}
