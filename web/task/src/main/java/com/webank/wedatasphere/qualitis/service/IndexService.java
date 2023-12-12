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
import com.webank.wedatasphere.qualitis.response.IndexAlarmChartResponse;
import com.webank.wedatasphere.qualitis.response.IndexAlarmTodayResponse;
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
   */
  List<IndexApplicationChartResponse> getApplicationChart(IndexRequest param);

  /**
   * 获取今日发送给当前登录用户的所有告警
   *
   * @param user 当前登录用户
   * @param pageRequest 分页参数
   * @return IndexAlarmTodayResponse
   */
  IndexAlarmTodayResponse getTodayAlarms(String user, PageRequest pageRequest);

  /**
   * 获取时间段内发送给当前登录用户的critical/major/minor级别的告警数
   *
   * @param param 查询参数
   * @return List<IndexAlarmChartResponse> 返回结果
   * @throws ParseException 日期格式错误异常
   */
  List<IndexAlarmChartResponse> getAlarmChart(IndexRequest param) throws ParseException;
}
