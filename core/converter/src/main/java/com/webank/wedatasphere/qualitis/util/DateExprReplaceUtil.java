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

import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.rule.entity.StandardValueVariables;
import com.webank.wedatasphere.qualitis.rule.entity.StandardValueVersion;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Replace variable like ${yyyyMMdd} - N
 *
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

    private static final Pattern CUSTOM_PLACEHOLODER_PATTERN = Pattern.compile("\\$\\{[^ ]*}");
    private static final Map<String, String> RUN_DATE_FORMAT = new HashMap<String, String>(2);

    static {
        RUN_DATE_FORMAT.put("run_date", "yyyyMMdd");
        RUN_DATE_FORMAT.put("run_date_std", "yyyy-MM-dd");
        RUN_DATE_FORMAT.put("run_today_h", "yyyyMMddHH");
        RUN_DATE_FORMAT.put("run_today_h_std", "yyyy-MM-dd HH");
        RUN_DATE_FORMAT.put("run_today", "yyyyMMdd");
        RUN_DATE_FORMAT.put("run_today_std", "yyyy-MM-dd");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DateExprReplaceUtil.class);

    /**
     * Expr statement replace function
     * ds=${yyyyMMdd} - 1 and
     *
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

    public static String replaceStandardValueWithinCustomSql(List<StandardValueVariables> standardValueVariablesList, Map<Long, StandardValueVersion> idAndStandardValueMap, String sql) {
        if (CollectionUtils.isEmpty(standardValueVariablesList) || MapUtils.isEmpty(idAndStandardValueMap) || StringUtils.isBlank(sql)) {
            return sql;
        }
        String customSql = sql;
        for(StandardValueVariables standardValueVariables: standardValueVariablesList) {
            if (!idAndStandardValueMap.containsKey(standardValueVariables.getStandardValueVersionId())) {
                continue;
            }
            StandardValueVersion standardValueVersion = idAndStandardValueMap.get(standardValueVariables.getStandardValueVersionId());
            customSql = customSql.replace(String.format("${%s}", standardValueVariables.getStandardValueVersionEnName()), standardValueVersion.getContent());
        }
        return customSql;
    }

    public static String replaceRunDate(Date date, String midTableAction) throws UnExpectedRequestException {
        Matcher matcher = CUSTOM_PLACEHOLODER_PATTERN.matcher(midTableAction);
        while (matcher.find()) {
            String replaceStr = matcher.group();
            boolean legalSystemParams = replaceStr.contains("run_date") || replaceStr.contains("run_date_std")
                    || replaceStr.contains("run_today") || replaceStr.contains("run_today_std") || replaceStr.contains("run_today_h_std");
            if (!legalSystemParams) {
                throw new UnExpectedRequestException("Custom placeholoder must be system variables.");
            }
            String currentParam = replaceStr.substring(2, replaceStr.length() - 1);
            String dateStr = "";
            Calendar calendar = Calendar.getInstance();
            if (currentParam.contains(SpecCharEnum.MINUS.getValue())) {
                String[] keys = currentParam.split(SpecCharEnum.MINUS.getValue());
                int forwayDay = Integer.parseInt(keys[1]);
                calendar.setTime(date);
                if ("run_today_h".equals(keys[0]) || "run_today_h_std".equals(keys[0])) {
                    calendar.add(Calendar.HOUR_OF_DAY, 0 - forwayDay);
                } else {
                    calendar.add(Calendar.DATE, 0 - forwayDay - 1);
                }
                dateStr = new SimpleDateFormat(RUN_DATE_FORMAT.get(keys[0])).format(calendar.getTime());
            } else if ("run_today_h".equals(currentParam) || "run_today_h_std".equals(currentParam) || "run_today".equals(currentParam) || "run_today_std".equals(currentParam)) {
                calendar.setTime(date);
                dateStr = new SimpleDateFormat(RUN_DATE_FORMAT.get(currentParam)).format(calendar.getTime());
            } else {
                calendar.setTime(date);
                calendar.add(Calendar.DATE, -1);
                dateStr = new SimpleDateFormat(RUN_DATE_FORMAT.get(currentParam)).format(calendar.getTime());
            }

            midTableAction = midTableAction.replace(replaceStr, dateStr);
        }

        return midTableAction;
    }

    public static String replaceFilter(Date date, String filter) throws UnExpectedRequestException {
        Matcher matcher = CUSTOM_PLACEHOLODER_PATTERN.matcher(filter);
        while (matcher.find()) {
            String replaceStr = matcher.group();
            boolean legalSystemParams = replaceStr.contains("run_date") || replaceStr.contains("run_date_std")
                    || replaceStr.contains("run_today") || replaceStr.contains("run_today_std") || replaceStr.contains("run_today_h_std");
            if (!legalSystemParams) {
                throw new UnExpectedRequestException("Custom placeholoder must be system variables.");
            }
            String currentParam = replaceStr.substring(2, replaceStr.length() - 1);
            String dateStr = "";
            Calendar calendar = Calendar.getInstance();
            if (currentParam.contains(SpecCharEnum.MINUS.getValue())) {
                String[] keys = currentParam.split(SpecCharEnum.MINUS.getValue());
                int forwayDay = Integer.parseInt(keys[1]);
                calendar.setTime(date);
                if ("run_today_h".equals(keys[0]) || "run_today_h_std".equals(keys[0])) {
                    calendar.add(Calendar.HOUR_OF_DAY, 0 - forwayDay);
                } else {
                    calendar.add(Calendar.DATE, 0 - forwayDay - 1);
                }
                dateStr = new SimpleDateFormat(RUN_DATE_FORMAT.get(keys[0])).format(calendar.getTime());
            } else if ("run_today_h".equals(currentParam) || "run_today_h_std".equals(currentParam) || "run_today".equals(currentParam) || "run_today_std".equals(currentParam)) {
                calendar.setTime(date);
                dateStr = new SimpleDateFormat(RUN_DATE_FORMAT.get(currentParam)).format(calendar.getTime());
            } else {
                calendar.setTime(date);
                calendar.add(Calendar.DATE, -1);
                dateStr = new SimpleDateFormat(RUN_DATE_FORMAT.get(currentParam)).format(calendar.getTime());
            }

            filter = filter.replace(replaceStr, dateStr);
        }

        return filter;
    }

    public static Long getDateTimeFilterSeconds(String filter) {
        Date date = new Date();
        long midnightMillis = 0;
        Matcher matcher = CUSTOM_PLACEHOLODER_PATTERN.matcher(filter);
        while (matcher.find()) {
            String replaceStr = matcher.group();
            boolean legalSystemParams = replaceStr.contains("run_date") || replaceStr.contains("run_date_std") || replaceStr.contains("run_today_h_std");
            if (!legalSystemParams) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                return calendar.getTimeInMillis() / 1000;
            }
            String currentParam = replaceStr.substring(2, replaceStr.length() - 1);
            Calendar calendar = Calendar.getInstance();
            if (currentParam.contains(SpecCharEnum.MINUS.getValue())) {
                String[] keys = currentParam.split(SpecCharEnum.MINUS.getValue());
                int forwayDay = Integer.parseInt(keys[1]);
                calendar.set(Calendar.DATE, calendar.get(Calendar.DAY_OF_MONTH) + (0 - forwayDay - 1));
            } else if ("run_today_h_std".equals(currentParam)) {
                calendar.set(Calendar.DATE, calendar.get(Calendar.DAY_OF_MONTH));
            } else {
                calendar.setTime(date);
                calendar.set(Calendar.DATE, calendar.get(Calendar.DAY_OF_MONTH) - 1);
            }
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            midnightMillis = calendar.getTimeInMillis();

        }
        return midnightMillis / 1000;
    }

    public static Long getDateTimeSeconds(String dates) {
        long dataTime = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date;
        try {
            date = dateFormat.parse(dates);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            // 当天零点时间戳
            dataTime = calendar.getTime().getTime() / 1000;
        } catch (Exception e) {
            LOGGER.error("Failed to parse date parameter.");
        }
        return dataTime;
    }
}
