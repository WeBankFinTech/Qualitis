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

import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.entity.TaskRuleSimple;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author howeye
 */
public interface TaskRuleSimpleRepository extends JpaRepository<TaskRuleSimple, Long> {

    /**
     * Find task rule simple by creator and project id
     * @param createUser
     * @param projectId
     * @param pageable
     * @return
     */
    @Query("select j from TaskRuleSimple j where j.projectCreator = ?1 and j.projectId = ?2 group by j.applicationId")
    List<TaskRuleSimple> findByCreateUserAndProjectId(String createUser, Long projectId, Pageable pageable);

    /**
     * Count task rule simple by creator and project id
     * @param createUser
     * @param projectId
     * @return
     */
    @Query("select count(j) from TaskRuleSimple j where j.projectCreator = ?1 and j.projectId = ?2 group by j.applicationId")
    List<Long> countByCreateUserAndProjectId(String createUser, Long projectId);

    /**
     * Find task rule simple by task
     * @param task
     * @return
     */
    List<TaskRuleSimple> findByTask(Task task);

    /**
     * Count yesterday num.
     * @param ruleId
     * @return
     */
    @Query(value = "select trs from TaskRuleSimple trs where to_days(now()) - to_days(trs.submitTime) = 1 and trs.ruleId = ?1")
    List<TaskRuleSimple> findBetweenYesterday(Long ruleId);
}
