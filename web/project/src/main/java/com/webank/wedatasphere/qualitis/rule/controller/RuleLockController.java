package com.webank.wedatasphere.qualitis.rule.controller;

import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.constant.RuleLockRangeEnum;
import com.webank.wedatasphere.qualitis.rule.exception.RuleLockException;
import com.webank.wedatasphere.qualitis.rule.service.RuleLockService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * @author v_minminghe@webank.com
 * @date 2023-01-13 14:56
 * @description
 */
@Path("api/v1/projector/rule/lock")
public class RuleLockController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleLockController.class);

    private HttpServletRequest httpServletRequest;

    public RuleLockController(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }


    @Autowired
    private RuleLockService ruleLockService;

    @GET
    @Path("acquire")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Boolean> getLock(@QueryParam("rule_lock_id") Long ruleLockId, @QueryParam("rule_lock_range") String lockRange) throws UnExpectedRequestException, RuleLockException {
        CommonChecker.checkObject(ruleLockId, "rule_lock_id");
        CommonChecker.checkString(lockRange, "rule_lock_range");
        RuleLockRangeEnum ruleLockRangeEnum = RuleLockRangeEnum.fromValue(lockRange);
        if (ruleLockRangeEnum == null) {
            throw new UnExpectedRequestException("Illegal parameter: {rule_lock_range}");
        }
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        if ((RuleLockRangeEnum.TABLE_GROUP_RULES.equals(ruleLockRangeEnum) || RuleLockRangeEnum.TABLE_GROUP_DATASOURCE.equals(ruleLockRangeEnum))
                && !ruleLockService.checkMultiLockIfFreeStatus(ruleLockId, loginUser)) {
            return new GeneralResponse<>("403", "{&RULE_LOCK_ACQUIRE_FAILED}", false);
        }
        if (ruleLockService.tryAcquire(ruleLockId, ruleLockRangeEnum, loginUser)) {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "success", true);
        }
        return new GeneralResponse<>("403", "{&RULE_LOCK_ACQUIRE_FAILED}", false);
    }

    @GET
    @Path("release")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Boolean> release(@QueryParam("rule_lock_id") Long ruleLockId, @QueryParam("rule_lock_range") String lockRange) throws UnExpectedRequestException {
        CommonChecker.checkObject(ruleLockId, "rule_lock_id");
        CommonChecker.checkString(lockRange, "rule_lock_range");
        RuleLockRangeEnum ruleLockRangeEnum = RuleLockRangeEnum.fromValue(lockRange);
        if (ruleLockRangeEnum == null) {
            throw new UnExpectedRequestException("Illegal parameter: {rule_lock_range}");
        }
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        try {
            ruleLockService.release(ruleLockId, ruleLockRangeEnum, loginUser);
        } catch (RuleLockException e) {
            LOGGER.warn("Failed to release lock", e);
        }
        return new GeneralResponse<>(ResponseStatusConstants.OK, "Success", true);
    }
}
