package com.webank.wedatasphere.qualitis.dao.repository;

import com.webank.wedatasphere.qualitis.entity.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author howeye
 */
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {

    /**
     * Find System config by key
     * @param keyName
     * @return
     */
    SystemConfig findByKeyName(String keyName);

}
