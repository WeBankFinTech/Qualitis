package com.webank.wedatasphere.qualitis.dao.impl;

import com.webank.wedatasphere.qualitis.dao.ColumnAnalysisResultDao;
import com.webank.wedatasphere.qualitis.dao.repository.ColumnAnalysisResultRepository;
import com.webank.wedatasphere.qualitis.entity.ColumnAnalysisResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ColumnAnalysisResultDaoImpl implements ColumnAnalysisResultDao {

    @Autowired
    private ColumnAnalysisResultRepository columnAnalysisResultRepository;

    @Override
    public void saveAll(List<ColumnAnalysisResult> columnAnalysisResultList) {
        columnAnalysisResultRepository.saveAll(columnAnalysisResultList);
    }

    @Override
    public List<ColumnAnalysisResult> queryColumnAnalysisResult(String clusterName, String dbName, String tableName) {
        return columnAnalysisResultRepository.queryColumnAnalysisResult(clusterName, dbName, tableName);
    }
}
