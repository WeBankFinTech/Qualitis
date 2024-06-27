package com.webank.wedatasphere.qualitis.metadata.client;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.request.LinkisDataSourceEnvRequest;
import com.webank.wedatasphere.qualitis.metadata.request.LinkisDataSourceRequest;
import com.webank.wedatasphere.qualitis.metadata.request.ModifyDataSourceParameterRequest;
import com.webank.wedatasphere.qualitis.metadata.response.datasource.LinkisDataSourceParamsResponse;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;

import java.util.List;
import java.util.Map;

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
    List<LinkisDataSourceEnvRequest> createDataSourceEnvAndSetEnvId(Integer inputType, Integer verifyType, List<LinkisDataSourceEnvRequest> linkisDataSourceEnvRequestList, String clusterName, String authUser) throws UnExpectedRequestException, MetaDataAcquireFailedException;

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


    /**
     * Getting mapping relation between name and id of dataSourceType
     * @return key: name of dataSourceType, value: id of dataSourceType
     */
    Map<String, Long> getDataSourceTypeNameAndIdMap();

    /**
     * connect
     *
     * @param linkisDataSourceId
     * @param versionId
     * @return
     * @throws Exception
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse connect(Long linkisDataSourceId, Long versionId) throws Exception;
}
