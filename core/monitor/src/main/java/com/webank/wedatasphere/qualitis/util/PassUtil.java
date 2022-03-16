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

package com.webank.wedatasphere.qualitis.util;

import com.webank.wedatasphere.qualitis.dao.TaskResultDao;
import com.webank.wedatasphere.qualitis.entity.*;
import com.webank.wedatasphere.qualitis.rule.constant.CheckTemplateEnum;
import com.webank.wedatasphere.qualitis.rule.constant.CompareTypeEnum;
import com.webank.wedatasphere.qualitis.submitter.impl.ExecutionManagerImpl;

import com.webank.wedatasphere.qualitis.entity.TaskRuleAlarmConfig;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;

/**
 * @author howeye
 */
public class PassUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger("monitor");

    private PassUtil() {

    }

    public static Boolean notSafe(String taskId, Long ruleId, TaskRuleAlarmConfig alarmConfig, TaskResult taskResult, TaskResultDao taskResultDao) {
        Integer checkTemplate = alarmConfig.getCheckTemplate();
        Double thresholds = alarmConfig.getThreshold();
        if (taskResult == null) {
            LOGGER.error("Failed to find task result, task ID: {}, rule ID: {}", taskId, ruleId);
            return false;
        }
        Double result = 0.0;
        try {
            if (StringUtils.isNotBlank(taskResult.getValue())) {
                result = Double.parseDouble(taskResult.getValue());
            }
        } catch (NumberFormatException e) {
            return false;
        }

        if (checkTemplate.equals(CheckTemplateEnum.MONTH_FLUCTUATION.getCode())) {
            Double monthAvg = getMonthAvg(taskResultDao, taskResult.getRuleId());
            return moreThanThresholds(result, monthAvg, thresholds);
        } else if (checkTemplate.equals(CheckTemplateEnum.WEEK_FLUCTUATION.getCode())) {
            Double weekAvg = getWeekAvg(taskResultDao, taskResult.getRuleId());
            return moreThanThresholds(result, weekAvg, thresholds);
        } else if (checkTemplate.equals(CheckTemplateEnum.DAY_FLUCTUATION.getCode())) {
            Double dayAvg = getDayAvg(taskResultDao, taskResult.getRuleId());
            return moreThanThresholds(result, dayAvg, thresholds);
        } else if (checkTemplate.equals(CheckTemplateEnum.FIXED_VALUE.getCode())) {
            Integer compareType = alarmConfig.getCompareType();
            return moreThanThresholds(result, thresholds, compareType);
        } else {
            // Ring growth.
            Integer compareType = alarmConfig.getCompareType();
            thresholds /= 100;
            try {
                result = getRingGrowth(taskResultDao, checkTemplate, ruleId, taskResult.getRuleMetricId());
            } catch (Exception e) {
                LOGGER.info("Because the data of the previous period does not exist, the chain ratio cannot be calculated.");
                return false;
            }

            return moreThanThresholds(result, thresholds, compareType);
        }
    }

    private static Double getMonthAvg(TaskResultDao taskResultDao, Long ruleId) {
        return getAvg(taskResultDao, Calendar.MONTH, ruleId);
    }

    private static Double getWeekAvg(TaskResultDao taskResultDao, Long ruleId) {
        return getAvg(taskResultDao, Calendar.WEEK_OF_MONTH, ruleId);
    }

    private static Double getDayAvg(TaskResultDao taskResultDao, Long ruleId) { return getAvg(taskResultDao, Calendar.DAY_OF_MONTH, ruleId); }

    private static Double getAvg(TaskResultDao taskResultDao, Integer calendarStepUnit, Long ruleId) {
        Date nowDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowDate);
        calendar.add(calendarStepUnit, -1);
        Date lastMonthDate = calendar.getTime();

        return taskResultDao.findAvgByCreateTimeBetweenAndRule(ExecutionManagerImpl.PRINT_TIME_FORMAT.format(lastMonthDate), ExecutionManagerImpl.PRINT_TIME_FORMAT.format(nowDate), ruleId);
    }

    private static Double getRingGrowth(TaskResultDao taskResultDao, Integer ringType, Long ruleId, Long ruleMetricId) {
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime start;
        LocalDateTime end;
        LocalDateTime startOfLast;
        LocalDateTime endOfLast;
        if (ringType.equals(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode())) {
            int year = localDateTime.getYear();
            // Location current time area and calculate avg.
            start = LocalDateTime.of(year, 1, 1, 0, 0, 0);
            end = LocalDateTime.of(year, 12, 31, 23, 59, 59);
            startOfLast = LocalDateTime.of(year - 1, 1, 1, 0, 0, 0);
            return specialTimeRingGrowth(start, end, startOfLast, taskResultDao, ruleId, ruleMetricId);

        } else if (ringType.equals(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode())) {
            int year = localDateTime.getYear();
            Month month = localDateTime.getMonth();
            // Location current time area and calculate avg.
            if (month.getValue() >= Month.JULY.getValue() && month.getValue() <= Month.DECEMBER.getValue()) {
                start = LocalDateTime.of(year, 6, 1, 0, 0, 0);
                end = LocalDateTime.of(year, 12, 31, 23, 59, 59);
                startOfLast = LocalDateTime.of(year, 1, 1, 0, 0, 0);
            } else {
                start = LocalDateTime.of(year, 1, 1, 0, 0, 0);
                end = LocalDateTime.of(year, 6, 30, 23, 59, 59);
                startOfLast = LocalDateTime.of(year - 1, 7, 1, 0, 0, 0);
            }
            return specialTimeRingGrowth(start, end, startOfLast, taskResultDao, ruleId, ruleMetricId);
        } else if (ringType.equals(CheckTemplateEnum.SEASON_RING_GROWTH.getCode())) {
            int year = localDateTime.getYear();
            Month month = localDateTime.getMonth();
            Month firstMonthOfQuarter = month.firstMonthOfQuarter();
            Month endMonthOfQuarter = Month.of(firstMonthOfQuarter.getValue() + 2);
            if (month.getValue() >= Month.MARCH.getValue()) {
                start = LocalDateTime.of(year, firstMonthOfQuarter.getValue(), 1, 0, 0, 0);
                end = LocalDateTime.of(year, endMonthOfQuarter.getValue(), endMonthOfQuarter.maxLength(), 23, 59, 59);
                startOfLast = LocalDateTime.of(year, 1, 1, 0, 0, 0);
                return specialTimeRingGrowth(start, end, startOfLast, taskResultDao, ruleId, ruleMetricId);
            } else {
                start = LocalDateTime.of(year, firstMonthOfQuarter.getValue(), 1, 0, 0, 0);
                end = LocalDateTime.of(year, endMonthOfQuarter.getValue(), endMonthOfQuarter.maxLength(), 23, 59, 59);
                startOfLast = LocalDateTime.of(year - 1, Month.OCTOBER.getValue(), 1, 0, 0, 0);
                return specialTimeRingGrowth(start, end, startOfLast, taskResultDao, ruleId, ruleMetricId);
            }
        } else if (ringType.equals(CheckTemplateEnum.MONTH_RING_GROWTH.getCode())) {
            int year = localDateTime.getYear();
            Month month = localDateTime.getMonth();
            start = LocalDateTime.of(year, month.getValue(), 1, 0, 0, 0);
            end = LocalDateTime.of(year, month.getValue(), month.maxLength(), 23, 59, 59);
            startOfLast = LocalDateTime.of(year, 1, 1, 0, 0, 0);
            return specialTimeRingGrowth(start, end, startOfLast, taskResultDao, ruleId, ruleMetricId);
        } else if (ringType.equals(CheckTemplateEnum.WEEK_RING_GROWTH.getCode())) {
            DayOfWeek dayOfWeek = localDateTime.getDayOfWeek();
            int week = dayOfWeek.getValue();
            start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).minusDays(week);
            end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).plusDays(DayOfWeek.SUNDAY.getValue() - week);
            startOfLast = start.minusWeeks(DayOfWeek.MONDAY.getValue());
            return specialTimeRingGrowth(start, end, startOfLast, taskResultDao, ruleId, ruleMetricId);
        } else if (ringType.equals(CheckTemplateEnum.DAY_RING_GROWTH.getCode())) {
            start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
            startOfLast = start.minusDays(1);
            return specialTimeRingGrowth(start, end, startOfLast, taskResultDao, ruleId, ruleMetricId);
        } else if (ringType.equals(CheckTemplateEnum.HOUR_RING_GROWTH.getCode())) {
            start = LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonthValue(), localDateTime.getDayOfMonth(), localDateTime.getHour()
                , 0, 0);
            end = start.plusHours(1);
            startOfLast = start.minusHours(1);
            return specialTimeRingGrowth(start, end, startOfLast, taskResultDao, ruleId, ruleMetricId);
        } else if (ringType.equals(CheckTemplateEnum.YEAR_ON_YEAR)) {
            return yearOnYear(localDateTime, taskResultDao, ruleId, ruleMetricId);
        } else {
            return - 1.0;
        }
    }

    private static Double yearOnYear(LocalDateTime localDateTime, TaskResultDao taskResultDao, Long ruleId, Long ruleMetricId) {
        int year = localDateTime.getYear();
        Month month = localDateTime.getMonth();

        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(year, month.getValue(), month.maxLength(), 23, 59, 59);
        LocalDateTime startOfLast = LocalDateTime.of(year - 1, month, 1, 0, 0, 0);
        LocalDateTime endOfLast = LocalDateTime.of(year - 1, month.getValue(), month.maxLength(), 23, 59, 59);

        Date startDate = Date.from(start.atZone( ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(end.atZone( ZoneId.systemDefault()).toInstant());
        Date startOfLastDate = Date.from(startOfLast.atZone( ZoneId.systemDefault()).toInstant());
        Date endOfLastDate = Date.from(endOfLast.atZone( ZoneId.systemDefault()).toInstant());

        Double avg = taskResultDao.findAvgByCreateTimeBetweenAndRuleAndRuleMetric(ExecutionManagerImpl.PRINT_TIME_FORMAT.format(startDate)
            , ExecutionManagerImpl.PRINT_TIME_FORMAT.format(endDate), ruleId, ruleMetricId);
        // Calculate pre time area.
        Double avgOfLast = taskResultDao.findAvgByCreateTimeBetweenAndRuleAndRuleMetric(ExecutionManagerImpl.PRINT_TIME_FORMAT.format(startOfLastDate)
            , ExecutionManagerImpl.PRINT_TIME_FORMAT.format(endOfLastDate), ruleId, ruleMetricId);
        // Growth.
        LOGGER.info("Finish to get this time ring.");
        return (avg - avgOfLast) / avgOfLast;
    }

    private static Double specialTimeRingGrowth(LocalDateTime start, LocalDateTime end, LocalDateTime startOfLast, TaskResultDao taskResultDao, Long ruleId
        , Long ruleMetricId) {
        Date startDate;
        Date endDate;
        Date startOfLastDate;

        LOGGER.info("Start to get this time ring.");
        startDate = Date.from(start.atZone( ZoneId.systemDefault()).toInstant());
        endDate = Date.from(end.atZone( ZoneId.systemDefault()).toInstant());
        startOfLastDate = Date.from(startOfLast.atZone( ZoneId.systemDefault()).toInstant());
        Double avgOfYear = taskResultDao.findAvgByCreateTimeBetweenAndRuleAndRuleMetric(ExecutionManagerImpl.PRINT_TIME_FORMAT.format(startDate)
            , ExecutionManagerImpl.PRINT_TIME_FORMAT.format(endDate), ruleId, ruleMetricId);
        // Calculate pre time area.
        Double avgOfLastYear = taskResultDao.findAvgByCreateTimeBetweenAndRuleAndRuleMetric(ExecutionManagerImpl.PRINT_TIME_FORMAT.format(startOfLastDate)
            , ExecutionManagerImpl.PRINT_TIME_FORMAT.format(startDate), ruleId, ruleMetricId);
        // Growth.
        LOGGER.info("Finish to get this time ring.");
        return (avgOfYear - avgOfLastYear) / avgOfLastYear;
    }

    private static Boolean moreThanThresholds(Double taskResult, Double compareValue, Double percentage) {
        if (compareValue != null) {
            if (taskResult == null) {
                return true;
            } else {
                Double maxPercentage = 1 + (percentage / 100);
                Double minPercentage = 1 - (percentage / 100);

                Double maxValue = maxPercentage * compareValue;
                Double minValue = minPercentage * compareValue;

                return taskResult <= maxValue && taskResult >= minValue;
            }
        } else {
            return false;
        }
    }

    private static Boolean moreThanThresholds(Double taskResult, Double compareValue, Integer compareType) {
        if (taskResult == null) {
            return true;
        }
        if (compareType.equals(CompareTypeEnum.EQUAL.getCode())) {
            return taskResult.equals(compareValue);
        } else if (compareType.equals(CompareTypeEnum.BIGGER.getCode())) {
            return taskResult > compareValue;
        } else if (compareType.equals(CompareTypeEnum.SMALLER.getCode())) {
            return taskResult < compareValue;
        } else if (compareType.equals(CompareTypeEnum.BIGGER_EQUAL.getCode())) {
            return taskResult >= compareValue;
        } else if (compareType.equals(CompareTypeEnum.SMALLER_EQUAL.getCode())) {
            return taskResult <= compareValue;
        } else if (compareType.equals(CompareTypeEnum.NOT_EQUAL.getCode())) {
            return !taskResult.equals(compareValue);
        }
        LOGGER.warn("Compare type is not found, {}", compareType);
        return null;
    }


}
