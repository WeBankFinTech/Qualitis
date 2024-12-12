package com.webank.wedatasphere.qualitis.dao.repository;

import com.webank.wedatasphere.qualitis.entity.Department;
import com.webank.wedatasphere.qualitis.entity.TenantUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

/**
 * @author allenzhou
 */
public interface DepartmentRepository extends JpaRepository<Department, Long>, JpaSpecificationExecutor<Department> {

    /**
     * Find department by department name.
     * @param departmentName
     * @return
     */
    Department findByName(String departmentName);

    /**
     * Find by tenant user.
     * @return
     * @param tenantUser
     */
    List<Department> findByTenantUser(TenantUser tenantUser);

    /**
     * find Sub Department With Page
     * @return
     * @param departmentName
     * @param subDepartmentName
     * @param departmentCode
     * @param pageable
     */
    @Query(value = "select d2.* from qualitis_auth_department d1 " +
            "inner join qualitis_auth_department d2 on d1.id = d2.parent_id and d2.parent_id is not null " +
            "where (?1 is null or d1.name like ?1) and (?2 is null or d2.name like ?2) and (?3 is null or d1.department_code = ?3)",
            countQuery = "select count(d2.id) from qualitis_auth_department d1 " +
                    "inner join qualitis_auth_department d2 on d1.id = d2.parent_id and d2.parent_id is not null " +
                    "where (?1 is null or d1.name like ?1) and (?2 is null or d2.name like ?2) and (?3 is null or d1.department_code = ?3)",
            nativeQuery = true)
    Page<Department> findSubDepartmentWithPage(String departmentName, String subDepartmentName, String departmentCode, Pageable pageable);

    /**
     * find By Department Code
     * @return
     * @param departmentCode
     */
    Department findByDepartmentCode(String departmentCode);

    /**
     * find By ParentId
     * @return
     * @param parentId
     */
    List<Department> findByParentId(long parentId);

    /**
     * find All Department Code And Name
     * @return
     */
    @Query("select new map(d.departmentCode as code, d.name as name) from Department d where d.parentId is null")
    List<Map<String, String>> findAllDepartmentCodeAndName();

    /**
     * find Null Tenant User
     * @return
     */
    @Query(value = "SELECT d FROM Department d WHERE d.tenantUser is null")
    List<Department> findNullTenantUser();
}
