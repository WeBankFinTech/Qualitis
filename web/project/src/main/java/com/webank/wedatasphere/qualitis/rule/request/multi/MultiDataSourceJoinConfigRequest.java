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

package com.webank.wedatasphere.qualitis.rule.request.multi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSourceMapping;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSourceMapping;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 * @author howeye
 */
public class MultiDataSourceJoinConfigRequest {

    private List<MultiDataSourceJoinColumnRequest> left;
    @JsonProperty("left_statement")
    private String leftStatement;
    private Integer operation;
    private List<MultiDataSourceJoinColumnRequest> right;
    @JsonProperty("right_statement")
    private String rightStatement;

    public MultiDataSourceJoinConfigRequest() {
    }

    public MultiDataSourceJoinConfigRequest(RuleDataSourceMapping ruleDataSourceMapping) {
        this.leftStatement = ruleDataSourceMapping.getLeftStatement();
        this.operation = ruleDataSourceMapping.getOperation();
        this.rightStatement = ruleDataSourceMapping.getRightStatement();
        this.left = new ArrayList<>();
        this.right = new ArrayList<>();
        String[] leftColumnNames = ruleDataSourceMapping.getLeftColumnNames().split(",");
        String[] rightColumnNames = ruleDataSourceMapping.getRightColumnNames().split(",");

        if (StringUtils.isNotBlank(ruleDataSourceMapping.getLeftColumnTypes()) && StringUtils.isNotBlank(ruleDataSourceMapping.getRightColumnTypes())) {
            String[] leftColumnTypes = ruleDataSourceMapping.getLeftColumnTypes().split("\\|");
            String[] rightColumnTypes = ruleDataSourceMapping.getRightColumnTypes().split("\\|");

            for (int i = 0; i < leftColumnNames.length && i < leftColumnTypes.length; i ++) {
                this.left.add(new MultiDataSourceJoinColumnRequest(leftColumnNames[i], leftColumnTypes[i]));
            }
            for (int j = 0; j < rightColumnNames.length && j < rightColumnTypes.length; j ++) {
                this.right.add(new MultiDataSourceJoinColumnRequest(rightColumnNames[j], rightColumnTypes[j]));
            }
        } else {
            for (int i = 0; i < leftColumnNames.length; i ++) {
                this.left.add(new MultiDataSourceJoinColumnRequest(leftColumnNames[i], ""));
            }
            for (int j = 0; j < rightColumnNames.length; j ++) {
                this.right.add(new MultiDataSourceJoinColumnRequest(rightColumnNames[j], ""));
            }
        }

    }

    public String getLeftStatement() {
        return leftStatement;
    }

    public void setLeftStatement(String leftStatement) {
        this.leftStatement = leftStatement;
    }

    public Integer getOperation() {
        return operation;
    }

    public void setOperation(Integer operation) {
        this.operation = operation;
    }

    public String getRightStatement() {
        return rightStatement;
    }

    public void setRightStatement(String rightStatement) {
        this.rightStatement = rightStatement;
    }

    public List<MultiDataSourceJoinColumnRequest> getLeft() {
        return left;
    }

    public void setLeft(List<MultiDataSourceJoinColumnRequest> left) {
        this.left = left;
    }

    public List<MultiDataSourceJoinColumnRequest> getRight() {
        return right;
    }

    public void setRight(List<MultiDataSourceJoinColumnRequest> right) {
        this.right = right;
    }
    
    public static void checkRequest(MultiDataSourceJoinConfigRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "Request");
        CommonChecker.checkString(request.getLeftStatement(), "Left statement");
        CommonChecker.checkString(request.getRightStatement(), "Right statement");

        Boolean leftColumnChooseFlag = false;
        Boolean rightColumnChooseFlag = false;
        for (MultiDataSourceJoinColumnRequest multiDataSourceJoinColumnRequest : request.getLeft()) {
            MultiDataSourceJoinColumnRequest.checkRequest(multiDataSourceJoinColumnRequest);
            if (multiDataSourceJoinColumnRequest.getColumnName().startsWith("tmp1.")) {
                leftColumnChooseFlag = true;
            } else if (multiDataSourceJoinColumnRequest.getColumnName().startsWith("tmp2.")) {
                rightColumnChooseFlag = true;
            }
        }
        for (MultiDataSourceJoinColumnRequest multiDataSourceJoinColumnRequest : request.getRight()) {
            MultiDataSourceJoinColumnRequest.checkRequest(multiDataSourceJoinColumnRequest);
            if (multiDataSourceJoinColumnRequest.getColumnName().startsWith("tmp1.")) {
                leftColumnChooseFlag = true;
            } else if (multiDataSourceJoinColumnRequest.getColumnName().startsWith("tmp2.")) {
                rightColumnChooseFlag = true;
            }
        }

        if (!leftColumnChooseFlag || !rightColumnChooseFlag) {
            throw new UnExpectedRequestException("{&PLEASE_CHOOSE_THE_CORRECT_MAPPING_RELATION_SHIP}");
        }
    }
}
