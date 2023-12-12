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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

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
     * @param multiEnvs
     * @param devDepartmentId
     * @param opsDepartmentId
     * @param actionRange
     * @param tableDataType
     * @param createUser
     * @param modifyUser
     * @param pageable
     * @return
     */
    @Query(value = "SELECT qrm FROM RuleMetric qrm where (LENGTH(?1) = 0 OR qrm.subSystemName = ?1) AND (LENGTH(?2) = 0 OR qrm.name LIKE ?2) AND (LENGTH(?3) = 0 or qrm.enCode = ?3) " +
            "AND (?4 is null or qrm.type = ?4) AND (?5 is null or qrm.available = ?5) AND (?6 is null or qrm.multiEnv = ?6) " +
            "AND (LENGTH(?7) = 0 or qrm.devDepartmentId = ?7) " +
            "AND (LENGTH(?8) = 0 or qrm.opsDepartmentId = ?8) " +
            "AND (LENGTH(?11) = 0 or qrm.createUser = ?11) " +
            "AND (LENGTH(?12) = 0 or qrm.modifyUser = ?12) " +
            "AND (?9 is null or EXISTS (SELECT dv.tableDataId FROM DataVisibility dv WHERE dv.tableDataType = ?10 AND (dv.departmentSubId in (?9) or dv.departmentSubId = 0) AND qrm = dv.tableDataId)) ")
    Page<RuleMetric> queryAll(String subSystemName, String ruleMetricName, String enCode, Integer type, Boolean available, Boolean multiEnvs, String devDepartmentId, String opsDepartmentId, Set<String> actionRange, String tableDataType,String createUser,String modifyUser, Pageable pageable);

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
    @Query(value = "SELECT count(qrm.id) FROM RuleMetric qrm where (LENGTH(?1) = 0 OR qrm.subSystemName = ?1) AND (LENGTH(?2) = 0 OR qrm.name LIKE ?2) AND (LENGTH(?3) = 0 or qrm.enCode = ?3) " +
            "AND (?4 is null or qrm.type = ?4) AND (?5 is null or qrm.available = ?5) AND (?6 is null or qrm.multiEnv = ?6)" +
            "AND (LENGTH(?7) = 0 or qrm.devDepartmentId = ?7) " +
            "AND (LENGTH(?8) = 0 or qrm.opsDepartmentId = ?8) " +
            "AND (LENGTH(?11) = 0 or qrm.createUser = ?11) " +
            "AND (LENGTH(?12) = 0 or qrm.modifyUser = ?12) " +
            "AND (?9 is null or EXISTS (SELECT dv.tableDataId FROM DataVisibility dv WHERE dv.tableDataType = ?10 AND (dv.departmentSubId in (?9) or dv.departmentSubId = 0) AND qrm = dv.tableDataId)) ")
    long countQueryAll(String subSystemName, String ruleMetricName, String enCode, Integer type, Boolean available, Boolean multiEnvs, String devDepartmentId, String opsDepartmentId, Set<String> actionRange, String tableDataType,String createUser,String modifyUser);

    /**
     * Query pageable rule metrics with different characters(DEPARTMENT_ADMIN, PROJECTOR).
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
     * @param pageable
     * @return
     */
    @Query(value = "SELECT qrm FROM RuleMetric qrm where " +
            " ((?1 = '' OR qrm.subSystemName = ?1) AND (?2 = '' OR qrm.name LIKE ?2) AND (?3 = '' OR qrm.enCode = ?3) AND (?4 is null OR qrm.type = ?4) AND (?5 is null or qrm.available = ?5) AND (?6 is null or qrm.multiEnv = ?6))" +
            " AND (?10 = '' or qrm.devDepartmentId = ?10) " +
            " AND (?11 = '' or qrm.opsDepartmentId = ?11) " +
            " AND (?13 = '' or qrm.createUser = ?13) " +
            " AND (?14 = '' or qrm.modifyUser = ?14) " +
            " AND (?12 is null or EXISTS (SELECT q.tableDataId FROM DataVisibility q WHERE q.tableDataType = ?7 AND (q.departmentSubId in (?12) or q.departmentSubId = 0) AND qrm = q.tableDataId)) " +
            " AND (qrm.createUser = ?9 OR qrm.devDepartmentId in (?8) OR qrm.opsDepartmentId in (?8) OR EXISTS (SELECT dv.tableDataId FROM DataVisibility dv WHERE dv.tableDataType = ?7 AND (dv.departmentSubId in (?8) or dv.departmentSubId = 0) AND qrm = dv.tableDataId))"
    )
    Page<RuleMetric> queryRuleMetrics(String subSystemName, String ruleMetricName,
                                      String enCode, Integer type, Boolean available, Boolean multiEnvs, String tableDataType, List<Long> dataVisibilityDeptList, String createUser, String devDepartmentId, String opsDepartmentId, Set<String> actionRange,String buildUser,String modifyUser, Pageable pageable);

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
    @Query(value = "SELECT count(qrm.id) FROM RuleMetric qrm where " +
            " ((?1 = '' OR qrm.subSystemName = ?1) AND (?2 = '' OR qrm.name LIKE ?2) AND (?3 = '' OR qrm.enCode = ?3) AND (?4 is null OR qrm.type = ?4) AND (?5 is null or qrm.available = ?5) AND (?6 is null or qrm.multiEnv = ?6))" +
            " AND (?10 = '' or qrm.devDepartmentId = ?10) " +
            " AND (?11 = '' or qrm.opsDepartmentId = ?11) " +
            " AND (?13 = '' or qrm.createUser = ?13) " +
            " AND (?14 = '' or qrm.modifyUser = ?14) " +
            " AND (?12 is null or EXISTS (SELECT q.tableDataId FROM DataVisibility q WHERE q.tableDataType = ?7 AND (q.departmentSubId in (?12) or q.departmentSubId = 0) AND qrm = q.tableDataId)) " +
            " AND (qrm.createUser = ?9 OR qrm.devDepartmentId in (?8) OR qrm.opsDepartmentId in (?8) OR EXISTS (SELECT dv.tableDataId FROM DataVisibility dv WHERE dv.tableDataType = ?7 AND (dv.departmentSubId in (?8) or dv.departmentSubId = 0) AND qrm = dv.tableDataId))")
    long countQueryRuleMetrics(String subSystemName, String ruleMetricName, String enCode, Integer type, Boolean available, Boolean multiEnvs, String tableDataType, List<Long> dataVisibilityDeptList, String createUser, String devDepartmentId, String opsDepartmentId, Set<String> actionRange,String buildUser,String modifyUser);

    /**
     * Find pageable rule metrics with different characters(DEPARTMENT_ADMIN, PROJECTOR).
     * @param level
     * @param departmentList
     * @param user
     * @param pageable
     * @return
     */
    @Query(value = "SELECT qrm FROM RuleMetric qrm where (?1 is null or qrm.level = ?1) OR (qrm IN (SELECT qrmdu.ruleMetric FROM RuleMetricDepartmentUser qrmdu where qrmdu.department in (?2) OR qrmdu.user = ?3))")
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
     * Query rule metrics by sub-system-id.
     * @param level
     * @param departmentList
     * @param user
     * @param subSystemId
     * @param pageable
     * @return
     */
    @Query(value = "SELECT qrm FROM RuleMetric qrm where qrm.level = ?1 AND qrm.subSystemId = ?4 OR (qrm IN (SELECT qrmdu.ruleMetric FROM RuleMetricDepartmentUser qrmdu where qrmdu.department in (?2) OR qrmdu.user IN (?3)))")
    Page<RuleMetric> findBySubSystemId(Integer level, List<Department> departmentList, User user, long subSystemId, Pageable pageable);

    /**
     * Count of querying rule metrics by sub-system-id.
     * @param level
     * @param departmentList
     * @param user
     * @param subSystemId
     * @return
     */
    @Query(value = "SELECT count(qrm.id) FROM RuleMetric qrm where qrm.level = ?1 AND qrm.subSystemId = ?4 OR (qrm IN (SELECT qrmdu.ruleMetric FROM RuleMetricDepartmentUser qrmdu where qrmdu.department in (?2) OR qrmdu.user IN (?3)))")
    long countBySubSystemId(Integer level, List<Department> departmentList, User user, long subSystemId);

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

    /**
     * Find not used rule metric
     * @param createUser
     * @param tableDataType
     * @param dataVisibilityDeptList
     * @param pageable
     * @return
     */
    @Query(value = "SELECT qrm FROM RuleMetric qrm where qrm.available = 1 AND ((qrm.createUser = ?1 OR qrm.devDepartmentId in (?3) OR qrm.opsDepartmentId in (?3) OR EXISTS (SELECT dv.tableDataId FROM DataVisibility dv WHERE dv.tableDataType = ?2 AND dv.departmentSubId in (?3) AND qrm = dv.tableDataId)) " +
            " AND NOT EXISTS (SELECT id FROM AlarmConfig qrac where qrm.id = qrac.ruleMetric.id))")
    Page<RuleMetric> findNotUsed(String createUser, String tableDataType, List<Long> dataVisibilityDeptList, Pageable pageable);

    /**
     * Count not used rule metric
     * @param createUser
     * @param tableDataType
     * @param dataVisibilityDeptList
     * @return
     */
    @Query(value =  "SELECT qrm FROM RuleMetric qrm where qrm.available = 1 AND ((qrm.createUser = ?1 OR qrm.devDepartmentId in (?3) OR qrm.opsDepartmentId in (?3) OR EXISTS (SELECT dv.tableDataId FROM DataVisibility dv WHERE dv.tableDataType = ?2 AND dv.departmentSubId in (?3) AND qrm = dv.tableDataId)) " +
            " AND NOT EXISTS (SELECT id FROM AlarmConfig qrac where qrm.id = qrac.ruleMetric.id))")
    long countNotUsed(String createUser, String tableDataType, List<Long> dataVisibilityDeptList);

    /**
     * Find not used rule metric
     * @param pageable
     * @return
     */
    @Query(value = "SELECT qrm FROM RuleMetric qrm WHERE qrm.available = 1 AND (NOT EXISTS (SELECT qrac.id FROM AlarmConfig qrac where qrm.id = qrac.ruleMetric.id))")
    Page<RuleMetric> findAllNotUsed(Pageable pageable);

    /**
     * Count not used rule metric
     * @return
     */
    @Query(value = "SELECT count(qrm.id) FROM RuleMetric qrm where qrm.available = 1 AND (NOT EXISTS (SELECT qrac.id FROM AlarmConfig qrac where qrm.id = qrac.ruleMetric.id))")
    long countAllNotUsed();

    /**
     * Find used rule metric
     * @param createUser
     * @param tableDataType
     * @param dataVisibilityDeptList
     * @param pageable
     * @return
     */
    @Query(value = "SELECT qrm FROM RuleMetric qrm where qrm.available = 1 AND (qrm.createUser = ?1 OR qrm.devDepartmentId in (?3) OR qrm.opsDepartmentId in (?3) OR EXISTS (SELECT dv.tableDataId FROM DataVisibility dv WHERE dv.tableDataType = ?2 AND dv.departmentSubId in (?3) AND qrm = dv.tableDataId))" +
            " AND EXISTS (SELECT id FROM AlarmConfig qrac where qrm.id = qrac.ruleMetric.id)")
    Page<RuleMetric> findUsed(String createUser, String tableDataType, List<Long> dataVisibilityDeptList, Pageable pageable);

    /**
     * Count used rule metric
     * @param createUser
     * @param tableDataType
     * @param dataVisibilityDeptList
     * @return
     */
    @Query(value = "SELECT count(qrm.id) FROM RuleMetric qrm where qrm.available = 1 AND (qrm.createUser = ?1 OR qrm.devDepartmentId in (?3) OR qrm.opsDepartmentId in (?3) OR EXISTS (SELECT dv.tableDataId FROM DataVisibility dv WHERE dv.tableDataType = ?2 AND dv.departmentSubId in (?3) AND qrm = dv.tableDataId))" +
            " AND EXISTS (SELECT id FROM AlarmConfig qrac where qrm.id = qrac.ruleMetric.id)")
    long countUsed(String createUser, String tableDataType, List<Long> dataVisibilityDeptList);

    /**
     * Find used rule metric
     * @param pageable
     * @return
     */
    @Query(value = "SELECT qrm FROM RuleMetric qrm WHERE qrm.available = 1 AND (EXISTS (SELECT qrac.id FROM AlarmConfig qrac where qrm.id = qrac.ruleMetric.id))")
    Page<RuleMetric> findAllUsed(Pageable pageable);

    /**
     * Count used rule metric
     * @return
     */
    @Query(value = "SELECT count(qrm.id) FROM RuleMetric qrm where qrm.available = 1 AND (EXISTS (SELECT qrac.id FROM AlarmConfig qrac where qrm.id = qrac.ruleMetric.id))")
    long countAllUsed();
}
