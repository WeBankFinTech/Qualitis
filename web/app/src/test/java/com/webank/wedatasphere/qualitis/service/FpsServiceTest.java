package com.webank.wedatasphere.qualitis.service;

import com.webank.wedatasphere.qualitis.client.config.CmdbConfig;
import com.webank.wedatasphere.qualitis.converter.SqlTemplateConverter;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.exception.TaskNotExistException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.OperateCiService;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.response.SubSystemResponse;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.dao.LinkisDataSourceDao;
import com.webank.wedatasphere.qualitis.rule.service.FpsService;
import com.webank.wedatasphere.qualitis.rule.service.RuleService;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * @author v_minminghe@webank.com
 * @date 2023-06-20 10:18
 * @description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FpsServiceTest {

    @Autowired
    private FpsService fpsService;

    @Autowired
    private LinkisDataSourceDao linkisDataSourceDao;

    @Autowired
    private SqlTemplateConverter sqlTemplateConverter;

    @Autowired
    private RuleService ruleService;

    @Value("${execution.controller.async: false}")
    private Boolean enableAsyncRequest;
    @Test
    public void downloadFpsFile() throws UnExpectedRequestException, TaskNotExistException, ClusterInfoNotConfigException {
        String fpsId = "94000000168983761377101845979255000000016891464137719324";
        String fpsHash = "69e72022fdfdd17958f3d9162489627c";
        String cluster = "HDP-GZPC-BDAP-SIT";
        User user = new User();
        user.setUsername("hadoop");
        long a1 = System.currentTimeMillis();
        fpsService.downloadFpsFile(fpsId, fpsHash, "hmm_test_data2.txt", cluster, user);
        long a2 = System.currentTimeMillis();
        System.out.println("end time: " + (a2-a1) / 1000 + "s");
//        Rule rule = ruleDao.findById(57021L);
//        sqlTemplateConverter.generateTempHiveTable(rule, "hduser05");
//        String serverName = ConfigUtil.getStringByKey("sso.client.serverName");
//        System.out.println(">>>> " + serverName);
    }

    @Autowired
    private MetaDataService metaDataService;
    @Autowired
    private OperateCiService operateCiService;


    @Test
    public void testRule () throws UnExpectedRequestException, MetaDataAcquireFailedException {
//        GeneralResponse<RuleDetailResponse> rule = ruleService.getRuleDetail(18L);
//        System.out.println(">>>> " + rule.getData());
//        GeneralResponse<Map<String, Object>>  s = metaDataService.getAllDataSourceTypes("BDP-DEV", null);
//        System.out.println(s);
        List<SubSystemResponse> s = operateCiService.getAllSubSystemInfo();
        System.out.println(s);
    }

    @Test
    public void tesss() throws IOException {
        String content = "{\"message\": \"Success to get all datasource types\",\"code\": \"200\",\"data\": {\"typeList\": [{\"id\": \"1\",\"name\": \"kafka\",\"description\": \"kafka\",\"option\": \"kafka\",\"classifier\": \"消息队列\",\"layers\": 2},{\"id\": \"2\",\"name\": \"hive\",\"description\": \"hive数据库\",\"option\": \"hive\",\"classifier\": \"大数据存储\",\"layers\": 3},{\"id\": \"3\",\"name\": \"elasticsearch\",\"description\": \"elasticsearch数据源\",\"option\": \"es无结构化存储\",\"classifier\": \"分布式全文索引\",\"layers\": 3},{\"id\": \"4\",\"name\": \"mysql\",\"description\": \"mysql数据库\",\"option\": \"mysql数据库\",\"classifier\": \"关系型数据库\",\"layers\": 3},{\"id\": \"5\",\"name\": \"oracle\",\"description\": \"oracle数据库\",\"option\": \"oracle\",\"classifier\": \"关系型数据库\",\"layers\": 3},{\"id\": \"6\",\"name\": \"dm\",\"description\": \"达梦数据库\",\"option\": \"dm\",\"classifier\": \"关系型数据库\",\"layers\": 3},{\"id\": \"7\",\"name\": \"kingbase\",\"description\": \"人大金仓数据库\",\"option\": \"kingbase\",\"classifier\": \"关系型数据库\",\"layers\": 3},{\"id\": \"8\",\"name\": \"postgresql\",\"description\": \"postgresql数据库\",\"option\": \"postgresql\",\"classifier\": \"关系型数据库\",\"layers\": 3},{\"id\": \"9\",\"name\": \"sqlserver\",\"description\": \"sqlserver数据库\",\"option\": \"sqlserver\",\"classifier\": \"关系型数据库\",\"layers\": 3},{\"id\": \"10\",\"name\": \"db2\",\"description\": \"db2数据库\",\"option\": \"db2\",\"classifier\": \"关系型数据库\",\"layers\": 3},{\"id\": \"11\",\"name\": \"greenplum\",\"description\": \"greenplum数据库\",\"option\": \"greenplum\",\"classifier\": \"关系型数据库\",\"layers\": 3},{\"id\": \"12\",\"name\": \"doris\",\"description\": \"doris数据库\",\"option\": \"doris\",\"classifier\": \"olap\",\"layers\": 4},{\"id\": \"13\",\"name\": \"clickhouse\",\"description\": \"clickhouse数据库\",\"option\": \"clickhouse\",\"classifier\": \"olap\",\"layers\": 4}]}}";

        GeneralResponse<Map<String, Object>> generalResponse = new ObjectMapper().readValue(content, GeneralResponse.class);
        if (generalResponse.getData() != null && generalResponse.getData().containsKey("typeList")) {
            List<Map<String, Object>> typeListMap = (List<Map<String, Object>>) generalResponse.getData().get("typeList");
            ListIterator<Map<String, Object>> listIterator = typeListMap.listIterator();
            while (listIterator.hasNext()) {
                Map<String, Object> typeMap = listIterator.next();
                if(!"mysql".equals(typeMap.get("name"))) {
                    listIterator.remove();
                }
            }
        }
        System.out.println("sdsdsd");
    }

}