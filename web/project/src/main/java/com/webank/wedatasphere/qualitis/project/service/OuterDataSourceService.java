package com.webank.wedatasphere.qualitis.project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.project.request.OuterDataSourceDcnRequest;
import com.webank.wedatasphere.qualitis.project.request.OuterDataSourceRequest;
import com.webank.wedatasphere.qualitis.project.response.OuterDataSourceDetailResponse;
import com.webank.wedatasphere.qualitis.project.response.OuterDataSourceVersionResponse;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;

import javax.management.relation.RoleNotFoundException;
import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2023-10-17 14:43
 * @description
 */
public interface OuterDataSourceService {

    /**
     * create Data Source
     * @param outerDataSourceRequest
     * @return
     * @throws RoleNotFoundException
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws JsonProcessingException
     */
    Long createDataSource(OuterDataSourceRequest outerDataSourceRequest) throws RoleNotFoundException, UnExpectedRequestException, MetaDataAcquireFailedException, JsonProcessingException;

    /**
     * modify Data Source
     * @param outerDataSourceRequest
     * @return
     * @throws RoleNotFoundException
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws JsonProcessingException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse<Long> modifyDataSource(OuterDataSourceRequest outerDataSourceRequest) throws RoleNotFoundException, UnExpectedRequestException, MetaDataAcquireFailedException, JsonProcessingException, PermissionDeniedRequestException;

    /**
     * Get detail of DataSource
     * @param linkisDataSourceName
     * @param versionId
     * @param username
     * @return
     * @throws Exception
     */
    OuterDataSourceDetailResponse getDataSourceDetail(String linkisDataSourceName, Long versionId, String username) throws Exception;

    /**
     * Publish DataSource with a specific version
     * @param linkisDataSourceName
     * @param versionId
     * @param username
     * @return
     * @throws Exception
     */
    GeneralResponse publish(String linkisDataSourceName, Long versionId, String username) throws Exception;

    /**
     * Checking connect if available
     * @param linkisDataSourceName
     * @param versionId
     * @param username
     * @return
     * @throws Exception
     */
    GeneralResponse connect(String linkisDataSourceName, Long versionId, String username) throws Exception;

    /**
     * Making certain DataSource expired
     * @param linkisDataSourceName
     * @param username
     * @return
     * @throws Exception
     */
    GeneralResponse expire(String linkisDataSourceName, String username) throws Exception;

    /**
     * versions
     * @param linkisDataSourceName
     * @param username
     * @return
     * @throws Exception
     */
    List<OuterDataSourceVersionResponse> versions(String linkisDataSourceName, String username) throws Exception;

    /**
     * update Dcn
     * @param outerDataSourceDcnRequest
     * @return
     * @throws UnExpectedRequestException
     */
    void updateDcn(OuterDataSourceDcnRequest outerDataSourceDcnRequest) throws UnExpectedRequestException;

    /**
     * getDataSourceNames
     * @param username
     * @return
     * @throws UnExpectedRequestException
     */
    List<String> getDataSourceNames(String username);
}
