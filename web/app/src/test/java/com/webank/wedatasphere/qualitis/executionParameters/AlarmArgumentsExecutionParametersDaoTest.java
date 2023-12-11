package com.webank.wedatasphere.qualitis.executionParameters;

import com.webank.wedatasphere.qualitis.rule.dao.AlarmArgumentsExecutionParametersDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.AlarmArgumentsExecutionParametersRepository;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmArgumentsExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;
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
public class AlarmArgumentsExecutionParametersDaoTest {

    @Autowired
    AlarmArgumentsExecutionParametersDao alarmArgumentsExecutionParametersDao;

    @Autowired
    AlarmArgumentsExecutionParametersRepository alarmArgumentsExecutionParametersRepository;

    @Test
    @Transactional
    public void test() {
        AlarmArgumentsExecutionParameters alarmArgumentsExecutionParameters = saveAlarmArgumentsExecutionParameters();

        AlarmArgumentsExecutionParameters execution = alarmArgumentsExecutionParametersDao.findById(alarmArgumentsExecutionParameters.getId());
        //删除后,是否还能找到对象
        alarmArgumentsExecutionParametersRepository.delete(execution);
        AlarmArgumentsExecutionParameters deleteEntity = alarmArgumentsExecutionParametersDao.findById(execution.getId());
        assertNull(deleteEntity);
    }

    private AlarmArgumentsExecutionParameters saveAlarmArgumentsExecutionParameters() {
        //保存是否成功
        AlarmArgumentsExecutionParameters entity = new AlarmArgumentsExecutionParameters();
        entity.setAlarmEvent(4);
        entity.setAlarmLevel(1);
        entity.setAlarmReceiver("test");
        entity.setExecutionParameters(new ExecutionParameters());

        AlarmArgumentsExecutionParameters alarmArgumentsExecutionParameters = alarmArgumentsExecutionParametersRepository.save(entity);
        assertNotNull(alarmArgumentsExecutionParameters);

        return alarmArgumentsExecutionParameters;
    }


}
