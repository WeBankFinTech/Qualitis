package com.webank.wedatasphere.qualitis.rule.dao.repository;

import com.webank.wedatasphere.qualitis.rule.entity.StandardValueVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface StandardValueVersionRepository extends JpaRepository<StandardValueVersion, Long>, JpaSpecificationExecutor<StandardValueVersion> {

    /**
     * get maxVersion
     *
     * @param standardVauleId
     * @return
     */
    @Query(value = "SELECT coalesce(MAX(qt.version),0) from StandardValueVersion qt where qt.standardValue.id = ?1")
    Long selectMaxVersion(long standardVauleId);

    /**
     * get maxVersion StandardValueVersion
     *
     * @param id
     * @return
     */
    @Query(value = "SELECT q.* from qualitis_standard_value_version q " +
            " where q.version =(SELECT max(qrm.version) from qualitis_standard_value_version qrm " +
            " where qrm.standard_value_id = q.standard_value_id  " +
            " AND qrm.is_available =1  and qrm.standard_value_id=?1" +
            " )", nativeQuery = true)
    StandardValueVersion getMaxVersion(Long id);

    /**
     * Find by StandardValueVersion by id
     *
     * @param id
     * @return
     */
    @Query(value = "select q.* from qualitis_standard_value_version q where q.is_available =1 and q.id not in(SELECT qt.id from qualitis_standard_value_version qt where  qt.is_available =1 AND qt.standard_value_id =?1)", nativeQuery = true)
    List<StandardValueVersion> findByStandardValueVersion(long id);

    /**
     * Find by enName
     *
     * @param enName
     * @return
     */
    @Query(value = "select q.* from qualitis_standard_value_version q where q.is_available =1 and q.en_name=?1", nativeQuery = true)
    List<StandardValueVersion> selectStandardValueVersion(String enName);


    /**
     * Find by standardValueId
     *
     * @param standardValueId
     * @return
     */
    @Query(value = "select q.* from qualitis_standard_value_version q where q.is_available =1 and q.standard_value_id=?1", nativeQuery = true)
    List<StandardValueVersion> selectWhereStandardValueId(long standardValueId);

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
     * @param pageable
     * @return
     */
    @Query(value = "SELECT qrm.* from qualitis_standard_value_version as qrm " +
            "where qrm.is_available =1 " +
            "AND if(nullif(?1,'')!='',  qrm.cn_name like ?1,1=1) " +
            "AND if(nullif(?2,'')!='',  qrm.en_name like ?2,1=1)  " +
            "AND if(nullif(?3,'')!='',  qrm.id = ?3,1=1) " +
            "AND if(nullif(?4,'')!='',  qrm.create_user = ?4,1=1)  " +
            "AND if(nullif(?5,'')!='',  qrm.dev_department_id = ?5,1=1)  " +
            "AND if(nullif(?6,'')!='',  qrm.ops_department_id = ?6,1=1)  " +
            "AND (coalesce(?7,null) is null or exists (select q.table_data_id from qualitis_auth_data_visibility as q where q.table_data_id=qrm.id and q.table_data_type=?8 and (q.department_sub_id in (?7) or q.department_sub_id=0) ))  " +
            "AND if(nullif(?9,'')!='',  qrm.source_value =?9,1=1)  " +
            "AND if(nullif(?10,'')!='', qrm.modify_user =?10,1=1)  ",
            countQuery = "SELECT count(*) from qualitis_standard_value_version as qrm " +
                    "where qrm.is_available =1 " +
                    "AND if(nullif(?1,'')!='',  qrm.cn_name like ?1,1=1) " +
                    "AND if(nullif(?2,'')!='',  qrm.en_name like ?2,1=1) " +
                    "AND if(nullif(?3,'')!='',  qrm.id = ?3,1=1) " +
                    "AND if(nullif(?4,'')!='',  qrm.create_user = ?4,1=1) " +
                    "AND if(nullif(?5,'')!='',  qrm.dev_department_id = ?5,1=1) " +
                    "AND if(nullif(?6,'')!='',  qrm.ops_department_id = ?6,1=1) " +
                    "AND (coalesce(?7,null) is null or exists (select q.table_data_id from qualitis_auth_data_visibility as q where q.table_data_id=qrm.id and q.table_data_type=?8 and (q.department_sub_id in (?7) or q.department_sub_id=0) ))   " +
                    "AND if(nullif(?9,'')!='',  qrm.source_value =?9,1=1) AND if(nullif(?10,'')!='', qrm.modify_user =?10,1=1) ", nativeQuery = true)
    Page<StandardValueVersion> findAllStandardValue(String cnName, String enName, String standardValueId, String createUser, String devDepartmentId, String opsDepartmentId, Set<String> actionRange, String dataType, String sourceType, String modifyUser, Pageable pageable);

    /**
     * Find all with another way
     *
     * @param cnName
     * @param enName
     * @param standardValueId
     * @param createUser
     * @param version
     * @param actionRange
     * @param page
     * @param size
     * @return
     */
    @Query(value = "SELECT tmp1.* FROM qualitis_standard_value_version tmp1 join (SELECT qsv.id as 'standard_value_id', max(qsvv.version) as 'standard_value_version' FROM qualitis_standard_value_version qsvv join qualitis_standard_value qsv on qsv.id = qsvv.standard_value_id group by qsv.id) tmp2 on (tmp1.standard_value_id = tmp2.standard_value_id AND (tmp1.version = tmp2.standard_value_version OR (?5 IS NOT NULL AND tmp1.version = ?5))) WHERE tmp1.is_available=1 AND (LENGTH(?1) = 0 OR tmp1.cn_name like ?1) AND (LENGTH(?2) = 0 OR tmp1.en_name like ?2) AND (?3 IS NULL OR tmp1.standard_value_id = ?3) AND (LENGTH(?4) = 0 OR tmp1.create_user = ?4) AND (?5 IS NULL OR tmp1.version = ?5) AND (?6 IS NULL OR EXISTS (SELECT DISTINCT qsvav.id FROM qualitis_standard_value_action_version qsvav where qsvav.action_range IN (?6))) order by tmp1.standard_value_id desc limit ?7, ?8", nativeQuery = true)
    List<StandardValueVersion> findAllStandardValueBak(String cnName, String enName, Long standardValueId, String createUser, Long version, Set<String> actionRange, int page, int size);

    /**
     * count All StandardValue
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
    @Query(value = "SELECT count(qrm.id) from qualitis_standard_value_version as qrm " +
            "where qrm.is_available =1 " +
            "AND if(nullif(?1,'')!='',  qrm.cn_name like ?1,1=1) " +
            "AND if(nullif(?2,'')!='',  qrm.en_name like ?2,1=1) " +
            "AND if(nullif(?3,'')!='',  qrm.id = ?3,1=1) " +
            "AND if(nullif(?4,'')!='',  qrm.create_user = ?4,1=1) " +
            "AND if(nullif(?5,'')!='',  qrm.dev_department_id = ?5,1=1) " +
            "AND if(nullif(?6,'')!='',  qrm.ops_department_id = ?6,1=1) " +
            "AND (coalesce(?7,null) is null or exists (select q.table_data_id from qualitis_auth_data_visibility as q where q.table_data_id=qrm.id and q.table_data_type=?8 and (q.department_sub_id in (?7) or q.department_sub_id=0) )) " +
            "AND if(nullif(?9,'')!='',  qrm.source_value =?9,1=1) AND if(nullif(?10,'')!='', qrm.modify_user =?10,1=1) ", nativeQuery = true)
    Long countAllStandardValue(String cnName, String enName, String standardValueId, String createUser, String devDepartmentId, String opsDepartmentId, Set<String> actionRange, String dataType, String sourceType, String modifyUser);

    /**
     * find All
     *
     * @param cnName
     * @param enName
     * @param standardValueId
     * @param createUser
     * @param version
     * @param actionRange
     * @return
     */
    @Query(value = "SELECT count(tmp1.id) FROM qualitis_standard_value_version tmp1 join (SELECT qsv.id as 'standard_value_id', max(qsvv.version) as 'standard_value_version' FROM qualitis_standard_value_version qsvv join qualitis_standard_value qsv on qsv.id = qsvv.standard_value_id group by qsv.id) tmp2 on (tmp1.standard_value_id = tmp2.standard_value_id AND (tmp1.version = tmp2.standard_value_version OR (?5 IS NOT NULL AND tmp1.version = ?5))) WHERE tmp1.is_available=1 AND (LENGTH(?1) = 0 OR tmp1.cn_name like ?1) AND (LENGTH(?2) = 0 OR tmp1.en_name like ?2) AND (?3 IS NULL OR tmp1.standard_value_id = ?3) AND (LENGTH(?4) = 0 OR tmp1.create_user = ?4) AND (?5 IS NULL OR tmp1.version = ?5) AND (?6 IS NULL OR EXISTS (SELECT DISTINCT qsvav.id FROM qualitis_standard_value_action_version qsvav where qsvav.action_range IN (?6)))", nativeQuery = true)
    Long countAllStandardValueBak(String cnName, String enName, Long standardValueId, String createUser, Long version, Set<String> actionRange);

    /**
     * Find StandardValueVersion by user permission.
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
     * @param pageable
     * @return
     */
    @Query(value = "SELECT qrm.* from qualitis_standard_value_version as qrm " +
            "where qrm.is_available =1 " +
            "AND if(nullif(?1,'')!='',  qrm.cn_name like ?1,1=1) " +
            "AND if(nullif(?2,'')!='',  qrm.en_name like ?2,1=1) " +
            "AND if(nullif(?3,'')!='',  qrm.id = ?3,1=1) " +
            "AND if(nullif(?4,'')!='',  qrm.create_user = ?4,1=1) " +
            "AND if(nullif(?5,'')!='',  qrm.dev_department_id = ?5,1=1) " +
            "AND if(nullif(?6,'')!='',  qrm.ops_department_id = ?6,1=1) " +
            "AND (coalesce(?7,null) is null or exists (select q.table_data_id from qualitis_auth_data_visibility as q where q.table_data_id=qrm.id and q.table_data_type=?8 and (q.department_sub_id in (?7) or q.department_sub_id=0) )) " +
            "AND (qrm.create_user = ?10 OR qrm.dev_department_id in (?9) OR qrm.ops_department_id in (?9) OR EXISTS (select dv.table_data_id from qualitis_auth_data_visibility as dv where dv.table_data_type = ?8 and (dv.department_sub_id in (?9) or dv.department_sub_id=0) and dv.table_data_id=qrm.id)) " +
            "AND if(nullif(?11,'')!='',  qrm.source_value =?11,1=1) AND if(nullif(?12,'')!='',  qrm.modify_user =?12,1=1) ",
            countQuery = "SELECT count(*) from qualitis_standard_value_version as qrm " +
                    "where qrm.is_available =1 " +
                    "AND if(nullif(?1,'')!='',  qrm.cn_name like ?1,1=1) " +
                    "AND if(nullif(?2,'')!='',  qrm.en_name like ?2,1=1) " +
                    "AND if(nullif(?3,'')!='',  qrm.id = ?3,1=1) " +
                    "AND if(nullif(?4,'')!='',  qrm.create_user = ?4,1=1) " +
                    "AND if(nullif(?5,'')!='',  qrm.dev_department_id = ?5,1=1) " +
                    "AND if(nullif(?6,'')!='',  qrm.ops_department_id = ?6,1=1) " +
                    "AND (coalesce(?7,null) is null or exists (select q.table_data_id from qualitis_auth_data_visibility as q where q.table_data_id=qrm.id and q.table_data_type=?8 and (q.department_sub_id in (?7) or q.department_sub_id=0) )) " +
                    "AND (qrm.create_user = ?10 OR qrm.dev_department_id in (?9) OR qrm.ops_department_id in (?9) OR EXISTS (select dv.table_data_id from qualitis_auth_data_visibility as dv where dv.table_data_type = ?8 and (dv.department_sub_id in (?9) or dv.department_sub_id=0) and dv.table_data_id=qrm.id)) " +
                    "AND if(nullif(?11,'')!='',  qrm.source_value =?11,1=1) AND if(nullif(?12,'')!='',  qrm.modify_user =?12,1=1) ", nativeQuery = true)
    Page<StandardValueVersion> findStandardValue(String cnName, String enName, String standardValueId, String createUser, String devDepartmentId, String opsDepartmentId, Set<String> actionRange
            , String tableDataType, List<Long> dataVisibilityDeptList, String loginUser, String sourceType, String modifyUser, Pageable pageable);

    /**
     * Find StandardValueVersion by user permission.
     *
     * @param cnName
     * @param enName
     * @param standardValueId
     * @param createUser
     * @param version
     * @param actionRange
     * @param departmentList
     * @param userList
     * @param tableDataType
     * @param dataVisibilityDeptList
     * @param page
     * @param size
     * @return
     */
    @Query(value = "SELECT tmp1.* FROM qualitis_standard_value_version tmp1 join (SELECT qsv.id as 'standard_value_id', max(qsvv.version) as 'standard_value_version' FROM qualitis_standard_value_version qsvv join qualitis_standard_value qsv on qsv.id = qsvv.standard_value_id group by qsv.id) tmp2 on (tmp1.standard_value_id = tmp2.standard_value_id AND (tmp1.version = tmp2.standard_value_version OR (?5 IS NOT NULL AND tmp1.version = ?5))) WHERE tmp1.is_available=1 AND (LENGTH(?1) = 0 OR tmp1.cn_name like ?1) AND (LENGTH(?2) = 0 OR tmp1.en_name like ?2) AND (?3 IS NULL OR tmp1.standard_value_id = ?3) AND (LENGTH(?4) = 0 OR tmp1.create_user = ?4) AND (?5 IS NULL OR tmp1.version = ?5) AND (?6 IS NULL OR EXISTS (SELECT DISTINCT qsvav.id FROM qualitis_standard_value_action_version qsvav where qsvav.action_range IN (?6))) AND (tmp1.standard_value_type = 1 OR (tmp1.standard_value_type = 2 AND EXISTS (SELECT DISTINCT qsvdv.standard_value_version_id FROM qualitis_standard_value_department_version qsvdv WHERE qsvdv.deptment_id IN (?7))) OR (tmp1.standard_value_type = 3 AND EXISTS (SELECT DISTINCT qsvuv.standard_value_version_id FROM qualitis_standard_value_user_version qsvuv WHERE qsvuv.user_id IN (?8)))) order by tmp1.standard_value_id desc limit ?9, ?10", nativeQuery = true)
    List<StandardValueVersion> findStandardValueBak(String cnName, String enName, Long standardValueId, String createUser, Long version, Set<String> actionRange, List<Long> departmentList, List<Long> userList, String tableDataType, List<Long> dataVisibilityDeptList, int page, int size);

    /**
     * count All Name
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
    @Query(value = "SELECT count(qrm.id) from qualitis_standard_value_version as qrm " +
            "where qrm.is_available =1 " +
            "AND if(nullif(?1,'')!='',  qrm.cn_name like ?1,1=1) " +
            "AND if(nullif(?2,'')!='',  qrm.en_name like ?2,1=1) " +
            "AND if(nullif(?3,'')!='',  qrm.id = ?3,1=1) " +
            "AND if(nullif(?4,'')!='',  qrm.create_user = ?4,1=1) " +
            "AND if(nullif(?5,'')!='',  qrm.dev_department_id = ?5,1=1) " +
            "AND if(nullif(?6,'')!='',  qrm.ops_department_id = ?6,1=1) " +
            "AND (coalesce(?7,null) is null or exists (select q.table_data_id from qualitis_auth_data_visibility as q where q.table_data_id=qrm.id and q.table_data_type=?8 and (q.department_sub_id in (?7) or q.department_sub_id=0) )) " +
            "AND (qrm.create_user = ?10 OR qrm.dev_department_id in (?9) OR qrm.ops_department_id in (?9) OR EXISTS (select dv.table_data_id from qualitis_auth_data_visibility as dv where dv.table_data_type = ?8 and (dv.department_sub_id in (?9) or dv.department_sub_id=0) and dv.table_data_id=qrm.id)) " +
            "AND if(nullif(?11,'')!='',  qrm.source_value =?11,1=1) AND if(nullif(?12,'')!='',  qrm.modify_user =?12,1=1) ", nativeQuery = true)
    Long countAllName(String cnName, String enName, String standardValueId, String createUser, String devDepartmentId, String opsDepartmentId, Set<String> actionRange
            , String tableDataType, List<Long> dataVisibilityDeptList, String loginUser, String sourceType, String modifyUser);

    /**
     * Count StandardValueVersion;
     *
     * @param cnName
     * @param enName
     * @param standardValueId
     * @param createUser
     * @param version
     * @param actionRange
     * @param departments
     * @param users
     * @return
     */
    @Query(value = "SELECT count(tmp1.id) FROM qualitis_standard_value_version tmp1 join (SELECT qsv.id as 'standard_value_id', max(qsvv.version) as 'standard_value_version' FROM qualitis_standard_value_version qsvv join qualitis_standard_value qsv on qsv.id = qsvv.standard_value_id group by qsv.id) tmp2 on (tmp1.standard_value_id = tmp2.standard_value_id AND (tmp1.version = tmp2.standard_value_version OR (?5 IS NOT NULL AND tmp1.version = ?5))) WHERE tmp1.is_available=1 AND (LENGTH(?1) = 0 OR tmp1.cn_name like ?1) AND (LENGTH(?2) = 0 OR tmp1.en_name like ?2) AND (?3 IS NULL OR tmp1.standard_value_id = ?3) AND (LENGTH(?4) = 0 OR tmp1.create_user = ?4) AND (?5 IS NULL OR tmp1.version = ?5) AND (?6 IS NULL OR EXISTS (SELECT DISTINCT qsvav.id FROM qualitis_standard_value_action_version qsvav where qsvav.action_range IN (?6))) AND (tmp1.standard_value_type = 1 OR (tmp1.standard_value_type = 2 AND EXISTS (SELECT DISTINCT qsvdv.standard_value_version_id FROM qualitis_standard_value_department_version qsvdv WHERE qsvdv.deptment_id IN (?7))) OR (tmp1.standard_value_type = 3 AND EXISTS (SELECT DISTINCT qsvuv.standard_value_version_id FROM qualitis_standard_value_user_version qsvuv WHERE qsvuv.user_id IN (?8))))", nativeQuery = true)
    Long countAllNameBak(String cnName, String enName, Long standardValueId, String createUser, Long version, Set<String> actionRange, List<Long> departments, List<Long> users);


    /**
     * get StandardValueVersion;
     *
     * @param id
     * @param pageable
     * @return
     */
    @Query(value = "SELECT q from StandardValueVersion q where q.standardValue.id= ?1 and q.isAvailable=1 ")
    Page<StandardValueVersion> selectStandardValue(Long id, Pageable pageable);

    /**
     * get StandardValueVersion count;
     *
     * @param id
     * @return
     */
    @Query(value = "SELECT count(q) from StandardValueVersion q where q.standardValue.id= ?1 and q.isAvailable=1 ")
    Long countStandardValue(Long id);


    /**
     * Find StandardValueVersion
     *
     * @param id
     * @param tableDataType
     * @param dataVisibilityDeptList
     * @param loginUser
     * @param pageable
     * @return
     */
    @Query(value = "select qrm.* from qualitis_standard_value_version qrm where qrm.standard_value_id=?1  and qrm.is_available=1 " +
            " AND (qrm.create_user = ?4 OR qrm.dev_department_id in (?3) OR qrm.ops_department_id in (?3) OR EXISTS (select dv.table_data_id from qualitis_auth_data_visibility dv where dv.table_data_type = ?2 and (dv.department_sub_id in (?3) or dv.department_sub_id=0) and dv.table_data_id=qrm.id)) ",
            countQuery = "select count(*) from qualitis_standard_value_version qrm where qrm.standard_value_id=?1  and qrm.is_available=1 " +
                    " AND (qrm.create_user = ?4 OR qrm.dev_department_id in (?3) OR qrm.ops_department_id in (?3) OR EXISTS (select dv.table_data_id from qualitis_auth_data_visibility dv where dv.table_data_type = ?2 and (dv.department_sub_id in (?3) or dv.department_sub_id=0)  and dv.table_data_id=qrm.id))", nativeQuery = true)
    Page<StandardValueVersion> selectSuit(Long id, String tableDataType, List<Long> dataVisibilityDeptList, String loginUser, Pageable pageable);

    /**
     * count StandardValueVersion
     *
     * @param id
     * @param tableDataType
     * @param dataVisibilityDeptList
     * @param loginUser
     * @return
     */
    @Query(value = "select count(qrm.id) from qualitis_standard_value_version qrm where qrm.standard_value_id=?1  and qrm.is_available=1 " +
            "  AND (qrm.create_user = ?4 OR qrm.dev_department_id in (?3) OR qrm.ops_department_id in (?3) OR EXISTS (select dv.table_data_id from qualitis_auth_data_visibility dv where dv.table_data_type = ?2 and (dv.department_sub_id in (?3) or dv.department_sub_id=0) and dv.table_data_id=qrm.id))", nativeQuery = true)
    Long countSuitSome(Long id, String tableDataType, List<Long> dataVisibilityDeptList, String loginUser);


    /**
     * get StandardValueVersion
     *
     * @param standardValueId
     * @return
     */
    @Query(value = "SELECT q.version from StandardValueVersion q where q.isAvailable=1 and q.standardValue.id= ?1")
    List<Long> getVersionList(Long standardValueId);


    /**
     * find By EnName
     *
     * @param enName
     * @return
     */
    @Query(value = "select q.* from qualitis_standard_value_version q where q.is_available =1 and q.en_name=?1", nativeQuery = true)
    StandardValueVersion findByEnName(String enName);
}
