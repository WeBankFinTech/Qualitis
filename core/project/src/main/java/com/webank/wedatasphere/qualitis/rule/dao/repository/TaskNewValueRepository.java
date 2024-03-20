package com.webank.wedatasphere.qualitis.rule.dao.repository;

import com.webank.wedatasphere.qualitis.rule.entity.TaskNewValue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface TaskNewValueRepository extends JpaRepository<TaskNewValue, Long>, JpaSpecificationExecutor<TaskNewValue> {

    /**
     * find All
     *
     * @param ruleId
     * @param pageable
     * @return
     */
    @Query(value = "select ds from TaskNewValue ds where  ds.ruleId = ?1 group by ds.resultValue,ds.status")
    Page<TaskNewValue> findAllData(Long ruleId, Pageable pageable);

    /**
     * count Total
     *
     * @param ruleId
     * @return
     */
    @Query(value = "SELECT COUNT(DISTINCT qr.result_value,qr.status) from qualitis_task_new_value qr where  qr.rule_id =?1  ", nativeQuery = true)
    int countTotal(Long ruleId);

    /**
     * findMatchTaskNewValue
     *
     * @param ruleId
     * @return
     */
    @Query(value = "select coalesce(count(ds.id),0) from TaskNewValue ds where  ds.status=1 and ds.ruleId = ?1 ")
    Long findMatchTaskNewValue(Long ruleId);

    /**
     * selectExactTaskNewValue
     *
     * @param ruleId
     * @param status
     * @param resultValue
     * @return
     */
    @Query(value = "select ds from TaskNewValue ds where  ds.ruleId = ?1 and ds.status=?2 and  ds.resultValue=?3 ")
    List<TaskNewValue> selectExactTaskNewValue(Long ruleId,Long status,String resultValue);

}
