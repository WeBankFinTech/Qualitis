package com.webank.wedatasphere.qualitis.metadata.client;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;

import java.net.URISyntaxException;
import java.util.Map;

/**
 * @author allenzhou@webank.com
 * @date 2021/6/15 11:30
 */
public interface DataStandardClient {
    /**
     * Get database info.
     *
     * @param searchKey
     * @param loginUser
     * @return
     * @throws MetaDataAcquireFailedException
     * @throws UnExpectedRequestException
     */
    Map<String, Object> getDatabase(String searchKey, String loginUser) throws MetaDataAcquireFailedException, UnExpectedRequestException;

    /**
     * Get dataset info.
     *
     * @param dbId
     * @param datasetName
     * @param page
     * @param size
     * @param loginUser
     * @return
     * @throws MetaDataAcquireFailedException
     * @throws UnExpectedRequestException
     */
    Map<String, Object> getDataset(String dbId, String datasetName, int page, int size, String loginUser) throws MetaDataAcquireFailedException, UnExpectedRequestException;

    /**
     * Get column standard code.
     *
     * @param dataSetId
     * @param fieldName
     * @param loginUser
     * @return
     * @throws MetaDataAcquireFailedException
     * @throws UnExpectedRequestException
     */
    Map<String, Object> getColumnStandard(Long dataSetId, String fieldName, String loginUser) throws MetaDataAcquireFailedException, UnExpectedRequestException;

    /**
     * Get data standard info from datamap with token authentication.
     *
     * @param stdCode
     * @param source
     * @param loginUser
     * @return
     * @throws MetaDataAcquireFailedException
     * @throws UnExpectedRequestException
     */
    Map<String, Object> getDataStandardDetail(String stdCode, String source, String loginUser) throws MetaDataAcquireFailedException, UnExpectedRequestException;

    /**
     * get Data Standard Category
     *
     * @param page
     * @param size
     * @param loginUser
     * @param stdSubName
     * @return
     * @throws MetaDataAcquireFailedException
     * @throws UnExpectedRequestException
     */
    Map<String, Object> getDataStandardCategory(int page, int size, String loginUser, String stdSubName) throws MetaDataAcquireFailedException, UnExpectedRequestException;


    /**
     * get Data Standard Big Category
     *
     * @param page
     * @param size
     * @param loginUser
     * @param stdSubName
     * @param stdBigCategoryName
     * @return
     * @throws MetaDataAcquireFailedException
     * @throws UnExpectedRequestException
     */
    Map<String, Object> getDataStandardBigCategory(int page, int size, String loginUser, String stdSubName, String stdBigCategoryName) throws MetaDataAcquireFailedException, UnExpectedRequestException;


    /**
     * get Data Standard Small Category
     *
     * @param page
     * @param size
     * @param loginUser
     * @param stdSubName
     * @param stdBigCategoryName
     * @param smallCategoryName
     * @return
     * @throws MetaDataAcquireFailedException
     * @throws UnExpectedRequestException
     */
    Map<String, Object> getDataStandardSmallCategory(int page, int size, String loginUser, String stdSubName, String stdBigCategoryName, String smallCategoryName) throws MetaDataAcquireFailedException, UnExpectedRequestException;


    /**
     * get Data Standard.
     *
     * @param page
     * @param size
     * @param loginUser
     * @param stdSmallCategoryUrn
     * @param stdCnName
     * @return
     * @throws MetaDataAcquireFailedException
     * @throws UnExpectedRequestException
     * @throws URISyntaxException
     */
    Map<String, Object> getDataStandard(int page, int size, String loginUser, String stdSmallCategoryUrn, String stdCnName) throws MetaDataAcquireFailedException, UnExpectedRequestException, URISyntaxException;

    /**
     * get Standard Code.
     *
     * @param stdUrn
     * @param page
     * @param size
     * @param loginUser
     * @return
     * @throws MetaDataAcquireFailedException
     * @throws UnExpectedRequestException
     * @throws URISyntaxException
     */
    Map<String, Object> getStandardCode(int page, int size, String loginUser, String stdUrn) throws MetaDataAcquireFailedException, UnExpectedRequestException, URISyntaxException;

    /**
     * get Standard Code Table
     *
     * @param stdCode
     * @param page
     * @param size
     * @param loginUser
     * @return
     * @throws MetaDataAcquireFailedException
     * @throws UnExpectedRequestException
     */
    Map<String, Object> getStandardCodeTable(int page, int size, String loginUser, String stdCode) throws MetaDataAcquireFailedException, UnExpectedRequestException;

}
