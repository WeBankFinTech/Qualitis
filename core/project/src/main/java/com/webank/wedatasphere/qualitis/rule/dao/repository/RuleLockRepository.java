package com.webank.wedatasphere.qualitis.rule.dao.repository;

import com.webank.wedatasphere.qualitis.rule.entity.RuleLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2022-12-19 9:44
 * @description
 */
public interface RuleLockRepository extends JpaRepository<RuleLock, Long> {

    /** find Multi Lock In Hold Status
     * @param lockKeys
     * @param expiredTimestamp
     * @return
     */
    @Query(value = "select * from qualitis_rule_lock qatr where lock_key in (?1) and status = 1 and timestamp >= ?2", nativeQuery = true)
    List<RuleLock> findMultiLockInHoldStatus(List<String> lockKeys, Long expiredTimestamp);

    /**
     * Finding by rule_id with record lock
     *
     * @param lockKey
     * @return
     */
    @Query(value = "select * from qualitis_rule_lock where lock_key = ?1 for update", nativeQuery = true)
    RuleLock findByLockKeyWithLock(String lockKey);

    /**
     * Creating a new lock
     *
     * @param lockKey
     * @param holder
     * @param timestamp
     * @param status
     * @return
     */
    @Modifying
    @Query(value = "insert into qualitis_rule_lock(lock_key, holder, timestamp, status) values(?1, ?2, ?3, ?4)", nativeQuery = true)
    Integer newLock(String lockKey, String holder, Long timestamp, Integer status);

    /**
     * Update holder of lock
     *
     * @param lockKey
     * @param holder
     * @param timestamp
     * @return
     */
    @Modifying
    @Query(value = "update qualitis_rule_lock set status=1,holder=?2,timestamp=?3 where lock_key=?1 and status=0", nativeQuery = true)
    Integer acquireLock(String lockKey, String holder, Long timestamp);

    /**
     * Release a lock
     *
     * @param lockKey
     * @param holder
     * @param timestamp
     * @return
     */
    @Modifying
    @Query(value = "update qualitis_rule_lock set status=0,timestamp=?3 where lock_key=?1 and holder=?2 and status=1", nativeQuery = true)
    Integer releaseLock(String lockKey, String holder, Long timestamp);

}
