package com.webank.wedatasphere.qualitis.rule.service;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.rule.constant.RuleLockRangeEnum;
import com.webank.wedatasphere.qualitis.rule.exception.RuleLockException;

/**
 * @author v_minminghe@webank.com
 * @date 2022-12-19 8:55
 * @description
 */
public interface RuleLockService {
    /**
     * check Multi Lock If Free Status
     * @param ruleLockId
     * @param holder
     * @return
     */
    boolean checkMultiLockIfFreeStatus(Long ruleLockId, String holder);

    /**
     * tryAcquire
     * @param ruleLockId
     * @param ruleLockRangeEnum
     * @param loginUser
     * @throws UnExpectedRequestException
     * @return
     */
    boolean tryAcquire(Long ruleLockId, RuleLockRangeEnum ruleLockRangeEnum, String loginUser) throws UnExpectedRequestException;

    /**
     * release
     * @param ruleLockId
     * @param ruleLockRangeEnum
     * @param loginUser
     * @throws RuleLockException
     * @return
     */
    void release(Long ruleLockId, RuleLockRangeEnum ruleLockRangeEnum, String loginUser) throws RuleLockException;

}
