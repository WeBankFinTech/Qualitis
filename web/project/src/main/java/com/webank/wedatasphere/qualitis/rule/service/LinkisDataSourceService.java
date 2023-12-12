package com.webank.wedatasphere.qualitis.rule.service;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.rule.entity.LinkisDataSource;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author v_minminghe@webank.com
 * @date 2023-05-17 14:11
 * @description
 */
public interface LinkisDataSourceService {

    /**
     * addOrModify
     * @throws UnExpectedRequestException
     * @param linkisDataSource
     */
    void addOrModify(LinkisDataSource linkisDataSource) throws UnExpectedRequestException;

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
     * get Linkis Env Name List
     * @return
     * @param linkisDataSource
     */
    List<String> getLinkisEnvNameList(LinkisDataSource linkisDataSource);

    /**
     * get Original Env Name List
     * @return
     * @param linkisDataSource
     */
    List<String> getOriginalEnvNameList(LinkisDataSource linkisDataSource);

    /**
     * 将环境ID和Name键值对，转换成LinkisDataSource表中envs字段所需的数据格式
     * @param envIdAndNameMap
     * @return
     */
    String formatEnvsField(Map<Long, String> envIdAndNameMap);

    /**
     * convert Original Env Name To Linkis
     * @return
     * @param linkisDataSourceId
     * @param originalEnvName
     * @param inputType
     * @param host
     */
    String convertOriginalEnvNameToLinkis(Long linkisDataSourceId, String originalEnvName, Integer inputType, String host);

    /**
     * convert Linkis Env Name To Original
     * @return
     * @param linkisDataSourceId
     * @param linkisEnvName
     * @param inputType
     */
    String convertLinkisEnvNameToOriginal(Long linkisDataSourceId, String linkisEnvName, Integer inputType);
}
