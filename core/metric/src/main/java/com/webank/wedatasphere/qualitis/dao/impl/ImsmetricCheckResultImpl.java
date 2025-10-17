package com.webank.wedatasphere.qualitis.dao.impl;

import com.webank.wedatasphere.qualitis.dao.ImsmetricCheckResultDao;
import com.webank.wedatasphere.qualitis.dao.repository.ImsmetricCheckResultRepository;
import com.webank.wedatasphere.qualitis.entity.ImsmetricCheckResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author v_wenxuanzhang
 */
@Repository
public class ImsmetricCheckResultImpl implements ImsmetricCheckResultDao {

    @Autowired
    private ImsmetricCheckResultRepository imsmetricCheckResultRepository;

    @Override
    public List< ImsmetricCheckResult > queryImsmetricCheckResult(List< Long > metricIds, String startDate, String endDate) {
        return imsmetricCheckResultRepository.queryImsmetricCheckResult(metricIds, startDate, endDate);
    }

}
