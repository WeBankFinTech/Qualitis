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

package com.webank.wedatasphere.qualitis.service;

import com.webank.wedatasphere.qualitis.request.IndexRequest;
import com.webank.wedatasphere.qualitis.response.IndexApplicationChartResponse;
import com.webank.wedatasphere.qualitis.response.IndexApplicationTodayResponse;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.request.IndexRequest;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.IndexApplicationChartResponse;
import com.webank.wedatasphere.qualitis.response.IndexApplicationTodayResponse;

import java.text.ParseException;
import java.util.List;

/**
 * @author v_wblwyan
 * @date 2018-11-14
 */
public interface IndexService {


  /**
   * Get applications that were submitted today
   * @param user
   * @param pageRequest
   * @return IndexApplicationTodayResponse
   */
  IndexApplicationTodayResponse getTodaySubmitApplications(String user, PageRequest pageRequest);

  /**
   * Get applications status information in the limitation of time
   * @param param
   * @return List<IndexApplicationChartResponse>
   * @throws ParseException
   */
  List<IndexApplicationChartResponse> getApplicationChart(IndexRequest param) throws ParseException;
}
