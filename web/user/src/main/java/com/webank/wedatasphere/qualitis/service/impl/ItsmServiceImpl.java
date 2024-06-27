package com.webank.wedatasphere.qualitis.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.webank.wedatasphere.qualitis.checkalert.dao.repository.CheckAlertWhiteListRepository;
import com.webank.wedatasphere.qualitis.checkalert.entity.CheckAlertWhiteList;
import com.webank.wedatasphere.qualitis.config.LinkisConfig;
import com.webank.wedatasphere.qualitis.constant.PositionRoleEnum;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constants.WhiteListTypeEnum;
import com.webank.wedatasphere.qualitis.dao.DepartmentDao;
import com.webank.wedatasphere.qualitis.dao.repository.UserProxyUserRepository;
import com.webank.wedatasphere.qualitis.dto.ItsmUserDto;
import com.webank.wedatasphere.qualitis.entity.Department;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.AddUserProxyUserRequest;
import com.webank.wedatasphere.qualitis.request.user.*;
import com.webank.wedatasphere.qualitis.service.ItsmService;
import com.webank.wedatasphere.qualitis.service.ProxyUserService;
import com.webank.wedatasphere.qualitis.service.UserProxyUserService;
import com.webank.wedatasphere.qualitis.service.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.RoleNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2024-03-12 16:40
 * @description
 */
@Service
public class ItsmServiceImpl implements ItsmService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItsmServiceImpl.class);

    @Autowired
    private UserService userService;
    @Autowired
    private DepartmentDao departmentDao;
    @Autowired
    private LinkisConfig linkisConfig;
    @Autowired
    private CheckAlertWhiteListRepository checkAlertWhiteListRepository;
    @Autowired
    private UserProxyUserService userProxyUserService;
    @Autowired
    private ProxyUserService proxyUserService;

    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    @Override
    public void handleAlertWhitelist(ItsmRequest request) throws UnExpectedRequestException {
        List<ItsmAlertWhitelistRequest> dataList = request.getItsmAlertWhitelist();
        if (CollectionUtils.isEmpty(dataList)) {
            throw new UnExpectedRequestException("The dataList must be not empty.");
        }

        List<CheckAlertWhiteList> addCheckAlertWhiteList = new ArrayList<>();
        List<CheckAlertWhiteList> deleteCheckAlertWhiteList = new ArrayList<>();
        formatAndCheckAlertWhitelistRequest(dataList, addCheckAlertWhiteList, deleteCheckAlertWhiteList);

        if (CollectionUtils.isNotEmpty(addCheckAlertWhiteList)) {
            for (CheckAlertWhiteList checkAlertWhiteList : addCheckAlertWhiteList) {
                CheckAlertWhiteList checkAlertWhiteListInDb = checkAlertWhiteListRepository.checkWhiteList(checkAlertWhiteList.getItem(), checkAlertWhiteList.getType(), checkAlertWhiteList.getAuthorizedUser());
                if (checkAlertWhiteListInDb != null && checkAlertWhiteListInDb.getId() != null) {
                    throw new UnExpectedRequestException("The record already exists.");
                }
            }
            checkAlertWhiteListRepository.saveAll(addCheckAlertWhiteList);
        }

        if (CollectionUtils.isNotEmpty(deleteCheckAlertWhiteList)) {
            for (CheckAlertWhiteList checkAlertWhiteList : deleteCheckAlertWhiteList) {
                CheckAlertWhiteList checkAlertWhiteListInDb = checkAlertWhiteListRepository.checkWhiteList(checkAlertWhiteList.getItem(), checkAlertWhiteList.getType(), checkAlertWhiteList.getAuthorizedUser());
                if (checkAlertWhiteListInDb != null && checkAlertWhiteListInDb.getId() != null) {
                    checkAlertWhiteListRepository.delete(checkAlertWhiteListInDb);
                }
            }
        }
    }

    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    @Override
    public void handleUser(ItsmRequest request) throws UnExpectedRequestException, RoleNotFoundException {
        List<ItsmUserRequest> itsmUserRequestList = request.getItsmUserList();
        if (CollectionUtils.isEmpty(itsmUserRequestList)) {
            throw new UnExpectedRequestException("The dataList must be not empty.");
        }
        List<ItsmUserDto> itsmUserDtoList = new ArrayList<>();
        List<String> deleteUserList = new ArrayList<>();
        formatAndCheckUserRequest(itsmUserRequestList, itsmUserDtoList, deleteUserList);

        for (ItsmUserDto itsmUserDto : itsmUserDtoList) {
            addUser(itsmUserDto);
        }

        for (String deleteUserName: deleteUserList) {
            User user = userService.findByUsername(deleteUserName);
            if (user != null) {
                UserRequest userRequest = new UserRequest();
                userRequest.setUserId(user.getId());
                userService.deleteUser(userRequest);
            }
        }
    }

    @Override
    public void addUser(ItsmUserDto itsmUserDto) throws UnExpectedRequestException, RoleNotFoundException {
        UserAddRequest userAddRequest = new UserAddRequest();
        userAddRequest.setUsername(itsmUserDto.getUsername());
        userAddRequest.setPositionEn(itsmUserDto.getPositionEn());
        userAddRequest.setPositionZh(itsmUserDto.getPositionZn());
        Department department = departmentDao.findByName(itsmUserDto.getDepartmentName());
        if (department == null) {
            throw new UnExpectedRequestException("The department is invalid: " + itsmUserDto.getDepartmentName());
        }
        Department subDepartment = departmentDao.findByName(itsmUserDto.getSubDepartmentName());
        if (subDepartment == null) {
            throw new UnExpectedRequestException("The department is invalid: " + itsmUserDto.getSubDepartmentName());
        }
        userAddRequest.setDepartmentId(department.getId());
        userAddRequest.setDepartmentSubCode(subDepartment.getDepartmentCode());
        userAddRequest.setDepartmentName(department.getName() + "/" + subDepartment.getName());
        userService.addItsmUser(userAddRequest);

        if (StringUtils.isNotBlank(itsmUserDto.getProxyUser())) {
            Iterable<String> proxyUserIterable = Splitter.on(SpecCharEnum.COMMA.getValue()).omitEmptyStrings().trimResults().split(itsmUserDto.getProxyUser());
            proxyUserIterable.forEach(proxyUser -> addUserProxyUser(itsmUserDto.getUsername(), proxyUser));
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    private void addUserProxyUser (String username, String proxyUserName) {
        try {
            userService.addUserIfNotExists(proxyUserName);
            proxyUserService.addProxyUserIfNotExists(proxyUserName);

            AddUserProxyUserRequest addUserProxyUserRequest = new AddUserProxyUserRequest();
            addUserProxyUserRequest.setUsername(username);
            addUserProxyUserRequest.setProxyUserName(proxyUserName);
            userProxyUserService.addUserProxyUser(addUserProxyUserRequest);
        } catch (UnExpectedRequestException e) {
            LOGGER.warn("Failed add proxyUser: {}, username: {}", proxyUserName, username, e.getMessage());
        } catch (RoleNotFoundException e) {
            LOGGER.warn("Failed add proxyUser: {}, username: {}", proxyUserName, username, e.getMessage());
        }
    }

    private void formatAndCheckAlertWhitelistRequest(List<ItsmAlertWhitelistRequest> dataList, List<CheckAlertWhiteList> addCheckAlertWhiteList, List<CheckAlertWhiteList> deleteCheckAlertWhiteList) {
        for (ItsmAlertWhitelistRequest alertWhitelistRequest : dataList) {
            String operationType = alertWhitelistRequest.getOperType();
            String authorizedUser = alertWhitelistRequest.getWebankName();
            String database = alertWhitelistRequest.getDatabase();
            String table = alertWhitelistRequest.getTable();
            Preconditions.checkArgument(StringUtils.isNotEmpty(operationType), "oper_type must be not empty");
            Preconditions.checkArgument(StringUtils.isNotEmpty(authorizedUser), "webankName must be not empty");
            Preconditions.checkArgument(StringUtils.isNotEmpty(database), "database must be not empty");
            Preconditions.checkArgument(StringUtils.isNotEmpty(table), "table must be not empty");

            CheckAlertWhiteList checkAlertWhiteList = new CheckAlertWhiteList();
            checkAlertWhiteList.setType(WhiteListTypeEnum.CHECK_ALERT_TABLE.getCode());
            checkAlertWhiteList.setAuthorizedUser(authorizedUser);
            checkAlertWhiteList.setItem(linkisConfig.getBdapCheckAlertCluster()
                    + SpecCharEnum.PERIOD_NO_ESCAPE.getValue()
                    + database
                    + SpecCharEnum.PERIOD_NO_ESCAPE.getValue()
                    + table);

            if ("add".equals(operationType)) {
                addCheckAlertWhiteList.add(checkAlertWhiteList);
            } else {
                deleteCheckAlertWhiteList.add(checkAlertWhiteList);
            }
        }
    }

    private void formatAndCheckUserRequest(List<ItsmUserRequest> itsmUserRequestList, List<ItsmUserDto> itsmUserDtos, List<String> deleteUserList) throws UnExpectedRequestException {
        for (ItsmUserRequest itsmUserRequest : itsmUserRequestList) {
            String operationType = itsmUserRequest.getOperType();
            String webankName = itsmUserRequest.getWebankName();
            Preconditions.checkArgument(StringUtils.isNotEmpty(operationType), "oper_type must be not empty");
            Preconditions.checkArgument(StringUtils.isNotEmpty(webankName), "webankName must be not empty");

            if ("add".equals(operationType)) {
                String proxyUser = itsmUserRequest.getProxyUser();
                String depName = itsmUserRequest.getDepName();
                String postName = itsmUserRequest.getPostName();

                Preconditions.checkArgument(StringUtils.isNotEmpty(operationType), "oper_type must be not empty");
                Preconditions.checkArgument(StringUtils.isNotEmpty(webankName), "webankName must be not empty");
                Preconditions.checkArgument(StringUtils.isNotEmpty(depName), "depName must be not empty");
                Preconditions.checkArgument(StringUtils.isNotEmpty(postName), "postName must be not empty");

                String[] dept = StringUtils.split(depName, SpecCharEnum.MINUS.getValue());
                if (dept.length != 2) {
                    throw new UnExpectedRequestException("parameter format error: depName");
                }
                ItsmUserDto addRequest = new ItsmUserDto();
                addRequest.setUsername(webankName);
                addRequest.setDepartmentName(dept[0]);
                addRequest.setSubDepartmentName(dept[1]);
                addRequest.setProxyUser(proxyUser);

                if (postName.contains(PositionRoleEnum.TEST_POSITION.getMessage())) {
                    addRequest.setPositionZn(PositionRoleEnum.TEST_POSITION.getMessage());
                    addRequest.setPositionEn(PositionRoleEnum.TEST_POSITION.getCode());
                } else if (postName.contains(PositionRoleEnum.DEVELOPMENT_POSITION.getMessage())) {
                    addRequest.setPositionZn(PositionRoleEnum.DEVELOPMENT_POSITION.getMessage());
                    addRequest.setPositionEn(PositionRoleEnum.DEVELOPMENT_POSITION.getCode());
                } else if (postName.contains(PositionRoleEnum.OPS_POSITION.getMessage())) {
                    addRequest.setPositionZn(PositionRoleEnum.OPS_POSITION.getMessage());
                    addRequest.setPositionEn(PositionRoleEnum.OPS_POSITION.getCode());
                }

                itsmUserDtos.add(addRequest);
            } else {
                deleteUserList.add(webankName);
            }

        }
    }

}
