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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Replace variable like ${yyyyMMdd} - N
 * @author howeye
 */
public class DateExprReplaceUtil {

    private DateExprReplaceUtil() {
        // Default Constructor
    }

    private static final Pattern EXPR_PATTERN = Pattern.compile("\\$\\{[\\s\\S]*?}(\\s*-\\s*[0-9]+)?");
    private static final Pattern DATE_PATTERN = Pattern.compile("\\$\\{[\\s\\S]*?}");
    private static final Pattern MINUS_PATTERN = Pattern.compile("\\s*-\\s*[0-9]+");
    private static final Pattern DIGITAL_PATTERN = Pattern.compile("[0-9]+");

    private static final Logger LOGGER = LoggerFactory.getLogger(DateExprReplaceUtil.class);

    /**
     * Expr statement replace function
     * ds=${yyyyMMdd} - 1 and
     * @param source
     * @return
     */
    public static final String replaceDateExpr(String source) {
        String str = source;
        Matcher m = EXPR_PATTERN.matcher(source);
        while (m.find()) {
            String replaceStr = m.group();
            // Get format in expression
            String format = getFormat(replaceStr);
            // Get time in expression
            Integer time = getTime(replaceStr);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            Date date = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DAY_OF_MONTH, -time);
            date = c.getTime();
            str = str.replace(replaceStr, simpleDateFormat.format(date));
        }
        LOGGER.info("Succeed to convert [{}] into [{}]", source, str);
        return str;
    }

    private static String getFormat(String replaceStr) {
        Matcher m = DATE_PATTERN.matcher(replaceStr);
        if (m.find()) {
            String dateExpr = m.group();
            return dateExpr.substring(2, dateExpr.length() - 1);
        } else {
            return null;
        }
    }

    private static Integer getTime(String replaceStr) {
        Matcher minusMatcher = MINUS_PATTERN.matcher(replaceStr);
        if (minusMatcher.find()) {
            String minusExpr = minusMatcher.group();

            Matcher digitalMatcher = DIGITAL_PATTERN.matcher(minusExpr);
            digitalMatcher.find();
            return Integer.valueOf(digitalMatcher.group());
        } else {
            return 0;
        }
    }

}
