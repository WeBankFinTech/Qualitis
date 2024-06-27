package com.webank.wedatasphere.qualitis.dao.impl;

import com.webank.wedatasphere.qualitis.dao.DepartmentDao;
import com.webank.wedatasphere.qualitis.dao.repository.DepartmentRepository;
import com.webank.wedatasphere.qualitis.entity.Department;
import com.webank.wedatasphere.qualitis.entity.TenantUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    public Department findByCode(String departmentCode) {
        return departmentRepository.findByDepartmentCode(departmentCode);
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
    public Page<Department> findAllDepartment(String departmentName, Integer sourceType, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<Department> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (departmentName != null && departmentName.trim() != "") {
                predicates.add(cb.like(root.get("name"), "%" + departmentName + "%"));
            }
            if (sourceType != null) {
                predicates.add(cb.equal(root.get("sourceType"), sourceType));
            }
            predicates.add(cb.isNull(root.get("parentId")));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return departmentRepository.findAll(spec, pageable);
    }

    @Override
    public List<Department> findAllDepartmentCodeAndName() {
        return departmentRepository.findAllDepartmentCodeAndName().stream().map(map -> {
            Department department = new Department();
            department.setDepartmentCode(map.get("code"));
            department.setName(map.get("name"));
            return department;
        }).collect(Collectors.toList());
    }

    @Override
    public Page<Department> findAllSubDepartment(String departmentName, String subDepartmentName, String departmentCode, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "department_code");
        Pageable pageable = PageRequest.of(page, size, sort);
        return departmentRepository.findSubDepartmentWithPage(departmentName, subDepartmentName, departmentCode, pageable);
    }

    @Override
    public List<Department> findByTenantUser(TenantUser tenantUser) {
        return departmentRepository.findByTenantUser(tenantUser);
    }

    @Override
    public List<Department> findByIds(List<Long> ids) {
        return departmentRepository.findAllById(ids);
    }

    @Override
    public List<Department> findByParentId(long parentId) {
        return departmentRepository.findByParentId(parentId);
    }

    @Override
    public List<Department> findNullTenantUser() {
        return departmentRepository.findNullTenantUser();
    }

}
