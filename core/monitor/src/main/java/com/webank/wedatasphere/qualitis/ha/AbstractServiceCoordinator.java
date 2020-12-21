package com.webank.wedatasphere.qualitis.ha;

/**
 * @author howeye
 */
public abstract class AbstractServiceCoordinator {

    /**
     * Do some init work
     */
    public abstract void init();

    /**
     * coordinate multi service
     */
    public abstract void coordinate();

    /**
     * Do some release work
     */
    public abstract void release();

}
