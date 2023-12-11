package com.webank.wedatasphere.qualitis.dao;

import com.webank.wedatasphere.qualitis.entity.AbnormalDataRecordInfo;
import java.util.Date;
import java.util.List;

/**
 * @author allenzhou
 */
public interface AbnormalDataRecordInfoDao {
    /**
     * Save.
     * @param abnormalDataRecordInfo
     * @return
     */
    AbnormalDataRecordInfo save(AbnormalDataRecordInfo abnormalDataRecordInfo);

    /**
     * 批量保存
     * @param abnormalDataRecordInfos 要保存的参数
     * @return 保存成功的返回值
     */
    List<AbnormalDataRecordInfo> saveAll(List<AbnormalDataRecordInfo> abnormalDataRecordInfos);

    /**
     * Find with primary key.
     * @param ruleId
     * @param dbName
     * @param tableName
     * @param date
     * @return
     */
    AbnormalDataRecordInfo findByPrimary(Long ruleId, String dbName, String tableName, String date);

    /**
     * Find all.
     * @return
     */
    List<AbnormalDataRecordInfo> findAll();

    /**
     * Find with rule by record date.
     * @param recordDate
     * @return
     */
    List<AbnormalDataRecordInfo> findWithExistRulesByRecordDate(String recordDate);
}
