package com.webank.wedatasphere.qualitis.client;

/**
 * @author
 */
public interface MailClient {

    /**
     * Send Esb Mail.
     * @param result
     * @param createUser
     * @throws Exception
     */
    void sendEsbMail(String result,String createUser) throws Exception;

    /**
     * get Hr Message.
     * @throws Exception
     * @return
     */
    String getHrMessage() throws Exception;
}
