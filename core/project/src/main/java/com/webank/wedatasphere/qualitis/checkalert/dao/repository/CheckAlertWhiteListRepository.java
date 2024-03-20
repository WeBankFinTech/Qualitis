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

package com.webank.wedatasphere.qualitis.checkalert.dao.repository;

import com.webank.wedatasphere.qualitis.checkalert.entity.CheckAlertWhiteList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author allenzhou
 */
public interface CheckAlertWhiteListRepository extends JpaRepository<CheckAlertWhiteList, Long> {

    /**
     * Check white list
     * @param item
     * @param type
     * @param user
     * @return
     */
    @Query(value = "SELECT qaw.* FROM qualitis_alert_whitelist qaw WHERE qaw.item = ?1 AND qaw.type = ?2 AND qaw.authorized_user = ?3", nativeQuery = true)
    CheckAlertWhiteList checkWhiteList(String item, Integer type, String user);
}
