package com.webank.wedatasphere.qualitis.dao;

import com.webank.wedatasphere.qualitis.entity.UploadRecord;
import java.util.Date;
import java.util.List;

/**
 * @author allenzhou
 */
public interface UploadRecordDao {
    /**
     * Save.
     * @param uploadRecord
     * @return
     */
    UploadRecord save(UploadRecord uploadRecord);

    /**
     * Find with unique keys(status, upload date).
     * @param recordDate
     * @param status
     * @return
     */
    UploadRecord findByUnique(Date recordDate, Boolean status);

    /**
     * Find all.
     * @return
     */
    List<UploadRecord> findAll();

}
