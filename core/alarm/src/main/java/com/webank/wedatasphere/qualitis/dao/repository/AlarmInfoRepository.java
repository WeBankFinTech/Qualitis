package com.webank.wedatasphere.qualitis.dao.repository;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Pageable;
import com.webank.wedatasphere.qualitis.entity.AlarmInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author allenzhou
 */
public interface AlarmInfoRepository  extends JpaRepository<AlarmInfo, Long> {
    /**
     * 用户在时间段接收的告警信息
     * @param username 用户
     * @param startAlarmTime 开始时间.yyyy-MM-dd HH:mm:ss
     * @param endAlarmTime 结束时间.yyyy-MM-dd HH:mm:ss
     * @return List<AlarmInfo> 告警信息列表
     */
    List<AlarmInfo> findAllByUsernameAndAlarmTimeBetween(String username, String startAlarmTime, String endAlarmTime);

    /**
     * 用户在时间段接收的告警信息
     * @param username 用户
     * @param startAlarmTime 开始时间.yyyy-MM-dd HH:mm:ss
     * @param endAlarmTime 结束时间.yyyy-MM-dd HH:mm:ss
     * @return 每天的各级别告警信息列表
     */
    @Query(value = "select new map(DATE_FORMAT(a.beginTime, '%Y-%m-%d') as alarm_day, a.alarmLevel as alarm_level, count(a.id) as alarm_count) from AlarmInfo a where a.username = ?1 and beginTime BETWEEN ?2 AND ?3 group by DATE_FORMAT(a.beginTime, '%Y-%m-%d'), a.alarmLevel")
    List<Map<String, Object>> findAllByUsernameAndAlarmTimeBetweenPerDay(String username, String startAlarmTime, String endAlarmTime);

    /**
     * 用户在时间段接收的告警信息
     * @param username 用户
     * @param startAlarmTime 开始时间.yyyy-MM-dd HH:mm:ss
     * @param endAlarmTime 结束时间.yyyy-MM-dd HH:mm:ss
     * @param pageable 分页参数
     * @return List<AlarmInfo> 告警信息列表
     */
    List<AlarmInfo> findAllByUsernameAndAlarmTimeBetween(String username, String startAlarmTime, String endAlarmTime, Pageable pageable);

    /**
     * 用户在时间段接收的告警信息按级别做数量统计
     * @param username 用户
     * @param startAlarmTime 开始时间.yyyy-MM-dd HH:mm:ss
     * @param endAlarmTime 结束时间.yyyy-MM-dd HH:mm:ss
     * @param alarmLevel
     * @return int 告警信息列表数量
     */
    long countByUsernameAndAlarmTimeBetweenAndAlarmLevel(String username, String startAlarmTime, String endAlarmTime, String alarmLevel);

    /**
     * 用户在时间段接收的告警信息数量
     * @param username 用户
     * @param startAlarmTime 开始时间.yyyy-MM-dd HH:mm:ss
     * @param endAlarmTime 结束时间.yyyy-MM-dd HH:mm:ss
     * @return int 告警信息列表数量
     */
    long countByUsernameAndAlarmTimeBetween(String username, String startAlarmTime, String endAlarmTime);

    /**
     * 查找告警条数
     * @param taskId 任务ID
     * @param alarmType 告警类型
     * @return 条数
     */
    boolean existsByTaskIdAndAlarmType(Integer taskId, int alarmType);
}
