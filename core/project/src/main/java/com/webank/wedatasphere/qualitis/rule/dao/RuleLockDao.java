package com.webank.wedatasphere.qualitis.rule.dao;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.rule.entity.RuleLock;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2022-12-19 9:44
 * @description
 */
public interface RuleLockDao {

    /**
     * check multi lock if free status
     * @param lockKeys
     * @param loginUser
     * @param expiredTimestamp
     * @return
     */
    boolean checkMultiLockIfFreeStatus(List<String> lockKeys, String loginUser, Long expiredTimestamp);

    /**
     * findByLockKeyWithLock
     * @param lockKey
     * @return
     */
    RuleLock findByLockKeyWithLock(String lockKey);

    /**
     * newLock
     * @param lockKey
     * @param holder
     * @param timestamp
     * @param status
     * @return
     */
    Integer newLock(String lockKey, String holder, Long timestamp, Integer status);

    /**
     * acquireLock
     * @param lockKey
     * @param holder
     * @param timestamp
     * @return
     */
    Integer acquireLock(String lockKey, String holder, Long timestamp);

    /**
     * releaseLock
     * @param lockKey
     * @param holder
     * @param timestamp
     * @return
     */
    Integer releaseLock(String lockKey, String holder, Long timestamp);

    /**
     * modify
     * @param ruleLock
     * @return
     * @throws UnExpectedRequestException
     */
    RuleLock modify(RuleLock ruleLock) throws UnExpectedRequestException;
}
