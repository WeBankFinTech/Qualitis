package com.webank.wedatasphere.qualitis.scheduled.util;

import com.google.common.collect.Maps;
import com.webank.wedatasphere.qualitis.scheduled.constant.ExecuteIntervalEnum;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-19 10:17
 * @description
 */
public class CronUtil {

    public final static String TIME_FORMAT = "HH:mm:ss";

    private static Map<Integer, String> WEEK_TRANS_EN_MAP = Maps.newConcurrentMap();

    static {
        WEEK_TRANS_EN_MAP.put(1, "MON");
        WEEK_TRANS_EN_MAP.put(2, "TUES");
        WEEK_TRANS_EN_MAP.put(3, "WED");
        WEEK_TRANS_EN_MAP.put(4, "THUR");
        WEEK_TRANS_EN_MAP.put(5, "FRI");
        WEEK_TRANS_EN_MAP.put(6, "SAT");
        WEEK_TRANS_EN_MAP.put(7, "SUN");
    }

    /**
     * @param interval
     * @param dateInInterval
     * @param timeInDate format example: 00:00:00
     * @return
     * @throws ParseException
     */
    public static String createIntervalCron(ExecuteIntervalEnum interval, Integer dateInInterval, String timeInDate) throws ParseException {
        Date date = DateUtils.parseDate(timeInDate, TIME_FORMAT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return CronUtil.createIntervalCron(interval
                , dateInInterval
                , calendar.get(Calendar.HOUR_OF_DAY)
                , calendar.get(Calendar.MINUTE)
                , calendar.get(Calendar.SECOND));
    }

    public static String createIntervalCron(ExecuteIntervalEnum interval, Integer dateInInterval, Integer hour, Integer minute, Integer seconds) {
        switch (interval) {
            case HOUR:
                return String.format("%d %d * * * ?", seconds, minute);
            case DAY:
                return String.format("%d %d %d * * ?", seconds, minute, hour);
            case WEEK:
                return String.format("%d %d %d ? * %s", seconds, minute, hour, WEEK_TRANS_EN_MAP.get(dateInInterval));
            case MONTH:
                return String.format("%d %d %d %d * ?", seconds, minute, hour, dateInInterval);
            default:
                return "";
        }
    }

}
