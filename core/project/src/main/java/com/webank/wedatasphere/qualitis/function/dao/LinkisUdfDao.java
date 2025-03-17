package com.webank.wedatasphere.qualitis.function.dao;

import com.webank.wedatasphere.qualitis.function.entity.LinkisUdf;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author allenzhou@webank.com
 * @date 2022/11/7 17:30
 */
public interface LinkisUdfDao {

    /**
     * Save
     * @param linkisUdf
     * @return
     */
    LinkisUdf save(LinkisUdf linkisUdf);

    /**
     * Find by id
     * @param id
     * @return
     */
    LinkisUdf findById(Long id);

    /**
     *Find by name
     * @param name
     * @return
     */
    LinkisUdf findByName(String name);

    /**
     * Filter
     * @param name
     * @param cnName
     * @param dir
     * @param implType
     * @param enableEngine
     * @param enableCluster
     * @param createUser
     * @param modifyUser
     * @param code
     * @param longs
     * @param loginUser
     * @param page
     * @param size
     * @return
     */
    Page<LinkisUdf> filter(String name, String cnName, String dir, Integer implType, List<Integer> enableEngine, List<String> enableCluster, String createUser, String modifyUser, String code, List<Long> longs, String loginUser, int page, int size);

    /**
     * For admin filter
     * @param name
     * @param cnName
     * @param dir
     * @param implType
     * @param enableEngine
     * @param enableCluster
     * @param createUser
     * @param modifyUser
     * @param devDepartmentId
     * @param opsDepartmentId
     * @param dataVisibilityIds
     * @param page
     * @param size
     * @return
     */
    Page<LinkisUdf> filterAll(String name, String cnName, String dir, Integer implType, List<Integer> enableEngine, List<String> enableCluster,
        String createUser, String modifyUser, Long devDepartmentId, Long opsDepartmentId, List<Long> dataVisibilityIds, int page,
        int size);

    /**
     * For admin count
     * @param name
     * @param cnName
     * @param dir
     * @param implType
     * @param enableEngine
     * @param enableCluster
     * @param createUser
     * @param modifyUser
     * @param devDepartmentId
     * @param opsDepartmentId
     * @return
     */
    int countAll(String name, String cnName, String dir, Integer implType, List<Integer> enableEngine, List<String> enableCluster, String createUser,
        String modifyUser, Long devDepartmentId, Long opsDepartmentId);

    /**
     * Delete
     * @param linkisUdf
     */
    void delete(LinkisUdf linkisUdf);
}
