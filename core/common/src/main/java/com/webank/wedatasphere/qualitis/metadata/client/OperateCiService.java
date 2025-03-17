package com.webank.wedatasphere.qualitis.metadata.client;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.response.*;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;

import java.io.IOException;
import java.util.List;

/**
 * @author allenzhou@webank.com
 * @date 2021/3/2 10:53
 */
public interface OperateCiService {

    /**
     *  Get all buz_domain info from cmdb
     * @return
     * @throws UnExpectedRequestException
     */
    List<BuzDomainResponse> getAllBuzDomainInfo() throws UnExpectedRequestException, IOException;
    /**
     * Get all sub_system info from http of cmdb, incloud: id,name,full english name.
     *
     * @return
     * @throws UnExpectedRequestException
     */
    List<SubSystemResponse> getAllSubSystemInfo() throws UnExpectedRequestException;

    List<SubSystemResponse> getSubSystemInfoByPage(String subSystemName, int page, int size) throws UnExpectedRequestException;

    /**
     * Get the specific sub-system by its name
     * @param subSystemName
     * @return
     * @throws UnExpectedRequestException
     */
    String getSubSystemIdByName(String subSystemName) throws UnExpectedRequestException;

    /**
     * Get all product info from http of cmdb, incloud: id,cn name.
     *
     * @return
     * @throws UnExpectedRequestException
     */
    List<ProductResponse> getAllProductInfo() throws UnExpectedRequestException;

    /**
     * Get all department info from http of cmdb, incloud:department name.
     *
     * @return
     * @throws UnExpectedRequestException
     */
    List<CmdbDepartmentResponse> getAllDepartmetInfo() throws UnExpectedRequestException;

    /**
     * Get dev and ops info
     *
     * @param deptCode
     * @return
     * @throws UnExpectedRequestException
     */
    List<DepartmentSubResponse> getDevAndOpsInfo(Integer deptCode) throws UnExpectedRequestException;

    /**
     * Get dcn
     * @param subSystemId
     * @param dcnRangeType
     * @param dcnRangeValues
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse getDcn(String subSystemId, String dcnRangeType, List<String> dcnRangeValues) throws UnExpectedRequestException;
}
