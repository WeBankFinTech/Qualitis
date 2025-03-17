package com.webank.wedatasphere.qualitis.function.dao.repository;

import com.webank.wedatasphere.qualitis.function.entity.LinkisUdf;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author allenzhou@webank.com
 * @date 2022/11/7 17:30
 */
public interface LinkisUdfRepository extends JpaRepository<LinkisUdf, Long> {

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
     * @param pageable
     * @return
     */
    @Query(value = "SELECT qlu.* FROM qualitis_linkis_udf qlu WHERE "
        + "(LENGTH(?1) = 0 OR qlu.name LIKE ?1) "
        + "AND (LENGTH(?2) = 0 OR qlu.cn_name LIKE ?2) "
        + "AND (LENGTH(?3) = 0 OR qlu.directory = ?3) "
        + "AND (?4 IS NULL OR qlu.impl_type_code = ?4) "
        + "AND (coalesce(?5,NULL) IS NULL OR (EXISTS (SELECT qluee.id FROM qualitis_linkis_udf_enable_engine qluee WHERE qlu.id = qluee.linkis_udf_id AND qluee.enable_engine_code IN (?5)))) "
        + "AND (coalesce(?6,NULL) IS NULL OR (EXISTS (SELECT qluec.id FROM qualitis_linkis_udf_enable_cluster qluec WHERE qlu.id = qluec.linkis_udf_id AND qluec.enable_cluster IN (?6)))) "
        + "AND (LENGTH(?7) = 0 OR qlu.create_user = ?7) "
        + "AND (LENGTH(?8) = 0 OR qlu.modify_user = ?8) "
        + "AND (?9 IS NULL OR qlu.dev_department_id = ?9) "
        + "AND (?10 IS NULL OR qlu.ops_department_id = ?10) "
        + "AND (coalesce(?11,NULL) IS NULL OR (EXISTS (SELECT qadv.id FROM qualitis_auth_data_visibility qadv WHERE qlu.id = qadv.table_data_id AND qadv.table_data_type = 'linkisUdf' AND qadv.department_sub_id IN (?11)))) ",
        countQuery = "SELECT count(qlu.id) FROM qualitis_linkis_udf qlu WHERE "
        + "(LENGTH(?1) = 0 OR qlu.name LIKE ?1) "
        + "AND (LENGTH(?2) = 0 OR qlu.cn_name LIKE ?2) "
        + "AND (LENGTH(?3) = 0 OR qlu.directory = ?3) "
        + "AND (?4 IS NULL OR qlu.impl_type_code = ?4) "
        + "AND (coalesce(?5,NULL) IS NULL OR (EXISTS (SELECT qluee.id FROM qualitis_linkis_udf_enable_engine qluee WHERE qlu.id = qluee.linkis_udf_id AND qluee.enable_engine_code IN (?5)))) "
        + "AND (coalesce(?6,NULL) IS NULL OR (EXISTS (SELECT qluec.id FROM qualitis_linkis_udf_enable_cluster qluec WHERE qlu.id = qluec.linkis_udf_id AND qluec.enable_cluster IN (?6)))) "
        + "AND (LENGTH(?7) = 0 OR qlu.create_user = ?7) "
        + "AND (LENGTH(?8) = 0 OR qlu.modify_user = ?8) "
        + "AND (?9 IS NULL OR qlu.dev_department_id = ?9) "
        + "AND (?10 IS NULL OR qlu.ops_department_id = ?10) "
        + "AND (coalesce(?11,NULL) IS NULL OR (EXISTS (SELECT qadv.id FROM qualitis_auth_data_visibility qadv WHERE qlu.id = qadv.table_data_id AND qadv.table_data_type = 'linkisUdf' AND qadv.department_sub_id IN (?11)))) ", nativeQuery = true)
    Page<LinkisUdf> filterAll(String name, String cnName, String dir, Integer implType, List<Integer> enableEngine, List<String> enableCluster
        , String createUser, String modifyUser, Long devDepartmentId, Long opsDepartmentId, List<Long> dataVisibilityIds, Pageable pageable);

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
    @Query(value = "SELECT count(qlu.id) FROM qualitis_linkis_udf qlu WHERE "
        + "(LENGTH(?1) = 0 OR qlu.name LIKE ?1) "
        + "AND (LENGTH(?2) = 0 OR qlu.cn_name LIKE ?2) "
        + "AND (LENGTH(?3) = 0 OR qlu.directory = ?3) "
        + "AND (?4 IS NULL OR qlu.impl_type_code = ?4) "
        + "AND (coalesce(?5,NULL) IS NULL OR (EXISTS (SELECT qluee.id FROM qualitis_linkis_udf_enable_engine qluee WHERE qlu.id = qluee.linkis_udf_id AND qluee.enable_engine_code IN (?5)))) "
        + "AND (coalesce(?6,NULL) IS NULL OR (EXISTS (SELECT qluec.id FROM qualitis_linkis_udf_enable_cluster qluec WHERE qlu.id = qluec.linkis_udf_id AND qluec.enable_cluster IN (?6)))) "
        + "AND (LENGTH(?7) = 0 OR qlu.create_user = ?7) "
        + "AND (LENGTH(?8) = 0 OR qlu.modify_user = ?8) "
        + "AND (?9 IS NULL OR qlu.dev_department_id = ?9) "
        + "AND (?10 IS NULL OR qlu.ops_department_id = ?10) ", nativeQuery = true)
    int countAll(String name, String cnName, String dir, Integer implType, List<Integer> enableEngine, List<String> enableCluster, String createUser, String modifyUser, Long devDepartmentId, Long opsDepartmentId);

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
     * @param tableDataType
     * @param dataVisibilityDeptList
     * @param loginUser
     * @param pageable
     * @return
     */
    @Query(value = "SELECT qlu.* FROM qualitis_linkis_udf qlu WHERE "
        + "(LENGTH(?1) = 0 OR qlu.name LIKE ?1) "
        + "AND (LENGTH(?2) = 0 OR qlu.cn_name LIKE ?2) "
        + "AND (LENGTH(?3) = 0 OR qlu.directory = ?3) "
        + "AND (?4 IS NULL OR qlu.impl_type_code = ?4) "
        + "AND (coalesce(?5,NULL) IS NULL OR (EXISTS (SELECT qluee.id FROM qualitis_linkis_udf_enable_engine qluee WHERE qlu.id = qluee.linkis_udf_id AND qluee.enable_engine_code IN (?5)))) "
        + "AND (coalesce(?6,NULL) IS NULL OR (EXISTS (SELECT qluec.id FROM qualitis_linkis_udf_enable_cluster qluec WHERE qlu.id = qluec.linkis_udf_id AND qluec.enable_cluster IN (?6)))) "
        + "AND (LENGTH(?7) = 0 OR qlu.create_user = ?7)"
        + "AND (LENGTH(?8) = 0 OR qlu.modify_user = ?8)"
        + "AND( " +
            "qlu.create_user = ?11 "
            + "OR qlu.dev_department_id IN (?10) "
            + "OR qlu.ops_department_id IN (?10) "
            + "OR (EXISTS (SELECT qadv.table_data_id FROM qualitis_auth_data_visibility qadv WHERE qlu.id = qadv.table_data_id AND qadv.table_data_type = ?9 AND qadv.department_sub_id IN (?10)))" +
            ")",
        countQuery = "SELECT count(qlu.id) FROM qualitis_linkis_udf qlu WHERE "
        + "(LENGTH(?1) = 0 OR qlu.name LIKE ?1) "
        + "AND (LENGTH(?2) = 0 OR qlu.cn_name LIKE ?2) "
        + "AND (LENGTH(?3) = 0 OR qlu.directory = ?3) "
        + "AND (?4 IS NULL OR qlu.impl_type_code = ?4) "
        + "AND (coalesce(?5,NULL) IS NULL OR (EXISTS (SELECT qluee.id FROM qualitis_linkis_udf_enable_engine qluee WHERE qlu.id = qluee.linkis_udf_id AND qluee.enable_engine_code IN (?5)))) "
        + "AND (coalesce(?6,NULL) IS NULL OR (EXISTS (SELECT qluec.id FROM qualitis_linkis_udf_enable_cluster qluec WHERE qlu.id = qluec.linkis_udf_id AND qluec.enable_cluster IN (?6)))) "
        + "AND (LENGTH(?7) = 0 OR qlu.create_user = ?7)"
        + "AND (LENGTH(?8) = 0 OR qlu.modify_user = ?8)"
        + "AND( " +
                "qlu.create_user = ?11 "
                + "OR qlu.dev_department_id IN (?10) "
                + "OR qlu.ops_department_id IN (?10) "
                + "OR (EXISTS (SELECT qadv.table_data_id FROM qualitis_auth_data_visibility qadv WHERE qlu.id = qadv.table_data_id AND qadv.table_data_type = ?9 AND qadv.department_sub_id IN (?10)))" +
                ")"
            , nativeQuery = true)
    Page<LinkisUdf> filter(String name, String cnName, String dir, Integer implType, List<Integer> enableEngine, List<String> enableCluster, String createUser, String modifyUser, String tableDataType, List<Long> dataVisibilityDeptList, String loginUser, Pageable pageable);

    /**
     * Find by name
     * @param name
     * @return
     */
    LinkisUdf findByName(String name);
}
