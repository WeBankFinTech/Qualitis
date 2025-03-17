package com.webank.wedatasphere.qualitis.util;

//import com.webank.utils.Sense;
//import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
//import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
//import org.apache.commons.lang.StringUtils;

/**
 * ScanRuleUtils
 */
public class ScanRulesUtils {
//    public static String auditSenseInfo(String senseInfo, int rules, boolean accurate) {
//        return Sense.auditSenseInfo(senseInfo, rules, accurate);
//    }
//
//    public static String auditSenseInfoFromIdentifyValue(String identifyValue) {
//         String result = Sense.auditSenseInfo(identifyValue, Sense.SenseType.ID| Sense.SenseType.BANK| Sense.SenseType.PHONE| Sense.SenseType.EMAIL| Sense.SenseType.HOME| Sense.SenseType.CAR| Sense.SenseType.OPENID| Sense.SenseType.CN, false);
//         if (StringUtils.isNotBlank(result)) {
//             String[] results = result.split(SpecCharEnum.DIVIDER.getValue());
//             for (String currentResult : results) {
//                 String[] currentResults = currentResult.split(SpecCharEnum.COLON.getValue());
//                 if (currentResults.length != QualitisConstants.LENGTH_TWO) {
//                     continue;
//                 }
//                 identifyValue = identifyValue.replace(currentResults[1], currentResults[0] + QualitisConstants.ALL_STARS);
//             }
//         }
//         return identifyValue;
//    }
}
