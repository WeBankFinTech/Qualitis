package com.webank.wedatasphere.qualitis.scheduled.request.checker;

import com.webank.wedatasphere.qualitis.scheduled.constant.SignalTypeEnum;

/**
 * @author v_minminghe@webank.com
 * @date 2022-11-03 17:30
 * @description
 */
public enum SignalParameterCheckerFactory {
    /**
     * instance
     */
    INSTANCE;

    public AbstractJsonParameterChecker fromSignalType(SignalTypeEnum signalTypeEnum) {
        switch (signalTypeEnum) {
            case RMB_SEND:
                return new RmbSendJsonParameterChecker();
            case EVENT_RECEIVE:
                return new EventReceiverJsonParameterChecker();
            case EVENT_SEND:
                return new EventSendJsonParameterChecker();
            default:
                return null;
        }
    }
}
