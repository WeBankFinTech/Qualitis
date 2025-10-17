package com.webank.wedatasphere.qualitis.dao.impl;

import com.webank.wedatasphere.qualitis.dao.ImsMetricHistorySchedulerDao;
import com.webank.wedatasphere.qualitis.dao.repository.ImsMetricHistorySchedulerRepository;
import com.webank.wedatasphere.qualitis.entity.ImsMetricHistoryScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ImsMetricHistorySchedulerDaoImpl implements ImsMetricHistorySchedulerDao {
    @Autowired
    private ImsMetricHistorySchedulerRepository imsMetricHistorySchedulerRepository;


    @Override
    public ImsMetricHistoryScheduler findLast() {
        return imsMetricHistorySchedulerRepository.findLast();
    }

    @Override
    public int countWithLastDay(String startTime, String endTime) {
        return imsMetricHistorySchedulerRepository.countWithLastDay(startTime, endTime);
    }

    @Override
    public List<ImsMetricHistoryScheduler> findByCollectStatus(String db, String table, Integer collectStatus) {
        return imsMetricHistorySchedulerRepository.findByCollectStatus(db, table, collectStatus);
    }
}
