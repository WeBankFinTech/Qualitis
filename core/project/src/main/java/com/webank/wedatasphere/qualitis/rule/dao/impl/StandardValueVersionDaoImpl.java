package com.webank.wedatasphere.qualitis.rule.dao.impl;

import com.webank.wedatasphere.qualitis.rule.dao.StandardValueVersionDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.StandardValueVersionRepository;
import com.webank.wedatasphere.qualitis.rule.entity.StandardValueVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * @author v_gaojiedeng@webank.com
 */
@Repository
public class StandardValueVersionDaoImpl implements StandardValueVersionDao {

    @Autowired
    private StandardValueVersionRepository standardValueVersionRepository;


    @Override
    public Long selectMaxVersion(long standardVauleId) {
        return standardValueVersionRepository.selectMaxVersion(standardVauleId);
    }

    @Override
    public StandardValueVersion saveStandardValueVersion(StandardValueVersion standardValueVersion) {
        return standardValueVersionRepository.save(standardValueVersion);
    }

    @Override
    public StandardValueVersion findById(Long id) {
        return standardValueVersionRepository.findById(id).orElse(null);
    }

    @Override
    public List<StandardValueVersion> findByStandardValueVersion(Long id) {
        return standardValueVersionRepository.findByStandardValueVersion(id);
    }

    @Override
    public List<StandardValueVersion> findAllData(String enName) {
        return standardValueVersionRepository.selectStandardValueVersion(enName);
    }

    @Override
    public void deleteStandardValueVersion(StandardValueVersion standardValueVersion) {
        standardValueVersionRepository.delete(standardValueVersion);
    }

    @Override
    public void deleteAll(Iterable<StandardValueVersion> standardValueVersion) {
        standardValueVersionRepository.deleteAll(standardValueVersion);
    }

    @Override
    public List<StandardValueVersion> findAllStandardValue(String cnName, String enName, String standardValueId, String createUser, String devDepartmentId, String opsDepartmentId, Set<String> actionRange, String dataType, String sourceType, String modifyUser, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return standardValueVersionRepository.findAllStandardValue(cnName, enName, standardValueId, createUser, devDepartmentId, opsDepartmentId, actionRange, dataType, sourceType, modifyUser, pageable).getContent();
    }

    @Override
    public Long countAllStandardValue(String cnName, String enName, String standardValueId, String createUser, String devDepartmentId, String opsDepartmentId, Set<String> actionRange, String dataType, String sourceType, String modifyUser) {
        return standardValueVersionRepository.countAllStandardValue(cnName, enName, standardValueId, createUser, devDepartmentId, opsDepartmentId, actionRange, dataType, sourceType, modifyUser);
    }

    @Override
    public List<StandardValueVersion> findStandardValue(String cnName, String enName, String standardValueId, String createUser, String devDepartmentId, String opsDepartmentId, Set<String> actionRange, String tableDataType, List<Long> dataVisibilityDeptList, String loginUser, String sourceType,String modifyUser, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return standardValueVersionRepository.findStandardValue(cnName, enName, standardValueId, createUser, devDepartmentId, opsDepartmentId, actionRange, tableDataType, dataVisibilityDeptList, loginUser, sourceType,modifyUser, pageable).getContent();
    }

    @Override
    public Long countStandardValue(String cnName, String enName, String standardValueId, String createUser, String devDepartmentId, String opsDepartmentId, Set<String> actionRange, String tableDataType, List<Long> dataVisibilityDeptList, String loginUser, String sourceType,String modifyUser) {
        return standardValueVersionRepository.countAllName(cnName, enName, standardValueId, createUser, devDepartmentId, opsDepartmentId, actionRange, tableDataType, dataVisibilityDeptList, loginUser, sourceType,modifyUser);
    }

    @Override
    public List<StandardValueVersion> selectStandardValue(Long id, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(page, size, sort);
        return standardValueVersionRepository.selectStandardValue(id, pageable).getContent();
    }

    @Override
    public Long countStandardValue(Long id) {
        return standardValueVersionRepository.countStandardValue(id);
    }

    @Override
    public List<StandardValueVersion> selectSuit(Long id, String tableDataType, List<Long> dataVisibilityDeptList, String loginUser, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "create_time");
        Pageable pageable = PageRequest.of(page, size, sort);
        return standardValueVersionRepository.selectSuit(id, tableDataType, dataVisibilityDeptList, loginUser, pageable).getContent();
    }

    @Override
    public Long countSuitSome(Long id, String tableDataType, List<Long> dataVisibilityDeptList, String loginUser) {
        return standardValueVersionRepository.countSuitSome(id, tableDataType, dataVisibilityDeptList, loginUser);
    }

    @Override
    public List<Long> getVersionList(Long standardValueId) {
        return standardValueVersionRepository.getVersionList(standardValueId);
    }

    @Override
    public StandardValueVersion getMaxVersion(Long id) {
        return standardValueVersionRepository.getMaxVersion(id);
    }

    @Override
    public List<StandardValueVersion> selectWhereStandardValueId(long standardValueId) {
        return standardValueVersionRepository.selectWhereStandardValueId(standardValueId);
    }

    @Override
    public List<StandardValueVersion> findAll() {
        return standardValueVersionRepository.findAll();
    }

    @Override
    public StandardValueVersion findByEnName(String enName) {
        return standardValueVersionRepository.findByEnName(enName);
    }

}
