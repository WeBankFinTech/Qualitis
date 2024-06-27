package com.webank.wedatasphere.qualitis.report.dao.repository;

import com.webank.wedatasphere.qualitis.report.entity.SubscribeOperateReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface SubscribeOperateReportRepository extends JpaRepository<SubscribeOperateReport, Long> {

    /**
     * find match operate report
     *
     * @param executionFrequency
     * @param projectId
     * @return
     */
    @Query(value = "SELECT qrm.* FROM qualitis_subscribe_operate_report as qrm,qualitis_subscribe_operate_report_associated_projects as qrmb where qrm.id = qrmb.operate_report_id AND qrm.execution_frequency= ?2 AND qrmb.project_id=?1 group by qrm.id", nativeQuery = true)
    SubscribeOperateReport findMatchOperateReport(Long projectId, Integer executionFrequency);


    /**
     * subscribe Operate Report Query
     *
     * @param projectName
     * @param receiver
     * @param projectType
     * @param loginUser
     * @param pageable
     * @return
     */
    @Query(value = "select qrm.* from qualitis_subscribe_operate_report as qrm,qualitis_subscribe_operate_report_associated_projects as qrmb,qualitis_project as qp,qualitis_project_user as pu where qrm.id = qrmb.operate_report_id and qrmb.project_id= qp.id and qrmb.project_id= pu.project_id " +
            " and pu.user_name = ?4 " +
            " and if(?3 is null, 1=1, qp.project_type = ?3) " +
            " and if(?1 is null, 1=1, qp.name like ?1) " +
            " and if(?2 is null,1=1, find_in_set(?2,qrm.receiver)) group by qrm.id"
            , countQuery = "SELECT COUNT(0) FROM (select qrm.* from qualitis_subscribe_operate_report as qrm,qualitis_subscribe_operate_report_associated_projects as qrmb,qualitis_project as qp,qualitis_project_user as pu where qrm.id = qrmb.operate_report_id and qrmb.project_id= qp.id and qrmb.project_id= pu.project_id " +
            " and pu.user_name = ?4 " +
            " and if(?3 is null, 1=1, qp.project_type = ?3) " +
            " and if(?1 is null, 1=1, qp.name like ?1) " +
            " and if(?2 is null,1=1, find_in_set(?2,qrm.receiver)) group by qrm.id" +
            ") as a"
            , nativeQuery = true)
    Page<SubscribeOperateReport> subscribeOperateReportQuery(String projectName, String receiver, Integer projectType, String loginUser, Pageable pageable);


    /**
     * find match operate report
     *
     * @param executionFrequency
     * @return
     */
    @Query(value = "select * from qualitis_subscribe_operate_report where execution_frequency =?1", nativeQuery = true)
    List<SubscribeOperateReport> selectAllMateFrequency(Integer executionFrequency);
}
