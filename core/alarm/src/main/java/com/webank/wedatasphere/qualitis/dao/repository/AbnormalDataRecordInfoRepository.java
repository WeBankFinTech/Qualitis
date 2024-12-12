package com.webank.wedatasphere.qualitis.dao.repository;

import com.webank.wedatasphere.qualitis.entity.AbnormalDataRecordInfo;
import com.webank.wedatasphere.qualitis.entity.AbnormalDataRecordPrimaryKey;
import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

/**
 * @author allenzhou
 */
public interface AbnormalDataRecordInfoRepository extends JpaRepository<AbnormalDataRecordInfo, AbnormalDataRecordPrimaryKey> {
    /**
     * Find one by primary keys.
     * @param ruleId
     * @param dbName
     * @param tableName
     * @param recordDate
     * @return
     */
    @Query(value = "SELECT a FROM AbnormalDataRecordInfo a WHERE a.ruleId = ?1 AND a.dbName = ?2 AND a.tableName = ?3 AND a.recordDate = ?4")
    AbnormalDataRecordInfo findByPrimary(Long ruleId, String dbName, String tableName, String recordDate);

    /**
     * Find with rule by record date.
     * @param recordDate
     * @return
     */
    @Query(value = "select a from AbnormalDataRecordInfo a where exists (select id from Rule r where r.id = a.ruleId) and a.recordDate = ?1")
    List<AbnormalDataRecordInfo> findWithExistRulesByRecordDate(String recordDate);
}
