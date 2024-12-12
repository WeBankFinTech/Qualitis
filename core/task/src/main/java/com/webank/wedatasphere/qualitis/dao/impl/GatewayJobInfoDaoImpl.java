package com.webank.wedatasphere.qualitis.dao.impl;

import com.webank.wedatasphere.qualitis.dao.GatewayJobInfoDao;
import com.webank.wedatasphere.qualitis.dao.repository.GatewayJobInfoRepository;
import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.entity.GatewayJobInfo;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author allenzhou@webank.com
 * @date 2022/1/7 10:39
 */
@Repository
public class GatewayJobInfoDaoImpl implements GatewayJobInfoDao {

    @Autowired
    private GatewayJobInfoRepository repository;

    @Override
    public List<Application> getAllApplication(String jobId) {
        return repository.getAllApplication(jobId);
    }

    @Override
    public GatewayJobInfo getByJobId(String jobId) {
        return repository.getByJobId(jobId);
    }

    @Override
    public GatewayJobInfo save(GatewayJobInfo gatewayJobInfo) {
        return repository.save(gatewayJobInfo);
    }

    @Override
    public GatewayJobInfo saveAndFlush(GatewayJobInfo gatewayJobInfo) {
        return repository.saveAndFlush(gatewayJobInfo);
    }
}
