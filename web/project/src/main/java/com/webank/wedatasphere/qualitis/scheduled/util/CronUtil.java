package com.webank.wedatasphere.qualitis.scheduled.util;

import com.google.common.collect.Maps;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.scheduled.constant.ExecuteIntervalEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

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
     * @param timeInDate     format example: 00:00:00
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

    /**
     * reverse cron expression to original text
     * Note: strongly rely on createIntervalCron()
     * @param cron
     * @return
     */
    public static String[] cronToText(String cron) throws UnExpectedRequestException {
        String[] cronUnitArray = StringUtils.split(cron, SpecCharEnum.EMPTY.getValue());
        if (cronUnitArray.length < 6) {
            throw new UnExpectedRequestException("Error cron expression.");
        }
        String timeInDate;
        Integer dateInInterval = -1;
        ExecuteIntervalEnum interval;

        String minAndSecond = padding(Integer.valueOf(cronUnitArray[1])) + SpecCharEnum.COLON.getValue() + padding(Integer.valueOf(cronUnitArray[0]));
        if (!SpecCharEnum.STAR.getValue().equals(cronUnitArray[2])) {
            timeInDate = padding(Integer.valueOf(cronUnitArray[2])) + SpecCharEnum.COLON.getValue() + minAndSecond;
        } else {
            timeInDate = minAndSecond;
        }

//        HOUR
        if (cronUnitArray[2].equals(SpecCharEnum.STAR.getValue()) && cronUnitArray[3].equals(SpecCharEnum.STAR.getValue()) && cronUnitArray[4].equals(SpecCharEnum.STAR.getValue()) && cronUnitArray[5].equals("?")) {
            interval = ExecuteIntervalEnum.HOUR;
        } else if (cronUnitArray[3].equals(SpecCharEnum.STAR.getValue()) && cronUnitArray[4].equals(SpecCharEnum.STAR.getValue()) && cronUnitArray[5].equals("?")) {
//        DAY
            interval = ExecuteIntervalEnum.DAY;
        } else if (cronUnitArray[3].equals("?") && cronUnitArray[4].equals(SpecCharEnum.STAR.getValue()) && WEEK_TRANS_EN_MAP.values().contains(cronUnitArray[5])) {
//            WEEK
            interval = ExecuteIntervalEnum.WEEK;
            Optional<Map.Entry<Integer, String>> weekEntry = WEEK_TRANS_EN_MAP.entrySet().stream().filter(entry -> entry.getValue().equals(cronUnitArray[5])).findFirst();
            if (weekEntry.isPresent()) {
                dateInInterval = weekEntry.get().getKey();
            }
        } else {
            interval = ExecuteIntervalEnum.MONTH;
            dateInInterval = Integer.valueOf(cronUnitArray[3]);
        }
        return new String[]{interval.getCode(), String.valueOf(dateInInterval), timeInDate};
    }

    private static String padding(Integer time) {
        return String.format("%02d", time);
    }

}
