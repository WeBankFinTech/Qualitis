package com.webank.wedatasphere.qualitis.timer;

import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.constant.ProjectUserPermissionEnum;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.response.ProjectDetail;
import com.webank.wedatasphere.qualitis.project.response.ProjectDetailResponse;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.request.GetRuleQueryRequest;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author v_gaojiedeng@webank.com
 */
public class RuleLisingCallable implements Callable<ProjectDetailResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleLisingCallable.class);

    private GetRuleQueryRequest request;

    private RuleDao ruleDao;

    private ProjectDao projectDao;

    private ProjectService projectService;

    public RuleLisingCallable(GetRuleQueryRequest request, RuleDao ruleDao, ProjectDao projectDao,ProjectService projectService) {
        this.request = request;
        this.ruleDao = ruleDao;
        this.projectDao = projectDao;
        this.projectService = projectService;
    }


    @Override
    public ProjectDetailResponse call() throws UnExpectedRequestException, PermissionDeniedRequestException {
        ProjectDetailResponse projectDetailResponse = null;
        try {
            GetRuleQueryRequest.checkRequest(request);
            // Check existence of project
            Project projectInDb = projectDao.findByNameAndCreateUser(request.getProjectName(),request.getCreateUser());
            if (projectInDb == null) {
                throw new UnExpectedRequestException("Project {&DOES_NOT_EXIST}");
            }

            // Check if user has permission get project.
            List<Integer> permissions = new ArrayList<>();
            permissions.add(ProjectUserPermissionEnum.BUSSMAN.getCode());
            projectService.checkProjectPermission(projectInDb, request.getLoginUser(), permissions);

            // Find rules by project
            List<Map<String, Object>> rules = ruleDao.findSpecialInfoByProject(projectInDb, null);
            projectDetailResponse = new ProjectDetailResponse(rules);
            projectDetailResponse.setTotal(rules != null ? rules.size() : 0);

            Page<Rule> rulePage = ruleDao.findByConditionWithPage(projectInDb, null, null, null
                    , null, null, null, null, null, null, null,
                    null, null, null, null, null, null, null, request.getPage(), request.getSize());
            if (rulePage.getSize() <= 0) {
                projectDetailResponse = new ProjectDetailResponse();
                projectDetailResponse.setProjectDetail(new ProjectDetail());
                projectDetailResponse.setRuleDetails(Collections.emptyList());
            }

            projectDetailResponse = new ProjectDetailResponse(projectInDb, rulePage.getContent());
            projectDetailResponse.setTotal(Long.valueOf(rulePage.getTotalElements()).intValue());
        }catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw new PermissionDeniedRequestException("User {&HAS_NO_PERMISSION_TO_ACCESS}");
        } catch (Exception e) {
            LOGGER.error("Failed to get rule query, caused by: {}", e.getMessage(), e);
            throw new UnExpectedRequestException("Failed to get rule query:"+ e.getMessage());
        }
        return projectDetailResponse;

    }

}
