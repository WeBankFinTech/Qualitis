package com.webank.wedatasphere.qualitis.dao.impl;

import com.webank.wedatasphere.qualitis.dao.DepartmentDao;
import com.webank.wedatasphere.qualitis.dao.repository.DepartmentRepository;
import com.webank.wedatasphere.qualitis.entity.Department;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

/**
 * @author allenzhou
 */
@Repository
public class DepartmentDaoImpl implements DepartmentDao {
    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public Department findByName(String departmentName) {
        return departmentRepository.findByName(departmentName);
    }

    @Override
    public Department findById(long departmentId) {
        if (departmentRepository.findById(departmentId).isPresent()) {
            return departmentRepository.findById(departmentId).get();
        } else {
            return null;
        }
    }

    @Override
    public Department saveDepartment(Department department) {
        return departmentRepository.save(department);
    }

    @Override
    public void deleteDepartment(Department departmentInDb) {
        departmentRepository.delete(departmentInDb);
    }

    @Override
    public long countDepartment() {
        return departmentRepository.count();
    }

    @Override
    public List<Department> findAllDepartment(int page, int size) {
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return departmentRepository.findAll(pageable).getContent();
    }

}
