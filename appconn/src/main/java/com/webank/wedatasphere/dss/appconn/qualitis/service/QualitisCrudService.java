/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.appconn.qualitis.service;

import com.webank.wedatasphere.dss.appconn.qualitis.ref.operation.QualitisRefCopyOperation;
import com.webank.wedatasphere.dss.appconn.qualitis.ref.operation.QualitisRefCreationOperation;
import com.webank.wedatasphere.dss.appconn.qualitis.ref.operation.QualitisRefDeletionOperation;
import com.webank.wedatasphere.dss.appconn.qualitis.ref.operation.QualitisRefUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefCreationOperation;
import com.webank.wedatasphere.dss.standard.app.development.service.AbstractRefCRUDService;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefCopyOperation;

/**
 * @author allenzhou@webank.com
 * @date 2021/6/21 14:40
 */
public class QualitisCrudService extends AbstractRefCRUDService {

    @Override
    public RefCreationOperation createRefCreationOperation() {
        QualitisRefCreationOperation creationOperation = new QualitisRefCreationOperation();
        return creationOperation;
    }

    @Override
    public RefCopyOperation createRefCopyOperation() {
        QualitisRefCopyOperation copyOperation = new QualitisRefCopyOperation();
        return copyOperation;
    }

    @Override
    public RefUpdateOperation createRefUpdateOperation() {
        QualitisRefUpdateOperation qualitisRefUpdateOperation = new QualitisRefUpdateOperation();
        return qualitisRefUpdateOperation;
    }

    @Override
    public RefDeletionOperation createRefDeletionOperation() {
        QualitisRefDeletionOperation deletionOperation = new QualitisRefDeletionOperation();
        return deletionOperation;
    }
}
