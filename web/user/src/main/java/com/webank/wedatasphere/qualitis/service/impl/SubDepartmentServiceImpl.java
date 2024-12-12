package com.webank.wedatasphere.qualitis.service.impl;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.dao.DepartmentDao;
import com.webank.wedatasphere.qualitis.dao.RoleDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.entity.Department;
import com.webank.wedatasphere.qualitis.entity.ProxyUserDepartment;
import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.request.QueryDepartmentRequest;
import com.webank.wedatasphere.qualitis.request.SubDepartmentAddRequest;
import com.webank.wedatasphere.qualitis.request.SubDepartmentModifyRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.response.SubDepartmentResponse;
import com.webank.wedatasphere.qualitis.service.ProxyUserService;
import com.webank.wedatasphere.qualitis.service.SubDepartmentService;
import com.webank.wedatasphere.qualitis.service.UserService;
import com.webank.wedatasphere.qualitis.util.DateUtils;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author v_minminghe@webank.com
 * @date 2023-06-14 9:48
 * @description
 */
@Service
public class SubDepartmentServiceImpl implements SubDepartmentService {

    @Autowired
    private DepartmentDao departmentDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserService userService;

    @Autowired
    private ProxyUserService proxyUserService;

    @Autowired
    private RoleDao roleDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    private HttpServletRequest httpServletRequest;

    public SubDepartmentServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public GeneralResponse<SubDepartmentResponse> addSubDepartment(SubDepartmentAddRequest request) throws UnExpectedRequestException {
        SubDepartmentAddRequest.checkRequest(request);
        if (departmentDao.findByName(request.getSubDepartmentName()) != null) {
            LOGGER.error("DepartmentName already exist.");
            throw new UnExpectedRequestException("sub department name {&ALREADY_EXIST}");
        }
        if (departmentDao.findByCode(request.getSubDepartmentCode()) != null) {
            LOGGER.error("DepartmentCode already exist.");
            throw new UnExpectedRequestException("sub department code {&ALREADY_EXIST}");
        }
        Department department = new Department();
        department.setName(request.getSubDepartmentName());
        department.setDepartmentCode(request.getSubDepartmentCode());
        Department parentDepartment = departmentDao.findByCode(request.getDepartmentCode());
        department.setParentId(parentDepartment.getId());
        department.setSourceType(request.getSourceType());
        department.setCreateUser(HttpUtils.getUserName(httpServletRequest));
        department.setCreateTime(DateUtils.now());
        Department savedDepartment = departmentDao.saveDepartment(department);
        SubDepartmentResponse departmentResponse = new SubDepartmentResponse(savedDepartment);
        departmentResponse.setDepartmentName(request.getDepartmentName());
        departmentResponse.setDepartmentCode(request.getDepartmentCode());
        LOGGER.info("Succeed to create department, saved department info is : {}", savedDepartment.toString());
        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&ADD_DEPARTMENT_SUCCESSFULLY}", departmentResponse);
    }

    @Override
    public GeneralResponse modifySubDepartment(SubDepartmentModifyRequest request) throws UnExpectedRequestException {
        SubDepartmentModifyRequest.checkRequest(request);
        Department departmentInDb = departmentDao.findById(request.getSubDepartmentId());
        if (departmentInDb == null) {
            LOGGER.error("subDepartment doesn't exist");
            throw new UnExpectedRequestException("subDepartment {&DOES_NOT_EXIST}");
        }
        if (!departmentInDb.getName().equals(request.getSubDepartmentName())
                && departmentDao.findByName(request.getSubDepartmentName()) != null) {
            LOGGER.error("subDepartmentName already exist.");
            throw new UnExpectedRequestException("sub department name {&ALREADY_EXIST}");
        }
        if (!departmentInDb.getDepartmentCode().equals(request.getSubDepartmentCode())) {
            throw new UnExpectedRequestException("Cannot to modify sub department code");
        }

        syncDepartmentName(departmentInDb, request);
        checkUserIfDepartmentHasChanged(departmentInDb, request);

        departmentInDb.setName(request.getSubDepartmentName());
        Department parentDepartment = departmentDao.findByCode(request.getDepartmentCode());
        departmentInDb.setParentId(parentDepartment.getId());
        departmentInDb.setSourceType(request.getSourceType());
        departmentInDb.setModifyUser(HttpUtils.getUserName(httpServletRequest));
        departmentInDb.setModifyTime(DateUtils.now());
        Department savedDepartment = departmentDao.saveDepartment(departmentInDb);
        LOGGER.info("Succeed to modify department, saved department info is : {}", savedDepartment.toString());
        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&MODIFY_DEPARTMENT_SUCCESSFULLY}", null);
    }

    /**
     * 如果名称发生变动，通知用户和代理用户同步更新科室名称
     * @param departmentInDb
     * @param request
     */
    private void syncDepartmentName(Department departmentInDb, SubDepartmentModifyRequest request) {
        boolean nameHasChanged = !departmentInDb.getName().equals(request.getSubDepartmentName());
        if (nameHasChanged) {
            userService.syncSubDepartmentName(Long.valueOf(departmentInDb.getDepartmentCode()), request.getSubDepartmentName());
            proxyUserService.syncSubDepartmentName(Long.valueOf(departmentInDb.getDepartmentCode()), request.getSubDepartmentName());
        }
    }

    /**
     * 如果变更了所属部门，需要校验是否存在使用了该科室的用户，防止部门-科室映射关系的破坏，导致关联的用户出现错误
     * @param departmentInDb
     * @param request
     * @throws UnExpectedRequestException
     */
    private void checkUserIfDepartmentHasChanged(Department departmentInDb, SubDepartmentModifyRequest request) throws UnExpectedRequestException {
        Department parentDepartmentInDb = departmentDao.findById(departmentInDb.getParentId());
        boolean parentHasChanged = !parentDepartmentInDb.getDepartmentCode().equals(request.getDepartmentCode());
        if (parentHasChanged) {
            List<User> userList = userDao.findBySubDepartmentCode(Long.valueOf(departmentInDb.getDepartmentCode()));
            if (CollectionUtils.isNotEmpty(userList)) {
                LOGGER.error("Has been associated with users: {}", request.getDepartmentName());
                throw new UnExpectedRequestException("Has been associated with users: " + request.getDepartmentName());
            }
            List<ProxyUserDepartment> proxyUserDepartmentList = proxyUserService.findBySubDepartmentCode(Long.valueOf(departmentInDb.getDepartmentCode()));
            if (CollectionUtils.isNotEmpty(proxyUserDepartmentList)) {
                LOGGER.error("Has been associated with proxyUsers: {}", request.getDepartmentName());
                throw new UnExpectedRequestException("Has been associated with proxyUsers: " + request.getDepartmentName());
            }
        }
    }

    @Override
    public GeneralResponse<GetAllResponse<SubDepartmentResponse>> findAllSubDepartment(QueryDepartmentRequest request) throws UnExpectedRequestException {
        PageRequest.checkRequest(request);
        if (StringUtils.isNotBlank(request.getDepartmentName())) {
            request.setDepartmentName(SpecCharEnum.PERCENT.getValue() + request.getDepartmentName() + SpecCharEnum.PERCENT.getValue());
        } else {
            request.setDepartmentName(null);
        }
        if (StringUtils.isNotBlank(request.getSubDepartmentName())) {
            request.setSubDepartmentName(SpecCharEnum.PERCENT.getValue() + request.getSubDepartmentName() + SpecCharEnum.PERCENT.getValue());
        } else {
            request.setSubDepartmentName(null);
        }
        if (StringUtils.isBlank(request.getDepartmentCode())) {
            request.setDepartmentCode(null);
        }
        Page<Department> departmentPage = departmentDao.findAllSubDepartment(request.getDepartmentName(), request.getSubDepartmentName(), request.getDepartmentCode(), request.getPage(), request.getSize());
        List<Department> subDepartmentList = departmentPage.getContent();

        Map<Long, Department> parentDepartmentMap = getParentDepartmentMap(subDepartmentList);

        List<SubDepartmentResponse> departmentResponses = Lists.newArrayListWithExpectedSize(subDepartmentList.size());
        for (Department subDepartment : subDepartmentList) {
            SubDepartmentResponse departmentResponse = new SubDepartmentResponse(subDepartment);
//        设置【所属部门】
            Department parentDepartment = parentDepartmentMap.get(subDepartment.getParentId());
            if (parentDepartment != null) {
                departmentResponse.setDepartmentName(parentDepartment.getName());
                departmentResponse.setDepartmentCode(parentDepartment.getDepartmentCode());
            }
            departmentResponses.add(departmentResponse);
        }

        GetAllResponse<SubDepartmentResponse> responses = new GetAllResponse<>();
        responses.setData(departmentResponses);
        responses.setTotal(departmentPage.getTotalElements());
        return new GeneralResponse(ResponseStatusConstants.OK, "{&GET_DEPARTMENT_SUCCESSFULLY}", responses);
    }

    private Map<Long, Department> getParentDepartmentMap(List<Department> subDepartmentList) {
        Map<Long, Department> parentDepartmentMap = Collections.emptyMap();
        if (CollectionUtils.isNotEmpty(subDepartmentList)) {
            List<Long> parentIds = subDepartmentList.stream().map(Department::getParentId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
            List<Department> parentDepartmentList = departmentDao.findByIds(parentIds);
            parentDepartmentMap = parentDepartmentList.stream().collect(Collectors.toMap(Department::getId, Function.identity(), (oldVal, newVal) -> oldVal));
        }
        return parentDepartmentMap;
    }

    @Override
    public GeneralResponse deleteDepartment(Long subDepartmentId) throws UnExpectedRequestException {
        if (subDepartmentId == null) {
            throw new UnExpectedRequestException("sub department id {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        Department departmentInDb = departmentDao.findById(subDepartmentId);
        if (departmentInDb == null) {
            LOGGER.error("Department donot exist.");
            throw new UnExpectedRequestException("subDepartment {&DOES_NOT_EXIST}");
        }
        // Check user exists.
        checkUser(departmentInDb);
        // Check role exists.
        checkRole(departmentInDb);
        // Check tenant use.
        checkTenant(departmentInDb);
        departmentDao.deleteDepartment(departmentInDb);
        LOGGER.info("Succeed to delete department, saved department id is : {}", subDepartmentId);
        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&DELETE_DEPARTMENT_SUCCESSFULLY}", null);
    }


    private void checkTenant(Department departmentInDb) throws UnExpectedRequestException {
        if (departmentInDb.getTenantUser() != null) {
            throw new UnExpectedRequestException("{&USERD_TENANT}");
        }
    }

    private void checkRole(Department departmentInDb) throws UnExpectedRequestException {
        Role role = roleDao.findByDepartment(departmentInDb);
        if (role != null) {
            throw new UnExpectedRequestException("{&DEPARTMENT_HAS_ROLES}");
        }
    }

    private void checkUser(Department departmentInDb) throws UnExpectedRequestException {
        List<User> users = userDao.findByDepartment(departmentInDb);
        if (users != null && users.size() > 0) {
            throw new UnExpectedRequestException("{&DEPARTMENT_HAS_USERS}");
        }
    }

}
