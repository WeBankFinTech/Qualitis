package com.webank.wedatasphere.qualitis.service.impl;

import com.webank.wedatasphere.qualitis.dao.DepartmentDao;
import com.webank.wedatasphere.qualitis.dao.ServiceInfoDao;
import com.webank.wedatasphere.qualitis.dao.repository.TenantUserRepository;
import com.webank.wedatasphere.qualitis.dao.repository.UserTenantUserRepository;
import com.webank.wedatasphere.qualitis.entity.Department;
import com.webank.wedatasphere.qualitis.entity.ServiceInfo;
import com.webank.wedatasphere.qualitis.entity.TenantUser;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.AddTenantUserRequest;
import com.webank.wedatasphere.qualitis.request.DeleteTenantUserRequest;
import com.webank.wedatasphere.qualitis.request.ModifyTenantUserRequest;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.request.QueryTenantUserRequest;
import com.webank.wedatasphere.qualitis.response.AddTenantUserResponse;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.service.TenantUserService;
import java.util.HashSet;
import java.util.Set;

import com.webank.wedatasphere.qualitis.util.DateUtils;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.ArrayList;
import java.util.List;

/**
 * @author allenzhou@webank.com
 * @date 2022/5/7 11:40
 */
@Service
public class TenantUserServiceImpl implements TenantUserService {
    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    private ServiceInfoDao serviceInfoDao;

    @Autowired
    private TenantUserRepository tenantUserRepository;

    @Autowired
    private UserTenantUserRepository userTenantUserRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantUserServiceImpl.class);

    private HttpServletRequest httpServletRequest;

    public TenantUserServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<AddTenantUserResponse> addTenantUser(AddTenantUserRequest request) throws UnExpectedRequestException {
        // Check Arguments
        AddTenantUserRequest.checkRequest(request);

        // Check existence of tenant user
        TenantUser tenantUserInDb = tenantUserRepository.findByTenantName(request.getTenantUserName());
        if (tenantUserInDb != null) {
            throw new UnExpectedRequestException("TenantUser name: [" + request.getTenantUserName() +  "] {&ALREADY_EXIST}");
        }

        // Save tenant user
        TenantUser tenantUser = new TenantUser();
        tenantUser.setTenantName(request.getTenantUserName());
        tenantUser.setCreateUser(HttpUtils.getUserName(httpServletRequest));
        tenantUser.setCreateTime(DateUtils.now());
        TenantUser savedTenantUser = tenantUserRepository.save(tenantUser);
        LOGGER.info("Succeed to save tenant user. Tenant user: {}", savedTenantUser.getTenantName());
        Set<Department> departments = new HashSet<>(request.getDeptIds().size());
        Set<ServiceInfo> serviceInfos = new HashSet<>(request.getServices().size());
        for (Long deptId : request.getDeptIds()) {
            Department department = departmentDao.findById(deptId);
            if (department == null) {
                throw new UnExpectedRequestException("Department " +  "{&DOES_NOT_EXIST}");
            }
            if (department.getTenantUser() != null) {
                throw new UnExpectedRequestException(department.getName() +  "{&OCCUPIED}");
            }
            department.setTenantUser(savedTenantUser);
            department.setModifyUser(HttpUtils.getUserName(httpServletRequest));
            department.setModifyTime(DateUtils.now());
            departments.add(departmentDao.saveDepartment(department));
        }
        for (Long service : request.getServices()) {
            ServiceInfo serviceInfo = serviceInfoDao.findById(service);
            if (serviceInfo == null) {
                throw new UnExpectedRequestException("Service of qualitis " +  "{&DOES_NOT_EXIST}");
            }
            if (serviceInfo.getTenantUser() != null) {
                throw new UnExpectedRequestException(serviceInfo.getIp() +  "{&OCCUPIED}");
            }
            serviceInfo.setTenantUser(savedTenantUser);
            serviceInfos.add(serviceInfoDao.save(serviceInfo));
        }
        savedTenantUser.setDepts(departments);
        savedTenantUser.setServiceInfos(serviceInfos);
        AddTenantUserResponse response = new AddTenantUserResponse(savedTenantUser);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_SAVE_TENANT_USER}", response);
    }

    @Override
    public GeneralResponse deleteTenantUser(DeleteTenantUserRequest request) throws UnExpectedRequestException {
        // Check Arguments
        DeleteTenantUserRequest.checkRequest(request);

        // Check existence of tenant user
        TenantUser tenantUserInDb = tenantUserRepository.findById(request.getTenantUserId()).orElse(null);
        if (tenantUserInDb == null) {
            throw new UnExpectedRequestException("Tenant user id: " + request.getTenantUserId() + " {&DOES_NOT_EXIST}");
        }
        // Clear relation
        List<Department> departments = departmentDao.findByTenantUser(tenantUserInDb);
        for (Department department : departments) {
            department.setTenantUser(null);
            department.setModifyUser(HttpUtils.getUserName(httpServletRequest));
            department.setModifyTime(DateUtils.now());
            departmentDao.saveDepartment(department);
        }
        List<ServiceInfo> serviceInfos = serviceInfoDao.findByTenantUser(tenantUserInDb);
        for (ServiceInfo serviceInfo : serviceInfos) {
            serviceInfo.setTenantUser(null);
            serviceInfoDao.save(serviceInfo);
        }
        tenantUserRepository.delete(tenantUserInDb);
        LOGGER.info("Succeed to delete tenant user. Tenant user: {}", tenantUserInDb.getTenantName());

        return new GeneralResponse<>("200", "{&SUCCEED_TO_DELETE_TENANT_USER}", null);
    }

    @Override
    public GeneralResponse modifyTenantUser(ModifyTenantUserRequest request) throws UnExpectedRequestException {
        // Check Arguments
        ModifyTenantUserRequest.checkRequest(request);

        // Check existence of Tenant user
        TenantUser tenantUserInDb = tenantUserRepository.findById(request.getTenantUserId()).orElse(null);
        if (tenantUserInDb == null) {
            throw new UnExpectedRequestException("Tenant user id: [" + request.getTenantUserId() + "] {&DOES_NOT_EXIST}");
        }
        String oldTenantUserName = tenantUserInDb.getTenantName();
        // Modify Tenant user name
        tenantUserInDb.setTenantName(request.getTenantUserName());
        tenantUserInDb.setModifyUser(HttpUtils.getUserName(httpServletRequest));
        tenantUserInDb.setModifyTime(DateUtils.now());
        tenantUserRepository.save(tenantUserInDb);
        clearDeptAndService(tenantUserInDb);
        Set<Department> departments = new HashSet<>(request.getDeptIds().size());
        Set<ServiceInfo> serviceInfos = new HashSet<>(request.getServices().size());
        for (Long deptId : request.getDeptIds()) {
            Department department = departmentDao.findById(deptId);
            if (department == null) {
                throw new UnExpectedRequestException("Department " +  "{&DOES_NOT_EXIST}");
            }
            department.setTenantUser(tenantUserInDb);
            department.setModifyUser(HttpUtils.getUserName(httpServletRequest));
            department.setModifyTime(DateUtils.now());
            departments.add(departmentDao.saveDepartment(department));
        }
        for (Long service : request.getServices()) {
            ServiceInfo serviceInfo = serviceInfoDao.findById(service);
            if (serviceInfo == null) {
                throw new UnExpectedRequestException("Service of qualitis " +  "{&DOES_NOT_EXIST}");
            }
            serviceInfo.setTenantUser(tenantUserInDb);
            serviceInfos.add(serviceInfoDao.save(serviceInfo));
        }
        tenantUserInDb.setDepts(departments);
        tenantUserInDb.setServiceInfos(serviceInfos);

        LOGGER.info("Succeed to modify tenant user. old tenant user name: {}, new tenant user name: {}", oldTenantUserName, tenantUserInDb.getTenantName());
        return new GeneralResponse<>("200", "{&SUCCEED_TO_MODIFY_TENANT_USER}", null);
    }

    private void clearDeptAndService(TenantUser tenantUserInDb) {
        if (CollectionUtils.isNotEmpty(tenantUserInDb.getDepts())) {
            for (Department department : tenantUserInDb.getDepts()) {
                department.setTenantUser(null);
                department.setModifyUser(HttpUtils.getUserName(httpServletRequest));
                department.setModifyTime(DateUtils.now());
                departmentDao.saveDepartment(department);
            }
            tenantUserInDb.setDepts(null);
        }
        if (CollectionUtils.isNotEmpty(tenantUserInDb.getServiceInfos())) {
            for (ServiceInfo serviceInfo : tenantUserInDb.getServiceInfos()) {
                serviceInfo.setTenantUser(null);
                serviceInfoDao.save(serviceInfo);
            }
            tenantUserInDb.setServiceInfos(null);
        }
    }

    @Override
    public GeneralResponse<GetAllResponse<AddTenantUserResponse>> getAllTenantUser(QueryTenantUserRequest request) throws UnExpectedRequestException {
        // Query by page and size
        int page = request.getPage();
        int size = request.getSize();
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, sort);
        List<TenantUser> tenantUsers = tenantUserRepository.findByTenantNameWithPage(StringUtils.isNotBlank(request.getTenantUserName()) ? "%" + request.getTenantUserName() + "%" : "", pageable).getContent();
        long total = tenantUserRepository.count();

        List<AddTenantUserResponse> tenantUserResponses = new ArrayList<>();
        for (TenantUser tenantUser : tenantUsers) {
            AddTenantUserResponse tmp = new AddTenantUserResponse(tenantUser);
            long membersNum = userTenantUserRepository.countByTenantUser(tenantUser);
            tmp.setTenantUserMembersNum(membersNum);
            tenantUserResponses.add(tmp);
        }
        GetAllResponse<AddTenantUserResponse> response = new GetAllResponse<>();
        response.setTotal(total);
        response.setData(tenantUserResponses);

        LOGGER.info("Succeed to find all tenant users, response: {}", response);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_FIND_ALL_TENANT_USERS}", response);
    }
}
