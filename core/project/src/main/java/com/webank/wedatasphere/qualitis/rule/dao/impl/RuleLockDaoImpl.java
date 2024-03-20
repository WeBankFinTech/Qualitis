package com.webank.wedatasphere.qualitis.rule.dao.impl;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.rule.dao.RuleLockDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.RuleLockRepository;
import com.webank.wedatasphere.qualitis.rule.entity.RuleLock;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

/**
 * @author v_minminghe@webank.com
 * @date 2022-12-19 9:46
 * @description
 */
@Repository
public class RuleLockDaoImpl implements RuleLockDao {

    @Autowired
    private RuleLockRepository ruleLockRepository;

    @Override
    public boolean checkMultiLockIfFreeStatus(List<String> lockKeys, String loginUser, Long expiredTimestamp) {
        List<RuleLock> ruleLockList = ruleLockRepository.findMultiLockInHoldStatus(lockKeys, expiredTimestamp);
        if (CollectionUtils.isEmpty(ruleLockList)) {
            return true;
        }
        return ruleLockList.stream().map(RuleLock::getHolder).anyMatch(holder -> holder.equals(loginUser));
    }

    @Override
    public RuleLock findByLockKeyWithLock(String lockKey) {
        return ruleLockRepository.findByLockKeyWithLock(lockKey);
    }

    @Override
    public Integer newLock(String lockKey, String holder, Long timestamp, Integer status) {
        return ruleLockRepository.newLock(lockKey, holder, timestamp, status);
    }

    @Override
    public Integer acquireLock(String lockKey, String holder, Long timestamp) {
        return ruleLockRepository.acquireLock(lockKey, holder, timestamp);
    }

    @Override
    public Integer releaseLock(String lockKey, String holder, Long timestamp) {
        return ruleLockRepository.releaseLock(lockKey, holder, timestamp);
    }

    @Override
    public RuleLock modify(RuleLock ruleLock) throws UnExpectedRequestException {
        if (Objects.isNull(ruleLock.getLockKey())) {
            throw new UnExpectedRequestException("LockId must be not null");
        }
        return ruleLockRepository.save(ruleLock);
    }
}
