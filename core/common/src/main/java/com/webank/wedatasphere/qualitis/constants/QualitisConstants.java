package com.webank.wedatasphere.qualitis.constants;

import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author allenzhou@webank.com
 * @date 2022/8/25 17:15
 */
public class QualitisConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(QualitisConstants.class);

    /**
     * 导入导出内置变量
     */
    public static final String EXECUTE_USER = "rule_execute_user";
    public static final String EXECUTE_CLUSETER = "rule_execute_cluster";
    public static final String WTSS_DEPLOY_USER = "wtss_deploy_user";
    public static final String WTSS_DEPLOY_CLUSETER = "wtss_deploy_cluster";

    /**
     * File suffix
     */
    public static final String SUPPORT_CONFIG_SUFFIX_NAME = ".properties";
    public static final String SUPPORT_SCALA_SUFFIX_NAME = ".scala";
    public static final String SUPPORT_EXCEL_SUFFIX_NAME = ".xlsx";
    public static final String SUPPORT_PYTHON_SUFFIX_NAME = ".py";
    public static final String SUPPORT_JAR_SUFFIX_NAME = ".jar";
    public static final String SUPPORT_ZIP_SUFFIX_NAME = ".zip";

    /**
     * Cluster type
     */
    public static final String BDAP = "BDAP";
    public static final String BDP = "BDP";

    /**
     * Branch Master
     */
    public static final String MASTER = "master";

    /**
     * Ordinary num
     */
    public static final int LENGTH_TWO = 2;

    /**
     * Group execution num
     */
    public static final int ONLY_ONE_GROUP = 1;

    /**
     * Check alert default alert content columns' num
     */
    public static final int DEFAULT_CONTENT_COLUMN_LENGTH = 5;

    /**
     * Dss node version num
     */
    public static final Integer DSS_NODE_VERSION_NUM = 5;

    /**
     * Common array index to fix magical value
     */
    public static final int COMMON_ARRAY_INDEX_O = 0;
    public static final int COMMON_ARRAY_INDEX_1 = 1;
    public static final int COMMON_ARRAY_INDEX_2 = 2;
    public static final int COMMON_ARRAY_INDEX_3 = 3;
    public static final int COMMON_ARRAY_INDEX_4 = 4;

    /**
     * Role name
     */
    public static final String ADMIN = "ADMIN";
    public static final String PROJECTOR = "PROJECTOR";

    /**
     * Subsystem ID
     */
    public static final Integer SUB_SYSTEM_ID = 5375;

    /**
     * Key of compared value
     */
    public static final String AVG_OF_CURRENT = "avgOfCurrent";
    public static final String AVG_OF_LAST_CYCLE = "avgOfLastCycle";

    /**
     * Rule datasource
     */
    public static final Integer ORIGINAL_INDEX = -1;
    public static final Integer RIGHT_INDEX = 1;
    public static final Integer LEFT_INDEX = 0;
    public static final String MAP_TYPE = "map";
    public static final String ARRAY_TYPE = "array";
    public static final String STRUCT_TYPE = "struct";

    /**
     * Datasouce management.
     */
    public static final String UNION_ALL = "All";
    public static final Pattern DATA_SOURCE_ID = Pattern.compile("\\.\\(ID=[0-9]+\\{[0-9,]+\\}\\)");
    public static final Pattern DATA_SOURCE_NAME = Pattern.compile("\\.\\(NAME=[\\u4E00-\\u9FA5A-Za-z0-9_]+\\{[\\u4E00-\\u9FA5A-Za-z0-9_,]+\\}\\)");

    /**
     * Date format
     */
    public static final FastDateFormat PRINT_DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");
    public static final FastDateFormat FILE_DATE_FORMATTER = FastDateFormat.getInstance("yyyyMMddHHmmss");
    public static final FastDateFormat PRINT_DATE_FORMAT_ELIMINATE = FastDateFormat.getInstance("yyyyMMdd");
    public static final FastDateFormat PRINT_TIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    /**
     * AlarmEventEnum
     * 告警事件：
     * 校验成功(1)：仅通过校验的任务
     * 校验失败(2)：未通过校验+阻断、引擎层失败的任务
     * 执行完成(3)：通过校验，未通过校验
     */
    public static final Integer CHECK_SUCCESS = 1;
    public static final Integer CHECK_FAILURE = 2;
    public static final Integer EXECUTION_COMPLETED = 3;

    /**
     * qualitis_template_default_input_meta, 0.23.0版本单表ID
     */
    public static final List<Integer> SINGLE_TABLE = Arrays.asList(17, 18, 19, 20, 21, 22, 23, 33);
    /**
     * qualitis_template_default_input_meta, 0.23.0版本跨表ID
     */
    public static final List<Integer> CROSS_TABLE = Arrays.asList(17, 18, 20, 24, 25, 26, 27, 28, 29, 30, 31, 32);
    /**
     * qualitis_template_default_input_meta, 0.23.0版本文件ID
     */
    public static final List<Integer> FILE_TABLE = Arrays.asList(17, 18, 20);
    /**
     * 剔除旧数据的占位符与0.23.0版本input_type不匹配的情况
     */
    public static final List<Integer> ELIMINATE_PLACEHOLDER = Arrays.asList(1, 7, 10, 20, 21, 22, 23, 25, 36, 37, 38);

    public static final List<String> OVER_TABLE_TYPE = Arrays.asList("11", "12", "13", "14", "30", "31");
    public static final String ROW_DATA_CONSISTENCY_VERIFICATION="行数据一致性校验";

    /**
     * 引擎配置 json数据格式name
     */
    public static final List<String> ENGINE_CONFIGURATION = Arrays.asList("spark引擎资源上限", "worker资源设置", "spark引擎资源设置", "spark资源设置");

    /**
     * 数值范围(最大值、最小值、中间表达式)
     */
    public static final String INTERMEDIATE_PLACEHOLDER = "intermediate_expression";
    public static final String INTERMEDIATE_EXPRESSION = "{&INTERMEDIATE_EXPRESSION}";
    public static final String INTERMEDIATE_PLACEHOLDER_DESCRIPTION = "{&REPLACE_PLACEHOLDER_IN_SQL}${intermediate_expression}";

    public static final String MAXIMUM = "{&MAXIMUM}";
    public static final String MAXIMUM_PLACEHOLDER = "maximum";
    public static final String MAXIMUM_PLACEHOLDER_DESCRIPTION = "{&REPLACE_PLACEHOLDER_IN_SQL}${maximum}";

    public static final String MINIMUM = "{&MINIMUM}";
    public static final String MINIMUM_PLACEHOLDER = "minimum";
    public static final String MINIMUM_PLACEHOLDER_DESCRIPTION = "{&REPLACE_PLACEHOLDER_IN_SQL}${minimum}";

    /**
     * qualitis_template_output_meta   校验值(output_name)  英文名(output_en_name)
     */
    public static final String DISSATISFIED_EN_NAME = "Number of dissatisfied en_name";
    public static final String DISSATISFACTION = "不满足";
    public static final String NUMS = "的数量";

    /**
     * qualitis_template_statistic_input_meta  result_type属性
     */
    public static final String DATA_TYPE_LONG = "Long";

    /**
     * show_sql * 替换
     */
    public static final String ASTERISK = "\\*";

    /**
     * 日志级别
     */
    public static final String LOG_INFO = " INFO ";
    public static final String LOG_WARN = " WARN ";
    public static final String LOG_ERROR = " ERROR ";


    public static final String FPS_DEFAULT_USER = "hadoop";

    /**
     * @Description：获取客户端内网ip
     */
    public static String QUALITIS_SERVER_HOST;

    static {
        try {
            QUALITIS_SERVER_HOST = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * @Description：获取客户端外网ip 此方法要接入互联网才行，内网不行
     **/
    public static String getPublicIp() {
        try {
            // 要获得html页面内容的地址
            String path = "http://www.net.cn/static/customercare/yourip.asp";
            // 创建url对象
            URL url = new URL(path);
            // 打开连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置url中文参数编码
            conn.setRequestProperty("contentType", "GBK");
            // 请求的时间
            conn.setConnectTimeout(5 * 1000);
            // 请求方式
            conn.setRequestMethod("GET");
            InputStream inStream = conn.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    inStream, "GBK"));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            // 读取获取到内容的最后一行,写入
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
            List<String> ips = new ArrayList<String>();

            //用正则表达式提取String字符串中的IP地址
            String regEx = "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";
            String str = buffer.toString();
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(str);
            while (m.find()) {
                String result = m.group();
                ips.add(result);
            }
            String PublicIp = ips.get(0);

            // 返回公网IP值
            return PublicIp;
        } catch (Exception e) {
            LOGGER.error("获取公网IP连接超时");
            return "";
        }
    }

    public static final String DEFAULT_NODE_NAME = "qualitis_0000";
    public static final String CHECKALERT_NODE_NAME_PREFIX = "checkalert";

    /**
     * Rule group default filter placeholder
     */
    public static final String RULE_GROUP_FILTER_PLACEHOLDER = "${table_value_filter}";

    public static final Long EXPECT_LINES_NOT_REPEAT_ID = 2149L;
    public static final Long EXPECT_DATA_NOT_REPEAT_ID = 4000L;

    /**
     * Execution param variables
     */
    public static final String QUALITIS_DELETE_FAIL_CHECK_RESULT = "qualitis_delete_fail_check_result";
    public static final String QUALITIS_UPLOAD_RULE_METRIC_VALUE = "qualitis_upload_rule_metric_value";
    public static final String QUALITIS_UPLOAD_ABNORMAL_VALUE = "qualitis_upload_abnormal_value";
    public static final String QUALITIS_ALERT_RECEIVERS = "qualitis_alert_receivers";
    public static final String QUALITIS_CLUSTER_NAME = "qualitis_cluster_name";
    public static final String QUALITIS_ALERT_LEVEL = "qualitis_alert_level";

    public static final String QUALITIS_ENGINE_REUSE = "engine_reuse";
    public static final String QUALITIS_STARTUP_PARAM = "qualitis_startup_param";
    public static final String QUALITIS_ENGINE_TYPE = "qualitis.linkis.engineType";
    public static final String QUALITIS_MID_TABLE_REUSE = "mid_table_reuse";
    public static final String QUALITIS_UNION_ALL_SAVE = "union_all_save";


    /**
     * Set flag
     */
    public static final String SPARK_SET_FLAG = "qualitis.spark.set.";
    /**
     * Special splitor
     */
    public static final String AND = "and";

    /**
     * other
     */
    public static final int APPLICATION_RANDOM_LENGTH = 6;

    /**
     * 数据源管理
     */
    public static final int DATASOURCE_MANAGER_INPUT_TYPE_MANUAL = 1;
    public static final int DATASOURCE_MANAGER_INPUT_TYPE_AUTO = 2;
    public static final int DATASOURCE_MANAGER_VERIFY_TYPE_SHARE = 1;
    public static final int DATASOURCE_MANAGER_VERIFY_TYPE_NON_SHARE = 2;

    /**
     * 认证方式
     */
    public static final String AUTH_TYPE_ACCOUNT_PWD = "accountPwd";
    public static final String AUTH_TYPE_DPM = "dpm";

}
