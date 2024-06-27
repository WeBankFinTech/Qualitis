package com.webank.wedatasphere.qualitis.report.dao.impl;

import com.webank.wedatasphere.qualitis.report.dao.SubscribeOperateReportProjectsDao;
import com.webank.wedatasphere.qualitis.report.dao.repository.SubscribeOperateReportProjectsRepository;
import com.webank.wedatasphere.qualitis.report.entity.SubscribeOperateReport;
import com.webank.wedatasphere.qualitis.report.entity.SubscribeOperateReportProjects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
@Repository
public class SubscribeOperateReportProjectsDaoImpl implements SubscribeOperateReportProjectsDao {

    @Autowired
    private SubscribeOperateReportProjectsRepository subscribeOperateReportProjectsRepository;

    @Override
    public SubscribeOperateReportProjects save(SubscribeOperateReportProjects subscribeOperateReportProjects) {
        return subscribeOperateReportProjectsRepository.save(subscribeOperateReportProjects);
    }

    @Override
    public List<SubscribeOperateReportProjects> saveAll(List<SubscribeOperateReportProjects> subscribeOperateReportProjects) {
        return subscribeOperateReportProjectsRepository.saveAll(subscribeOperateReportProjects);
    }

    @Override
    public void deleteBySubscribeOperateReport(SubscribeOperateReport subscribeOperateReport) {
        subscribeOperateReportProjectsRepository.deleteBySubscribeOperateReport(subscribeOperateReport);
    }

    @Override
    public List<SubscribeOperateReportProjects> findByProjectId(Long projectId) {
        return subscribeOperateReportProjectsRepository.findByProjectId(projectId);
    }


}
