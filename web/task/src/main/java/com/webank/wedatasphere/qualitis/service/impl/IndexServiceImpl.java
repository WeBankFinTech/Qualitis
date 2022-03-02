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

package com.webank.wedatasphere.qualitis.service.impl;

import com.webank.wedatasphere.qualitis.constant.ApplicationStatusEnum;
import com.webank.wedatasphere.qualitis.dao.ApplicationDao;
import com.webank.wedatasphere.qualitis.dao.TaskDao;
import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.request.IndexRequest;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.IndexApplicationChartResponse;
import com.webank.wedatasphere.qualitis.response.IndexApplicationResponse;
import com.webank.wedatasphere.qualitis.response.IndexApplicationTodayResponse;
import com.webank.wedatasphere.qualitis.service.IndexService;
import com.webank.wedatasphere.qualitis.util.DateUtils;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author v_wblwyan
 * @date 2018-11-14
 */
@Service
public class IndexServiceImpl implements IndexService {

  private static final Logger LOGGER = LoggerFactory.getLogger(IndexServiceImpl.class);

  private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
  @Autowired
  private ApplicationDao applicationDao;
  @Autowired
  private TaskDao taskDao;

  @Override
  public IndexApplicationTodayResponse getTodaySubmitApplications(String username, PageRequest pageRequest) {
    // Find applications submitted today
    String today = DateFormatUtils.format(new Date(), DATE_FORMAT_PATTERN);
    List<Application> applications = getUserApplications(username, today, pageRequest);
    if (applications == null || applications.isEmpty()) {
      LOGGER.info("[Home overview]user:{},date:{},The user and the task submitted by the specified date were not found.", username, today);
      return null;
    }
    // Get total num of applications today
    long totalNum = countUserApplications(username, today);

    // Get successful total num of applications today
    long totalSuccNum = countUserSuccApplications(username, today);

    // Get failed total num of applications today
    long totalFailNum = countUserFailApplications(username, today);

    // Get not pass total num of applications today
    long totalFailCheckNum = countUserFailCheckApplications(username, today);

    LOGGER.info("[Home overview]user:{},date:{},Find {} tasks submitted by the user's specified date, for a total of {}.", username, today,
                applications.size(), totalNum);

    List<IndexApplicationResponse> applicationResponses = new ArrayList<>();
    for (Application application : applications) {
      List<Task> tasks = taskDao.findByApplication(application);
      IndexApplicationResponse applicationResponse = new IndexApplicationResponse(application,
                                                                                  tasks);
      applicationResponses.add(applicationResponse);
    }

    return new IndexApplicationTodayResponse(applicationResponses, totalNum, totalSuccNum,
                                             totalFailNum, totalFailCheckNum);
  }

  @Override
  public List<IndexApplicationChartResponse> getApplicationChart(IndexRequest param)
      throws ParseException {
    // 7/30
    if (param.getStepSize() != null) {
      List<Application> applications = getUserApplications(param.getUser(), param.getStepSize());
      if (applications == null || applications.isEmpty()) {
        LOGGER.info("[Home overview]user:{},recent:{} days,The user and the task submitted by the specified date were not found.", param.getUser(), param.getStepSize());
        return null;
      }
      LOGGER.info("[Home overview]user:{},recent:{} days,Find {} tasks submitted by the user's specified date.", param.getUser(), param.getStepSize(),
                  applications.size());
      return getApplicationChartPerDayRecent(new Date(), param.getStepSize(), applications);
    }
    // Find in limitation of time
    List<Application> applications = getUserApplications(param.getUser(),
                                                                 param.getStartDate(),
                                                                 param.getEndDate());
    if (applications == null || applications.isEmpty()) {
      LOGGER.info("[Home overview]user:{},between start date:{},and end date:{},The user and the task submitted by the specified date were not found.", param.getUser(),
                  param.getStartDate(), param.getEndDate());
      return null;
    }
    LOGGER.info("[Home overview]user:{},between start date:{},and end date:{},Find {} tasks submitted by the user's specified date.", param.getUser(),
                param.getStartDate(), param.getEndDate(), applications.size());
    return getApplicationChartPerDayRange(param.getStartDate(), param.getEndDate(), applications);
  }


  private List<Application> getUserApplications(String username, String date,
                                                PageRequest pageRequest) {
    return applicationDao.findApplicationByUserAndSubmitTimeBetweenPage(username,
            date + " 00:00:00", date + " 23:59:59",
            pageRequest.getPage(), pageRequest.getSize());
  }

  private long countUserApplications(String username, String date) {
    return countUserApplications(username, date, null);
  }

  private long countUserSuccApplications(String username, String date) {
    return countUserApplications(username, date, new Integer[] {ApplicationStatusEnum.FINISHED.getCode()});
  }

  private long countUserFailApplications(String username, String date) {
    return countUserApplications(username, date, new Integer[] {ApplicationStatusEnum.FAILED.getCode()});
  }

  private long countUserFailCheckApplications(String username, String date) {
    return countUserApplications(username, date, new Integer[] {ApplicationStatusEnum.NOT_PASS.getCode()});
  }

  private long countUserApplications(String username, String date, Integer[] appStatus) {
    return applicationDao.countApplicationByUserAndSubmitTimeBetweenAndStatusIn(username, date + " 00:00:00", date + " 23:59:59", appStatus);
  }

  /**
   * Find applications list according to the limitation of time in [now, now - stepSize]
   * @param username login user
   * @param stepSize
   * @return
   */
  private List<Application> getUserApplications(String username, int stepSize) {
    Calendar calendar = Calendar.getInstance();
    String endDate = DateFormatUtils.format(calendar.getTime(), DATE_FORMAT_PATTERN);
    calendar.add(Calendar.DAY_OF_YEAR, stepSize);
    String startDate = DateFormatUtils.format(calendar.getTime(), DATE_FORMAT_PATTERN);
    return getUserApplications(username, startDate, endDate);
  }

  private List<Application> getUserApplications(String username, String startSubmitDate,
                                                String endSubmitDate) {
    endSubmitDate = endSubmitDate + " 23:59:59";
    startSubmitDate = startSubmitDate + " 00:00:00";
    return applicationDao.findApplicationByUserAndSubmitTimeBetween(username, startSubmitDate, endSubmitDate);
  }

  private List<IndexApplicationChartResponse> getApplicationChartPerDayRecent(Date calendarDate,
      int stepSize, List<Application> applications) {
    // Find applications status in the form of day, grouping by "yyyy-MM-dd"
    Map<String, List<Application>> perDayAppsMap = getPerDayApplications(applications);
    List<IndexApplicationChartResponse> responses = new ArrayList<>();

    for (int i = 0; i > stepSize; i--) {
      // Get time before i day
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(calendarDate);
      calendar.add(Calendar.DAY_OF_YEAR, i);
      String date = DateFormatUtils.format(calendar.getTime(), DATE_FORMAT_PATTERN);
      IndexApplicationChartResponse response;
      // If there is applications in that day
      if (perDayAppsMap.containsKey(date)) {
        // Find application lists
        response = new IndexApplicationChartResponse(date, perDayAppsMap.get(date));
        responses.add(response);
        continue;
      }
      // Return zero if there is no applications that day
      response = new IndexApplicationChartResponse(date);
      responses.add(response);
    }
    return responses;
  }

  private List<IndexApplicationChartResponse> getApplicationChartPerDayRange(String startDate, String endDate, List<Application> applications) {
    DateTimeFormatter formatter = DateTimeFormat.forPattern(DATE_FORMAT_PATTERN);
    Date calendarEndDate = DateTime.parse(endDate, formatter).toDate();
    Date calendarStartDate = DateTime.parse(startDate, formatter).toDate();

    int stepSize = DateUtils.getDayDiffBetween(calendarStartDate, calendarEndDate);
    return getApplicationChartPerDayRecent(calendarEndDate, -(stepSize + 1), applications);
  }

  private Map<String, List<Application>> getPerDayApplications(List<Application> applications) {
    Map<String, List<Application>> perDayAppsMap = new HashMap<>(applications.size());
    for (Application application : applications) {
      // Get date of time
      String date = application.getSubmitTime().substring(0, 10);
      if (perDayAppsMap.containsKey(date)) {
        perDayAppsMap.get(date).add(application);
        continue;
      }
      List<Application> applicationList = new ArrayList<>();
      applicationList.add(application);
      perDayAppsMap.put(date, applicationList);
    }
    return perDayAppsMap;
  }
}
