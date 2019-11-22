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

package com.webank.wedatasphere.qualitis.query.service;

import com.webank.wedatasphere.qualitis.query.request.RuleQueryRequest;
import com.webank.wedatasphere.qualitis.query.response.RuleQueryProject;
import com.webank.wedatasphere.qualitis.query.request.RuleQueryRequest;
import com.webank.wedatasphere.qualitis.query.response.RuleQueryProject;

import java.util.List;
import java.util.Map;

/**
 * @author v_wblwyan
 * @date 2018-11-1
 */
public interface RuleQueryService {

  /**
   * initial query api
   * @param user
   * @return
   */
  List<RuleQueryProject> init(String user);

  /**
   * Rule query api
   * @param param
   * @return
   */
  List<RuleQueryProject> query(RuleQueryRequest param);

  /**
   * Get all rule datasource by user
   * @param user
   * @return
   */
  Map<String, Object> conditions(String user);

}
