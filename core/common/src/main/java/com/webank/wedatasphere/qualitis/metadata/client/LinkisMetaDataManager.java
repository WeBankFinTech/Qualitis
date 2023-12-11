package com.webank.wedatasphere.qualitis.metadata.client;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.request.LinkisDataSourceEnvRequest;
import com.webank.wedatasphere.qualitis.metadata.request.LinkisDataSourceRequest;
import com.webank.wedatasphere.qualitis.metadata.request.ModifyDataSourceParameterRequest;
import com.webank.wedatasphere.qualitis.metadata.response.datasource.LinkisDataSourceParamsResponse;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2023-05-12 10:59
 * @description
 */
public interface LinkisMetaDataManager {

    /**
     * create Data Source
     *
     * @param linkisDataSourceRequest
     * @param cluster
     * @param authUser
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    Long createDataSource(LinkisDataSourceRequest linkisDataSourceRequest, String cluster, String authUser) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * modify Data Source
     *
     * @param linkisDataSourceRequest
     * @param cluster
     * @param authUser
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    Long modifyDataSource(LinkisDataSourceRequest linkisDataSourceRequest, String cluster, String authUser) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * create Data Source Env
     *
     * @param inputType
     * @param verifyType
     * @param linkisDataSourceEnvRequestList
     * @param clusterName
     * @param authUser
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    List<LinkisDataSourceEnvRequest> createDataSourceEnv(Integer inputType, Integer verifyType, List<LinkisDataSourceEnvRequest> linkisDataSourceEnvRequestList, String clusterName, String authUser) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * modify Data Source Env
     *
     * @param inputType
     * @param verifyType
     * @param linkisDataSourceEnvRequestList
     * @param clusterName
     * @param authUser
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    List<LinkisDataSourceEnvRequest> modifyDataSourceEnv(Integer inputType, Integer verifyType, List<LinkisDataSourceEnvRequest> linkisDataSourceEnvRequestList, String clusterName, String authUser) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * modify Data Source Params
     *
     * @param modifyDataSourceParameterRequest
     * @param clusterName
     * @param authUser
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    LinkisDataSourceParamsResponse modifyDataSourceParams(ModifyDataSourceParameterRequest modifyDataSourceParameterRequest, String clusterName, String authUser) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * delete Data Source
     *
     * @param linkisDataSourceId
     * @param clusterName
     * @param userName
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    void deleteDataSource(Long linkisDataSourceId, String clusterName, String userName) throws UnExpectedRequestException, MetaDataAcquireFailedException;

}
