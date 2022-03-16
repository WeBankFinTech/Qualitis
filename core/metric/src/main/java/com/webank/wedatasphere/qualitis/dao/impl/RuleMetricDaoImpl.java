package com.webank.wedatasphere.qualitis.dao.impl;

import com.webank.wedatasphere.qualitis.dao.RuleMetricDao;
import com.webank.wedatasphere.qualitis.dao.repository.RuleMetricRepository;
import com.webank.wedatasphere.qualitis.entity.Department;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.entity.User;
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
public class RuleMetricDaoImpl implements RuleMetricDao {
    @Autowired
    private RuleMetricRepository ruleMetricRepository;

    @Override
    public List<RuleMetric> queryAllRuleMetrics(String subSystemName, String ruleMetricName, String enCode, Integer type, Boolean available, int page, int size) {
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return ruleMetricRepository.queryAll(subSystemName, ruleMetricName, enCode, type, available, pageable).getContent();
    }

    @Override
    public long countQueryAllRuleMetrics(String subSystemName, String ruleMetricName, String enCode, Integer type, Boolean available) {
        return ruleMetricRepository.countQueryAll(subSystemName, ruleMetricName, enCode, type, available);
    }

    @Override
    public List<RuleMetric> queryRuleMetrics(Integer level, List<Department> departmentList, User user, String subSystemName, String ruleMetricName
        , String enCode, Integer type, Boolean available, int page, int size) {
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return ruleMetricRepository.queryRuleMetrics(level, departmentList, user, subSystemName, ruleMetricName, enCode, type, available, pageable).getContent();
    }

    @Override
    public long countQueryRuleMetrics(Integer level, List<Department> departmentList, User user, String subSystemName, String ruleMetricName, String enCode
        , Integer type, Boolean available) {
        return ruleMetricRepository.countQueryRuleMetrics(level, departmentList, user, subSystemName, ruleMetricName, enCode, type, available);
    }

    @Override
    public List<RuleMetric> findWithRuleMetricName(Integer level, List<Department> departmentList,
        User user, String name, int page, int size) {
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return ruleMetricRepository.findWithRuleMetricName(level, departmentList, user, "%".concat(name).concat("%"), pageable).getContent();
    }

    @Override
    public long countWithRuleMetricName(Integer level, List<Department> departmentList,
        User user, String name) {
        return ruleMetricRepository.countWithRuleMetricName(level, departmentList, user, "%".concat(name).concat("%"));
    }

    @Override
    public List<RuleMetric> findAllRuleMetrics(int page, int size) {
        Sort sort = new Sort(Sort.Direction.ASC, "id");
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
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return ruleMetricRepository.findRuleMetrics(level, departmentList, user, pageable).getContent();
    }

    @Override
    public long countRuleMetrics(Integer level, List<Department> departmentList, User user) {
        return ruleMetricRepository.countRuleMetrics(level, departmentList, user);
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
      return ruleMetricRepository.findById(id).get();
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
