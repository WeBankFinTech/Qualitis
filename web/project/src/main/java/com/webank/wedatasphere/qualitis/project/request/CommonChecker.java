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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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

    public static void checkListSize(List list, Integer maxSize, String listName) throws UnExpectedRequestException {
        if (list == null || list.size() <= maxSize) {
            return;
        }
        throw new UnExpectedRequestException("{&THE_SIZE_OF} [" + listName + "] {&CAN_NOT_LARGER_THAN_THE_MAX_SIZE}, [" + maxSize + "]");
    }

    public static void checkListMinSize(List list, Integer minSize, String listName) throws UnExpectedRequestException {
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

    public static void checkCollections(Collection collections, String objName) throws UnExpectedRequestException {
        if (CollectionUtils.isEmpty(collections)) {
            throw new UnExpectedRequestException(objName + " {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
    }
}
