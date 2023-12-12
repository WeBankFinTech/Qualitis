package com.webank.wedatasphere.qualitis.executionParameters;

import com.webank.wedatasphere.qualitis.rule.dao.ExecutionParametersDao;
import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author v_gaojiedeng@webank.com
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ExecutionParametersDaoTest {

    @Autowired
    ExecutionParametersDao executionParametersDao;

    @Test
    @Transactional
    public void test() {
        ExecutionParameters saveExecutionParameters = saveExecutionParameters();

        //总数量大于0
        long size = executionParametersDao.countTotal(3703L);
        assertTrue(size > 0);

        //分页查询有结果
        List<ExecutionParameters> datas = executionParametersDao.findAll(3703L, 0, 5);
        assertTrue(datas.size() > 0);

        //保存到数据库的对象是否和保存的值一致
        ExecutionParameters executionParameters = executionParametersDao.findById(saveExecutionParameters.getId());

        assertNotNull(executionParameters);
        assertEquals(executionParameters.getName(), saveExecutionParameters.getName());
        assertEquals(executionParameters.getConcurrencyGranularity(), saveExecutionParameters.getConcurrencyGranularity());

        //根据name、projectId查询的数据库对象是否和保存的值一致

        ExecutionParameters xxx = executionParametersDao.findByNameAndProjectId("xxx", 3703L);
        assertNotNull(xxx);
        assertEquals(xxx.getName(), saveExecutionParameters.getName());

        //删除后,是否还能找到对象
        executionParametersDao.deleteExecutionParameters(xxx);
        ExecutionParameters deleteEntity = executionParametersDao.findById(saveExecutionParameters.getId());
        assertNull(deleteEntity);
    }

    private ExecutionParameters saveExecutionParameters() {
        //保存是否成功
        ExecutionParameters entity = new ExecutionParameters();
        entity.setName("xxx");
        entity.setAbortOnFailure(false);
        entity.setSpecifyStaticStartupParam(false);
        entity.setStaticStartupParam("");
        entity.setAlert(false);
        entity.setProjectId(3703L);

        entity.setAbnormalDataStorage(false);
        entity.setAbnormalDatabase("");
        entity.setCluster("");
        entity.setAbnormalProxyUser("");
        entity.setSpecifyFilter(false);
        entity.setDeleteFailCheckResult(false);
        entity.setUploadAbnormalValue(false);
        entity.setUploadRuleMetricValue(false);
        entity.setUnionAll(false);
        entity.setWhetherNoise(false);
        entity.setExecutionVariable(false);
        entity.setEngineReuse(true);
        entity.setConcurrencyGranularity("split_by:table");
        entity.setDynamicPartitioning(false);

        ExecutionParameters saveEntity = executionParametersDao.saveExecutionParameters(entity);
        assertTrue(saveEntity.getId() != 0);
        return saveEntity;
    }


}
