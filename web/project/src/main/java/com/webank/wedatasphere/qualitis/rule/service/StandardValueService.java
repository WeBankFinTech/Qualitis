package com.webank.wedatasphere.qualitis.rule.service;

import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.query.request.EditionRequest;
import com.webank.wedatasphere.qualitis.query.request.StandardValueRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.rule.request.AddStandardValueRequest;
import com.webank.wedatasphere.qualitis.rule.request.DeleteStandardValueVersionRequest;
import com.webank.wedatasphere.qualitis.rule.request.DmsQueryRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyStandardValueRequest;
import com.webank.wedatasphere.qualitis.rule.response.StandardValueResponse;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface StandardValueService {

    /**
     * add StandardValue
     *
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    StandardValueResponse addStandardValue(AddStandardValueRequest request)
            throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * modify StandardValue
     *
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    StandardValueResponse modifyStandardValue(ModifyStandardValueRequest request)
            throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * delete StandardValue
     *
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    void deleteStandardValue(DeleteStandardValueVersionRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * get All StandardValueRequest
     *
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<GetAllResponse<StandardValueResponse>> getAllStandardValue(StandardValueRequest request) throws UnExpectedRequestException;


    /**
     * get Accord StandardValue
     *
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<GetAllResponse<StandardValueResponse>> getAccordStandardValue(EditionRequest request) throws UnExpectedRequestException;


    /**
     * get StandardValue Detail
     *
     * @param standardValueId
     * @return
     * @throws UnExpectedRequestException
     */
    StandardValueResponse geStandardValueDetail(Long standardValueId) throws UnExpectedRequestException;

    /**
     * get All Approve Enum
     *
     * @return
     */
    List<Map<String, Object>> getAllApproveEnum();

    /**
     * get Version List by standardValueId
     *
     * @param standardValueId
     * @return
     */
    List<Long> getVersionList(Long standardValueId);

    /**
     * get All SourceEnum
     *
     * @return
     */
    List<Map<String, Object>> getAllSourceEnum();


    /**
     * get Dms Standard Category
     *
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    Map<String, Object> getDmsStandardCategory(DmsQueryRequest request) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * get Dms Standard Big Category
     *
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    Map<String, Object> getDmsStandardBigCategory(DmsQueryRequest request) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * get Dms Standard Small Category
     *
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    Map<String, Object> getDmsStandardSmallCategory(DmsQueryRequest request) throws UnExpectedRequestException, MetaDataAcquireFailedException;


    /**
     * get Dms Standard Urn
     *
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws URISyntaxException
     */
    Map<String, Object> getDmsStandardUrn(DmsQueryRequest request) throws UnExpectedRequestException, MetaDataAcquireFailedException, URISyntaxException;

    /**
     * get Dms Standard Code Name
     *
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws URISyntaxException
     */
    Map<String, Object> getDmsStandardCodeName(DmsQueryRequest request) throws UnExpectedRequestException, MetaDataAcquireFailedException, URISyntaxException;

    /**
     * get Dms Standard Code Table
     *
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    Map<String, Object> getDmsStandardCodeTable(DmsQueryRequest request) throws UnExpectedRequestException, MetaDataAcquireFailedException;

}
