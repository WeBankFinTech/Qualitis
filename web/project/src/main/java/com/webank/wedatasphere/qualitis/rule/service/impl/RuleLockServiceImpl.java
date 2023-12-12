package com.webank.wedatasphere.qualitis.rule.service.impl;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.rule.constant.RuleLockRangeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.RuleLockDao;
import com.webank.wedatasphere.qualitis.rule.entity.RuleLock;
import com.webank.wedatasphere.qualitis.rule.exception.RuleLockException;
import com.webank.wedatasphere.qualitis.rule.service.RuleLockService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2022-12-19 8:56
 * @description
 */
@Service
public class RuleLockServiceImpl implements RuleLockService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleLockServiceImpl.class);

    @Value("${rule.lock.max-duration-seconds: 600}")
    private Long maxEditableDurationOnPage;

    private final Integer LOCK_STATUS_FREE = 0;
    private final Integer LOCK_STATUS_HELD = 1;

    @Autowired
    private RuleLockDao ruleLockDao;

    @Override
    public boolean checkMultiLockIfFreeStatus(Long ruleLockId, String holder) {
        List<String> lockKeys = Lists.newArrayListWithExpectedSize(RuleLockRangeEnum.values().length);
        for (RuleLockRangeEnum ruleLockRangeEnum : RuleLockRangeEnum.values()) {
            lockKeys.add(generateLockKey(ruleLockId, ruleLockRangeEnum));
        }
        Long expiredTimestamp = (System.currentTimeMillis() / 1000) - maxEditableDurationOnPage;
        return ruleLockDao.checkMultiLockIfFreeStatus(lockKeys, holder, expiredTimestamp);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @Override
    public synchronized boolean tryAcquire(Long ruleLockId, RuleLockRangeEnum ruleLockRangeEnum, String loginUser) throws UnExpectedRequestException {
        String lockKey = generateLockKey(ruleLockId, ruleLockRangeEnum);
        RuleLock ruleLock = ruleLockDao.findByLockKeyWithLock(lockKey);
        if (ruleLock == null) {
            LOGGER.info("Ready to initial a lock");
            Long currentTime = System.currentTimeMillis() / 1000;
            Integer recordCount = ruleLockDao.newLock(lockKey, loginUser, currentTime, LOCK_STATUS_HELD);
            return recordCount > 0;
        } else if (getLockIfFreeStatus(ruleLock, loginUser)) {
            LOGGER.info("Success to acquire a free lock");
            return true;
        } else if (getLockIfReentry(ruleLock, loginUser)) {
            LOGGER.info("Success to acquire a reentry lock");
            return true;
        } else if (getLockIfTimeout(ruleLock, loginUser)) {
            LOGGER.info("Success to acquire a timeout lock");
            return true;
        }
        return false;
    }

    private boolean getLockIfFreeStatus(RuleLock ruleLock, String loginUser) {
        if (LOCK_STATUS_FREE.equals(ruleLock.getStatus())) {
            Long currentTime = System.currentTimeMillis() / 1000;
            Integer recordCount = ruleLockDao.acquireLock(ruleLock.getLockKey(), loginUser, currentTime);
            return recordCount > 0;
        }
        return false;
    }

    private boolean getLockIfReentry(RuleLock ruleLock, String loginUser) {
        if (loginUser == null && ruleLock.getHolder() == null) {
            return true;
        } else if (loginUser != null) {
            return LOCK_STATUS_HELD.equals(ruleLock.getStatus()) && loginUser.equals(ruleLock.getHolder());
        } else {
            return LOCK_STATUS_HELD.equals(ruleLock.getStatus()) && ruleLock.getHolder().equals(null);
        }
    }

    private boolean getLockIfTimeout(RuleLock ruleLock, String loginUser) throws UnExpectedRequestException {
        Long currentTime = System.currentTimeMillis() / 1000;
        boolean isPossessed = LOCK_STATUS_HELD.equals(ruleLock.getStatus());
        boolean isTimeout = (currentTime - ruleLock.getTimestamp()) > maxEditableDurationOnPage;
        if (isPossessed && isTimeout) {
            ruleLock.setStatus(LOCK_STATUS_HELD);
            ruleLock.setTimestamp(currentTime);
            ruleLock.setHolder(loginUser);
            ruleLock = ruleLockDao.modify(ruleLock);
            return loginUser.equals(ruleLock.getHolder());
        }
        return false;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public synchronized void release(Long ruleLockId, RuleLockRangeEnum ruleLockRangeEnum, String loginUser) throws RuleLockException {
        String lockKey = generateLockKey(ruleLockId, ruleLockRangeEnum);
        RuleLock ruleLock = ruleLockDao.findByLockKeyWithLock(lockKey);
        if (ruleLock == null) {
            LOGGER.warn("The lock is not exists");
            throw new RuleLockException("{&RULE_LOCK_RELEASE_FAILED}: The lock is not exists");
        }
        if (loginUser != null && !loginUser.equals(ruleLock.getHolder())) {
            LOGGER.warn("The lock had been possessed by other, loginUser: {}, holder: {}, lockKey: {}", loginUser, ruleLock.getHolder(), lockKey);
            throw new RuleLockException("{&RULE_LOCK_RELEASE_FAILED}: The lock was possessed by other");
        }

        Long currentTime = System.currentTimeMillis() / 1000;
        Integer recordCount = ruleLockDao.releaseLock(lockKey, loginUser, currentTime);
        if (recordCount <= 0) {
            LOGGER.warn("Failed to release the lock, loginUser: {}, holder: {}, lockKey: {}", loginUser, ruleLock.getHolder(), lockKey);
            throw new RuleLockException("{&RULE_LOCK_RELEASE_FAILED}: Failed to release the lock");
        }
    }

    private String generateLockKey(Long ruleLockId, RuleLockRangeEnum ruleLockRangeEnum) {
        return ruleLockId + SpecCharEnum.BOTTOM_BAR.getValue() + ruleLockRangeEnum.getValue();
    }

}
