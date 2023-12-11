package com.webank.wedatasphere.qualitis.metadata.client;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.response.DataInfo;
import com.webank.wedatasphere.qualitis.metadata.response.table.TableMetadataInfo;
import com.webank.wedatasphere.qualitis.metadata.response.table.TableTagInfo;

/**
 * @author v_minminghe@webank.com
 * @date 2022-05-31 9:41
 * @description
 */
public interface RuleClient {

    /**
     * get tag of table
     * @param sourceType
     * @param clusterType
     * @param dbName
     * @param tableName
     * @param loginUser
     * @return
     * @throws MetaDataAcquireFailedException
     * @throws UnExpectedRequestException
     */
    TableTagInfo getTableTag(String sourceType, String clusterType, String dbName, String tableName, String loginUser) throws MetaDataAcquireFailedException, UnExpectedRequestException;

    /**
     * get MetaData by table
     * @param sourceType
     * @param clusterType
     * @param dbName
     * @param tableName
     * @param loginUser
     * @return
     * @throws MetaDataAcquireFailedException
     * @throws UnExpectedRequestException
     */
    TableMetadataInfo getMetaData(String sourceType, String clusterType, String dbName, String tableName, String loginUser) throws MetaDataAcquireFailedException, UnExpectedRequestException;

    /**
     * get tag list
     * @param loginUser
     * @param page
     * @param size
     * @return
     * @throws MetaDataAcquireFailedException
     */
    DataInfo<TableTagInfo> getTagList(String loginUser, int page, int size) throws MetaDataAcquireFailedException, UnExpectedRequestException;

}
