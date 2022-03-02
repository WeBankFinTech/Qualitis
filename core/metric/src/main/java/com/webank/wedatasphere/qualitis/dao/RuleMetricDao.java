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

import com.webank.wedatasphere.qualitis.entity.Department;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.entity.User;
import java.util.List;

/**
 * @author allenzhou
 */
public interface RuleMetricDao {
    /**
     * Query pageable rule metrics with SYS_ADMIN.
     * @param subSystemName
     * @param ruleMetricName
     * @param enCode
     * @param type
     * @param available
     * @param page
     * @param size
     * @return
     */
    List<RuleMetric> queryAllRuleMetrics(String subSystemName, String ruleMetricName, String enCode, Integer type, Boolean available, int page,
        int size);

    /**
     * Count query rule metrics with SYS_ADMIN.
     * @param subSystemName
     * @param ruleMetricName
     * @param enCode
     * @param type
     * @param available
     * @return
     */
    long countQueryAllRuleMetrics(String subSystemName, String ruleMetricName, String enCode, Integer type, Boolean available);

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
     * @param page
     * @param size
     * @return
     */
    List<RuleMetric> queryRuleMetrics(Integer level, List<Department> departmentList,
        User user, String subSystemName, String ruleMetricName, String enCode, Integer type, Boolean available, int page, int size);

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
    long countQueryRuleMetrics(Integer level, List<Department> departmentList,
        User user, String subSystemName, String ruleMetricName, String enCode, Integer type, Boolean available);

    /**
     * Query rule metrics with name.
     * @param level
     * @param departmentList
     * @param user
     * @param name
     * @param page
     * @param size
     * @return
     */
    List<RuleMetric> findWithRuleMetricName(Integer level, List<Department> departmentList, User user, String name, int page, int size);

    /**
     * Count of querying rule metrics with name.
     * @param level
     * @param departmentList
     * @param user
     * @param name
     * @return
     */
    long countWithRuleMetricName(Integer level, List<Department> departmentList, User user, String name);

    /**
     * Find all rule metrics.
     * @param page
     * @param size
     * @return
     */
    List<RuleMetric> findAllRuleMetrics(int page, int size);

    /**
     * Count all rule metrics.
     * @return
     */
    long countAllRuleMetrics();

    /**
     * Find pageable rule metrics with different characters(SYS_ADMIN, DEPARTMENT_ADMIN, PROJECTOR).
     * @param level
     * @param departmentList
     * @param user
     * @param page
     * @param size
     * @return
     */
    List<RuleMetric> findRuleMetrics(Integer level, List<Department> departmentList, User user, int page, int size);

    /**
     * Count all rule metrics with different characters(SYS_ADMIN, DEPARTMENT_ADMIN, PROJECTOR).
     * @param level
     * @param departmentList
     * @param user
     * @return
     */
    long countRuleMetrics(Integer level, List<Department> departmentList, User user);

    /**
     * Add
     * @param ruleMetric
     * @return
     */
    RuleMetric add(RuleMetric ruleMetric);

    /**
     * Modify
     * @param ruleMetric
     * @return
     */
    RuleMetric modify(RuleMetric ruleMetric);

    /**
     * Delete
     * @param ruleMetric
     */
    void delete(RuleMetric ruleMetric);

    /**
     * Find by id.
     * @param id
     * @return
     */
    RuleMetric findById(long id);

    /**
     * Find by en code.
     * @param name
     * @return
     */
    RuleMetric findByEnCode(String name);

    /**
     * Find by IDs.
     * @param ids
     * @return
     */
    List<RuleMetric> findByIds(List<Long> ids);

    /**
     * Find by name.
     * @param name
     * @return
     */
    RuleMetric findByName(String name);
}
