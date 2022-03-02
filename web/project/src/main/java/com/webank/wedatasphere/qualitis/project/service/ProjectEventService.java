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

package com.webank.wedatasphere.qualitis.project.service;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author allenzhou
 */
public interface ProjectEventService {

    /**
     * Record project event.
     * @param project
     * @param userName
     * @param operation
     * @param content
     * @param code
     */
    void record(Long project, String userName, String operation, String content, Integer code);

    /**
     * Field modify record.
     * @param projectInDb
     * @param userName
     * @param field
     * @param beforeModify
     * @param afterModify
     * @param typeId
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    void recordModifyProject(Project projectInDb, String userName, String field, String beforeModify, String afterModify, Integer typeId);
}
