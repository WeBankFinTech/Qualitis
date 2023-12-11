package com.webank.wedatasphere.qualitis.executionParameters;

import com.webank.wedatasphere.qualitis.rule.dao.StaticExecutionParametersDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.StaticExecutionParametersRepository;
import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.entity.StaticExecutionParameters;
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
public class StaticExecutionParametersDaoTest {

    @Autowired
    StaticExecutionParametersDao staticExecutionParametersDao;

    @Autowired
    StaticExecutionParametersRepository staticExecutionParametersRepository;

    @Test
    @Transactional
    public void test() {
        StaticExecutionParameters staticExecutionParameters = saveStaticExecutionParameters();

        StaticExecutionParameters execution = staticExecutionParametersDao.findById(staticExecutionParameters.getId());
        //删除后,是否还能找到对象
        staticExecutionParametersRepository.delete(execution);
        StaticExecutionParameters deleteEntity = staticExecutionParametersDao.findById(execution.getId());
        assertNull(deleteEntity);
    }

    private StaticExecutionParameters saveStaticExecutionParameters() {
        //保存是否成功
        StaticExecutionParameters entity = new StaticExecutionParameters();
        entity.setParameterType(1);
        entity.setParameterName("wds.linkis.rm.yarnqueue.instance.max");
        entity.setParameterValue("30");
        entity.setExecutionParameters(new ExecutionParameters());

        StaticExecutionParameters staticExecutionParameters = staticExecutionParametersRepository.save(entity);
        assertNotNull(staticExecutionParameters);

        return staticExecutionParameters;
    }


}
