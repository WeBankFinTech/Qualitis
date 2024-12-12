package com.webank.wedatasphere.qualitis.service.impl;

import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.dao.DepartmentDao;
import com.webank.wedatasphere.qualitis.dao.RoleDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.entity.Department;
import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.response.DepartmentSubResponse;
import com.webank.wedatasphere.qualitis.request.DepartmentAddRequest;
import com.webank.wedatasphere.qualitis.request.DepartmentModifyRequest;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.request.QueryDepartmentRequest;
import com.webank.wedatasphere.qualitis.response.DepartmentResponse;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.service.DepartmentService;
import com.webank.wedatasphere.qualitis.service.ProxyUserService;
import com.webank.wedatasphere.qualitis.service.UserService;
import com.webank.wedatasphere.qualitis.util.DateUtils;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author allenzhou
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {
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

    public DepartmentServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<DepartmentResponse> addDepartment(DepartmentAddRequest request) throws UnExpectedRequestException {
        DepartmentAddRequest.checkRequest(request);
        if (departmentDao.findByName(request.getDepartmentName()) != null) {
            LOGGER.error("DepartmentName already exist.");
            throw new UnExpectedRequestException("department name {&ALREADY_EXIST}");
        }
        if (departmentDao.findByCode(request.getDepartmentCode()) != null) {
            LOGGER.error("DepartmentCode already exist.");
            throw new UnExpectedRequestException("department code {&ALREADY_EXIST}");
        }
        Department department = new Department();
        department.setName(request.getDepartmentName());
        department.setDepartmentCode(request.getDepartmentCode());
        department.setSourceType(request.getSourceType());
        department.setCreateUser(HttpUtils.getUserName(httpServletRequest));
        department.setCreateTime(DateUtils.now());
        Department savedDepartment = departmentDao.saveDepartment(department);
        LOGGER.info("Succeed to create department, saved department info is : {}", savedDepartment.toString());
        return new GeneralResponse<>("200", "{&ADD_DEPARTMENT_SUCCESSFULLY}", new DepartmentResponse(savedDepartment));
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse modifyDepartment(DepartmentModifyRequest request) throws UnExpectedRequestException {
        DepartmentModifyRequest.checkRequest(request);
        Department departmentInDb = departmentDao.findById(request.getDepartmentId());
        if (departmentInDb == null) {
            LOGGER.error("Department doesn't exist");
            throw new UnExpectedRequestException("Department {&DOES_NOT_EXIST}");
        }
        if (!departmentInDb.getName().equals(request.getDepartmentName())
                && departmentDao.findByName(request.getDepartmentName()) != null) {
            LOGGER.error("DepartmentName already exist.");
            throw new UnExpectedRequestException("department name {&ALREADY_EXIST}");
        }
        if (!departmentInDb.getDepartmentCode().equals(request.getDepartmentCode())) {
            throw new UnExpectedRequestException("Cannot to modify department code");
        }
        syncDepartmentName(departmentInDb, request);

        departmentInDb.setName(request.getDepartmentName());
        departmentInDb.setSourceType(request.getSourceType());
        departmentInDb.setModifyUser(HttpUtils.getUserName(httpServletRequest));
        departmentInDb.setModifyTime(DateUtils.now());
        Department savedDepartment = departmentDao.saveDepartment(departmentInDb);
        LOGGER.info("Succeed to modify department, saved department info is : {}", savedDepartment.toString());
        return new GeneralResponse<>("200", "{&MODIFY_DEPARTMENT_SUCCESSFULLY}", null);
    }

    /**
     * 如果名称发生变动，通知用户和代理用户同步更新部门名称
     * @param departmentInDb
     * @param request
     */
    private void syncDepartmentName(Department departmentInDb, DepartmentModifyRequest request) {
        boolean nameHasChanged = !departmentInDb.getName().equals(request.getDepartmentName());
        if (nameHasChanged) {
            userService.syncDepartmentName(departmentInDb, request.getDepartmentName());
            proxyUserService.syncDepartmentName(departmentInDb, request.getDepartmentName());
        }
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse deleteDepartment(Long departmentId) throws UnExpectedRequestException {
        if (departmentId == null) {
            throw new UnExpectedRequestException("department id {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        Department departmentInDb = departmentDao.findById(departmentId);
        if (departmentInDb == null) {
            LOGGER.error("Department donot exist.");
            throw new UnExpectedRequestException("Department {&DOES_NOT_EXIST}");
        }
        // Check user exists.
        checkUser(departmentInDb);
        // Check role exists.
        checkRole(departmentInDb);
        // Check tenant use.
        checkTenant(departmentInDb);
        departmentDao.deleteDepartment(departmentInDb);
        LOGGER.info("Succeed to delete department, saved department id is : {}", departmentId);
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

    @Override
    public GeneralResponse<GetAllResponse<DepartmentResponse>> findAllDepartment(QueryDepartmentRequest request) throws UnExpectedRequestException {
        PageRequest.checkRequest(request);
        int page = request.getPage();
        int size = request.getSize();
        Page<Department> departmentPage = departmentDao.findAllDepartment(request.getDepartmentName(), request.getSourceType(), page, size);
        GetAllResponse<DepartmentResponse> responses = new GetAllResponse<>();
        if (!departmentPage.isEmpty()) {
            List<DepartmentResponse> departmentResponses = departmentPage.getContent().stream().map(DepartmentResponse::new).collect(Collectors.toList());
            responses.setData(departmentResponses);
        }
        responses.setTotal(departmentPage.getTotalElements());
        LOGGER.info("Succeed to find all departments, page: {}, size: {}, departments: {}", page, size, responses);
        return new GeneralResponse(ResponseStatusConstants.OK, "{&GET_DEPARTMENT_SUCCESSFULLY}", responses);
    }

    @Override
    public List<DepartmentSubResponse> getSubDepartmentByDeptCode(Integer deptCode) {
        Department department = departmentDao.findByCode(String.valueOf(deptCode));
        if (department == null) {
            return Collections.EMPTY_LIST;
        }
        List<Department> subDepartmentList = departmentDao.findByParentId(department.getId());
        return subDepartmentList.stream().map(subDepartment -> {
            DepartmentSubResponse departmentSubResponse = new DepartmentSubResponse();
            departmentSubResponse.setName(subDepartment.getName());
            departmentSubResponse.setId(subDepartment.getDepartmentCode());
            return departmentSubResponse;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Department> findAllDepartmentCodeAndName() {
        return departmentDao.findAllDepartmentCodeAndName();
    }

}
