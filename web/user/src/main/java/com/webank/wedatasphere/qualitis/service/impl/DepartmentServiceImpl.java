package com.webank.wedatasphere.qualitis.service.impl;

import com.webank.wedatasphere.qualitis.dao.DepartmentDao;
import com.webank.wedatasphere.qualitis.dao.RoleDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.entity.Department;
import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.DepartmentAddRequest;
import com.webank.wedatasphere.qualitis.request.DepartmentModifyRequest;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.DepartmentResponse;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.service.DepartmentService;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private RoleDao roleDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<DepartmentResponse> addDepartment(DepartmentAddRequest request) throws UnExpectedRequestException {
        Department departmentInDb = departmentDao.findByName(request.getDepartmentName());
        if (departmentInDb != null) {
            LOGGER.error("Department already exist.");
            throw new UnExpectedRequestException("Department {&ALREADY_EXIST}");
        }
        Department department = new Department();
        department.setName(request.getDepartmentName());
        Department savedDepartment = departmentDao.saveDepartment(department);
        LOGGER.info("Succeed to create department, saved department info is : {}", savedDepartment.toString());
        return new GeneralResponse<>("200", "{&ADD_DEPARTMENT_SUCCESSFULLY}", new DepartmentResponse(savedDepartment));
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> modifyDepartment(DepartmentModifyRequest request) throws UnExpectedRequestException {
        request.checkRequest();
        Department departmentInDb = departmentDao.findById(request.getDepartmentId());
        if (departmentInDb == null) {
            LOGGER.error("Department donot exist.");
            throw new UnExpectedRequestException("Department {&DOES_NOT_EXIST}");
        }
        departmentInDb.setName(request.getDepartmentName());
        Department savedDepartment = departmentDao.saveDepartment(departmentInDb);
        LOGGER.info("Succeed to modify department, saved department info is : {}", savedDepartment.toString());
        return new GeneralResponse<>("200", "{&MODIFY_DEPARTMENT_SUCCESSFULLY}", null);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> deleteDepartment(Long departmentId) throws UnExpectedRequestException {
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
        departmentDao.deleteDepartment(departmentInDb);
        LOGGER.info("Succeed to delete department, saved department id is : {}", departmentId);
        return new GeneralResponse<>("200", "{&DELETE_DEPARTMENT_SUCCESSFULLY}", null);
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
    public GeneralResponse<GetAllResponse<DepartmentResponse>> findAllDepartment(PageRequest request) throws UnExpectedRequestException {
        PageRequest.checkRequest(request);
        int page = request.getPage();
        int size = request.getSize();
        long total = departmentDao.countDepartment();
        List<Department> departmentList = departmentDao.findAllDepartment(page, size);
        List<DepartmentResponse> departmentResponses = new ArrayList<>(departmentList.size());
        for (Department department : departmentList) {
            departmentResponses.add(new DepartmentResponse(department));
        }
        GetAllResponse<DepartmentResponse> responses = new GetAllResponse<>();
        responses.setData(departmentResponses);
        responses.setTotal(total);
        LOGGER.info("Succeed to find all departments, page: {}, size: {}, departments: {}", page, size, responses);
        return new GeneralResponse("200", "{&GET_DEPARTMENT_SUCCESSFULLY}", responses);
    }

}
