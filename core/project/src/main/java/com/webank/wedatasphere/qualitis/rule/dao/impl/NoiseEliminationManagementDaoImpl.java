package com.webank.wedatasphere.qualitis.rule.dao.impl;

import com.webank.wedatasphere.qualitis.rule.dao.NoiseEliminationManagementDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.NoiseEliminationManagementRepository;
import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.entity.NoiseEliminationManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author v_gaojiedeng@webank.com
 */
@Repository
public class NoiseEliminationManagementDaoImpl implements NoiseEliminationManagementDao {

    @Autowired
    private NoiseEliminationManagementRepository noiseEliminationManagementRepository;

    @Override
    public List<NoiseEliminationManagement> findByExecutionParameters(ExecutionParameters executionParameters) {
        return noiseEliminationManagementRepository.findByExecutionParameters(executionParameters);
    }

    @Override
    public NoiseEliminationManagement findById(Long noiseEliminationManagementId) {
        return noiseEliminationManagementRepository.findById(noiseEliminationManagementId).orElse(null);
    }

    @Override
    public Set<NoiseEliminationManagement> saveAll(List<NoiseEliminationManagement> noiseEliminationManagement) {
        Set<NoiseEliminationManagement> result = new HashSet<>();
        result.addAll(noiseEliminationManagementRepository.saveAll(noiseEliminationManagement));
        return result;
    }

    @Override
    public void deleteByExecutionParameters(ExecutionParameters executionParametersInDb) {
        noiseEliminationManagementRepository.deleteByExecutionParameters(executionParametersInDb);
    }
}
