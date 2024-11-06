package com.webank.wedatasphere.qualitis.rule.dao;

import com.webank.wedatasphere.qualitis.rule.entity.StandardValueVersion;

import java.util.List;
import java.util.Set;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface StandardValueVersionDao {

    /**
     * select number.
     *
     * @param standardVauleId
     * @return
     */
    Long selectMaxVersion(long standardVauleId);

    /**
     * save StandardValueVersion
     *
     * @param standardValueVersion
     * @return
     */
    StandardValueVersion saveStandardValueVersion(StandardValueVersion standardValueVersion);

    /**
     * find StandardValueVersion
     *
     * @param id
     * @return
     */
    StandardValueVersion findById(Long id);

    /**
     * Find by StandardValueVersion by standardValue
     *
     * @param id
     * @return
     */
    List<StandardValueVersion> findByStandardValueVersion(Long id);

    /**
     * Find by StandardValueVersion by enName
     *
     * @param enName
     * @return
     */
    List<StandardValueVersion> findAllData(String enName);

    /**
     * delete StandardValueVersion
     *
     * @param standardValueVersion
     */
    void deleteStandardValueVersion(StandardValueVersion standardValueVersion);

    /**
     * delete All StandardValueVersion
     *
     * @param standardValueVersion
     */
    void deleteAll(Iterable<StandardValueVersion> standardValueVersion);


    /**
     * find All StandardValue
     *
     * @param cnName
     * @param enName
     * @param standardValueId
     * @param createUser
     * @param devDepartmentId
     * @param opsDepartmentId
     * @param actionRange
     * @param dataType
     * @param sourceType
     * @param modifyUser
     * @param page
     * @param size
     * @return
     */
    List<StandardValueVersion> findAllStandardValue(String cnName, String enName, String standardValueId, String createUser, String devDepartmentId, String opsDepartmentId, Set<String> actionRange, String dataType, String sourceType, String modifyUser, int page, int size);

    /**
     * count StandardValue
     *
     * @param cnName
     * @param enName
     * @param standardValueId
     * @param createUser
     * @param devDepartmentId
     * @param opsDepartmentId
     * @param actionRange
     * @param dataType
     * @param sourceType
     * @param modifyUser
     * @return
     */
    Long countAllStandardValue(String cnName, String enName, String standardValueId, String createUser, String devDepartmentId, String opsDepartmentId, Set<String> actionRange, String dataType, String sourceType, String modifyUser);

    /**
     * Find StandardValue.
     *
     * @param cnName
     * @param enName
     * @param standardValueId
     * @param createUser
     * @param devDepartmentId
     * @param opsDepartmentId
     * @param actionRange
     * @param tableDataType
     * @param dataVisibilityDeptList
     * @param loginUser
     * @param sourceType
     * @param modifyUser
     * @param page
     * @param size
     * @return
     */
    List<StandardValueVersion> findStandardValue(String cnName, String enName, String standardValueId, String createUser, String devDepartmentId, String opsDepartmentId, Set<String> actionRange,
                                                 String tableDataType, List<Long> dataVisibilityDeptList, String loginUser, String sourceType, String modifyUser, int page, int size);

    /**
     * Count StandardValue.
     *
     * @param cnName
     * @param enName
     * @param standardValueId
     * @param createUser
     * @param devDepartmentId
     * @param opsDepartmentId
     * @param actionRange
     * @param tableDataType
     * @param dataVisibilityDeptList
     * @param loginUser
     * @param sourceType
     * @param modifyUser
     * @return
     */
    Long countStandardValue(String cnName, String enName, String standardValueId, String createUser, String devDepartmentId, String opsDepartmentId, Set<String> actionRange
            , String tableDataType, List<Long> dataVisibilityDeptList, String loginUser, String sourceType, String modifyUser);

    /**
     * find StandardValueVersion.
     *
     * @param id
     * @param page
     * @param size
     * @return
     */
    List<StandardValueVersion> selectStandardValue(Long id, int page, int size);

    /**
     * get count StandardValueVersion.
     *
     * @param id
     * @return
     */
    Long countStandardValue(Long id);

    /**
     * get count StandardValueVersion.
     *
     * @param id
     * @param tableDataType
     * @param dataVisibilityDeptList
     * @param loginUser
     * @param page
     * @param size
     * @return
     */
    List<StandardValueVersion> selectSuit(Long id, String tableDataType, List<Long> dataVisibilityDeptList, String loginUser, int page, int size);

    /**
     * get count StandardValueVersion.
     *
     * @param id
     * @param tableDataType
     * @param dataVisibilityDeptList
     * @param loginUser
     * @return
     */
    Long countSuitSome(Long id, String tableDataType, List<Long> dataVisibilityDeptList, String loginUser);

    /**
     * get Version list.
     *
     * @param standardValueId
     * @return
     */
    List<Long> getVersionList(Long standardValueId);

    /**
     * get MaxVersion StandardValueVersion.
     *
     * @param id
     * @return
     */
    StandardValueVersion getMaxVersion(Long id);

    /**
     * selectWhereStandardValueId
     *
     * @param standardValueId
     * @return
     */
    List<StandardValueVersion> selectWhereStandardValueId(long standardValueId);

    /**
     * find All
     *
     * @return
     */
    List<StandardValueVersion> findAll();

    /**
     * find By EnName
     *
     * @param enName
     * @return
     */
    StandardValueVersion findByEnName(String enName);
}
