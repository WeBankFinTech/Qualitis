package com.webank.wedatasphere.qualitis.rule.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.request.LinkisDataSourceEnvRequest;
import com.webank.wedatasphere.qualitis.metadata.request.LinkisDataSourceRequest;
import com.webank.wedatasphere.qualitis.project.request.OuterDataSourceRequest;
import com.webank.wedatasphere.qualitis.rule.entity.LinkisDataSource;

import java.util.List;
import java.util.Map;

/**
 * @author v_minminghe@webank.com
 * @date 2023-05-17 14:11
 * @description
 */
public interface LinkisDataSourceService {

    /**
     * save
     * @param linkisDataSourceId
     * @param dataSourceTypeId
     * @param request
     * @param userInDb
     * @throws UnExpectedRequestException
     * @throws JsonProcessingException
     * @return
     */
    LinkisDataSource save(Long linkisDataSourceId, Long dataSourceTypeId, LinkisDataSourceRequest request, User userInDb) throws UnExpectedRequestException, JsonProcessingException;

    /**
     * modify
     * @param linkisDataSource
     * @param dataSourceTypeId
     * @param request
     * @param userInDb
     * @throws UnExpectedRequestException
     * @throws JsonProcessingException
     */
    void modify(LinkisDataSource linkisDataSource, Long dataSourceTypeId, LinkisDataSourceRequest request, User userInDb) throws UnExpectedRequestException, JsonProcessingException;

    /**
     * addOrModify
     * @throws UnExpectedRequestException
     * @param linkisDataSource
     */
    void save(LinkisDataSource linkisDataSource) throws UnExpectedRequestException;

    /**
     * get Env Name And Id Map
     * @return
     * @param linkisDataSource
     */
    Map<String, Long> getEnvNameAndIdMap(LinkisDataSource linkisDataSource);

    /**
     * get By Linkis Data Source Ids
     * @return
     * @param linkisDataSourceIds
     */
    List<LinkisDataSource> getByLinkisDataSourceIds(List<Long> linkisDataSourceIds);

    /**
     * get By Linkis Data Source Name
     * @return
     * @param linkisDataSourceName
     */
    LinkisDataSource getByLinkisDataSourceName(String linkisDataSourceName);

    /**
     * convert Original Env Name To Linkis
     * @return
     * @param linkisDataSourceId
     * @param originalEnvName
     * @param inputType
     * @param host
     * @param port
     * @param databaseInstance
     */
    String convertOriginalEnvNameToLinkis(Long linkisDataSourceId, String originalEnvName, Integer inputType, String host, String port, String databaseInstance);

    /**
     * convert Linkis Env Name To Original
     * @return
     * @param linkisDataSourceId
     * @param linkisEnvName
     * @param inputType
     */
    String convertLinkisEnvNameToOriginal(Long linkisDataSourceId, String linkisEnvName, Integer inputType);

}
