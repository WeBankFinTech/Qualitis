package com.webank.wedatasphere.qualitis.service.impl;

import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.entity.TenantUser;
import com.webank.wedatasphere.qualitis.entity.UserTenantUser;
import com.webank.wedatasphere.qualitis.dao.repository.TenantUserRepository;
import com.webank.wedatasphere.qualitis.dao.repository.UserTenantUserRepository;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.DeleteUserTenantUserRequest;
import com.webank.wedatasphere.qualitis.response.AddUserTenantUserResponse;
import com.webank.wedatasphere.qualitis.request.AddUserTenantUserRequest;
import com.webank.wedatasphere.qualitis.service.UserTenantUserService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

/**
 * @author allenzhou@webank.com
 * @date 2022/5/7 12:05
 */
@Service
public class UserTenantUserServiceImpl implements UserTenantUserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private TenantUserRepository tenantUserRepository;

    @Autowired
    private UserTenantUserRepository userTenantUserRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserTenantUserServiceImpl.class);

    @Override
    public GeneralResponse<AddUserTenantUserResponse> addUserTenantUser(AddUserTenantUserRequest request) throws UnExpectedRequestException {
        // Check Arguments
        AddUserTenantUserRequest.checkRequest(request);

        // Find user and Tenant user by username
        User userInDb = userDao.findByUsername(request.getUsername());
        if (userInDb == null) {
            throw new UnExpectedRequestException("User {&DOES_NOT_EXIST}, request: " + request);
        }
        TenantUser tenantUserInDb = tenantUserRepository.findByTenantName(request.getTenantUserName());
        if (tenantUserInDb == null) {
            throw new UnExpectedRequestException("Tenant user {&DOES_NOT_EXIST}, request: " + request);
        }

        UserTenantUser userTenantUserInDb = userTenantUserRepository.findByUserAndTenantUser(userInDb, tenantUserInDb);
        if (userTenantUserInDb != null) {
            throw new UnExpectedRequestException("User tenant user {&ALREADY_EXIST}, request: " + request);
        }

        UserTenantUser newUserTenantUser = new UserTenantUser();
        newUserTenantUser.setTenantUser(tenantUserInDb);
        newUserTenantUser.setUser(userInDb);
        UserTenantUser savedUserTenantUser = userTenantUserRepository.save(newUserTenantUser);
        LOGGER.info("Succeed to save user tenant user. User tenant user id: {}", savedUserTenantUser.getId());

        AddUserTenantUserResponse response = new AddUserTenantUserResponse(savedUserTenantUser);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_CREATE_USER_TENANT_USER}", response);
    }

    @Override
    public GeneralResponse deleteUserTenantUser(DeleteUserTenantUserRequest request) throws UnExpectedRequestException {
        // Check Arguments
        DeleteUserTenantUserRequest.checkRequest(request);

        // Find user Tenant user by id
        UserTenantUser userTenantUserInDb = userTenantUserRepository.findById(request.getUserTenantUserId()).orElse(null);
        if (userTenantUserInDb == null) {
            throw new UnExpectedRequestException("User tenant user {&DOES_NOT_EXIST}, request: " + request);
        }

        userTenantUserRepository.delete(userTenantUserInDb);
        LOGGER.info("Succeed to delete user tenant user. user_Tenant_user_id: {}", request.getUserTenantUserId());
        return new GeneralResponse<>("200", "{&SUCCEED_TO_DELETE_USER_TENANT_USER}", null);
    }

    @Override
    public GeneralResponse<GetAllResponse<AddUserTenantUserResponse>> getAllUserTenantUserByTenantUserName(String tenantUserName, PageRequest request) throws UnExpectedRequestException {
        // Check Arguments
        PageRequest.checkRequest(request);

        // Check existence of tenant user
        TenantUser tenantUserInDb = tenantUserRepository.findByTenantName(tenantUserName);
        if (tenantUserInDb == null) {
            throw new UnExpectedRequestException("Tenant user {&DOES_NOT_EXIST}, request: " + request);
        }

        // Find user Tenant user by Tenant user
        int page = request.getPage();
        int size = request.getSize();
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, sort);
        List<UserTenantUser> userTenantUsers = userTenantUserRepository.findByTenantUser(tenantUserInDb, pageable);
        long total = userTenantUserRepository.countByTenantUser(tenantUserInDb);

        List<AddUserTenantUserResponse> userTenantUserResponses = new ArrayList<>();
        for (UserTenantUser userTenantUser : userTenantUsers) {
            AddUserTenantUserResponse tmp = new AddUserTenantUserResponse(userTenantUser);
            userTenantUserResponses.add(tmp);
        }
        GetAllResponse<AddUserTenantUserResponse> response = new GetAllResponse<>();
        response.setTotal(total);
        response.setData(userTenantUserResponses);

        LOGGER.info("Succeed to find all user tenant users by tenant user name, response: {}", response);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_FIND_ALL_USER_TENANT_USERS}", response);
    }
}
