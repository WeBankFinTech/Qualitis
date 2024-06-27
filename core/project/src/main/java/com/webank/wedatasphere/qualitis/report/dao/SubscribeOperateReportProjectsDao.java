package com.webank.wedatasphere.qualitis.report.dao;

import com.webank.wedatasphere.qualitis.report.entity.SubscribeOperateReport;
import com.webank.wedatasphere.qualitis.report.entity.SubscribeOperateReportProjects;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface SubscribeOperateReportProjectsDao {

    /**
     * Save
     * @param subscribeOperateReportProjects
     * @return
     */
    SubscribeOperateReportProjects save(SubscribeOperateReportProjects subscribeOperateReportProjects);

    /**
     * 批量保存
     * @param subscribeOperateReportProjects 要保存的参数
     * @return 保存成功的返回值
     */
    List<SubscribeOperateReportProjects> saveAll(List<SubscribeOperateReportProjects> subscribeOperateReportProjects);

    /**
     * delete
     * @param subscribeOperateReport
     */
    void deleteBySubscribeOperateReport(SubscribeOperateReport subscribeOperateReport);

    /**
     * find By ProjectId
     * @param projectId
     * @return
     */
    List<SubscribeOperateReportProjects> findByProjectId(Long projectId);
}
