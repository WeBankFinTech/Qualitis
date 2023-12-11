package com.webank.wedatasphere.qualitis.executionParameters;

import com.webank.wedatasphere.qualitis.rule.dao.NoiseEliminationManagementDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.NoiseEliminationManagementRepository;
import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.entity.NoiseEliminationManagement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author v_gaojiedeng@webank.com
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class NoiseEliminationManagementDaoTest {

    @Autowired
    NoiseEliminationManagementDao noiseEliminationManagementDao;

    @Autowired
    NoiseEliminationManagementRepository noiseEliminationManagementRepository;

    @Test
    @Transactional
    public void test() {
        NoiseEliminationManagement noiseEliminationManagement = saveNoiseEliminationManagement();

        NoiseEliminationManagement execution = noiseEliminationManagementDao.findById(noiseEliminationManagement.getId());
        //删除后,是否还能找到对象
        noiseEliminationManagementRepository.delete(execution);
        NoiseEliminationManagement deleteEntity = noiseEliminationManagementDao.findById(execution.getId());
        assertNull(deleteEntity);
    }

    private NoiseEliminationManagement saveNoiseEliminationManagement() {
        //保存是否成功
        NoiseEliminationManagement entity = new NoiseEliminationManagement();
        entity.setDateSelectionMethod(1);
        entity.setBusinessDate("1673452800000,1674057600000");
        entity.setTemplateId(1L);
        entity.setNoiseNormRatio("[{\"output_meta_id\":2,\"output_meta_name\":\"主键重复数目\",\"check_template\":1,\"compare_type\":\"\",\"threshold\":\"1\"},{\"output_meta_id\":2,\"output_meta_name\":\"主键重复数目\",\"check_template\":2,\"compare_type\":\"\",\"threshold\":\"2\"},{\"output_meta_id\":2,\"output_meta_name\":\"主键重复数目\",\"check_template\":8,\"compare_type\":1,\"threshold\":\"33\"}]");
        entity.setEliminateStrategy(1);
        entity.setAvailable(true);
        entity.setExecutionParameters(new ExecutionParameters());

        NoiseEliminationManagement noiseEliminationManagement = noiseEliminationManagementRepository.save(entity);
        assertNotNull(noiseEliminationManagement);

        return noiseEliminationManagement;
    }

}
