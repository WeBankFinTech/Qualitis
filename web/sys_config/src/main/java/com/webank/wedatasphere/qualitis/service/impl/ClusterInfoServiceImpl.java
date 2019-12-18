/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.qualitis.service.impl;

import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.request.DeleteClusterInfoRequest;
import com.webank.wedatasphere.qualitis.request.ModifyClusterInfoRequest;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.AddClusterInfoRequest;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.service.ClusterInfoService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
@Service
public class ClusterInfoServiceImpl implements ClusterInfoService {

    @Autowired
    private ClusterInfoDao clusterInfoDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterInfoServiceImpl.class);

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<ClusterInfo> addClusterInfo(AddClusterInfoRequest request) throws UnExpectedRequestException {
        // 检查参数
        checkRequest(request);

        // 查看clusterName是否已存在
        String clusterName = request.getClusterName();
        String clusterType = request.getClusterType();
        String linkisAddress = request.getLinkisAddress();
        String linkisToken = request.getLinkisToken();
        ClusterInfo clusterInfoInDb = clusterInfoDao.findByClusterName(clusterName);
        if (clusterInfoInDb != null) {
            throw new UnExpectedRequestException("cluster name {&ALREADY_EXIST}");
        }

        // 创建新ClusterInfo并保存
        ClusterInfo newClusterInfo = new ClusterInfo();
        newClusterInfo.setClusterName(clusterName);
        newClusterInfo.setClusterType(clusterType);
        newClusterInfo.setLinkisAddress(linkisAddress);
        newClusterInfo.setLinkisToken(linkisToken);
        ClusterInfo savedClusterInfo = clusterInfoDao.saveClusterInfo(newClusterInfo);

        LOGGER.info("Succeed to add cluster_info, response: {}", savedClusterInfo);
        return new GeneralResponse<>("200", "{&ADD_CLUSTER_INFO_SUCCESSFULLY}", savedClusterInfo);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> deleteClusterInfo(DeleteClusterInfoRequest request) throws UnExpectedRequestException {
        // 检查参数
        checkRequest(request);

        // 根据id查找ClusterInfo,不存在则抛出异常
        Long clusterInfoId = request.getClusterInfoId();
        ClusterInfo clusterInfoInDb = clusterInfoDao.findById(clusterInfoId);
        if (clusterInfoInDb == null) {
            throw new UnExpectedRequestException("id {&DOES_NOT_EXIST}");
        }

        // 删除clusterInfo
        clusterInfoDao.deleteClusterInfo(clusterInfoInDb);
        LOGGER.info("Succeed to delete cluster_info. id: {}", request.getClusterInfoId());
        return new GeneralResponse<>("200", "{&DELETE_CLUSTER_INFO_SUCCESSFULLY}", null);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> modifyClusterInfo(ModifyClusterInfoRequest request) throws UnExpectedRequestException {
        // 检查参数
        checkRequest(request);

        // 根据id查找clusterInfo,不存在抛出异常
        Long id = request.getClusterInfoId();
        ClusterInfo clusterInfoInDb = clusterInfoDao.findById(id);
        if (clusterInfoInDb == null) {
            throw new UnExpectedRequestException("id {&DOES_NOT_EXIST}");
        }
        LOGGER.info("Succeed to find cluster_info. cluster_info: {}", clusterInfoInDb);

        // 修改clusterInfo信息
        String clusterName = request.getClusterName();
        String clusterType = request.getClusterType();
        clusterInfoInDb.setClusterName(clusterName);
        clusterInfoInDb.setClusterType(clusterType);
        clusterInfoInDb.setLinkisAddress(request.getLinkisAddress());
        clusterInfoInDb.setLinkisToken(request.getLinkisToken());

        // 保存clusterInfo
        ClusterInfo savedClusterInfo = clusterInfoDao.saveClusterInfo(clusterInfoInDb);

        LOGGER.info("Succeed to modify cluster_info. cluster_info: {}", savedClusterInfo);
        return new GeneralResponse<>("200", "{&MODIFY_CLUSTER_INFO_SUCCESSFULLY}", null);
    }

    @Override
    public GeneralResponse<GetAllResponse<ClusterInfo>> findAllClusterInfo(PageRequest request) throws UnExpectedRequestException {
        // 检查参数
        PageRequest.checkRequest(request);

        // 根据page，size分页查找clusterInfo
        int page = request.getPage();
        int size = request.getSize();
        List<ClusterInfo> clusterInfos = clusterInfoDao.findAllClusterInfo(page, size);
        long total = clusterInfoDao.countAll();
        GetAllResponse<ClusterInfo> response = new GetAllResponse<>();
        response.setData(clusterInfos);
        response.setTotal(total);

        List<Long> clusterInfoIdList = response.getData().stream().map(ClusterInfo::getId).collect(Collectors.toList());
        LOGGER.info("Succeed to find cluster_infos. total: {}, id of cluster_infos: {}", total, clusterInfoIdList);
        return new GeneralResponse<>("200", "{&FIND_CLUSTER_INFOS_SUCCESSFULLY}", response);
    }

    private void checkRequest(ModifyClusterInfoRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        checkId(request.getClusterInfoId());
        checkString(request.getClusterName(), "cluster name");
        checkString(request.getClusterType(), "cluster type");
        checkString(request.getLinkisAddress(), "Ujes_address");
        checkString(request.getLinkisToken(), "Ujes_token");
    }

    private void checkRequest(DeleteClusterInfoRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        checkId(request.getClusterInfoId());
    }

    private void checkRequest(AddClusterInfoRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        checkString(request.getClusterName(), "cluster name");
        checkString(request.getClusterType(), "cluster type");
        checkString(request.getLinkisAddress(), "Ujes_address");
        checkString(request.getLinkisToken(), "Ujes_token");
    }

    private void checkString(String str, String strName) throws UnExpectedRequestException {
        if (StringUtils.isBlank(str)) {
            throw new UnExpectedRequestException(strName + " {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
    }

    private void checkId(Long id) throws UnExpectedRequestException {
        if (null == id) {
            throw new UnExpectedRequestException("id {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
    }
}
