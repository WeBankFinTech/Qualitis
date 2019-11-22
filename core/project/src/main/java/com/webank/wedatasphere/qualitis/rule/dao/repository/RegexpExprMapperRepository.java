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

import com.webank.wedatasphere.qualitis.rule.entity.TemplateRegexpExpr;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateRegexpExpr;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author howeye
 */
public interface RegexpExprMapperRepository extends JpaRepository<TemplateRegexpExpr, Long> {

    /**
     * Find regexp expr mapper by regexp type and key name
     * @param regexpType
     * @param keyName
     * @return
     */
    TemplateRegexpExpr findByRegexpTypeAndKeyName(Integer regexpType, String keyName);

    /**
     * Find regexp expr mapper by regexp type
     * @param regexpType
     * @return
     */
    List<TemplateRegexpExpr> findByRegexpType(Integer regexpType);

}
