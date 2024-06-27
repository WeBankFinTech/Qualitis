package com.webank.wedatasphere.qualitis.rule.service;

import com.webank.wedatasphere.qualitis.metadata.request.LinkisDataSourceEnvRequest;
import com.webank.wedatasphere.qualitis.rule.entity.LinkisDataSourceEnv;
import com.webank.wedatasphere.qualitis.rule.request.GetLinkisDataSourceEnvRequest;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2023-12-01 16:44
 * @description
 */
public interface LinkisDataSourceEnvService {

    /**
     * delete by envIds
     * @param envIds
     */
    void deleteBatch(List<Long> envIds);

    /**
     * modify
     * @param linkisDataSourceEnvList
     */
    void modifyBatch(List<LinkisDataSourceEnv> linkisDataSourceEnvList);

    /**
     * save Batch
     * @param linkisDataSourceId
     * @param dataSourceEnvRequestList
     * @return
     */
    void createBatch(Long linkisDataSourceId, List<LinkisDataSourceEnvRequest> dataSourceEnvRequestList);

    /**
     * query Envs
     * @param linkisDataSourceId
     * @return
     */
    List<LinkisDataSourceEnv> queryAllEnvs(Long linkisDataSourceId);

    /**
     * query envs in advance
     * @param request
     * @return
     */
    List<LinkisDataSourceEnv> queryEnvsInAdvance(GetLinkisDataSourceEnvRequest request);

}
