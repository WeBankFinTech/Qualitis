package com.webank.wedatasphere.qualitis.dao.repository;

import com.webank.wedatasphere.qualitis.entity.CalcuUnit;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author
 * @date 2024-04-16 17:09
 * @description
 */
public interface CalcuUnitRepository extends JpaRepository<CalcuUnit, Long > {

    /**
     * get by name
     * @param name
     * @return
     */
    CalcuUnit findByName(String name);

}

