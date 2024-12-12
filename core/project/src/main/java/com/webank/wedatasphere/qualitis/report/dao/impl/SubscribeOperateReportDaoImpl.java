package com.webank.wedatasphere.qualitis.report.dao.impl;

import com.webank.wedatasphere.qualitis.report.dao.SubscribeOperateReportDao;
import com.webank.wedatasphere.qualitis.report.dao.repository.SubscribeOperateReportRepository;
import com.webank.wedatasphere.qualitis.report.entity.SubscribeOperateReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
@Repository
public class SubscribeOperateReportDaoImpl implements SubscribeOperateReportDao {

    @Autowired
    private SubscribeOperateReportRepository subscribeOperateReportRepository;

    @Override
    public SubscribeOperateReport findMatchOperateReport(Long projectId, Integer executionFrequency) {
        return subscribeOperateReportRepository.findMatchOperateReport(projectId, executionFrequency);
    }

    @Override
    public SubscribeOperateReport save(SubscribeOperateReport subscribeOperateReport) {
        return subscribeOperateReportRepository.save(subscribeOperateReport);
    }

    @Override
    public SubscribeOperateReport findById(Long subscribeOperateReportId) {
        if (subscribeOperateReportRepository.findById(subscribeOperateReportId).isPresent()) {
            return subscribeOperateReportRepository.findById(subscribeOperateReportId).get();
        } else {
            return null;
        }
    }

    @Override
    public void delete(SubscribeOperateReport subscribeOperateReport) {
        subscribeOperateReportRepository.delete(subscribeOperateReport);
    }

    @Override
    public Page<SubscribeOperateReport> subscribeOperateReportQuery(String projectName, String receiver, Integer projectType, String loginUser, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return subscribeOperateReportRepository.subscribeOperateReportQuery(projectName, receiver, projectType, loginUser, pageable);
    }

    @Override
    public List<SubscribeOperateReport> selectAllMateFrequency(Integer executionFrequency) {
        return subscribeOperateReportRepository.selectAllMateFrequency(executionFrequency);
    }
}
