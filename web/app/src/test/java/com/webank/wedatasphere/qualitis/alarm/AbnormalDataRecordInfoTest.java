package com.webank.wedatasphere.qualitis.alarm;

import static org.junit.Assert.assertTrue;

import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.dao.repository.AbnormalDataRecordInfoRepository;
import com.webank.wedatasphere.qualitis.entity.AbnormalDataRecordInfo;
import java.util.Date;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

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
        abnormalDataRecordInfo.setDepartmentName("基础科技产品部");
        abnormalDataRecordInfo.setDatasource("Hive");
        abnormalDataRecordInfo.setDbName("default");
        abnormalDataRecordInfo.setTableName("bcd");
        abnormalDataRecordInfo.setExecuteNum(1);
        abnormalDataRecordInfo.setEventNum(1);
        long currentTime = System.currentTimeMillis();
        abnormalDataRecordInfo.setRecordTime(QualitisConstants.PRINT_TIME_FORMAT.format(currentTime));

        abnormalDataRecordInfo.setSubSystemId(4375);
        AbnormalDataRecordInfo saveAbnormalDataRecordInfo = repository.save(abnormalDataRecordInfo);

        assertTrue(saveAbnormalDataRecordInfo != null);
    }

}
