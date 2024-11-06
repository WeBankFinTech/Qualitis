package com.webank.wedatasphere.qualitis.rule.dao;

import com.webank.wedatasphere.qualitis.rule.entity.LinkisDataSourceEnv;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2023-12-01 16:42
 * @description
 */
public interface LinkisDataSourceEnvDao {

    /**
     * delete By Env Ids
     * @param envIds
     */
    void deleteByEnvIds(List<Long> envIds);

    /**
     * saveAll
     * @param linkisDataSourceEnvList
     */
    void saveAll(List<LinkisDataSourceEnv> linkisDataSourceEnvList);

    /**
     * query By Linkis Data Source Id
     * @param linkisDataSourceId
     * @return
     */
    List<LinkisDataSourceEnv> queryByLinkisDataSourceId(Long linkisDataSourceId);

    /**
     * query env by multi-conditions
     * @param linkisDataSourceId
     * @param envIds
     * @param dcnNums
     * @param logicAreas
     * @return
     */
    List<LinkisDataSourceEnv> query(Long linkisDataSourceId, List<Long> envIds, List<String> dcnNums, List<String> logicAreas);
}
