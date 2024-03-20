package com.webank.wedatasphere.qualitis.executionParameters;

import com.webank.wedatasphere.qualitis.rule.dao.ExecutionVariableDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.ExecutionVariableRepository;
import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.entity.ExecutionVariable;
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
public class ExecutionVariableDaoTest {

    @Autowired
    ExecutionVariableDao executionVariableDao;

    @Autowired
    ExecutionVariableRepository executionVariableRepository;

    @Test
    @Transactional
    public void test() {
        ExecutionVariable executionVariable = saveExecutionVariable();

        ExecutionVariable execution = executionVariableDao.findById(executionVariable.getId());
        //删除后,是否还能找到对象
        executionVariableRepository.delete(execution);
        ExecutionVariable deleteEntity = executionVariableDao.findById(execution.getId());
        assertNull(deleteEntity);
    }

    private ExecutionVariable saveExecutionVariable() {
        //保存是否成功
        ExecutionVariable entity = new ExecutionVariable();
        entity.setVariableType(1);
        entity.setVariableName("fps_id");
        entity.setVariableValue("2026");
        entity.setExecutionParameters(new ExecutionParameters());

        ExecutionVariable executionVariable = executionVariableRepository.save(entity);
        assertNotNull(executionVariable);

        return executionVariable;
    }


}
