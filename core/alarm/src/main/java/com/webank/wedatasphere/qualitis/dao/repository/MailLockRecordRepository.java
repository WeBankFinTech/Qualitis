package com.webank.wedatasphere.qualitis.dao.repository;

import com.webank.wedatasphere.qualitis.entity.MailLockRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

/**
 * @author 
 */
public interface MailLockRecordRepository extends JpaRepository<MailLockRecord, Long> {

    /**
     * Find one by unique keys(status, upload date,executionFrequency).
     *
     * @param recordDate
     * @param status
     * @param executionFrequency
     * @return
     */
    @Query(value = "SELECT ur FROM MailLockRecord ur WHERE ur.sendDate = ?1 and ur.status = ?2 and ur.executionFrequency = ?3")
    MailLockRecord findByUnique(Date recordDate, Boolean status, Integer executionFrequency);
}
