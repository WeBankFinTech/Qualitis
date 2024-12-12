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
import java.util.Set;

/**
 * @author allenzhou
 */
public interface RuleMetricDao {
    /**
     * Query pageable rule metrics with SYS_ADMIN.
     *
     * @param subSystemName
     * @param ruleMetricName
     * @param enCode
     * @param type
     * @param available
     * @param multiEnvs
     * @param devDepartmentId
     * @param opsDepartmentId
     * @param actionRange
     * @param tableDataType
     * @param createUser
     * @param modifyUser
     * @param page
     * @param size
     * @return
     */
    List<RuleMetric> queryAllRuleMetrics(String subSystemName, String ruleMetricName, String enCode, Integer type, Boolean available,
                                         Boolean multiEnvs, String devDepartmentId, String opsDepartmentId, Set<String> actionRange, String tableDataType, String createUser, String modifyUser, int page, int size);

    /**
     * Count query rule metrics with SYS_ADMIN.
     *
     * @param subSystemName
     * @param ruleMetricName
     * @param enCode
     * @param type
     * @param available
     * @param multiEnvs
     * @param devDepartmentId
     * @param opsDepartmentId
     * @param actionRange
     * @param tableDataType
     * @param createUser
     * @param modifyUser
     * @return
     */
    long countQueryAllRuleMetrics(String subSystemName, String ruleMetricName, String enCode, Integer type, Boolean available,
                                  Boolean multiEnvs, String devDepartmentId, String opsDepartmentId, Set<String> actionRange, String tableDataType, String createUser, String modifyUser);

    /**
     * Query pageable rule metrics with different characters(DEPARTMENT_ADMIN, PROJECTOR).
     *
     * @param subSystemName
     * @param ruleMetricName
     * @param enCode
     * @param type
     * @param requestAvailable
     * @param available
     * @param multiEnvs
     * @param tableDataType
     * @param dataVisibilityDeptList
     * @param createUser
     * @param devDepartmentId
     * @param opsDepartmentId
     * @param actionRange
     * @param buildUser
     * @param modifyUser
     * @param page
     * @param size
     * @return
     */
    List<RuleMetric> queryRuleMetrics(String subSystemName, String ruleMetricName, String enCode, Integer type, Boolean requestAvailable, Boolean available,
                                      Boolean multiEnvs, String tableDataType, List<Long> dataVisibilityDeptList, String createUser, String devDepartmentId, String opsDepartmentId, Set<String> actionRange, String buildUser, String modifyUser, int page, int size);

    /**
     * Count query rule metrics with different characters(DEPARTMENT_ADMIN, PROJECTOR).
     *
     * @param subSystemName
     * @param ruleMetricName
     * @param enCode
     * @param type
     * @param available
     * @param multiEnvs
     * @param tableDataType
     * @param dataVisibilityDeptList
     * @param createUser
     * @param devDepartmentId
     * @param opsDepartmentId
     * @param actionRange
     * @param buildUser
     * @param modifyUser
     * @return
     */
    long countQueryRuleMetrics(String subSystemName, String ruleMetricName, String enCode, Integer type, Boolean available, Boolean multiEnvs
            , String tableDataType, List<Long> dataVisibilityDeptList, String createUser, String devDepartmentId, String opsDepartmentId, Set<String> actionRange, String buildUser, String modifyUser);

    /**
     * Query rule metrics with name.
     *
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
     *
     * @param level
     * @param departmentList
     * @param user
     * @param name
     * @return
     */
    long countWithRuleMetricName(Integer level, List<Department> departmentList, User user, String name);

    /**
     * Query rule metrics by sub-system-id.
     *
     * @param level
     * @param departmentList
     * @param user
     * @param subSystemId
     * @param page
     * @param size
     * @return
     */
    List<RuleMetric> findBySubSystemId(Integer level, List<Department> departmentList, User user, long subSystemId, int page, int size);

    /**
     * Count of querying rule metrics by sub-system-id.
     *
     * @param level
     * @param departmentList
     * @param user
     * @param subSystemId
     * @return
     */
    long countBySubSystemId(Integer level, List<Department> departmentList, User user, long subSystemId);

    /**
     * Find all rule metrics.
     *
     * @param page
     * @param size
     * @return
     */
    List<RuleMetric> findAllRuleMetrics(int page, int size);

    /**
     * Count all rule metrics.
     *
     * @return
     */
    long countAllRuleMetrics();

    /**
     * Find pageable rule metrics with different characters(SYS_ADMIN, DEPARTMENT_ADMIN, PROJECTOR).
     *
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
     *
     * @param level
     * @param departmentList
     * @param user
     * @return
     */
    long countRuleMetrics(Integer level, List<Department> departmentList, User user);

    /**
     * Find not used rule metric
     *
     * @param level
     * @param departmentList
     * @param user
     * @param createUser
     * @param tableDataType
     * @param dataVisibilityDeptList
     * @param page
     * @param size
     * @return
     */
    List<RuleMetric> findNotUsed(Integer level, List<Department> departmentList,
                                 User user, String createUser, String tableDataType, List<Long> dataVisibilityDeptList, int page, int size);

    /**
     * Count not used rule metric
     *
     * @param level
     * @param departmentList
     * @param user
     * @param createUser
     * @param tableDataType
     * @param dataVisibilityDeptList
     * @return
     */
    long countNotUsed(Integer level, List<Department> departmentList, User user, String createUser, String tableDataType, List<Long> dataVisibilityDeptList);

    /**
     * Find not used rule metric
     *
     * @param page
     * @param size
     * @return
     */
    List<RuleMetric> findAllNotUsed(int page, int size);

    /**
     * Count not used rule metric
     *
     * @return
     */
    long countAllNotUsed();

    /**
     * Find used rule metric
     *
     * @param level
     * @param departmentList
     * @param user
     * @param createUser
     * @param tableDataType
     * @param dataVisibilityDeptList
     * @param page
     * @param size
     * @return
     */
    List<RuleMetric> findUsed(Integer level, List<Department> departmentList,
                              User user, String createUser, String tableDataType, List<Long> dataVisibilityDeptList, int page, int size);

    /**
     * Count used rule metric
     *
     * @param level
     * @param departmentList
     * @param user
     * @param createUser
     * @param tableDataType
     * @param dataVisibilityDeptList
     * @return
     */
    long countUsed(Integer level, List<Department> departmentList, User user, String createUser, String tableDataType, List<Long> dataVisibilityDeptList);

    /**
     * Find used rule metric
     *
     * @param page
     * @param size
     * @return
     */
    List<RuleMetric> findAllUsed(int page, int size);

    /**
     * Count used rule metric
     *
     * @return
     */
    long countAllUsed();

    /**
     * Add
     *
     * @param ruleMetric
     * @return
     */
    RuleMetric add(RuleMetric ruleMetric);

    /**
     * Modify
     *
     * @param ruleMetric
     * @return
     */
    RuleMetric modify(RuleMetric ruleMetric);

    /**
     * Delete
     *
     * @param ruleMetric
     */
    void delete(RuleMetric ruleMetric);

    /**
     * Find by id.
     *
     * @param id
     * @return
     */
    RuleMetric findById(long id);

    /**
     * Find by en code.
     *
     * @param name
     * @return
     */
    RuleMetric findByEnCode(String name);

    /**
     * Find by IDs.
     *
     * @param ids
     * @return
     */
    List<RuleMetric> findByIds(List<Long> ids);

    /**
     * Find by name.
     *
     * @param name
     * @return
     */
    RuleMetric findByName(String name);
}
