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
import com.webank.wedatasphere.qualitis.entity.RuleMetricDepartmentUser;
import com.webank.wedatasphere.qualitis.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author howeye
 */
public interface RuleMetricDepartmentUserRepository extends JpaRepository<RuleMetricDepartmentUser, Long> {
    /**
     * Find by user.
     * @param user
     * @return
     */
    List<RuleMetricDepartmentUser> findByUser(User user);

    /**
     * Find by department.
     * @param department
     * @return
     */
    List<RuleMetricDepartmentUser> findByDepartment(Department department);

    /**
     * Find by rule metric.
     * @param ruleMetricInDb
     * @return
     */
    RuleMetricDepartmentUser findByRuleMetric(RuleMetric ruleMetricInDb);
}
