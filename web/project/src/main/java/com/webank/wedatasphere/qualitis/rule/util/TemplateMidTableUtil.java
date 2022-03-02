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

package com.webank.wedatasphere.qualitis.rule.util;

import com.webank.wedatasphere.qualitis.rule.constant.TemplateInputTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateRegexpTypeEnum;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateMidTableInputMeta;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateInputTypeEnum;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateMidTableInputMeta;

/**
 * @author howeye
 */
public class TemplateMidTableUtil {

    private TemplateMidTableUtil() {
        // Default Constructor
    }

    public static boolean shouldResponse(TemplateMidTableInputMeta templateMidTableInputMeta) {
        if (templateMidTableInputMeta.getInputType().equals(TemplateInputTypeEnum.REGEXP.getCode())) {
            if (templateMidTableInputMeta.getRegexpType() == null) {
                return true;
            }

            if (templateMidTableInputMeta.getRegexpType().equals(TemplateRegexpTypeEnum.IDENTITY.getCode()) ||
            templateMidTableInputMeta.getRegexpType().equals(TemplateRegexpTypeEnum.NOT_NUMBER.getCode())) {
                return false;
            }
        }

        // Return false if can not auto adapt. return true if type equals to field, table, database, field_concat
         if (!templateMidTableInputMeta.getInputType().equals(TemplateInputTypeEnum.FIELD.getCode()) &&
                !templateMidTableInputMeta.getInputType().equals(TemplateInputTypeEnum.TABLE.getCode()) &&
                !templateMidTableInputMeta.getInputType().equals(TemplateInputTypeEnum.DATABASE.getCode()) &&
                !templateMidTableInputMeta.getInputType().equals(TemplateInputTypeEnum.FIELD_CONCAT.getCode()) &&
                !templateMidTableInputMeta.getInputType().equals(TemplateInputTypeEnum.FIELD_REPLACE_NULL_CONCAT.getCode())) {
            return true;
        }
        return false;
    }

}
