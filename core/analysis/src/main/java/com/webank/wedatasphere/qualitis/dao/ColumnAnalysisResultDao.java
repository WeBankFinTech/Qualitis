package com.webank.wedatasphere.qualitis.dao;

import com.webank.wedatasphere.qualitis.entity.ColumnAnalysisResult;

import java.util.List;

public interface ColumnAnalysisResultDao {

    /**
     * @param columnAnalysisResult
     * @return
     */
    void saveAll(List<ColumnAnalysisResult> columnAnalysisResult);

    List<ColumnAnalysisResult> queryColumnAnalysisResult(String clusterName, String dbName, String tableName);
}
