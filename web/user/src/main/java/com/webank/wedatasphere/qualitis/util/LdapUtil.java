package com.webank.wedatasphere.qualitis.util;

import com.webank.wedatasphere.qualitis.config.LdapConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

/**
 * @author allenzhou@webank.com
 * @date 2021/8/27 10:30
 */
@Component
public class LdapUtil {
    @Autowired
    private LdapConfig ldapConfig;

    private static final Logger LOGGER = LoggerFactory.getLogger(LdapUtil.class);

    public boolean loginByLdap(String userName, String password) {
//        LDAPConnectionFactory ldf = new LDAPConnectionFactory(ldapConfig.getIp(), ldapConfig.getPort());
//        Connection conn;
//        try {
//            conn = ldf.getConnection();
//        } catch (LdapException e) {
//            LOGGER.info("connecting failed. please check ip :" + ldapConfig.getIp() + " port: " + ldapConfig.getPort());
//            return false;
//        }
//        BindRequest request = Requests.newSimpleBindRequest(userName , password.getBytes());
//        try {
//            conn.bind(request);
//            LOGGER.info("Login by ladp success! User: {}", userName);
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage(), e);
//            return false;
//        } finally {
//            conn.close();
//        }
//        return true;

        String ip = ldapConfig.getIp();
        int port = ldapConfig.getPort();
        String baseDn = ldapConfig.getBaseDn();

        InitialDirContext ctx = null;
        Hashtable<String, String> hashtable = new Hashtable<>();
        // LDAP 访问安全级别（none，simple，strong）
        hashtable.put(Context.SECURITY_AUTHENTICATION, "simple");
        // AD 的用户名
        hashtable.put(Context.SECURITY_PRINCIPAL, "cn=" + userName + "," + baseDn);
        // AD 的密码
        hashtable.put(Context.SECURITY_CREDENTIALS, password);
        // LDAP 工厂类
        hashtable.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        // 连接超时设置
        hashtable.put("com.sun.jndi.ldap.connect.timeout", "3000");
        hashtable.put(Context.PROVIDER_URL, "ldap://" + ip + ":" + port);
        LOGGER.info("LDAP URL: ldap://" + ip + ":" + port);

        try {
            ctx = new InitialDirContext(hashtable);
            LOGGER.info("LDAP auth " + userName + " success. ");
            return true;
        } catch (AuthenticationException e) {
            LOGGER.error("LDAP auth " + userName + " failed. ", e);
            return false;
        } catch (CommunicationException e) {
            LOGGER.error("LDAP connect failed. ", e);
            return false;
        } catch (Exception e) {
            LOGGER.error("LDAP auth " + userName + " failed. ", e);
            return false;
        } finally {
            if (null != ctx) {
                try {
                    ctx.close();
                } catch (Exception e) {
                    LOGGER.error("LDAP disconnect failed. ", e);
                }
            }
        }
    }
}
