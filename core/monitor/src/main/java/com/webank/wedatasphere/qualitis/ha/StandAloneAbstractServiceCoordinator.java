package com.webank.wedatasphere.qualitis.ha;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @author howeye
 */
@Component
@ConditionalOnProperty(name = "ha.enable", havingValue = "false")
public class StandAloneAbstractServiceCoordinator extends AbstractServiceCoordinator {
    @Override
    public void init() {
        // No need to init
    }

    @Override
    public void coordinate() {
        // No need to coordinate with other service
    }

    @Override
    public void release() {
        // No need to release
    }
}
