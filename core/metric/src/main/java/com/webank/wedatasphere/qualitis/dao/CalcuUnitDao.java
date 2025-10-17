package com.webank.wedatasphere.qualitis.dao;

import com.webank.wedatasphere.qualitis.entity.CalcuUnit;

import java.util.List;
import java.util.Optional;

/**
 * @author 
 * @date 2024-04-16 17:10
 * @description
 */
public interface CalcuUnitDao {

    /**
     * get all CalcuUnit
     * @return
     */
    List<CalcuUnit> findAll();

    /**
     * get CalcuUnits by ids
     * @param ids
     * @return
     */
    List<CalcuUnit> findByIds(List<Long> ids);

    /**
     * get CalcuUnit by id
     * @param id
     * @return
     */
    Optional<CalcuUnit> findById(Long id);

    CalcuUnit save(CalcuUnit calcuUnit);
}
