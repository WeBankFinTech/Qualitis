package com.webank.wedatasphere.qualitis.dao.repository;

import com.webank.wedatasphere.qualitis.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author allenzhou
 */
public interface DepartmentRepository  extends JpaRepository<Department, Long> {

    /**
     * Find department by department name.
     * @param departmentName
     * @return
     */
    Department findByName(String departmentName);
}
