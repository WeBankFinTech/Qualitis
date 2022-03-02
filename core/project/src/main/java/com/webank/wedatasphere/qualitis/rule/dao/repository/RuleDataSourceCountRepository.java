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

package com.webank.wedatasphere.qualitis.rule.dao.repository;

import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSourceCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * @author allenzhou
 */
public interface RuleDataSourceCountRepository extends JpaRepository<RuleDataSourceCount, Long>, JpaSpecificationExecutor<RuleDataSourceCount> {

    /**
     * Find count with name and userID.
     * @param datasourceName
     * @param userId
     * @return
     */
    @Query(value = "SELECT dsc FROM RuleDataSourceCount dsc where dsc.datasourceName = ?1 and dsc.userId = ?2")
    RuleDataSourceCount findByDatasourceNameAndUserId(String datasourceName, Long userId);
}
