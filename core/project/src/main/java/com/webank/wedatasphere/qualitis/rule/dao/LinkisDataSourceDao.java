package com.webank.wedatasphere.qualitis.rule.dao;

import com.webank.wedatasphere.qualitis.rule.entity.LinkisDataSource;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2022-09-16 9:57
 * @description
 */
public interface LinkisDataSourceDao {

    /**
     * save
     * @param linkisDataSource
     */
    void save(LinkisDataSource linkisDataSource);

    /**
     * get By Linkis Data Source Id
     * @param dataSourceId
     * @return
     */
    LinkisDataSource getByLinkisDataSourceId(Long dataSourceId);

    /**
     * get By Linkis Data Source Id
     * @param dataSourceName
     * @return
     */
    LinkisDataSource getByLinkisDataSourceName(String dataSourceName);

    /**
     * get By Linkis Data Source Ids
     * @param dataSourceIds
     * @return
     */
    List<LinkisDataSource> getByLinkisDataSourceIds(List<Long> dataSourceIds);

    /**
     * filter
     * @param dataSourceName
     * @param dataSourceTypeId
     * @param dataVisibilityDeptList
     * @param createUser
     * @param searchCreateUser
     * @param searchModifyUser
     * @param subSystemName
     * @param devDepartmentId
     * @param opsDepartmentId
     * @param ignoreDataAuthorityCondition 忽视对数据权限的查询条件限制，一般用于管理员
     * @param searchDataVisibilityDeptList
     * @param page
     * @param size
     * @return
     */
    Page<LinkisDataSource> filterWithPage(String dataSourceName, Long dataSourceTypeId, List<Long> dataVisibilityDeptList, String createUser
            , String searchCreateUser, String searchModifyUser, String subSystemName, Long devDepartmentId, Long opsDepartmentId
            , Boolean ignoreDataAuthorityCondition, List<Long> searchDataVisibilityDeptList, int page, int size);

    /**
     * get all datasource name
     * @return
     */
    List<String> getAllDataSourceNameList();

}
