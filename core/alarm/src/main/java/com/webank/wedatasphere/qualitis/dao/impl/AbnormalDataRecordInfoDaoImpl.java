package com.webank.wedatasphere.qualitis.dao.impl;

import com.webank.wedatasphere.qualitis.dao.AbnormalDataRecordInfoDao;
import com.webank.wedatasphere.qualitis.dao.repository.AbnormalDataRecordInfoRepository;
import com.webank.wedatasphere.qualitis.entity.AbnormalDataRecordInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

/**
 * @author allenzhou@webank.com
 * @date 2021/10/5 12:20
 */
@Repository
public class AbnormalDataRecordInfoDaoImpl implements AbnormalDataRecordInfoDao {
    @Autowired
    private AbnormalDataRecordInfoRepository abnormalDataRecordInfoRepository;

    @Override
    public AbnormalDataRecordInfo save(AbnormalDataRecordInfo abnormalDataRecordInfo) {
        return abnormalDataRecordInfoRepository.save(abnormalDataRecordInfo);
    }

    @Override
    public List<AbnormalDataRecordInfo> saveAll(List<AbnormalDataRecordInfo> abnormalDataRecordInfos) {
        return abnormalDataRecordInfoRepository.saveAll(abnormalDataRecordInfos);
    }

    @Override
    public AbnormalDataRecordInfo findByPrimary(Long ruleId, String dbName, String tableName, String recordDate) {
        return abnormalDataRecordInfoRepository.findByPrimary(ruleId, dbName, tableName, recordDate);
    }

    @Override
    public List<AbnormalDataRecordInfo> findAll() {
        return abnormalDataRecordInfoRepository.findAll();
    }

    @Override
    public List<AbnormalDataRecordInfo> findWithExistRulesByRecordDate(String recordDate) {
        return abnormalDataRecordInfoRepository.findWithExistRulesByRecordDate(recordDate);
    }
}
