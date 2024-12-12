package com.webank.wedatasphere.qualitis.report.dao.repository;

import com.webank.wedatasphere.qualitis.report.entity.SubscribeOperateReport;
import com.webank.wedatasphere.qualitis.report.entity.SubscribeOperateReportProjects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface SubscribeOperateReportProjectsRepository extends JpaRepository<SubscribeOperateReportProjects, Long> {

    /**
     * delete By Subscribe Operate Report
     * @param subscribeOperateReport
     */
    void deleteBySubscribeOperateReport(SubscribeOperateReport subscribeOperateReport);

    /**
     * find By ProjectId
     * @param projectId
     * @return
     */
    @Query(value = "select * from qualitis_subscribe_operate_report_associated_projects where project_id=?1", nativeQuery = true)
    List<SubscribeOperateReportProjects> findByProjectId(Long projectId);
}
