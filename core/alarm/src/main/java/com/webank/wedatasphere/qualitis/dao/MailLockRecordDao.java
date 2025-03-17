package com.webank.wedatasphere.qualitis.dao;

import com.webank.wedatasphere.qualitis.entity.MailLockRecord;

import java.util.Date;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface MailLockRecordDao {

    /**
     * Save.
     *
     * @param mailLockRecord
     * @return
     */
//    MailLockRecord save(MailLockRecord mailLockRecord);

    /**
     * Find with unique keys(status, upload date).
     *
     * @param recordDate
     * @param status
     * @param executionFrequency
     * @return
     */
//    MailLockRecord findByUnique(Date recordDate, Boolean status, Integer executionFrequency);
}
