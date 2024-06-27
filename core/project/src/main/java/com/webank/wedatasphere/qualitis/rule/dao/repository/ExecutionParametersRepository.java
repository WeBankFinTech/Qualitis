package com.webank.wedatasphere.qualitis.rule.dao.repository;

import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author v_gaojiedeng
 */
public interface ExecutionParametersRepository extends JpaRepository<ExecutionParameters, Long> {

    /**
     * find By Name
     * @param projectId
     * @param name
     * @param pageable
     * @return
     */
    @Query(value = "select ds from ExecutionParameters ds where ds.projectId = ?1 and ds.name like ?2 ")
    Page<ExecutionParameters> findByName(Long projectId, String name, Pageable pageable);

    /**
     * find By ProjectId
     * @param projectId
     * @param pageable
     * @return
     */
    @Query(value = "select ds from ExecutionParameters ds where ds.projectId = ?1 ")
    Page<ExecutionParameters> findByProjectId(Long projectId, Pageable pageable);

    /**
     * count Total
     * @param projectId
     * @return
     */
    @Query(value = "SELECT COUNT(qr.id) from ExecutionParameters qr where qr.projectId = ?1")
    int countTotal(Long projectId);

    /**
     * count Total By Name
     * @param projectId
     * @param name
     * @return
     */
    @Query(value = "SELECT COUNT(qr.id) from ExecutionParameters qr where qr.projectId = ?1 and qr.name like ?2")
    int countTotalByName(Long projectId, String name);

    /**
     * find By Name And ProjectId
     * @param name
     * @param projectId
     * @return
     */
    @Query(value = "select ds from ExecutionParameters ds where ds.name = ?1 and ds.projectId = ?2")
    ExecutionParameters findByNameAndProjectId(String name, Long projectId);

    /**
     * find By Names And ProjectId
     * @param projectId
     * @param names
     * @return
     */
    @Query(value = "select ds from ExecutionParameters ds where ds.projectId = ?1 and ds.name in (?2)")
    List<ExecutionParameters> findByProjectIdAndNames(Long projectId, List<String> names);

    /**
     * get All ExecutionParameters
     * @param projectId
     * @return
     */
    @Query(value = "select ds from ExecutionParameters ds where ds.projectId = ?1")
    List<ExecutionParameters> getAllExecutionParameters(Long projectId);

}
