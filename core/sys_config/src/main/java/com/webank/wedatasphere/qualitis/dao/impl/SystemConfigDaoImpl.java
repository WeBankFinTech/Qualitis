package com.webank.wedatasphere.qualitis.dao.impl;

import com.webank.wedatasphere.qualitis.dao.SystemConfigDao;
import com.webank.wedatasphere.qualitis.dao.repository.SystemConfigRepository;
import com.webank.wedatasphere.qualitis.entity.SystemConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author howeye
 */
@Repository
public class SystemConfigDaoImpl implements SystemConfigDao {

    @Autowired
    private SystemConfigRepository systemConfigRepository;

    @Override
    public SystemConfig findByKeyName(String keyName) {
        return systemConfigRepository.findByKeyName(keyName);
    }

    @Override
    public SystemConfig saveSystemConfig(SystemConfig systemConfig) {
        return systemConfigRepository.save(systemConfig);
    }
}
