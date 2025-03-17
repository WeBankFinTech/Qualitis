package com.webank.wedatasphere.qualitis.rule.dao.repository;

import com.webank.wedatasphere.qualitis.rule.entity.StandardValueVariables;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2024-09-10 15:25
 * @description
 */
public interface StandardValueVariablesRepository extends JpaRepository<StandardValueVariables, Long> {

    /**
     *
     * @param ruldId
     * @return
     */
    List<StandardValueVariables> findByRuleId(Long ruldId);

}
