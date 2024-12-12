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

import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.dao.TaskResultDao;
import com.webank.wedatasphere.qualitis.entity.TaskResult;
import com.webank.wedatasphere.qualitis.entity.TaskRuleAlarmConfig;
import com.webank.wedatasphere.qualitis.exception.ArgumentException;
import com.webank.wedatasphere.qualitis.rule.constant.CheckTemplateEnum;
import com.webank.wedatasphere.qualitis.rule.constant.CompareTypeEnum;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
        Date nowDate = new Date();
        if (checkTemplate.equals(CheckTemplateEnum.MONTH_FLUCTUATION.getCode())) {
            long existValueNums = countValue(nowDate, taskResultDao, Calendar.MONTH, taskResult.getRuleId(), taskResult.getRuleMetricId(), taskResult.getApplicationId());
            if (existValueNums <= 0) {
                return true;
            }
            Double monthAvg = getMonthAvg(nowDate, taskResultDao, taskResult.getRuleId(), taskResult.getRuleMetricId(), taskResult.getApplicationId());
            taskResult.setCompareValue(monthAvg != null ? String.valueOf(monthAvg) : "");
            taskResultDao.saveTaskResult(taskResult);
            return moreThanThresholds(result, monthAvg, thresholds);
        } else if (checkTemplate.equals(CheckTemplateEnum.WEEK_FLUCTUATION.getCode())) {
            long existValueNums = countValue(nowDate, taskResultDao, Calendar.WEEK_OF_MONTH, taskResult.getRuleId(), taskResult.getRuleMetricId(), taskResult.getApplicationId());
            if (existValueNums <= 0) {
                return true;
            }
            Double weekAvg = getWeekAvg(nowDate, taskResultDao, taskResult.getRuleId(), taskResult.getRuleMetricId(), taskResult.getApplicationId());
            taskResult.setCompareValue(weekAvg != null ? String.valueOf(weekAvg) : "");
            taskResultDao.saveTaskResult(taskResult);
            return moreThanThresholds(result, weekAvg, thresholds);
        } else if (checkTemplate.equals(CheckTemplateEnum.DAY_FLUCTUATION.getCode())) {
            long existValueNums = countValue(nowDate, taskResultDao, Calendar.DAY_OF_MONTH, taskResult.getRuleId(), taskResult.getRuleMetricId(), taskResult.getApplicationId());
            if (existValueNums <= 0) {
                return true;
            }
            Double dayAvg = getDayAvg(nowDate, taskResultDao, taskResult.getRuleId(), taskResult.getRuleMetricId(), taskResult.getApplicationId());
            taskResult.setCompareValue(dayAvg != null ? String.valueOf(dayAvg) : "");
            taskResultDao.saveTaskResult(taskResult);
            return moreThanThresholds(result, dayAvg, thresholds);
        } else if (checkTemplate.equals(CheckTemplateEnum.FIXED_VALUE.getCode())) {
            Integer compareType = alarmConfig.getCompareType();
            return moreThanThresholds(result, thresholds, compareType);
        } else {
            // Ring growth.
            Integer compareType = alarmConfig.getCompareType();
            thresholds /= 100;
            try {
                Map<String, String> comparedValueMap = new HashMap<>(2);
                result = getRingGrowth(taskResultDao, checkTemplate, taskResult.getRuleId(), taskResult.getRuleMetricId(), taskResult.getApplicationId(), comparedValueMap);
                StringBuilder computeRex = new StringBuilder();
                String avgOfLastCycle = StringUtils.isNotEmpty(comparedValueMap.get(QualitisConstants.AVG_OF_LAST_CYCLE)) ? comparedValueMap.get(QualitisConstants.AVG_OF_LAST_CYCLE) : "";
                String avgOfCurrent = StringUtils.isNotEmpty(comparedValueMap.get(QualitisConstants.AVG_OF_CURRENT)) ? comparedValueMap.get(QualitisConstants.AVG_OF_CURRENT) : "";
                if (StringUtils.isNotEmpty(avgOfLastCycle) && StringUtils.isNotEmpty(avgOfCurrent)) {
                    computeRex.append("(").append(avgOfCurrent).append(" - ").append(avgOfLastCycle).append(")").append("/").append(avgOfLastCycle);
                    taskResult.setCompareValue(computeRex.toString());
                    taskResultDao.saveTaskResult(taskResult);
                }
            } catch (ArgumentException e) {
                LOGGER.info("When first check ring growth, pass it.");
                return true;
            }

            if (result != null && result.equals(Double.NaN)) {
                result = 0.0;
            }
            return moreThanThresholds(result, thresholds, compareType);
        }
    }

    private static Double getMonthAvg(Date nowDate, TaskResultDao taskResultDao, Long ruleId, Long ruleMetricId, String applicationId) {
        return getAvg(nowDate, taskResultDao, Calendar.MONTH, ruleId, ruleMetricId, applicationId);
    }

    private static Double getWeekAvg(Date nowDate, TaskResultDao taskResultDao, Long ruleId, Long ruleMetricId, String applicationId) {
        return getAvg(nowDate, taskResultDao, Calendar.WEEK_OF_MONTH, ruleId, ruleMetricId, applicationId);
    }

    private static Double getDayAvg(Date nowDate, TaskResultDao taskResultDao, Long ruleId, Long ruleMetricId, String applicationId) {
        return getAvg(nowDate, taskResultDao, Calendar.DAY_OF_MONTH, ruleId, ruleMetricId, applicationId);
    }

    private static Double getAvg(Date nowDate, TaskResultDao taskResultDao, Integer calendarStepUnit, Long ruleId, Long ruleMetricId, String applicationId) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowDate);
        calendar.add(calendarStepUnit, -1);
        Date lastMonthDate = calendar.getTime();

        return taskResultDao.findAvgByCreateTimeBetweenAndRuleAndMetricAndApplication(QualitisConstants.PRINT_DATE_FORMAT.format(lastMonthDate), QualitisConstants.PRINT_DATE_FORMAT.format(nowDate), ruleId, ruleMetricId, applicationId);
    }

    private static long countValue(Date nowDate, TaskResultDao taskResultDao, Integer calendarStepUnit, Long ruleId, Long ruleMetricId, String applicationId) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowDate);
        calendar.add(calendarStepUnit, -1);
        Date lastMonthDate = calendar.getTime();

        return taskResultDao.countByCreateTimeBetweenAndRuleAndMetricAndApplication(QualitisConstants.PRINT_DATE_FORMAT.format(lastMonthDate), QualitisConstants.PRINT_DATE_FORMAT.format(nowDate), ruleId, ruleMetricId, applicationId);
    }

    private static Double getRingGrowth(TaskResultDao taskResultDao, Integer ringType, Long ruleId, Long ruleMetricId, String applicationId, Map<String, String> comparedValueMap)
            throws ArgumentException {
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime start;
        LocalDateTime end;
        LocalDateTime startOfLast;

        if (ringType.equals(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode())) {
            int year = localDateTime.getYear();
            // Location current time area and calculate avg.
            start = LocalDateTime.of(year, 1, 1, 0, 0, 0);
            end = LocalDateTime.of(year, 12, 31, 23, 59, 59);
            startOfLast = LocalDateTime.of(year - 1, 1, 1, 0, 0, 0);
            return specialTimeRingGrowth(start, end, startOfLast, taskResultDao, ruleId, ruleMetricId, applicationId, comparedValueMap);

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
            return specialTimeRingGrowth(start, end, startOfLast, taskResultDao, ruleId, ruleMetricId, applicationId, comparedValueMap);
        } else if (ringType.equals(CheckTemplateEnum.SEASON_RING_GROWTH.getCode())) {
            int year = localDateTime.getYear();
            Month month = localDateTime.getMonth();
            Month firstMonthOfQuarter = month.firstMonthOfQuarter();
            Month endMonthOfQuarter = Month.of(firstMonthOfQuarter.getValue() + 2);
            if (month.getValue() >= Month.MARCH.getValue()) {
                start = LocalDateTime.of(year, firstMonthOfQuarter.getValue(), 1, 0, 0, 0);
                end = LocalDateTime.of(year, endMonthOfQuarter.getValue(), endMonthOfQuarter.maxLength(), 23, 59, 59);
                startOfLast = LocalDateTime.of(year, 1, 1, 0, 0, 0);
                return specialTimeRingGrowth(start, end, startOfLast, taskResultDao, ruleId, ruleMetricId, applicationId, comparedValueMap);
            } else {
                start = LocalDateTime.of(year, firstMonthOfQuarter.getValue(), 1, 0, 0, 0);
                end = LocalDateTime.of(year, endMonthOfQuarter.getValue(), endMonthOfQuarter.maxLength(), 23, 59, 59);
                startOfLast = LocalDateTime.of(year - 1, Month.OCTOBER.getValue(), 1, 0, 0, 0);
                return specialTimeRingGrowth(start, end, startOfLast, taskResultDao, ruleId, ruleMetricId, applicationId, comparedValueMap);
            }
        } else if (ringType.equals(CheckTemplateEnum.MONTH_RING_GROWTH.getCode())) {
            int year = localDateTime.getYear();
            Month month = localDateTime.getMonth();
            start = LocalDateTime.of(year, month.getValue(), 1, 0, 0, 0);
            end = LocalDateTime.of(year, month.getValue(), month.maxLength(), 23, 59, 59);
            startOfLast = LocalDateTime.of(year, 1, 1, 0, 0, 0);
            return specialTimeRingGrowth(start, end, startOfLast, taskResultDao, ruleId, ruleMetricId, applicationId, comparedValueMap);
        } else if (ringType.equals(CheckTemplateEnum.WEEK_RING_GROWTH.getCode())) {
            DayOfWeek dayOfWeek = localDateTime.getDayOfWeek();
            int week = dayOfWeek.getValue();
            start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).minusDays(week);
            end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).plusDays(Long.parseLong((DayOfWeek.SUNDAY.getValue() - week) + ""));
            startOfLast = start.minusWeeks(DayOfWeek.MONDAY.getValue());
            return specialTimeRingGrowth(start, end, startOfLast, taskResultDao, ruleId, ruleMetricId, applicationId, comparedValueMap);
        } else if (ringType.equals(CheckTemplateEnum.DAY_RING_GROWTH.getCode())) {
            start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
            startOfLast = start.minusDays(1);
            return specialTimeRingGrowth(start, end, startOfLast, taskResultDao, ruleId, ruleMetricId, applicationId, comparedValueMap);
        } else if (ringType.equals(CheckTemplateEnum.HOUR_RING_GROWTH.getCode())) {
            start = LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonthValue(), localDateTime.getDayOfMonth(), localDateTime.getHour()
                    , 0, 0);
            end = start.plusHours(1);
            startOfLast = start.minusHours(1);
            return specialTimeRingGrowth(start, end, startOfLast, taskResultDao, ruleId, ruleMetricId, applicationId, comparedValueMap);
        } else if (ringType.equals(CheckTemplateEnum.YEAR_ON_YEAR)) {
            return yearOnYear(localDateTime, taskResultDao, ruleId, ruleMetricId);
        } else {
            return -1.0;
        }
    }

    private static Double yearOnYear(LocalDateTime localDateTime, TaskResultDao taskResultDao, Long ruleId, Long ruleMetricId) {
        int year = localDateTime.getYear();
        Month month = localDateTime.getMonth();

        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(year, month.getValue(), month.maxLength(), 23, 59, 59);
        LocalDateTime startOfLast = LocalDateTime.of(year - 1, month, 1, 0, 0, 0);
        LocalDateTime endOfLast = LocalDateTime.of(year - 1, month.getValue(), month.maxLength(), 23, 59, 59);

        Date startDate = Date.from(start.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(end.atZone(ZoneId.systemDefault()).toInstant());
        Date startOfLastDate = Date.from(startOfLast.atZone(ZoneId.systemDefault()).toInstant());
        Date endOfLastDate = Date.from(endOfLast.atZone(ZoneId.systemDefault()).toInstant());

        Double avg = taskResultDao.findAvgByCreateTimeBetweenAndRuleAndRuleMetric(QualitisConstants.PRINT_TIME_FORMAT.format(startDate)
                , QualitisConstants.PRINT_TIME_FORMAT.format(endDate), ruleId, ruleMetricId);
        // Calculate pre time area.
        Double avgOfLast = taskResultDao.findAvgByCreateTimeBetweenAndRuleAndRuleMetric(QualitisConstants.PRINT_TIME_FORMAT.format(startOfLastDate)
                , QualitisConstants.PRINT_TIME_FORMAT.format(endOfLastDate), ruleId, ruleMetricId);
        // Growth.
        LOGGER.info("Finish to get this time ring.");
        return (avg - avgOfLast) / avgOfLast;
    }

    private static Double specialTimeRingGrowth(LocalDateTime start, LocalDateTime end, LocalDateTime startOfLast, TaskResultDao taskResultDao, Long ruleId
            , Long ruleMetricId, String applicationId, Map<String, String> comparedValueMap) throws ArgumentException {
        Date startDate;
        Date endDate;
        Date startOfLastDate;

        LOGGER.info("Start to get this time ring.");
        startDate = Date.from(start.atZone(ZoneId.systemDefault()).toInstant());
        endDate = Date.from(end.atZone(ZoneId.systemDefault()).toInstant());
        startOfLastDate = Date.from(startOfLast.atZone(ZoneId.systemDefault()).toInstant());
        Double avgOfCurrent = taskResultDao.findAvgByCreateTimeBetweenAndRuleAndRuleMetric(QualitisConstants.PRINT_TIME_FORMAT.format(startDate)
                , QualitisConstants.PRINT_TIME_FORMAT.format(endDate), ruleId, ruleMetricId);
        if (avgOfCurrent == null) {
            avgOfCurrent = Double.valueOf("0");
            LOGGER.info("Avg of current is null, return 0.");
        }
        // Calculate pre time area.
        // Check for the previous cycle records
        long recordNumber = taskResultDao.countByCreateTimeBetweenAndRuleAndRuleMetric(QualitisConstants.PRINT_TIME_FORMAT.format(startOfLastDate)
                , QualitisConstants.PRINT_TIME_FORMAT.format(startDate), ruleId, ruleMetricId, applicationId);

        if (recordNumber > 0) {
            Double avgOfLastCycle = taskResultDao.findAvgByCreateTimeBetweenAndRuleAndRuleMetric(QualitisConstants.PRINT_TIME_FORMAT.format(startOfLastDate)
                    , QualitisConstants.PRINT_TIME_FORMAT.format(startDate), ruleId, ruleMetricId);
            if (avgOfLastCycle == null) {
                avgOfLastCycle = Double.valueOf("0");
                LOGGER.info("Last cycle is null, return 0.");
            }
            // Growth.
            LOGGER.info("Finish to get this time ring.");
            comparedValueMap.put(QualitisConstants.AVG_OF_LAST_CYCLE, String.valueOf(avgOfLastCycle));
            comparedValueMap.put(QualitisConstants.AVG_OF_CURRENT, String.valueOf(avgOfCurrent));
            return (avgOfCurrent - avgOfLastCycle) / avgOfLastCycle;
        } else {
            long recordNumberOfPast = taskResultDao.countByCreateTimeBetweenAndRuleAndRuleMetric("2019-01-01 00:00:00", QualitisConstants.PRINT_TIME_FORMAT.format(startDate)
                    , ruleId, ruleMetricId, applicationId);
            Double avgOfLastCycle = 0.0;
            if (recordNumberOfPast > 0) {
                LOGGER.info("No records in the previous cycle but not first execution.");
                comparedValueMap.put(QualitisConstants.AVG_OF_LAST_CYCLE, String.valueOf(avgOfLastCycle));
                comparedValueMap.put(QualitisConstants.AVG_OF_CURRENT, String.valueOf(avgOfCurrent));
                //Make sure "avgOfLastCycle" can't be zero before doing this division (不做判断，sonar扫描会出错)
                //return (avgOfCurrent - avgOfLastCycle) / avgOfLastCycle; 原代码
                return avgOfLastCycle;
            } else {
                LOGGER.info("No records before.");
                throw new ArgumentException("No records before");
            }
        }
    }

    private static Boolean moreThanThresholds(Double taskResult, Double compareValue, Double percentage) {
        if (compareValue != null) {
            if (taskResult == null) {
                return true;
            } else {
                if (percentage < 0) {
                    percentage = 1 - percentage;
                }
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
            return BigDecimal.valueOf(taskResult).compareTo(BigDecimal.valueOf(compareValue)) == 0;
        } else if (compareType.equals(CompareTypeEnum.BIGGER.getCode())) {
            return taskResult > compareValue;
        } else if (compareType.equals(CompareTypeEnum.SMALLER.getCode())) {
            return taskResult < compareValue;
        } else if (compareType.equals(CompareTypeEnum.BIGGER_EQUAL.getCode())) {
            return taskResult >= compareValue;
        } else if (compareType.equals(CompareTypeEnum.SMALLER_EQUAL.getCode())) {
            return taskResult <= compareValue;
        } else if (compareType.equals(CompareTypeEnum.NOT_EQUAL.getCode())) {
            return BigDecimal.valueOf(taskResult).compareTo(BigDecimal.valueOf(compareValue)) != 0;
        }
        LOGGER.warn("Compare type is not found, {}", compareType);
        return false;
    }

}
