package com.webank.wedatasphere.qualitis.dao;

import com.webank.wedatasphere.qualitis.entity.LinksErrorCode;

import java.util.List;
import java.util.Set;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface LinksErrorCodeDao {

    /**
     * find All Links Error Code
     * @return
     */
    List<LinksErrorCode> findAllLinksErrorCode();

    /**
     * Save all LinksErrorCode .
     * @param linksErrorCodes
     * @return
     */
    Set<LinksErrorCode> saveAll(List<LinksErrorCode> linksErrorCodes);

}
