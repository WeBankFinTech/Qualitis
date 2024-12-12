/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.qualitis.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.webank.wedatasphere.qualitis.project.constant.ExcelSheetName;
import com.webank.wedatasphere.qualitis.project.excel.ExcelRuleMetric;
import java.util.ArrayList;
import java.util.List;

/**
 * @author allenzhou
 */
public class ExcelRuleMetricListener extends AnalysisEventListener {
    private List<ExcelRuleMetric> ruleMetricContent = new ArrayList<>();

    @Override
    public void invoke(Object object, AnalysisContext context) {
        if (context.getCurrentSheet().getSheetName().equals(ExcelSheetName.RULE_METRIC_NAME)) {
            ruleMetricContent.add((ExcelRuleMetric) object);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // no need to implement
    }

    public List<ExcelRuleMetric> getRuleMetricContent() {
        return ruleMetricContent;
    }
}
