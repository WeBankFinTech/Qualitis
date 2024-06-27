package com.webank.wedatasphere.qualitis.config;

import bsp.encrypt.EncryptUtil;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author allenzhou
 */
@Configuration
public class DataSourceConfig {

    @Value("${task.persistent.private_key}")
    private String privateKey;
    @Value("${task.persistent.password}")
    private String password;

    @Resource
    private JpaProperties jpaProperties;

    @Resource
    private HibernateProperties hibernateProperties;

    @Primary
    @Bean(name = "masterDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.master")
    public DataSourceProperties masterDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "workerDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.worker")
    public DataSourceProperties workerDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean
    @ConditionalOnProperty(name = "spring.datasource.master.type", havingValue = "com.webank.wedatasphere.qualitis.util.MyDataSource", matchIfMissing = true)
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public HikariDataSource dataSource(@Qualifier("masterDataSourceProperties") DataSourceProperties properties) throws Exception {
        HikariDataSource dataSource = properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
        dataSource.setPassword(EncryptUtil.decrypt(privateKey, password));
        return dataSource;
    }

    @Bean(name = "workerDataSource")
    @ConditionalOnProperty(name = "spring.datasource.worker.type", havingValue = "com.webank.wedatasphere.qualitis.util.MyDataSource", matchIfMissing = true)
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public HikariDataSource workerDataSource(@Qualifier("workerDataSourceProperties") DataSourceProperties properties) throws Exception {
        HikariDataSource dataSource = properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
        dataSource.setPassword(EncryptUtil.decrypt(privateKey, password));
        return dataSource;
    }

    @Bean
    public HibernateJpaVendorAdapter hibernateJpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setDatabase(jpaProperties.getDatabase());
        hibernateJpaVendorAdapter.setDatabasePlatform(jpaProperties.getDatabasePlatform());
        hibernateJpaVendorAdapter.setShowSql(jpaProperties.isShowSql());
        hibernateJpaVendorAdapter.setGenerateDdl(jpaProperties.isGenerateDdl());

        return hibernateJpaVendorAdapter;
    }

    @Bean
    public Map<String, Object> vendorProperties() {
        return hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings());
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
