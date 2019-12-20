package com.webank.wedatasphere.qualitis.dao;

import com.webank.wedatasphere.qualitis.entity.SystemConfig;

/**
 * @author howeye
 */
public interface SystemConfigDao {

    /**
     * Find system config by key name
     * @param keyName
     * @return
     */
    SystemConfig findByKeyName(String keyName);

    /**
     * Save system config
     * @param systemConfig
     * @return
     */
    SystemConfig saveSystemConfig(SystemConfig systemConfig);

}
