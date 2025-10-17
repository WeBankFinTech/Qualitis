package com.webank.wedatasphere.qualitis.dao.repository;

import com.webank.wedatasphere.qualitis.entity.ColumnAnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ColumnAnalysisResultRepository extends JpaRepository<ColumnAnalysisResult, Long> {

    @Query(value = "select * from qualitis_column_analysis_result where cluster_name = ?1 and db_name = ?2 and table_name = ?3", nativeQuery = true)
    List<ColumnAnalysisResult> queryColumnAnalysisResult(String clusterName, String dbName, String tableName);
}
