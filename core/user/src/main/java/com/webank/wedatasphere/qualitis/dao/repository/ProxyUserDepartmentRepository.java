package com.webank.wedatasphere.qualitis.dao.repository;

import com.webank.wedatasphere.qualitis.entity.ProxyUserDepartment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProxyUserDepartmentRepository extends JpaRepository<ProxyUserDepartment, Long> {

    /**
     * find By Sub Department Code
     * @return
     * @param subDepartmentCode
     */
    List<ProxyUserDepartment> findBySubDepartmentCode(Long subDepartmentCode);

}
