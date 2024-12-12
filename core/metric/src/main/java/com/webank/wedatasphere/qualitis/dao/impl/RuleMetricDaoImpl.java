package com.webank.wedatasphere.qualitis.dao.impl;

import com.webank.wedatasphere.qualitis.dao.RuleMetricDao;
import com.webank.wedatasphere.qualitis.dao.repository.RuleMetricRepository;
import com.webank.wedatasphere.qualitis.entity.Department;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author allenzhou
 */
@Repository
public class RuleMetricDaoImpl implements RuleMetricDao {
    @Autowired
    private RuleMetricRepository ruleMetricRepository;

    @Override
    public List<RuleMetric> queryAllRuleMetrics(String subSystemName, String ruleMetricName, String enCode, Integer type, Boolean available, Boolean multiEnvs, String devDepartmentId, String opsDepartmentId, Set<String> actionRange, String tableDataType, String createUser, String modifyUser, int page, int size) {
        Sort sort = Sort.by(Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return ruleMetricRepository.queryAll(subSystemName, ruleMetricName, enCode, type, available, multiEnvs, devDepartmentId, opsDepartmentId, actionRange, tableDataType, createUser, modifyUser, pageable).getContent();
    }

    @Override
    public long countQueryAllRuleMetrics(String subSystemName, String ruleMetricName, String enCode, Integer type, Boolean available, Boolean multiEnvs, String devDepartmentId, String opsDepartmentId, Set<String> actionRange, String tableDataType, String createUser, String modifyUser) {
        return ruleMetricRepository.countQueryAll(subSystemName, ruleMetricName, enCode, type, available, multiEnvs, devDepartmentId, opsDepartmentId, actionRange, tableDataType, createUser, modifyUser);
    }

    @Override
    public List<RuleMetric> queryRuleMetrics(String subSystemName, String ruleMetricName
            , String enCode, Integer type, Boolean requestAvailable, Boolean available, Boolean multiEnvs, String tableDataType, List<Long> dataVisibilityDeptList, String createUser, String devDepartmentId, String opsDepartmentId, Set<String> actionRange, String buildUser, String modifyUser, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return ruleMetricRepository.queryRuleMetrics(subSystemName, ruleMetricName, enCode, type, available, multiEnvs, tableDataType, dataVisibilityDeptList, createUser, devDepartmentId, opsDepartmentId, actionRange, buildUser, modifyUser, pageable).getContent();
    }

    @Override
    public long countQueryRuleMetrics(String subSystemName, String ruleMetricName,
                                      String enCode, Integer type, Boolean available, Boolean multiEnvs, String tableDataType, List<Long> dataVisibilityDeptList, String createUser, String devDepartmentId, String opsDepartmentId, Set<String> actionRange, String buildUser, String modifyUser) {
        return ruleMetricRepository.countQueryRuleMetrics(subSystemName, ruleMetricName, enCode, type, available, multiEnvs, tableDataType, dataVisibilityDeptList, createUser, devDepartmentId, opsDepartmentId, actionRange, buildUser, modifyUser);
    }

    @Override
    public List<RuleMetric> findWithRuleMetricName(Integer level, List<Department> departmentList,
                                                   User user, String name, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return ruleMetricRepository.findWithRuleMetricName(level, departmentList, user, "%".concat(name).concat("%"), pageable).getContent();
    }

    @Override
    public long countWithRuleMetricName(Integer level, List<Department> departmentList,
                                        User user, String name) {
        return ruleMetricRepository.countWithRuleMetricName(level, departmentList, user, "%".concat(name).concat("%"));
    }

    @Override
    public List<RuleMetric> findBySubSystemId(Integer level, List<Department> departmentList,
                                              User user, long subSystemId, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return ruleMetricRepository.findBySubSystemId(level, departmentList, user, subSystemId, pageable).getContent();
    }

    @Override
    public long countBySubSystemId(Integer level, List<Department> departmentList,
                                   User user, long subSystemId) {
        return ruleMetricRepository.countBySubSystemId(level, departmentList, user, subSystemId);
    }

    @Override
    public List<RuleMetric> findAllRuleMetrics(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return ruleMetricRepository.findAll(pageable).getContent();
    }

    @Override
    public long countAllRuleMetrics() {
        return ruleMetricRepository.count();
    }

    @Override
    public List<RuleMetric> findRuleMetrics(Integer level, List<Department> departmentList,
                                            User user, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return ruleMetricRepository.findRuleMetrics(level, departmentList, user, pageable).getContent();
    }

    @Override
    public long countRuleMetrics(Integer level, List<Department> departmentList, User user) {
        return ruleMetricRepository.countRuleMetrics(level, departmentList, user);
    }

    @Override
    public List<RuleMetric> findNotUsed(Integer level, List<Department> departmentList,
                                        User user, String createUser, String tableDataType, List<Long> dataVisibilityDeptList, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return ruleMetricRepository.findNotUsed(createUser, tableDataType, dataVisibilityDeptList, pageable).getContent();
    }

    @Override
    public long countNotUsed(Integer level, List<Department> departmentList, User user, String createUser, String tableDataType, List<Long> dataVisibilityDeptList) {
        return ruleMetricRepository.countNotUsed(createUser, tableDataType, dataVisibilityDeptList);
    }

    @Override
    public List<RuleMetric> findAllNotUsed(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return ruleMetricRepository.findAllNotUsed(pageable).getContent();
    }

    @Override
    public long countAllNotUsed() {
        return ruleMetricRepository.countAllNotUsed();
    }

    @Override
    public List<RuleMetric> findUsed(Integer level, List<Department> departmentList,
                                     User user, String createUser, String tableDataType, List<Long> dataVisibilityDeptList, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return ruleMetricRepository.findUsed(createUser, tableDataType, dataVisibilityDeptList, pageable).getContent();
    }

    @Override
    public long countUsed(Integer level, List<Department> departmentList, User user, String createUser, String tableDataType, List<Long> dataVisibilityDeptList) {
        return ruleMetricRepository.countUsed(createUser, tableDataType, dataVisibilityDeptList);
    }

    @Override
    public List<RuleMetric> findAllUsed(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return ruleMetricRepository.findAllUsed(pageable).getContent();
    }

    @Override
    public long countAllUsed() {
        return ruleMetricRepository.countAllUsed();
    }

    @Override
    public RuleMetric add(RuleMetric ruleMetric) {
        return ruleMetricRepository.save(ruleMetric);
    }

    @Override
    public RuleMetric modify(RuleMetric ruleMetric) {
        return ruleMetricRepository.save(ruleMetric);
    }

    @Override
    public void delete(RuleMetric ruleMetric) {
        ruleMetricRepository.delete(ruleMetric);
    }

    @Override
    public RuleMetric findById(long id) {
        Optional<RuleMetric> optional = ruleMetricRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    @Override
    public RuleMetric findByEnCode(String enCode) {
        return ruleMetricRepository.findByEnCode(enCode);
    }

    @Override
    public List<RuleMetric> findByIds(List<Long> ids) {
        return ruleMetricRepository.findAllById(ids);
    }

    @Override
    public RuleMetric findByName(String name) {
        return ruleMetricRepository.findByName(name);
    }
}
