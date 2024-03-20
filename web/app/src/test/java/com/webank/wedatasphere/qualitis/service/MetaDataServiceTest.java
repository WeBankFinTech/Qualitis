package com.webank.wedatasphere.qualitis.service;

import com.webank.wedatasphere.qualitis.BaseTest;
import com.webank.wedatasphere.qualitis.client.impl.MetaDataClientImpl;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.impl.MetaDataServiceImpl;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

/**
 * @author v_minminghe@webank.com
 * @date 2023-03-21 9:47
 * @description
 */
@RunWith(MockitoJUnitRunner.class)
//@SpringBootTest
public class MetaDataServiceTest extends BaseTest {

//    注入测试目标类
    @InjectMocks
    private MetaDataServiceImpl metaDataService;
//    模拟目标类中的依赖类
    @MockBean
    @Spy
    private MetaDataClient metaDataClient;

    @Before
    public void setup() {
        super.initLoginUser(null, null);
    }

    @Test
    public void testGetAllDataSourceTypes() throws UnExpectedRequestException, MetaDataAcquireFailedException {
        String clusterName = "BDP-CLIENT";
        GeneralResponse<Map<String, Object>> mockGeneralResponse = new GeneralResponse<>();
        mockGeneralResponse.setCode("200");
        when(metaDataClient.getAllDataSourceTypes(clusterName, super.defaultLoginUserName)).thenReturn(mockGeneralResponse);
        GeneralResponse<Map<String, Object>> generalResponse = metaDataService.getAllDataSourceTypes(clusterName, null);
        Assert.assertEquals("200", generalResponse.getCode());
    }
}