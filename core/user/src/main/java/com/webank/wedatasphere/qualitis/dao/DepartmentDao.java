package com.webank.wedatasphere.qualitis.dao;

import com.webank.wedatasphere.qualitis.entity.Department;
import com.webank.wedatasphere.qualitis.entity.TenantUser;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author allenzhou
 */
public interface DepartmentDao {
    /**
     * Find department by department name.
     * @param departmentName
     * @return
     */
    Department findByName(String departmentName);

    /**
     * find By Code
     * @param departmentCode
     * @return
     */
    Department findByCode(String departmentCode);

    /**
     * Find department by department id.
     * @param departmentId
     * @return
     * @throws NoSuchElementException
     */
    Department findById(long departmentId) throws NoSuchElementException;

    /**
     * Save department.
     * @param department
     * @return
     */
    Department saveDepartment(Department department);

    /**
     * Delete department.
     * @param departmentInDb
     */
    void deleteDepartment(Department departmentInDb);

    /**
     * Find all department.
     * @param departmentName
     * @param sourceType
     * @param page
     * @param size
     * @return
     */
    Page<Department> findAllDepartment(String departmentName, Integer sourceType, int page, int size);

    /**
     * find code and name
     * @return
     */
    List<Department> findAllDepartmentCodeAndName();

    /**
     * Find all subDepartment.
     * @param departmentName
     * @param subDepartmentName
     * @param departmentCode
     * @param page
     * @param size
     * @return
     */
    Page<Department> findAllSubDepartment(String departmentName, String subDepartmentName, String departmentCode, int page, int size);

    /**
     * Find by tenant user
     * @param tenantUser
     * @return
     */
    List<Department> findByTenantUser(TenantUser tenantUser);

    /**
     * Find by ids
     * @param ids
     * @return
     */
    List<Department> findByIds(List<Long> ids);

    /**
     * find By ParentId
     * @param parentId
     * @return
     */
    List<Department> findByParentId(long parentId);
}
