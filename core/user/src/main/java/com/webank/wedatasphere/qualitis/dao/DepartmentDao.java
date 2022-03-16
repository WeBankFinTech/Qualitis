package com.webank.wedatasphere.qualitis.dao;

import com.webank.wedatasphere.qualitis.entity.Department;
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
     * Couunt department.
     * @return
     */
    long countDepartment();

    /**
     * Find all department.
     * @param page
     * @param size
     * @return
     */
    List<Department> findAllDepartment(int page, int size);
}
