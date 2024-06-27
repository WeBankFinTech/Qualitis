package com.webank.wedatasphere.qualitis.rule.dao.repository;

import com.webank.wedatasphere.qualitis.rule.entity.LinkisDataSourceEnv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2023-12-01 16:42
 * @description
 */
public interface LinkisDataSourceEnvRepository extends JpaRepository<LinkisDataSourceEnv, Long>, JpaSpecificationExecutor<LinkisDataSourceEnv> {

    /**
     * delete By Linkis Data SourceId
     * @param envIds
     */
    void deleteByEnvIdIn(List<Long> envIds);

    /**
     * find By Linkis Data Source Id
     * @param linkisDataSourceId
     * @return
     */
    List<LinkisDataSourceEnv> findByLinkisDataSourceId(Long linkisDataSourceId);
}
