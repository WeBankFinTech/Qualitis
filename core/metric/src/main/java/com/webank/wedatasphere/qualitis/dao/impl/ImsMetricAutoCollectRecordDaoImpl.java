package com.webank.wedatasphere.qualitis.dao.impl;

import com.webank.wedatasphere.qualitis.dao.ImsMetricAutoCollectRecordDao;
import com.webank.wedatasphere.qualitis.dao.repository.ImsMetricAutoCollectRecordRepository;
import com.webank.wedatasphere.qualitis.entity.ImsMetricAutoCollectRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ImsMetricAutoCollectRecordDaoImpl implements ImsMetricAutoCollectRecordDao {
    @Autowired
    private ImsMetricAutoCollectRecordRepository imsMetricAutoCollectRecordRepository;

    @Override
    public List<ImsMetricAutoCollectRecord> findByStatus(Integer status) {
        return imsMetricAutoCollectRecordRepository.findByStatus(status);
    }

    @Override
    public void saveAll(List<ImsMetricAutoCollectRecord> noPermissionRecordList) {
        imsMetricAutoCollectRecordRepository.saveAll(noPermissionRecordList);
    }

    @Override
    public void save(ImsMetricAutoCollectRecord imsMetricAutoCollectRecord) {
        imsMetricAutoCollectRecordRepository.save(imsMetricAutoCollectRecord);
    }

    @Override
    public ImsMetricAutoCollectRecord findByConditions(String dbName, String tableName, String proxyUser) {
        return imsMetricAutoCollectRecordRepository.findByConditions(dbName, tableName, proxyUser);
    }

    @Override
    public List<ImsMetricAutoCollectRecord> findByConditions(String startDate, String endDate, List<String> proxyUserNames) {
        return imsMetricAutoCollectRecordRepository.findByConditions(startDate, endDate, proxyUserNames);
    }

}
