package com.webank.wedatasphere.qualitis.service;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.dto.ItsmUserDto;
import com.webank.wedatasphere.qualitis.request.user.ItsmRequest;

import javax.management.relation.RoleNotFoundException;
import java.util.Map;

/**
 * @author v_minminghe@webank.com
 * @date 2024-03-12 16:39
 * @description
 */
public interface ItsmService {

    void handleAlertWhitelist(ItsmRequest request) throws UnExpectedRequestException;

    void handleUser(ItsmRequest request) throws UnExpectedRequestException, RoleNotFoundException;

    void addUser(ItsmUserDto itsmUserDto) throws UnExpectedRequestException, RoleNotFoundException;

}
