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

package com.webank.wedatasphere.qualitis.service.impl;

import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.dao.DepartmentDao;
import com.webank.wedatasphere.qualitis.dao.repository.ProxyUserDepartmentRepository;
import com.webank.wedatasphere.qualitis.dao.repository.ProxyUserRepository;
import com.webank.wedatasphere.qualitis.dao.repository.UserProxyUserRepository;
import com.webank.wedatasphere.qualitis.entity.Department;
import com.webank.wedatasphere.qualitis.entity.ProxyUser;
import com.webank.wedatasphere.qualitis.entity.ProxyUserDepartment;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.*;
import com.webank.wedatasphere.qualitis.response.AddProxyUserResponse;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.service.ProxyUserService;
import com.webank.wedatasphere.qualitis.util.DateUtils;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.ArrayList;
import java.util.List;

/**
 * @author howeye
 */
@Service
public class ProxyUserServiceImpl implements ProxyUserService {

    @Autowired
    private ProxyUserRepository proxyUserRepository;

    @Autowired
    private ProxyUserDepartmentRepository proxyUserDepartmentRepository;

    @Autowired
    private UserProxyUserRepository userProxyUserRepository;

    @Autowired
    private DepartmentDao departmentDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyUserServiceImpl.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private HttpServletRequest httpServletRequest;

    public ProxyUserServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<AddProxyUserResponse> addProxyUser(AddProxyUserRequest request) throws UnExpectedRequestException {
        // Check Arguments
        AddProxyUserRequest.checkRequest(request);

        // Check existence of proxy user
        ProxyUser proxyUserInDb = proxyUserRepository.findByProxyUserName(request.getProxyUserName());
        if (proxyUserInDb != null) {
            throw new UnExpectedRequestException("ProxyUser name: [" + request.getProxyUserName() +  "] {&ALREADY_EXIST}");
        }

        // Save proxy user
        ProxyUser proxyUser = new ProxyUser();
        proxyUser.setProxyUserName(request.getProxyUserName());
        proxyUser.setUserConfigJson(request.getUserConfigJson());
        Department departmentInDb = departmentDao.findById(request.getDepartment());
        proxyUser.setDepartment(departmentInDb);
        proxyUser.setCreateUser(HttpUtils.getUserName(httpServletRequest));
        proxyUser.setCreateTime(DateUtils.now());
        List<DepartmentInfo> departmentInfo = request.getDepartmentInfo();
        ProxyUser savedProxyUser = proxyUserRepository.save(proxyUser);

        ArrayList<ProxyUserDepartment> departments = new ArrayList<>();
        for (DepartmentInfo department : departmentInfo) {
            ProxyUserDepartment proxyUserDepartment = new ProxyUserDepartment();
            proxyUserDepartment.setProxyUser(proxyUser);
            proxyUserDepartment.setDepartment(department.getName());
            proxyUserDepartment.setSubDepartmentCode(department.getSubId());
            departments.add(proxyUserDepartment);
            proxyUserDepartmentRepository.save(proxyUserDepartment);
        }

        proxyUser.setProxyUserDepartment(departments);
        LOGGER.info("Succeed to save proxyUser. proxy_user: {}", savedProxyUser.getProxyUserName());

        AddProxyUserResponse response = new AddProxyUserResponse(savedProxyUser);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_SAVE_PROXYUSER}", response);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse deleteProxyUser(DeleteProxyUserRequest request) throws UnExpectedRequestException {
        // Check Arguments
        DeleteProxyUserRequest.checkRequest(request);


        // Check existence of proxy user
        ProxyUser proxyUserInDb = proxyUserRepository.findById(request.getProxyUserId()).orElse(null);
        if (proxyUserInDb == null) {
            throw new UnExpectedRequestException("ProxyUser id: " + request.getProxyUserId() + " {&DOES_NOT_EXIST}");
        }

        proxyUserRepository.delete(proxyUserInDb);
        LOGGER.info("Succeed to delete proxy user. proxy_user: {}", proxyUserInDb.getProxyUserName());

        return new GeneralResponse<>("200", "{&SUCCEED_TO_DELETE_PROXY_USER}", null);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse modifyProxyUser(ModifyProxyUserRequest request) throws UnExpectedRequestException {
        // Check Arguments
        ModifyProxyUserRequest.checkRequest(request);

        // Check existence of proxy user
        ProxyUser proxyUserInDb = proxyUserRepository.findById(request.getProxyUserId()).orElse(null);
        if (proxyUserInDb == null) {
            throw new UnExpectedRequestException("ProxyUser id: [" + request.getProxyUserId() + "] {&DOES_NOT_EXIST}");
        }
        proxyUserDepartmentRepository.deleteAll(proxyUserInDb.getProxyUserDepartment());
        String oldProxyUserName = proxyUserInDb.getProxyUserName();
        // Modify proxy user name
        proxyUserInDb.setProxyUserName(request.getProxyUserName());
        List<DepartmentInfo> departmentInfo = request.getDepartmentInfo();
        ArrayList<ProxyUserDepartment> proxyUserDepartments = new ArrayList<>();
        for (DepartmentInfo department : departmentInfo) {
            ProxyUserDepartment proxyUserDepartment = new ProxyUserDepartment();
            proxyUserDepartment.setProxyUser(proxyUserInDb);
            proxyUserDepartment.setDepartment(department.getName());
            proxyUserDepartment.setSubDepartmentCode(department.getSubId());
            proxyUserDepartments.add(proxyUserDepartment);
        }
        proxyUserInDb.setProxyUserDepartment(proxyUserDepartments);
        proxyUserInDb.setUserConfigJson(request.getUserConfigJson());
        Department departmentInDb = departmentDao.findById(request.getDepartment());
        proxyUserInDb.setDepartment(departmentInDb);
        proxyUserInDb.setModifyUser(HttpUtils.getUserName(httpServletRequest));
        proxyUserInDb.setModifyTime(DateUtils.now());
        proxyUserRepository.save(proxyUserInDb);

        LOGGER.info("Succeed to modify proxy user. old proxy_user name: {}, new proxy_user name: {}", oldProxyUserName, proxyUserInDb.getProxyUserName());
        return new GeneralResponse<>("200", "{&SUCCEED_TO_MODIFY_PROXY_USER_NAME}", null);
    }

    @Override
    public GeneralResponse<GetAllResponse<AddProxyUserResponse>> getAllProxyUser(QueryProxyUserRequest request) throws UnExpectedRequestException {
        // Check Arguments
        PageRequest.checkRequest(request);

        // Query by page and size
        int page = request.getPage();
        int size = request.getSize();

        Long subDepartmentCode = null;
        if (StringUtils.isNotBlank(request.getSubDepartmentCode())) {
            subDepartmentCode = Long.valueOf(request.getSubDepartmentCode());
        }
        Page<ProxyUser> proxyUserPage = findAllProxyUser(request.getProxyUserName(), request.getDepartmentCode(), subDepartmentCode, page, size);

        List<ProxyUser> proxyUsers = proxyUserPage.getContent();
        List<AddProxyUserResponse> proxyUserResponses = new ArrayList<>();
        for (ProxyUser proxyUser : proxyUsers) {
            AddProxyUserResponse tmp = new AddProxyUserResponse(proxyUser);
            long membersNum = userProxyUserRepository.countByProxyUser(proxyUser);
            tmp.setProxyUserMembersNum(membersNum);
            proxyUserResponses.add(tmp);
        }
        GetAllResponse<AddProxyUserResponse> response = new GetAllResponse<>();
        response.setTotal(proxyUserPage.getTotalElements());
        response.setData(proxyUserResponses);

        LOGGER.info("Succeed to find all proxyUsers, response: {}", response);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_FIND_ALL_PROXYUSERS}", response);
    }

    @Override
    public List<String> getAllProxyUserName() {
        return proxyUserRepository.getAllProxyUserName();
    }

    @Override
    public void syncDepartmentName(Department department, String departmentName) {
        List<ProxyUser> proxyUserList = proxyUserRepository.findByDepartment(department);
        for (ProxyUser proxyUser: proxyUserList) {
            List<ProxyUserDepartment> proxyUserDepartmentList = proxyUser.getProxyUserDepartment();
            for (ProxyUserDepartment proxyUserDepartment: proxyUserDepartmentList) {
                String departmentNameInDb = proxyUserDepartment.getDepartment();
                String[] splitDepartmentName = StringUtils.split(departmentNameInDb, SpecCharEnum.SLASH.getValue());
                if (splitDepartmentName.length < 2) {
                    continue;
                }
                departmentNameInDb = departmentName + SpecCharEnum.SLASH.getValue() + splitDepartmentName[1];
                proxyUserDepartment.setDepartment(departmentNameInDb);
            }
            proxyUserDepartmentRepository.saveAll(proxyUserDepartmentList);
        }
    }

    @Override
    public void syncSubDepartmentName(Long subDepartmentCode, String departmentName) {
        List<ProxyUserDepartment> proxyUserDepartmentList = proxyUserDepartmentRepository.findBySubDepartmentCode(subDepartmentCode);
        for (ProxyUserDepartment proxyUserDepartment: proxyUserDepartmentList) {
            String departmentNameInDb = proxyUserDepartment.getDepartment();
            String[] splitDepartmentName = StringUtils.split(departmentNameInDb, SpecCharEnum.SLASH.getValue());
            if (splitDepartmentName.length < 2) {
                continue;
            }
            departmentNameInDb = splitDepartmentName[0] + SpecCharEnum.SLASH.getValue() + departmentName;
            proxyUserDepartment.setDepartment(departmentNameInDb);
        }
        proxyUserDepartmentRepository.saveAll(proxyUserDepartmentList);
    }

    @Override
    public List<ProxyUserDepartment> findBySubDepartmentCode(Long subDepartmentCode) {
        return proxyUserDepartmentRepository.findBySubDepartmentCode(subDepartmentCode);
    }

    private Page<ProxyUser> findAllProxyUser(String proxyUserName, String departmentCode, Long subDepartmentCode, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, sort);

        Specification specification = (Specification<User>) (root, query, criteriaBuilder) -> {
            List<Predicate> andPredicates = new ArrayList<>();
            if (proxyUserName != null && proxyUserName.trim() != "") {
                andPredicates.add(criteriaBuilder.like(root.get("proxyUserName"), "%" + proxyUserName + "%"));
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
                Subquery<Long> subQuery = query.subquery(Long.class);
                Root<ProxyUserDepartment> subRoot = subQuery.from(ProxyUserDepartment.class);
                subQuery.select(subRoot.get("id"));
                subQuery.where(criteriaBuilder.and(
                        criteriaBuilder.equal(subRoot.get("proxyUser"), root.get("id")),
                        criteriaBuilder.equal(subRoot.get("subDepartmentCode"), subDepartmentCode)
                ));

                andPredicates.add(criteriaBuilder.exists(subQuery));
            }
            query.where(criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()])));
            return query.getRestriction();
        };
        return proxyUserRepository.findAll(specification, pageable);
    }

}
