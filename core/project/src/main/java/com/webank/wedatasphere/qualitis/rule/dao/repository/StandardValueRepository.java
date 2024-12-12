package com.webank.wedatasphere.qualitis.rule.dao.repository;

import com.webank.wedatasphere.qualitis.rule.entity.StandardValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface StandardValueRepository extends JpaRepository<StandardValue, Long>, JpaSpecificationExecutor<StandardValue> {



}
