package com.webank.wedatasphere.qualitis.dao;

import com.webank.wedatasphere.qualitis.entity.AlarmInfo;
import java.util.List;
import java.util.Map;

/**
 * @author allenzhou
 */
public interface AlarmInfoDao {
    /**
     * 保存alarmInfo
     * @param alarmInfo 要保存的参数
     * @return 保存成功的返回值
     */
    AlarmInfo save(AlarmInfo alarmInfo);

    /**
     * 用户在时间段接收的告警分页信息
     * @param username 用户
     * @param startAlarmTime 开始时间.yyyy-MM-dd HH:mm:ss
     * @param endAlarmTime 结束时间.yyyy-MM-dd HH:mm:ss
     * @param page 页码
     * @param size 一页获取数量
     * @return 用户在时间段接收的告警分页信息
     */
    List<AlarmInfo> findAllByUsernameAndAlarmTimeBetweenPage(String username, String startAlarmTime, String endAlarmTime, int page, int size);

    /**
     * 用户在时间段接收的告警信息
     * @param username 用户
     * @param startAlarmTime 开始时间.yyyy-MM-dd HH:mm:ss
     * @param endAlarmTime 结束时间.yyyy-MM-dd HH:mm:ss
     * @return 用户在时间段接收的告警信息
     */
    List<AlarmInfo> findAllByUsernameAndAlarmTimeBetween(String username, String startAlarmTime,
        String endAlarmTime);

    /**
     * 用户在时间段接收的告警信息
     * @param username
     * @param startAlarmTime
     * @param endAlarmTime
     * @return
     */
    List<Map<String, Object>> findAllByUsernameAndAlarmTimeBetweenPerDay(String username, String startAlarmTime, String endAlarmTime);

    /**
     * 用户在时间段接收的告警信息总数
     * @param username 用户
     * @param startAlarmTime 开始时间.yyyy-MM-dd HH:mm:ss
     * @param endAlarmTime 结束时间.yyyy-MM-dd HH:mm:ss
     * @return 用户在时间段接收的告警信息总数
     */
    long countByUsernameAndAlarmTimeBetween(String username, String startAlarmTime, String endAlarmTime);

    /**
     * 用户在时间段接收的告警信息按级别做数量统计
     * @param username
     * @param startAlarmTime
     * @param endAlarmTime
     * @param level
     * @return
     */
    long countByUsernameAndAlarmTimeBetweenAndAlarmLevel(String username, String startAlarmTime, String endAlarmTime, String level);

    /**
     * 查找是否告警
     * @param taskId 任务ID
     * @param alarmType 告警类型
     * @return 是否存在
     */
    boolean existsByTaskIdAndAlarmType(Integer taskId, int alarmType);

    /**
     * 批量保存alarmInfo
     * @param alarmInfos 要保存的参数
     * @return 保存成功的返回值
     */
    List<AlarmInfo> saveAll(List<AlarmInfo> alarmInfos);
}
