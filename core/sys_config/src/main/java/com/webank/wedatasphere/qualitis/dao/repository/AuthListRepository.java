package com.webank.wedatasphere.qualitis.dao.repository;

import com.webank.wedatasphere.qualitis.entity.AuthList;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author v_minminghe@webank.com
 * @date 2024-03-13 14:34
 * @description
 */
public interface AuthListRepository extends JpaRepository<AuthList, String> {

    /**
     *
     * @param appId
     * @return
     */
    AuthList findByAppId(String appId);

}
