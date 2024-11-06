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
     * Find by linkisDataSourceName
     * @param linkisDataSourceName
     * @return
     */
    LinkisDataSource findByLinkisDataSourceName(String linkisDataSourceName);

    /**
     * find By Linkis Data Source Name In
     * @param linkisDataSourceNameList
     * @return
     */
    List<LinkisDataSource> findByLinkisDataSourceNameIn(List<String> linkisDataSourceNameList);
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

    /**
     * find Linkis Data Source Ids By Dcn Range
     * @param dcnRangeType
     * @param dcnNums
     * @param logicAreas
     * @return
     */
    @Query(value = "select DISTINCT ds.linkis_data_source_id from qualitis_linkis_datasource ds inner join qualitis_linkis_datasource_env dse on dse.linkis_data_source_id = ds.linkis_data_source_id \n" +
            "where ds.dcn_range_type = ?1 and (dse.dcn_num in (?2) or dse.logic_area in (?3))", nativeQuery = true)
    List<Long> findLinkisDataSourceIdsByDcnRange(String dcnRangeType, List<String> dcnNums, List<String> logicAreas);
}
