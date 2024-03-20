package com.webank.wedatasphere.qualitis.dao.impl;

import com.webank.wedatasphere.qualitis.dao.AlarmInfoDao;
import com.webank.wedatasphere.qualitis.dao.repository.AlarmInfoRepository;
import com.webank.wedatasphere.qualitis.entity.AlarmInfo;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

/**
 * @author allenzhou
 */
@Repository
public class AlarmInfoDaoImpl implements AlarmInfoDao {
    @Autowired
    private AlarmInfoRepository alarmInfoRepository;

    @Override
    public AlarmInfo save(AlarmInfo alarmInfo) {
        return alarmInfoRepository.save(alarmInfo);
    }

    /**
     * 用户在时间段接收的告警信息
     * @param username 用户
     * @param startAlarmTime 开始时间.yyyy-MM-dd HH:mm:ss
     * @param endAlarmTime 结束时间.yyyy-MM-dd HH:mm:ss
     * @param page 页码
     * @param size 一页获取条数
     * @return List<AlarmInfo> 告警信息列表
     */
    @Override
    public List<AlarmInfo> findAllByUsernameAndAlarmTimeBetweenPage(String username, String startAlarmTime, String endAlarmTime, int page, int size){
        Sort sort = Sort.by(Sort.Direction.DESC, "alarmTime");
        Pageable pageable = PageRequest.of(page, size, sort);
        return alarmInfoRepository.findAllByUsernameAndAlarmTimeBetween(username, startAlarmTime, endAlarmTime, pageable);
    }

    /**
     * 用户在时间段接收的告警信息
     * @param username 用户
     * @param startAlarmTime 开始时间.yyyy-MM-dd HH:mm:ss
     * @param endAlarmTime 结束时间.yyyy-MM-dd HH:mm:ss
     * @return List<AlarmInfo> 告警信息列表
     */
    @Override
    public List<AlarmInfo> findAllByUsernameAndAlarmTimeBetween(String username, String startAlarmTime, String endAlarmTime){
        return alarmInfoRepository.findAllByUsernameAndAlarmTimeBetween(username, startAlarmTime, endAlarmTime);
    }

    /**
     * 用户在时间段接收的告警信息
     * @param username 用户
     * @param startAlarmTime 开始时间.yyyy-MM-dd HH:mm:ss
     * @param endAlarmTime 结束时间.yyyy-MM-dd HH:mm:ss
     * @return 每天的各级别告警信息列表
     */
    @Override
    public List<Map<String, Object>> findAllByUsernameAndAlarmTimeBetweenPerDay(String username, String startAlarmTime, String endAlarmTime){
        return alarmInfoRepository.findAllByUsernameAndAlarmTimeBetweenPerDay(username, startAlarmTime, endAlarmTime);
    }

    /**
     * 用户在时间段接收的告警信息数量
     * @param username 用户
     * @param startAlarmTime 开始时间.yyyy-MM-dd HH:mm:ss
     * @param endAlarmTime 结束时间.yyyy-MM-dd HH:mm:ss
     * @return int 告警信息列表数量
     */
    @Override
    public long countByUsernameAndAlarmTimeBetween(String username, String startAlarmTime,
        String endAlarmTime) {
        return alarmInfoRepository.countByUsernameAndAlarmTimeBetween(username, startAlarmTime, endAlarmTime);
    }

    /**
     * 用户在时间段接收的告警信息按级别做数量统计
     * @param username 用户
     * @param startAlarmTime 开始时间.yyyy-MM-dd HH:mm:ss
     * @param endAlarmTime 结束时间.yyyy-MM-dd HH:mm:ss
     * @param level
     * @return int 告警信息列表数量
     */
    @Override
    public long countByUsernameAndAlarmTimeBetweenAndAlarmLevel(String username, String startAlarmTime, String endAlarmTime, String level) {
        return alarmInfoRepository.countByUsernameAndAlarmTimeBetweenAndAlarmLevel(username, startAlarmTime, endAlarmTime, level);
    }

    /**
     * 查找告警条数
     * @param taskId 任务ID
     * @param alarmType 告警类型
     * @return 条数
     */
    @Override
    public boolean existsByTaskIdAndAlarmType(Integer taskId, int alarmType){
        return alarmInfoRepository.existsByTaskIdAndAlarmType(taskId, alarmType);
    }

    @Override
    public List<AlarmInfo> saveAll(List<AlarmInfo> alarmInfos) {
        return alarmInfoRepository.saveAll(alarmInfos);
    }
}
