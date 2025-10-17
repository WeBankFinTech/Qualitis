package com.webank.wedatasphere.qualitis.dao.impl;

import com.webank.wedatasphere.qualitis.dao.CalcuUnitDao;
import com.webank.wedatasphere.qualitis.dao.repository.CalcuUnitRepository;
import com.webank.wedatasphere.qualitis.entity.CalcuUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author 
 * @date 2024-04-16 17:10
 * @description
 */
@Repository
public class CalcuUnitDaoImpl implements CalcuUnitDao {

    @Autowired
    private CalcuUnitRepository calcuUnitRepository;

    @Override
    public List<CalcuUnit> findAll() {
        return calcuUnitRepository.findAll();
    }

    @Override
    public List<CalcuUnit> findByIds(List<Long> ids) {
        return calcuUnitRepository.findAllById(ids);
    }

    @Override
    public Optional<CalcuUnit> findById(Long id) {
        return calcuUnitRepository.findById(id);
    }

    @Override
    public CalcuUnit save(CalcuUnit calcuUnit) {
        return calcuUnitRepository.save(calcuUnit);
    }

}
