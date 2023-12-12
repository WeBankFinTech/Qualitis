package com.webank.wedatasphere.qualitis.service;

import com.webank.wedatasphere.qualitis.entity.SystemConfig;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.ModifySystemConfigRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import javax.ws.rs.PathParam;

/**
 * @author howeye
 */
public interface SystemConfigService {

    /**
     * Modify System Config
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<Object> modifySystemConfig(ModifySystemConfigRequest request) throws UnExpectedRequestException;

    /**
     * Find System Config by key
     * @param keyName
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<SystemConfig> findByKeyName(String keyName) throws UnExpectedRequestException;

}
