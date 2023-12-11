package com.webank.wedatasphere.qualitis.dao.impl;

import com.webank.wedatasphere.qualitis.dao.UploadRecordDao;
import com.webank.wedatasphere.qualitis.dao.repository.UploadRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.webank.wedatasphere.qualitis.entity.UploadRecord;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

/**
 * @author allenzhou@webank.com
 * @date 2021/10/25 14:10
 */
@Repository
public class UploadRecordDaoImpl implements UploadRecordDao {
    @Autowired
    private UploadRecordRepository uploadRecordRepository;

    @Override
    public UploadRecord save(UploadRecord uploadRecord) {
        return uploadRecordRepository.save(uploadRecord);
    }

    @Override
    public UploadRecord findByUnique(Date recordDate, Boolean status) {
        return uploadRecordRepository.findByUnique(recordDate, status);
    }

    @Override
    public List<UploadRecord> findAll() {
        return uploadRecordRepository.findAll();
    }
}
