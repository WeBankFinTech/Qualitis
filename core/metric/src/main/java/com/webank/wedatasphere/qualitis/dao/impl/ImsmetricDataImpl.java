package com.webank.wedatasphere.qualitis.dao.impl;

import com.webank.wedatasphere.qualitis.dao.ImsmetricDataDao;
import com.webank.wedatasphere.qualitis.dao.repository.ImsmetricDataRepository;
import com.webank.wedatasphere.qualitis.entity.ImsmetricData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author v_wenxuanzhang
 */
@Repository
public class ImsmetricDataImpl implements ImsmetricDataDao {

    @Autowired
    private ImsmetricDataRepository imsmetricDataRepository;


    @Override
    public List<ImsmetricData> queryImsmetricData(String metricIds, String startDate, String endDate) {
        return imsmetricDataRepository.queryImsmetricData(metricIds, startDate, endDate);
    }

    @Override
    public List<ImsmetricData> queryImsmetricDatas(List<Long> metricIds, int startDate, int endDate) {
        return imsmetricDataRepository.queryImsmetricDatas(metricIds, startDate, endDate);
    }

    @Override
    public List<String> findAllDataUsers() {
        return imsmetricDataRepository.findAllDataUsers();
    }

}
