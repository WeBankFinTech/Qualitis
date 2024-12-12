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
import com.webank.wedatasphere.qualitis.constant.ImsLevelEnum;
import com.webank.wedatasphere.qualitis.dao.AlarmInfoDao;
import com.webank.wedatasphere.qualitis.dao.ApplicationDao;
import com.webank.wedatasphere.qualitis.dao.TaskDao;
import com.webank.wedatasphere.qualitis.entity.AlarmInfo;
import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.request.IndexRequest;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.IndexAlarmChartResponse;
import com.webank.wedatasphere.qualitis.response.IndexAlarmTodayResponse;
import com.webank.wedatasphere.qualitis.response.IndexApplicationChartResponse;
import com.webank.wedatasphere.qualitis.response.IndexApplicationResponse;
import com.webank.wedatasphere.qualitis.response.IndexApplicationTodayResponse;
import com.webank.wedatasphere.qualitis.service.IndexService;
import com.webank.wedatasphere.qualitis.util.DateUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
  private AlarmInfoDao alarmInfoDao;
  @Autowired
  private TaskDao taskDao;

  /**
   * 获取今日发送给当前登录用户的所有告警
   *
   * @param user 当前登录用户
   * @param pageRequest 分页参数
   * @return IndexAlarmTodayResponse
   */
  @Override
  public IndexAlarmTodayResponse getTodayAlarms(String user, PageRequest pageRequest) {
    String today = DateFormatUtils.format(new Date(), DATE_FORMAT_PATTERN);
    //当日发送给用户的告警信息
    List<AlarmInfo> alarmInfos = alarmInfoDao.findAllByUsernameAndAlarmTimeBetweenPage(user, today +
        " 00:00:00", today + " 23:59:59", pageRequest.getPage(), pageRequest.getSize());
    if (alarmInfos == null || alarmInfos.isEmpty()) {
      LOGGER.info("[Home overview]user:{},date:{},No alarm information found.", user, today);
      return new IndexAlarmTodayResponse();
    }
    //查询结果总数
    long totalNum = alarmInfoDao.countByUsernameAndAlarmTimeBetween(user, today + " 00:00:00",
        today + " 23:59:59");

    long alarmCriticalNum = alarmInfoDao.countByUsernameAndAlarmTimeBetweenAndAlarmLevel(user, today + " 00:00:00",
        today + " 23:59:59", ImsLevelEnum.CRITICAL.getCode());
    long alarmMajorNum = alarmInfoDao.countByUsernameAndAlarmTimeBetweenAndAlarmLevel(user, today + " 00:00:00",
        today + " 23:59:59", ImsLevelEnum.MAJOR.getCode());
    long alarmMinorNum = alarmInfoDao.countByUsernameAndAlarmTimeBetweenAndAlarmLevel(user, today + " 00:00:00",
        today + " 23:59:59", ImsLevelEnum.MINOR.getCode());
    long alarmWarnningNum = alarmInfoDao.countByUsernameAndAlarmTimeBetweenAndAlarmLevel(user, today + " 00:00:00",
        today + " 23:59:59", ImsLevelEnum.WARNING.getCode());
    long alarmInfoNum = alarmInfoDao.countByUsernameAndAlarmTimeBetweenAndAlarmLevel(user, today + " 00:00:00",
        today + " 23:59:59", ImsLevelEnum.INFO.getCode());

    LOGGER.info("[Home overview]user:{},date:{},Found {} alarm messages, a total of {}", user, today, alarmInfos.size(), totalNum);
    IndexAlarmTodayResponse indexAlarmTodayResponse = new IndexAlarmTodayResponse(alarmInfos, totalNum);
    indexAlarmTodayResponse.setAlarmCriticalNum(alarmCriticalNum);
    indexAlarmTodayResponse.setAlarmMajorNum(alarmMajorNum);
    indexAlarmTodayResponse.setAlarmMinorNum(alarmMinorNum);
    indexAlarmTodayResponse.setAlarmWarningNum(alarmWarnningNum);
    indexAlarmTodayResponse.setAlarmInfoNum(alarmInfoNum);
    //转换为前端数据模型
    return indexAlarmTodayResponse;
  }

  /**
   * 获取时间段内发送给当前登录用户的critical/major/minor/warning/info级别的告警数
   *
   * @param param 查询参数
   * @return List<IndexAlarmChartResponse> 返回结果
   */
  @Override
  public List<IndexAlarmChartResponse> getAlarmChart(IndexRequest param) throws ParseException {
    //近7/30天数据
    if (param.getStepSize() != null) {
      List<Map<String, Object>> alarmInfos = getRecentAlarmInfos(param.getUser(), param.getStepSize());
      if (alarmInfos == null || alarmInfos.isEmpty()) {
        LOGGER.info("[Home overview]user:{},recent:{} day,No alarm information found.", param.getUser(), param.getStepSize());
        return Collections.emptyList();
      }
      LOGGER.info("[Home overview]user:{},recent:{},Found {} alarm messages.", param.getUser(), param.getStepSize(),
          alarmInfos.size());
      return getAlarmChartPerDayRecent(new Date(), param.getStepSize(), alarmInfos);
    }
    //时间范围查找
    List<Map<String, Object>> alarmInfos = getRecentAlarmInfos(param.getUser(), param.getStartDate(),
        param.getEndDate());
    if (alarmInfos == null || alarmInfos.isEmpty()) {
      LOGGER.info("[Home overview]user:{},between start date:{},and end date:{},No alarm information found.", param.getUser(), param.getStartDate(),
          param.getEndDate());
      return Collections.emptyList();
    }
    LOGGER.info("[Home overview]user:{},between start date:{},and end date:{},Found {} alarm messages.", param.getUser(), param.getStartDate(),
        param.getEndDate(), alarmInfos.size());
    return getAlarmChartPerDayRange(param.getStartDate(), param.getEndDate(), alarmInfos);
  }

  @Override
  public IndexApplicationTodayResponse getTodaySubmitApplications(String username, PageRequest pageRequest) {
    // Find applications submitted today
    String today = DateFormatUtils.format(new Date(), DATE_FORMAT_PATTERN);
    List<Application> applications = getUserApplications(username, today, pageRequest);
    if (applications == null || applications.isEmpty()) {
      LOGGER.info("[Home overview]user:{},date:{},The user and the task submitted by the specified date were not found.", username, today);
      return new IndexApplicationTodayResponse();
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
      IndexApplicationResponse applicationResponse = new IndexApplicationResponse(application, tasks);
      applicationResponses.add(applicationResponse);
    }

    return new IndexApplicationTodayResponse(applicationResponses, totalNum, totalSuccNum, totalFailNum, totalFailCheckNum);
  }

  @Override
  public List<IndexApplicationChartResponse> getApplicationChart(IndexRequest param) {
    // 7/30
    if (param.getStepSize() != null) {
      LOGGER.info("[Home overview]user:{},recent:{} days, find applications submitted by the user's specified date.", param.getUser(), param.getStepSize());
      return getApplicationChartPerDayRecent(new Date(), param.getStepSize(), param.getUser());
    }
    // Find in limitation of time
    LOGGER.info("[Home overview]user:{},between start date:{},and end date:{}, find applications submitted by the user's specified date.", param.getUser(),
                param.getStartDate(), param.getEndDate());
    return getApplicationChartPerDayRange(param.getStartDate(), param.getEndDate(), param.getUser());
  }


  private List<Application> getUserApplications(String username, String date,
                                                PageRequest pageRequest) {
    return applicationDao.findApplicationByUserAndSubmitTimeBetween(username, date + " 00:00:00", date + " 23:59:59"
        , pageRequest.getPage(), pageRequest.getSize());
  }

  private long countUserApplications(String username, String date) {
    return countUserApplications(username, date, null);
  }

  private long countUserSuccApplications(String username, String date) {
    return countUserApplications(username, date, ApplicationStatusEnum.FINISHED.getCode());
  }

  private long countUserFailApplications(String username, String date) {
    return countUserApplications(username, date, ApplicationStatusEnum.FAILED.getCode());
  }

  private long countUserFailCheckApplications(String username, String date) {
    return countUserApplications(username, date, ApplicationStatusEnum.NOT_PASS.getCode());
  }

  private long countUserApplications(String username, String date, Integer appStatus) {
    return applicationDao.countApplicationByUserAndSubmitTimeBetweenAndStatus(username, date + " 00:00:00", date + " 23:59:59", appStatus);
  }

  private long countUserApplicationsInRange(String username, String startDate, String endDate, Integer appStatus) {
    return applicationDao.countApplicationByUserAndSubmitTimeBetweenAndStatus(username, startDate + " 00:00:00", endDate + " 23:59:59", appStatus);
  }

  private List<IndexApplicationChartResponse> getApplicationChartPerDayRecent(Date calendarDate, int stepSize, String user) {
    List<IndexApplicationChartResponse> responses = new ArrayList<>();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(calendarDate);
    String endDate = DateFormatUtils.format(calendar.getTime(), DATE_FORMAT_PATTERN);
    calendar.add(Calendar.DAY_OF_YEAR, stepSize);
    String startDate = DateFormatUtils.format(calendar.getTime(), DATE_FORMAT_PATTERN);
    List<Map<String, Object>> applications = applicationDao.chartApplicationByUserAndSubmitTimeBetween(user, startDate, endDate);

    for (int i = 0; i > stepSize; i--) {
      // Get time before i day
      Calendar calendarPer = Calendar.getInstance();
      calendarPer.setTime(calendarDate);
      calendarPer.add(Calendar.DAY_OF_YEAR, i);
      String date = DateFormatUtils.format(calendarPer.getTime(), DATE_FORMAT_PATTERN);
      IndexApplicationChartResponse response = new IndexApplicationChartResponse(date);
      long total = countUserApplicationsInRange(user, date, date, null);
      response.setApplicationTotalNum((int) total);
      for (Iterator<Map<String, Object>> applicationsIterator = applications.iterator(); applicationsIterator.hasNext(); ) {
        Map<String, Object> currentApplicationInfos = applicationsIterator.next();
        if (date.equals(currentApplicationInfos.get("submit_time").toString())) {
          Integer status = (Integer) currentApplicationInfos.get("status");
          int count = ((Long) currentApplicationInfos.get("count")).intValue();
          if (status.equals(ApplicationStatusEnum.FINISHED.getCode())) {
            response.setApplicationSuccNum(count);
          } else if (status.equals(ApplicationStatusEnum.FAILED.getCode())) {
            response.setApplicationFailNum(count);
          } else if (status.equals(ApplicationStatusEnum.NOT_PASS.getCode())) {
            response.setApplicationFailCheckNum(count);
          }
          applicationsIterator.remove();
        }
      }

      // Return zero if there total equals zero that day
      responses.add(response);
    }
    return responses;
  }

  private List<IndexApplicationChartResponse> getApplicationChartPerDayRange(String startDate, String endDate, String user) {
    DateTimeFormatter formatter = DateTimeFormat.forPattern(DATE_FORMAT_PATTERN);
    Date calendarEndDate = DateTime.parse(endDate, formatter).toDate();
    Date calendarStartDate = DateTime.parse(startDate, formatter).toDate();

    int stepSize = DateUtils.getDayDiffBetween(calendarStartDate, calendarEndDate);
    return getApplicationChartPerDayRecent(calendarEndDate, -(stepSize + 1), user);
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

  /**
   * 将最近天数(负数)转化为时间范围
   *
   * @param user 用户名
   * @param stepSize 最近天数
   * @return 告警列表
   */
  private List<Map<String, Object>> getRecentAlarmInfos(String user, int stepSize) {
    Calendar calendar = Calendar.getInstance();
    String endDate = DateFormatUtils.format(calendar.getTime(), DATE_FORMAT_PATTERN);
    calendar.add(Calendar.DAY_OF_YEAR, stepSize);
    String startDate = DateFormatUtils.format(calendar.getTime(), DATE_FORMAT_PATTERN);
    return getRecentAlarmInfos(user, startDate, endDate);
  }

  private List<Map<String, Object>> getRecentAlarmInfos(String user, String startDate, String endDate) {
    String endTime = endDate + " 23:59:59";
    String startTime = startDate + " 00:00:00";
    return alarmInfoDao.findAllByUsernameAndAlarmTimeBetweenPerDay(user, startTime, endTime);
  }

  /**
   * 以calendarDate 初始化日期为基准,向前统计stepSize 天数,每天critical/major/minor级别的告警数
   *
   * @param calendarDate 初始化日期
   * @param stepSize 天数
   * @param alarmInfos 告警列表
   * @return 每天critical/major/minor/warning/info级别的告警数
   */
  private List<IndexAlarmChartResponse> getAlarmChartPerDayRecent(Date calendarDate, int stepSize, List<Map<String, Object>> alarmInfos) {
    List<IndexAlarmChartResponse> responses = new ArrayList<>();
    for (int i = 0; i > stepSize; i--) {
      //最开始时间不变,在最开始时间上得到范围时间中的每一天
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(calendarDate);
      calendar.add(Calendar.DAY_OF_YEAR, i);
      String date = DateFormatUtils.format(calendar.getTime(), DATE_FORMAT_PATTERN);
      IndexAlarmChartResponse response = new IndexAlarmChartResponse(date);

      for (Iterator<Map<String, Object>> alarmInfosIterator = alarmInfos.iterator(); alarmInfosIterator.hasNext(); ) {
        Map<String, Object> currentAlarmInfos = alarmInfosIterator.next();
        if (date.equals(currentAlarmInfos.get("alarm_day").toString())) {
          String alarmLevel = (String) currentAlarmInfos.get("alarm_level");
          int alarmCount = ((Long) currentAlarmInfos.get("alarm_count")).intValue();
          if (ImsLevelEnum.CRITICAL.getCode().equals(alarmLevel)) {
            response.setAlarmCriticalNum(alarmCount);
          } else if (ImsLevelEnum.MAJOR.getCode().equals(alarmLevel)) {
            response.setAlarmMajorNum(alarmCount);
          } else if (ImsLevelEnum.MINOR.getCode().equals(alarmLevel)) {
            response.setAlarmMinorNum(alarmCount);
          } else if (ImsLevelEnum.WARNING.getCode().equals(alarmLevel)) {
            response.setAlarmWarningNum(alarmCount);
          } else if (ImsLevelEnum.INFO.getCode().equals(alarmLevel)) {
            response.setAlarmInfoNum(alarmCount);
          }
          alarmInfosIterator.remove();
        }
      }
      responses.add(response);
    }
    return responses;
  }

  /**
   * 根据startDate 开始日期,endDate 结束日期,计算出天数差,复用getAlarmChartPerDay4recent方法
   * 统计每天critical/major/minor/warning/info级别的告警数
   *
   * @param startDate 开始日期
   * @param endDate 结束日期
   * @param alarmInfos 告警列表
   * @return 每天critical/major/minor/warning/info级别的告警数
   */
  private List<IndexAlarmChartResponse> getAlarmChartPerDayRange(String startDate, String endDate, List<Map<String, Object>> alarmInfos) {
    DateTimeFormatter formatter = DateTimeFormat.forPattern(DATE_FORMAT_PATTERN);
    Date calendarEndDate = DateTime.parse(endDate, formatter).toDate();
    Date calendarStartDate = DateTime.parse(startDate, formatter).toDate();
    //计算时间范围相差天数,但没包含开始日期,所以会少一天,调用getAlarmChartPerDay4recent时,stepSize要多加1天
    int stepSize = DateUtils.getDayDiffBetween(calendarStartDate, calendarEndDate);
    return getAlarmChartPerDayRecent(calendarEndDate, -(stepSize + 1), alarmInfos);
  }
}
