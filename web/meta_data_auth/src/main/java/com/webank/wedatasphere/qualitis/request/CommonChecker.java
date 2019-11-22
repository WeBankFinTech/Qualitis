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

package com.webank.wedatasphere.qualitis.request;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.rule.constant.InputActionStepEnum;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author howeye
 */
public class CommonChecker {

    private CommonChecker() {
        // Default Constructor
    }

    public static void checkStringLength(String str, Integer maxLength, String objName) throws UnExpectedRequestException {
        if (StringUtils.isBlank(str) || str.length() <= maxLength) {
            return;
        }
        throw new UnExpectedRequestException("The length of [" + objName + "] can not larger than the max length, [" + maxLength + "]");
    }

    public static void checkListSize(List list, Integer maxSize, String listName) throws UnExpectedRequestException {
        if (list == null || list.size() <= maxSize) {
            return;
        }
        throw new UnExpectedRequestException("The size of [" + listName + "] can not larger than the max size, [" + maxSize + "]");
    }

    public static void checkActionStepEnum(Integer key) throws UnExpectedRequestException {
        for (InputActionStepEnum e : InputActionStepEnum.values()) {
            if (e.getCode().equals(key)) {
                return;
            }
        }
        throw new UnExpectedRequestException("action_step is not support");
    }

    public static void checkString(String str, String strName) throws UnExpectedRequestException {
        if (StringUtils.isBlank(str)) {
            throw new UnExpectedRequestException(strName + " 不能是空内容");
        }
    }

    public static void checkObject(Object obj, String objName) throws UnExpectedRequestException {
        if (null == obj) {
            throw new UnExpectedRequestException(objName + " 不能为空");
        }
    }

    public static Date checkDateStringPattern(String propertyValue, String datePattern, String propertyName)
        throws UnExpectedRequestException {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
            return simpleDateFormat.parse(propertyValue);
        } catch (ParseException e) {
            throw new UnExpectedRequestException(propertyName + " can not match date format pattern : " + datePattern);
        }
    }
}
