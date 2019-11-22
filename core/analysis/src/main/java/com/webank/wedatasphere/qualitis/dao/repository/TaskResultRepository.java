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
import com.webank.wedatasphere.qualitis.entity.TaskResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author howeye
 */
public interface TaskResultRepository extends JpaRepository<TaskResult, Long> {

    /**
     * Find task result by application and ruls
     * @param applicationId
     * @param ruleId
     * @return
     */
    TaskResult findByApplicationIdAndRuleId(String applicationId, Long ruleId);

    /**
     * Find value avg from begin time and end time
     * @param begin
     * @param end
     * @param ruleId
     * @return
     */
    @Query("select avg(value) from TaskResult t where (t.createTime between ?1 and ?2) and t.ruleId = ?3")
    Double findAvgByCreateTimeBetweenAndRuleId(String begin, String end, Long ruleId);

    /**
     * Find task result by application and rule id
     * @param applicationId
     * @param ruleIds
     * @return
     */
    List<TaskResult> findByApplicationIdAndRuleIdIn(String applicationId, List<Long> ruleIds);
}
