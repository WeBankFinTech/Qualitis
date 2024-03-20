package com.webank.wedatasphere.qualitis.metadata.client;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.response.DcnResponse;
import com.webank.wedatasphere.qualitis.metadata.response.CmdbDepartmentResponse;
import com.webank.wedatasphere.qualitis.metadata.response.DepartmentSubResponse;
import com.webank.wedatasphere.qualitis.metadata.response.ProductResponse;
import com.webank.wedatasphere.qualitis.metadata.response.SubSystemResponse;

import com.webank.wedatasphere.qualitis.response.GeneralResponse;

import java.util.List;

/**
 * @author allenzhou@webank.com
 * @date 2021/3/2 10:53
 */
public interface OperateCiService {
    /**
     * Get all sub_system info from http of cmdb, incloud: id,name,full english name.
     *
     * @return
     * @throws UnExpectedRequestException
     */
    List<SubSystemResponse> getAllSubSystemInfo() throws UnExpectedRequestException;

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
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<DcnResponse> getDcn(Long subSystemId) throws UnExpectedRequestException;
}
