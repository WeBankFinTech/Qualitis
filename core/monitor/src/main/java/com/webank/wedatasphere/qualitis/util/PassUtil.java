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
            LOGGER.error("Failed to find task result, task_id: {}, rule_id: {}", taskId, ruleId);
            return false;
        }
        Double result = taskResult.getValue();
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
        }
        LOGGER.error("CheckTemplate of alarmConfig is not supported, task_rule_alarmConfig id : {}", alarmConfig.getId());
        return false;
    }

    private static Double getWeekAvg(TaskResultDao taskResultDao, Long ruleId) {
        return getAvg(taskResultDao, Calendar.WEEK_OF_MONTH, ruleId);
    }

    private static Double getMonthAvg(TaskResultDao taskResultDao, Long ruleId) {
        return getAvg(taskResultDao, Calendar.MONTH, ruleId);
    }

    private static Double getDayAvg(TaskResultDao taskResultDao, Long ruleId) {
        return getAvg(taskResultDao, Calendar.DAY_OF_MONTH, ruleId);
    }

    private static Double getAvg(TaskResultDao taskResultDao, Integer calendarStepUnit, Long ruleId) {
        Date nowDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowDate);
        calendar.add(calendarStepUnit, -1);
        Date lastMonthDate = calendar.getTime();

        return taskResultDao.findAvgByCreateTimeBetweenAndRuleId(ExecutionManagerImpl.PRINT_TIME_FORMAT.format(lastMonthDate), ExecutionManagerImpl.PRINT_TIME_FORMAT.format(nowDate), ruleId);
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
        LOGGER.warn("CompareType is not found, {}", compareType);
        return null;
    }


}
