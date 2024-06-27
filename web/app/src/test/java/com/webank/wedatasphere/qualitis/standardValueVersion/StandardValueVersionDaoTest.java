package com.webank.wedatasphere.qualitis.standardValueVersion;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.webank.wedatasphere.qualitis.BaseTest;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.rule.dao.StandardValueDao;
import com.webank.wedatasphere.qualitis.rule.dao.StandardValueVersionDao;
import com.webank.wedatasphere.qualitis.rule.entity.StandardValue;
import com.webank.wedatasphere.qualitis.rule.entity.StandardValueVersion;
import com.webank.wedatasphere.qualitis.rule.request.AddStandardValueRequest;
import com.webank.wedatasphere.qualitis.rule.service.StandardValueService;
import org.apache.commons.lang.time.FastDateFormat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

/**
 * @author v_gaojiedeng@webank.com
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StandardValueVersionDaoTest extends BaseTest {

    @Autowired
    StandardValueDao standardValueDao;

    @Autowired
    StandardValueVersionDao standardValueVersionDao;

    @Autowired
    StandardValueService standardValueService;

    @Test
    @Transactional
    public void test() throws UnExpectedRequestException, PermissionDeniedRequestException {

//        {
//            "cn_name": "久久",
//                "content": "啊啊啊啊啊啊",
//                "dev_department_id": "210100",
//                "dev_department_name": "零售信贷部/需求与系统管理室",
//                "en_name": "jiujiu",
//                "ops_department_id": "210100",
//                "ops_department_name": "零售信贷部/需求与系统管理室",
//                "source": "aaaa",
//        }


        AddStandardValueRequest addStandardValueRequest = new AddStandardValueRequest();
        addStandardValueRequest.setCnName("测试");
        addStandardValueRequest.setEnName("oye");
        addStandardValueRequest.setDevDepartmentId(210100L);
        addStandardValueRequest.setDevDepartmentName("零售信贷部/需求与系统管理室");
        addStandardValueRequest.setOpsDepartmentId(210100L);
        addStandardValueRequest.setOpsDepartmentName("零售信贷部/需求与系统管理室");
        addStandardValueRequest.setContent("啊啊啊啊啊啊");

        addStandardValueRequest.setSourceValue(1);
        addStandardValueRequest.setSource("jwjw");
        addStandardValueRequest.setVisibilityDepartmentList(Lists.newArrayList());

        standardValueService.addStandardValue(addStandardValueRequest);

//        StandardValueVersion standardValueVersion = saveStandardValueVersion();
//        StandardValueVersion execution = standardValueVersionDao.findById(standardValueVersion.getId());
//
//        //分页查询有结果
//        List<StandardValueVersion> list = standardValueVersionDao.findAllStandardValue("", "test", "", "", "", null, 0, 5);
//        assertTrue(list.size() > 0);
//
//        //多版本标准值
//        List<StandardValueVersion> collect = standardValueVersionDao.findByStandardValueVersion(standardValueVersion.getStandardValue().getId());
//        assertTrue(collect.size() > 0);
//
//        //删除后,是否还能找到对象
//        standardValueDao.deleteStandardValue(execution.getStandardValue());
//        standardValueVersionDao.deleteStandardValueVersion(execution);
//        StandardValueVersion deleteEntity = standardValueVersionDao.findById(standardValueVersion.getId());
//        assertNull(deleteEntity);
    }

    private StandardValueVersion saveStandardValueVersion() {
        //保存是否成功
        StandardValue standardValue = new StandardValue();
        StandardValue newStandardValue = standardValueDao.saveStandardValue(standardValue);

        StandardValueVersion entity = new StandardValueVersion();
        entity.setCnName("测试");
        entity.setEnName("test");
        entity.setVersion(1L);
        entity.setStandardValueLabelVersion(Sets.newHashSet());
        entity.setStandardValueActionVersion(Sets.newHashSet());
        entity.setStandardValue(newStandardValue);
        entity.setContent("test");
        entity.setSource("test");
        entity.setApproveSystem("test");
        entity.setApproveNumber("test");
        entity.setStandardValueType(1);
        entity.setDevDepartmentName("基础科技产品部/大数据平台室");
        entity.setOpsDepartmentName("零售互联网产品部/开发室");
        entity.setDevDepartmentId(410140L);
        entity.setOpsDepartmentId(230300L);
        entity.setCreateUser("test");
        entity.setCreateTime(FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").format(new Date()));
        entity.setAvailable(true);

        StandardValueVersion standardValueVersion = standardValueVersionDao.saveStandardValueVersion(entity);
        assertNotNull(standardValueVersion);

        return standardValueVersion;
    }


}
