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

package com.webank.wedatasphere.qualitis.project.request;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.rule.constant.CheckTemplateEnum;
import com.webank.wedatasphere.qualitis.rule.constant.CompareTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.FunctionTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.InputActionStepEnum;
import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author howeye
 */
public class CommonChecker {

    private CommonChecker() {
        // Default Constructor
    }

    public static void checkFunctionTypeEnum(Integer key) throws UnExpectedRequestException {
        for (FunctionTypeEnum e : FunctionTypeEnum.values()) {
            if (e.getCode().equals(key)) {
                return;
            }
        }
        throw new UnExpectedRequestException("Function type {&IS_NOT_SUPPORT}");
    }

    public static void checkCompareType(Integer key) throws UnExpectedRequestException {
        for (CompareTypeEnum e : CompareTypeEnum.values()) {
            if (e.getCode().equals(key)) {
                return;
            }
        }
        throw new UnExpectedRequestException("CompareType {&IS_NOT_SUPPORT}");
    }

    public static void checkCheckTemplate(Integer key) throws UnExpectedRequestException {
        for (CheckTemplateEnum e : CheckTemplateEnum.values()) {
            if (e.getCode().equals(key)) {
                return;
            }
        }
        throw new UnExpectedRequestException("CheckTemplate {&IS_NOT_SUPPORT}");
    }

    public static void checkStringLength(String str, Integer maxLength, String objName) throws UnExpectedRequestException {
        if (StringUtils.isBlank(str) || str.length() <= maxLength) {
            return;
        }
        throw new UnExpectedRequestException("{&THE_LENGTH_OF} [" + objName + "] {&CAN_NOT_LARGER_THAN_THE_MAX_LENGTH}, [" + maxLength + "]");
    }

    public static void checkListSize(List<?> list, Integer maxSize, String listName) throws UnExpectedRequestException {
        if (list == null || list.size() <= maxSize) {
            return;
        }
        throw new UnExpectedRequestException("{&THE_SIZE_OF} [" + listName + "] {&CAN_NOT_LARGER_THAN_THE_MAX_SIZE}, [" + maxSize + "]");
    }

    public static void checkListMinSize(List<?> list, Integer minSize, String listName) throws UnExpectedRequestException {
        if (list == null || list.size() >= minSize) {
            return;
        }
        throw new UnExpectedRequestException("{&THE_SIZE_OF} [" + listName + "] {&CAN_NOT_LESS_THAN_THE_MIN_SIZE}, [" + minSize + "]");
    }

    public static void checkActionStepEnum(Integer key) throws UnExpectedRequestException {
        for (InputActionStepEnum e : InputActionStepEnum.values()) {
            if (e.getCode().equals(key)) {
                return;
            }
        }
        throw new UnExpectedRequestException("action_step {&IS_NOT_SUPPORT}");
    }

    public static void checkString(String str, String strName) throws UnExpectedRequestException {
        if (StringUtils.isBlank(str)) {
            throw new UnExpectedRequestException(strName + " {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
    }

    public static void checkObject(Object obj, String objName) throws UnExpectedRequestException {
        if (null == obj) {
            throw new UnExpectedRequestException(objName + " {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
    }

    public static Date checkDateStringPattern(String propertyValue, String datePattern, String propertyName)
        throws UnExpectedRequestException {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
            return simpleDateFormat.parse(propertyValue);
        } catch (ParseException e) {
            throw new UnExpectedRequestException(propertyName + " {&CAN_NOT_MATCH_DATE_FORMAT_PATTERN} : " + datePattern);
        }
    }

    public static void checkCollections(Collection<?> collections, String objName) throws UnExpectedRequestException {
        if (CollectionUtils.isEmpty(collections)) {
            throw new UnExpectedRequestException(objName + " {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
    }

    public static boolean compareIdentical(Boolean unionAll, Boolean abortOnFailure, Boolean specifyStaticStartupParam, String staticStartupParam, String abnormalDatabase
            , String cluster, Boolean alert, Integer alertLevel, String alertReceiver, String abnormalProxyUser, Boolean deleteFailCheckResult, Boolean uploadRuleMetricValue, Boolean uploadAbnormalValue, ExecutionParameters executionParameters) {
        boolean flag = false;
        boolean compareEqual = ((abortOnFailure == null && executionParameters.getAbortOnFailure() == null) || (abortOnFailure != null && abortOnFailure.equals(executionParameters.getAbortOnFailure()))) &&
                ((specifyStaticStartupParam == null && executionParameters.getSpecifyStaticStartupParam() == null) || (specifyStaticStartupParam != null && specifyStaticStartupParam.equals(executionParameters.getSpecifyStaticStartupParam()))) &&
                (StringUtils.isNotBlank(staticStartupParam) ? staticStartupParam : "").equals(StringUtils.isNotBlank(executionParameters.getStaticStartupParam()) ? executionParameters.getStaticStartupParam() : "") &&
                (StringUtils.isNotBlank(abnormalDatabase) ? abnormalDatabase : "").equals(StringUtils.isNotBlank(executionParameters.getAbnormalDatabase()) ? executionParameters.getAbnormalDatabase() : "") &&
                (StringUtils.isNotBlank(cluster) ? cluster : "").equals(StringUtils.isNotBlank(executionParameters.getCluster()) ? executionParameters.getCluster() : "") &&
                ((alert == null && executionParameters.getAlert() == null) || (alert != null && alert.equals(executionParameters.getAlert()))) &&
                (alertLevel != null ? alertLevel : "").equals(executionParameters.getAlertLevel() != null ? executionParameters.getAlertLevel() : "") &&
                (StringUtils.isNotBlank(alertReceiver) ? alertReceiver : "").equals(StringUtils.isNotBlank(executionParameters.getAlertReceiver()) ? executionParameters.getAlertReceiver() : "") &&
                (StringUtils.isNotBlank(abnormalProxyUser) ? abnormalProxyUser : "").equals(StringUtils.isNotBlank(executionParameters.getAbnormalProxyUser()) ? executionParameters.getAbnormalProxyUser() : "") &&
                ((deleteFailCheckResult == null && executionParameters.getDeleteFailCheckResult() == null) || (deleteFailCheckResult != null && deleteFailCheckResult.equals(executionParameters.getDeleteFailCheckResult()))) &&
                ((uploadRuleMetricValue == null && executionParameters.getUploadRuleMetricValue() == null) || (uploadRuleMetricValue != null && uploadRuleMetricValue.equals(executionParameters.getUploadRuleMetricValue()))) &&
                ((uploadAbnormalValue == null && executionParameters.getUploadAbnormalValue() == null) || (uploadAbnormalValue != null && uploadAbnormalValue.equals(executionParameters.getUploadAbnormalValue()))) &&
                ((unionAll == null && executionParameters.getUnionAll() == null) || (unionAll != null && unionAll.equals(executionParameters.getUnionAll())));
        if (compareEqual) {
            flag = true;
        }
        return flag;
    }

    public static void checkIntegerMaxLength(Integer propertyValue, Integer maxLength, String propertyName) throws UnExpectedRequestException {
        if (null == propertyValue) {
            return;
        }
        if (propertyValue > maxLength) {
            throw new UnExpectedRequestException(propertyName + " {&EXCEED_MAX_LENGTH}");
        }
    }

    public static void checkMatcher(String regex, String propertyValue) throws UnExpectedRequestException {
        if (null == regex || null == propertyValue) {
            return;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(propertyValue);
        if (!matcher.matches()) {
            throw new UnExpectedRequestException(propertyValue + " {&NOT_MATCH_OF_REGEX}");
        }
    }

}
