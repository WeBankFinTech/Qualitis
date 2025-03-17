package com.webank.wedatasphere.qualitis.dao.repository;

import com.webank.wedatasphere.qualitis.entity.LinksErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface LinksErrorCodeRepository extends JpaRepository<LinksErrorCode, Long> {

    /**
     * 查询所有Linkis错误状态码
     * @return
     */
    List<LinksErrorCode> findByLinkisErrorCodeIsNotNull();

    /**
     * 查询所有WTSS错误表达式
     * @return
     */
    List<LinksErrorCode> findByWtssErrorExpressionIsNotNull();

}
