package com.webank.wedatasphere.qualitis.dao.repository;

import com.webank.wedatasphere.qualitis.entity.UploadRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Date;

/**
 * @author allenzhou
 */
public interface UploadRecordRepository extends JpaRepository<UploadRecord, Long> {
    /**
     * Find one by unique keys(status, upload date).
     * @param recordDate
     * @param status
     * @return
     */
    @Query(value = "SELECT ur FROM UploadRecord ur WHERE ur.uploadDate = ?1 and ur.status = ?2")
    UploadRecord findByUnique(Date recordDate, Boolean status);
}
