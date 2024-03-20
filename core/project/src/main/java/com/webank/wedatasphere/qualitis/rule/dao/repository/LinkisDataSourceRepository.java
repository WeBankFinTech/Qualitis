package com.webank.wedatasphere.qualitis.rule.dao.repository;

import com.webank.wedatasphere.qualitis.rule.entity.LinkisDataSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2022-09-16 9:55
 * @description
 */
public interface LinkisDataSourceRepository extends JpaRepository<LinkisDataSource, Long>, JpaSpecificationExecutor<LinkisDataSource> {

    /**
     * Find by linkisDataSourceId
     * @param linkisDataSourceId
     * @return
     */
    LinkisDataSource findByLinkisDataSourceId(Long linkisDataSourceId);

    /**
     * Find by linkisDataSourceId
     * @param linkisDataSourceName
     * @return
     */
    LinkisDataSource findByLinkisDataSourceName(String linkisDataSourceName);

    /**
     * Find by linkisDataSourceIds
     * @param dataSourceIds
     * @return
     */
    List<LinkisDataSource> findByLinkisDataSourceIdIn(List<Long> dataSourceIds);

    /**
     * find All Linkis Data Source Name List
     * @return
     */
    @Query(value = "select linkis_data_source_name from qualitis_linkis_datasource", nativeQuery = true)
    List<String> findAllLinkisDataSourceNameList();
}
