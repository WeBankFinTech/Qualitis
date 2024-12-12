/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.qualitis.dao.impl;

import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.dao.repository.UserRepository;
import com.webank.wedatasphere.qualitis.entity.Department;
import com.webank.wedatasphere.qualitis.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author howeye
 */
@Repository
public class UserDaoImpl implements UserDao {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void saveAllUser(List<User> users) {
        userRepository.saveAll(users);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> findByUsernameList(List<String> usernameList) {
        return userRepository.findByUsernameIn(usernameList);
    }

    @Override
    public User findById(long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    @Override
    public Page<User> findAllUser(String userName, String departmentCode, Long subDepartmentCode, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        Specification specification = (Specification<User>) (root, query, criteriaBuilder) -> {
            List<Predicate> andPredicates = new ArrayList<>();
            if (userName != null && userName.trim() != "") {
                andPredicates.add(criteriaBuilder.like(root.get("username"), "%" + userName + "%"));
            }
            if (departmentCode != null && departmentCode.trim() != "") {
                Subquery<Long> subQuery = query.subquery(Long.class);
                Root<Department> subRoot = subQuery.from(Department.class);
                subQuery.select(subRoot.get("id"));
                subQuery.where(criteriaBuilder.and(
                        criteriaBuilder.equal(subRoot.get("departmentCode"), departmentCode),
                        criteriaBuilder.equal(subRoot.get("id"), root.get("department"))
                ));

                andPredicates.add(criteriaBuilder.exists(subQuery));
            }
            if (subDepartmentCode != null) {
                andPredicates.add(criteriaBuilder.equal(root.get("subDepartmentCode"), subDepartmentCode));
            }
            query.where(criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()])));
            return query.getRestriction();
        };
        return userRepository.findAll(specification, pageable);
    }

    @Override
    public List<Map<String, Object>> findAllUserIdAndName() {
        return userRepository.findAllUserIdAndName();
    }

    @Override
    public List<String> findAllUserName() {
        return userRepository.findAllUserName();
    }

    @Override
    public List<User> findByDepartment(Department departmentInDb) {
        return userRepository.findByDepartment(departmentInDb);
    }

    @Override
    public boolean checkTemplate(User userInDb) {
        return userRepository.checkTemplate(userInDb) <= 0;
    }

    @Override
    public List<User> findBySubDepartmentCode(Long subDepartmentCode) {
        return userRepository.findBySubDepartmentCode(subDepartmentCode);
    }

}
