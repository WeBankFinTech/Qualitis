package com.webank.wedatasphere.qualitis.service.impl;

import com.webank.wedatasphere.qualitis.constant.ServiceInfoCollectStatusEnum;
import com.webank.wedatasphere.qualitis.constant.ServiceInfoStatusEnum;
import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.dao.ApplicationDao;
import com.webank.wedatasphere.qualitis.dao.ServiceInfoDao;
import com.webank.wedatasphere.qualitis.entity.ServiceInfo;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.AddServiceInfoRequest;
import com.webank.wedatasphere.qualitis.request.DeleteServiceInfoRequest;
import com.webank.wedatasphere.qualitis.request.FindServiceInfoRequest;
import com.webank.wedatasphere.qualitis.request.ModifyServiceInfoRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.response.ServiceInfoResponse;
import com.webank.wedatasphere.qualitis.service.ServiceInfoService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.webank.wedatasphere.qualitis.util.DateUtils;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

/**
 * @author allenzhou@webank.com
 * @date 2022/2/23 16:35
 */
@Service
public class ServiceInfoServiceImpl implements ServiceInfoService {
    @Autowired
    private ServiceInfoDao serviceInfoDao;

    @Autowired
    private ApplicationDao applicationDao;

    private HttpServletRequest httpServletRequest;

    public ServiceInfoServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public GeneralResponse<ServiceInfoResponse> addServiceInfo(AddServiceInfoRequest request) throws UnExpectedRequestException {
        if (request == null || StringUtils.isEmpty(request.getIp())) {
            throw new UnExpectedRequestException("IP {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }

        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setIp(request.getIp());
        serviceInfo.setStatus(ServiceInfoStatusEnum.RUNNING.getCode());
        serviceInfo.setCollectStatus(ServiceInfoCollectStatusEnum.CLOSED.getCode());
        serviceInfo.setUpdatingApplicationNum(Long.parseLong("0"));
        serviceInfo.setCreateUser(HttpUtils.getUserName(httpServletRequest));
        serviceInfo.setCreateTime(DateUtils.now());
        ServiceInfo savedServiceInfo = serviceInfoDao.save(serviceInfo);

        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&SUCCEED_TO_ADD_SERVICE_INFO}", new ServiceInfoResponse(savedServiceInfo));
    }

    @Override
    public GeneralResponse<Object> deleteServiceInfo(DeleteServiceInfoRequest request) throws UnExpectedRequestException {
        if (request == null || request.getId() == null) {
            throw new UnExpectedRequestException("ID {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        ServiceInfo serviceInfoInDb = serviceInfoDao.findById(request.getId());
        if (serviceInfoInDb == null) {
            throw new UnExpectedRequestException("Service {&DOES_NOT_EXIST}");
        }
        if (serviceInfoInDb.getTenantUser() != null) {
            throw new UnExpectedRequestException("{&USERD_TENANT}");
        }
        serviceInfoDao.delete(request.getId());
        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&SUCCEED_TO_DELETE_SERVICE_INFO}", null);
    }

    @Override
    public GeneralResponse<ServiceInfoResponse> modifyServiceInfo(ModifyServiceInfoRequest request) throws UnExpectedRequestException {
        if (request == null || request.getId() == null) {
            throw new UnExpectedRequestException("ID {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        ServiceInfo serviceInfo = serviceInfoDao.findById(request.getId());
        if (ServiceInfoStatusEnum.STOPED.getCode().equals(request.getStatus())) {
            int num = applicationDao.countNotFinishApplicationNum(serviceInfo.getIp());
            if (num > 0) {
                throw new UnExpectedRequestException("{&CAN_NOT_CLOSE_RUNNING_SERVICE_WITH_NOT_FINISH_APPLICATION}");
            }
            if (ServiceInfoCollectStatusEnum.OPEN.getCode().equals(request.getCollectStatus())) {
                throw new UnExpectedRequestException("Please close collect first.");
            }
        }
        serviceInfo.setStatus(request.getStatus());
        serviceInfo.setCollectStatus(request.getCollectStatus());
        serviceInfo.setModifyUser(HttpUtils.getUserName(httpServletRequest));
        serviceInfo.setModifyTime(DateUtils.now());

        ServiceInfo savedServiceInfo = serviceInfoDao.save(serviceInfo);
        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&SUCCEED_TO_MODIFY_SERVICE_INFO}", new ServiceInfoResponse(savedServiceInfo));
    }

    @Override
    public GeneralResponse<GetAllResponse<ServiceInfoResponse>> findAllServiceInfo(FindServiceInfoRequest request) throws UnExpectedRequestException {
        long page = request.getPage();
        long size = request.getSize();
        if (page < 0) {
            throw new UnExpectedRequestException("page should >= 0, request: " + request);
        }
        if (size <= 0) {
            throw new UnExpectedRequestException("size should > 0, request: " + request);
        }
        if (StringUtils.isEmpty(request.getIp())) {
            request.setIp("");
        }
        List<ServiceInfo> serviceInfoMapList = serviceInfoDao.pageServiceInfo(request.getIp(), request.getPage(), request.getSize());
        int total = ((Long) serviceInfoDao.countAll(request.getIp())).intValue();

        // Get application num
        List<Map<String, Object>> serviceWithApplicationNum = applicationDao.getServiceWithApplicationNum();

        List<ServiceInfoResponse> serviceInfoResponses = new ArrayList<>();
        for (ServiceInfo currentServiceInfoMap : serviceInfoMapList) {
            ServiceInfoResponse response = new ServiceInfoResponse(currentServiceInfoMap);
            String ip = response.getIp();
            for (Map<String, Object> map : serviceWithApplicationNum) {

                if (ip.equals(map.get("ip"))) {
                    Long num = (Long) map.get("num");
                    response.setUpdatingApplicationNum(num);
                    break;
                }
            }
            serviceInfoResponses.add(response);
        }

        GetAllResponse<ServiceInfoResponse> response = new GetAllResponse<>();
        response.setData(serviceInfoResponses);
        response.setTotal(total);

        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&SUCCEED_TO_FIND_SERVICE_INFOS}", response);
    }

    @Override
    public GeneralResponse<GetAllResponse<ServiceInfoResponse>> findFromTenantUser() {
        List<ServiceInfo> serviceInfoMapList = serviceInfoDao.findNonRelatedTenantUser();

        List<ServiceInfoResponse> serviceInfoResponses = serviceInfoMapList.stream().map(ServiceInfoResponse::new).collect(Collectors.toList());

        GetAllResponse<ServiceInfoResponse> response = new GetAllResponse<>();
        response.setData(serviceInfoResponses);

        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&SUCCEED_TO_FIND_SERVICE_INFOS}", response);
    }
}
