package com.webank.wedatasphere.dss.appconn.qualitis.publish;

import com.webank.wedatasphere.dss.appconn.qualitis.QualitisAppConn;
import com.webank.wedatasphere.dss.standard.app.development.operation.AbstractDevelopmentOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.DevelopmentRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;

/**
 * @author allenzhou
 * @date 2022-03-09
 */
public class QualitisDevelopmentOperation<K extends DevelopmentRequestRef<K>, V extends ResponseRef>
        extends AbstractDevelopmentOperation<K, V> {

    /**
     * I override this method, since I want to use SSORequestOperation to request Visualis server.
     * @return visualis appConn name
     */
    @Override
    protected String getAppConnName() {
        return QualitisAppConn.QUALITIS_APPCONN_NAME;
    }

    protected DevelopmentService getDevelopmentService() {
        return (DevelopmentService) service;
    }

    protected AppInstance getAppInstance() {
        return service.getAppInstance();
    }

}
