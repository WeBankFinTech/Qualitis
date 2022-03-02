package com.webank.wedatasphere.qualitis.alarm;

import static org.junit.Assert.assertTrue;

import com.webank.wedatasphere.qualitis.dao.repository.AbnormalDataRecordInfoRepository;
import com.webank.wedatasphere.qualitis.entity.AbnormalDataRecordInfo;
import java.sql.Date;
import java.sql.Timestamp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author allenzhou@webank.com
 * @date 2021/10/5 12:40
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class AbnormalDataRecordInfoTest {
    @Autowired
    AbnormalDataRecordInfoRepository repository;

    @Test
    public void saveTest() {
        AbnormalDataRecordInfo abnormalDataRecordInfo = new AbnormalDataRecordInfo();
        abnormalDataRecordInfo.setRuleId(59740L);
        abnormalDataRecordInfo.setRuleName("qwer_new");
        abnormalDataRecordInfo.setRuleDetail("qwer_new");
        abnormalDataRecordInfo.setDatasource("Hive");
        abnormalDataRecordInfo.setDbName("default");
        abnormalDataRecordInfo.setTableName("bcd");
        abnormalDataRecordInfo.setDepartmentName("基础科技产品部");
        abnormalDataRecordInfo.setExecuteNum(1);
        abnormalDataRecordInfo.setEventNum(1);
        abnormalDataRecordInfo.setRecordDate(new Date(System.currentTimeMillis()));
        abnormalDataRecordInfo.setRecordTime(new Timestamp(System.currentTimeMillis()));
        abnormalDataRecordInfo.setSubSystemId(4375);
        AbnormalDataRecordInfo saveAbnormalDataRecordInfo = repository.save(abnormalDataRecordInfo);
        assertTrue(saveAbnormalDataRecordInfo != null);
    }

    @Test
    public void findTest() {
        AbnormalDataRecordInfo saveAbnormalDataRecordInfo = repository.findByPrimary(59740L, "default", "abc", new Date(System.currentTimeMillis()));
        assertTrue(saveAbnormalDataRecordInfo != null);
    }
}
